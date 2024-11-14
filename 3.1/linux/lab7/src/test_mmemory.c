/* test_mmemory.c */
#include "mmemory.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// Функция для тестирования инициализации менеджера памяти
void test_memory_manager_init()
{
    MemoryManager manager;
    int init_result = memory_manager_init(&manager, 1024, FIRST_FIT); // 1 КБ
    assert(init_result == 0);
    assert(manager.memory_start != NULL);
    assert(manager.total_size == 1024);
    assert(manager.free_list->size == 1024 - sizeof(MemoryBlock));
    assert(manager.free_list->is_free == 1);
    assert(manager.free_list->next == NULL);
    assert(manager.free_list->prev == NULL);
    printf("test_memory_manager_init пройден.\n");

    memory_manager_destroy(&manager);
}

// Функция для тестирования выделения и освобождения памяти
void test_memory_alloc_free()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024, FIRST_FIT);

    // Выделяем 200 байт
    void *ptr1 = memory_alloc(&manager, 200);
    assert(ptr1 != NULL);
    assert(!((MemoryBlock *)((char *)ptr1 - sizeof(MemoryBlock)))->is_free);
    assert(((MemoryBlock *)((char *)ptr1 - sizeof(MemoryBlock)))->size == 200);

    // Выделяем 300 байт
    void *ptr2 = memory_alloc(&manager, 300);
    assert(ptr2 != NULL);
    assert(!((MemoryBlock *)((char *)ptr2 - sizeof(MemoryBlock)))->is_free);
    assert(((MemoryBlock *)((char *)ptr2 - sizeof(MemoryBlock)))->size == 300);

    // Освобождаем первый блок
    int free_result1 = memory_free_block(&manager, ptr1);
    assert(free_result1 == 0);
    assert(((MemoryBlock *)((char *)ptr1 - sizeof(MemoryBlock)))->is_free == 1);

    // Освобождаем второй блок
    int free_result2 = memory_free_block(&manager, ptr2);
    assert(free_result2 == 0);
    assert(((MemoryBlock *)((char *)ptr2 - sizeof(MemoryBlock)))->is_free == 1);

    // После освобождения оба блока должны слиться в один большой
    assert(manager.free_list->size == 1024 - sizeof(MemoryBlock));
    assert(manager.free_list->is_free == 1);
    assert(manager.free_list->next == NULL);

    printf("test_memory_alloc_free пройден.\n");

    memory_manager_destroy(&manager);
}

// Функция для тестирования записи и чтения данных
void test_memory_write_read()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024, FIRST_FIT);

    // Выделяем блок
    void *ptr = memory_alloc(&manager, 100);
    assert(ptr != NULL);

    // Данные для записи
    char data_write[100];
    for (int i = 0; i < 100; i++)
    {
        data_write[i] = (char)i;
    }

    // Записываем данные
    int write_result = memory_write(&manager, ptr, data_write, 100);
    assert(write_result == 0);

    // Буфер для чтения
    char data_read[100];
    memset(data_read, 0, 100);

    // Читаем данные
    int read_result = memory_read(&manager, ptr, data_read, 100);
    assert(read_result == 0);

    // Проверяем данные
    assert(memcmp(data_write, data_read, 100) == 0);

    printf("test_memory_write_read пройден.\n");

    // Освобождаем блок
    memory_free_block(&manager, ptr);
    memory_manager_destroy(&manager);
}

// Функция для тестирования границ выделения и ошибок
void test_memory_boundaries()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024, FIRST_FIT);

    // Попытка выделить больше памяти, чем доступно
    void *ptr = memory_alloc(&manager, 2000);
    assert(ptr == NULL);

    // Выделяем блок
    ptr = memory_alloc(&manager, 500);
    assert(ptr != NULL);

    // Попытка записи за пределы блока
    char data[600];
    int write_result = memory_write(&manager, ptr, data, 600);
    assert(write_result == -1);
    assert(errno == EFAULT);

    // Попытка чтения за пределы блока
    char buffer[600];
    int read_result = memory_read(&manager, ptr, buffer, 600);
    assert(read_result == -1);
    assert(errno == EFAULT);

    // Освобождаем блок
    int free_result = memory_free_block(&manager, ptr);
    assert(free_result == 0);

    // Попытка освобождения уже освобожденного блока
    free_result = memory_free_block(&manager, ptr);
    assert(free_result == -1);
    assert(errno == EINVAL);

    printf("test_memory_boundaries пройден.\n");

    memory_manager_destroy(&manager);
}

// Функция для стресс-тестирования менеджера памяти
void test_memory_stress()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024 * 1024, FIRST_FIT); // 1 МБ

#define NUM_OPERATIONS 10000
#define MAX_ALLOC_SIZE 1024

    void **allocations = calloc(NUM_OPERATIONS, sizeof(void *));
    size_t *alloc_sizes = calloc(NUM_OPERATIONS, sizeof(size_t));
    assert(allocations != NULL && alloc_sizes != NULL);

    // Многократное выделение и освобождение
    for (int i = 0; i < NUM_OPERATIONS; i++)
    {
        size_t size = rand() % MAX_ALLOC_SIZE + 1;
        allocations[i] = memory_alloc(&manager, size);
        alloc_sizes[i] = size;
        if (allocations[i] != NULL)
        {
            // Заполняем память некоторыми данными
            memset(allocations[i], 0xAA, size);
        }
        // Случайно освобождаем некоторые блоки
        if (i % 3 == 0 && allocations[i] != NULL)
        {
            int free_result = memory_free_block(&manager, allocations[i]);
            assert(free_result == 0);
            allocations[i] = NULL;
        }
    }

    // Освобождаем все оставшиеся блоки
    for (int i = 0; i < NUM_OPERATIONS; i++)
    {
        if (allocations[i] != NULL)
        {
            int free_result = memory_free_block(&manager, allocations[i]);
            assert(free_result == 0);
            allocations[i] = NULL;
        }
    }

    // Проверяем, что вся память свободна
    assert(manager.free_list->size == manager.total_size - sizeof(MemoryBlock));
    assert(manager.free_list->is_free == 1);
    assert(manager.free_list->next == NULL);

    printf("test_memory_stress пройден.\n");

    free(allocations);
    free(alloc_sizes);
    memory_manager_destroy(&manager);
}

// Функция для тестирования разных алгоритмов распределения
void test_allocation_algorithms()
{
    // Тестирование First Fit
    {
        MemoryManager manager;
        memory_manager_init(&manager, 1024, FIRST_FIT);

        void *ptr1 = memory_alloc(&manager, 200);
        void *ptr2 = memory_alloc(&manager, 300);
        void *ptr3 = memory_alloc(&manager, 100);

        assert(ptr1 != NULL && ptr2 != NULL && ptr3 != NULL);

        // Освобождаем средний блок
        memory_free_block(&manager, ptr2);

        // Выделяем блок, который должен занять место ptr2
        void *ptr4 = memory_alloc(&manager, 250);
        assert(ptr4 == ptr2);

        memory_manager_destroy(&manager);
    }

    // Тестирование Best Fit
    {
        MemoryManager manager;
        memory_manager_init(&manager, 1024, BEST_FIT);

        void *ptr1 = memory_alloc(&manager, 300);
        void *ptr2 = memory_alloc(&manager, 200);
        void *ptr3 = memory_alloc(&manager, 100);

        assert(ptr1 != NULL && ptr2 != NULL && ptr3 != NULL);

        // Освобождаем блок ptr1 и ptr3
        memory_free_block(&manager, ptr1);
        memory_free_block(&manager, ptr3);

        // Выделяем блок, который должен занять наилучшее соответствие (ptr3)
        void *ptr4 = memory_alloc(&manager, 150);
        assert(ptr4 == ptr3);

        memory_manager_destroy(&manager);
    }

    // Тестирование Worst Fit
    {
        MemoryManager manager;
        memory_manager_init(&manager, 1024, WORST_FIT);

        void *ptr1 = memory_alloc(&manager, 100);
        void *ptr2 = memory_alloc(&manager, 200);
        void *ptr3 = memory_alloc(&manager, 300);

        assert(ptr1 != NULL && ptr2 != NULL && ptr3 != NULL);

        // Освобождаем блок ptr1 и ptr2
        memory_free_block(&manager, ptr1);
        memory_free_block(&manager, ptr2);

        // Выделяем блок, который должен занять наихудшее соответствие (ptr1)
        void *ptr4 = memory_alloc(&manager, 150);
        assert(ptr4 == ptr2);

        memory_manager_destroy(&manager);
    }

    printf("test_allocation_algorithms пройден.\n");
}

// Функция для тестирования дефрагментации
void test_defragmentation()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024, FIRST_FIT);

    // Выделяем и освобождаем блоки, чтобы создать фрагментацию
    void *ptr1 = memory_alloc(&manager, 200);
    void *ptr2 = memory_alloc(&manager, 100);
    void *ptr3 = memory_alloc(&manager, 150);

    assert(ptr1 != NULL && ptr2 != NULL && ptr3 != NULL);

    memory_free_block(&manager, ptr1);
    memory_free_block(&manager, ptr3);

    // Проверяем, что блоки не объединены
    assert(manager.free_list->next != NULL);
    assert(manager.free_list->is_free == 1);
    assert(manager.free_list->next->is_free == 1);

    // Дефрагментация
    memory_defragment(&manager);

    // Проверяем, что блоки объединены
    assert(manager.free_list->next == NULL);
    assert(manager.free_list->size >= 200 + 150 + sizeof(MemoryBlock));

    printf("test_defragmentation пройден.\n");

    memory_manager_destroy(&manager);
}

// Функция для тестирования освобождения неверного указателя
void test_invalid_free()
{
    MemoryManager manager;
    memory_manager_init(&manager, 1024, FIRST_FIT);

    // Попытка освобождения NULL
    int free_result = memory_free_block(&manager, NULL);
    assert(free_result == -1);
    assert(errno == EINVAL);

    // Попытка освобождения невыделенного указателя
    int fake;
    free_result = memory_free_block(&manager, &fake);
    assert(free_result == -1);
    assert(errno == EFAULT);

    // Освобождение корректного блока
    void *ptr = memory_alloc(&manager, 100);
    assert(ptr != NULL);
    free_result = memory_free_block(&manager, ptr);
    assert(free_result == 0);

    // Попытка двойного освобождения
    free_result = memory_free_block(&manager, ptr);
    assert(free_result == -1);
    assert(errno == EINVAL);

    printf("test_invalid_free пройден.\n");

    memory_manager_destroy(&manager);
}

int main()
{
    printf("Запуск тестов менеджера памяти...\n");

    test_memory_manager_init();
    test_memory_alloc_free();
    test_memory_write_read();
    test_memory_boundaries();
    test_memory_stress();
    test_allocation_algorithms();
    test_defragmentation();
    test_invalid_free();

    printf("Все тесты успешно пройдены.\n");
    return 0;
}
