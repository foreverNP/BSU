/* mmemory.c */
#include "mmemory.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

// Инициализация менеджера памяти
int memory_manager_init(MemoryManager *manager, size_t size, AllocationAlgorithm algorithm)
{
    if (manager == NULL || size < sizeof(MemoryBlock))
    {
        errno = EINVAL;
        return -1;
    }

    manager->memory_start = malloc(size);
    if (manager->memory_start == NULL)
    {
        // Обработка ошибки выделения памяти
        fprintf(stderr, "Не удалось выделить память.\n");
        return -1;
    }
    manager->total_size = size;
    manager->algorithm = algorithm;

    // Инициализация первого блока памяти
    manager->free_list = (MemoryBlock *)manager->memory_start;
    manager->free_list->size = size - sizeof(MemoryBlock);
    manager->free_list->is_free = 1;
    manager->free_list->next = NULL;
    manager->free_list->prev = NULL;

    LOG("Инициализирован менеджер памяти: %zu байт, алгоритм %d\n", size, algorithm);
    return 0;
}

// Поиск подходящего блока в зависимости от алгоритма
static MemoryBlock *find_best_fit(MemoryManager *manager, size_t size)
{
    MemoryBlock *current = manager->free_list;
    MemoryBlock *best_fit = NULL;

    while (current != NULL)
    {
        if (current->is_free && current->size >= size)
        {
            if (best_fit == NULL || current->size < best_fit->size)
            {
                best_fit = current;
                if (best_fit->size == size)
                {
                    break; // Наилучшее совпадение
                }
            }
        }
        current = current->next;
    }
    return best_fit;
}

static MemoryBlock *find_worst_fit(MemoryManager *manager, size_t size)
{
    MemoryBlock *current = manager->free_list;
    MemoryBlock *worst_fit = NULL;

    while (current != NULL)
    {
        if (current->is_free && current->size >= size)
        {
            if (worst_fit == NULL || current->size > worst_fit->size)
            {
                worst_fit = current;
            }
        }
        current = current->next;
    }
    return worst_fit;
}

// Выделение блока памяти
void *memory_alloc(MemoryManager *manager, size_t size)
{
    if (manager == NULL || size == 0)
    {
        errno = EINVAL;
        return NULL;
    }

    MemoryBlock *selected_block = NULL;

    if (manager->algorithm == FIRST_FIT)
    {
        selected_block = manager->free_list;
        while (selected_block != NULL)
        {
            if (selected_block->is_free && selected_block->size >= size)
            {
                break;
            }
            selected_block = selected_block->next;
        }
    }
    else if (manager->algorithm == BEST_FIT)
    {
        selected_block = find_best_fit(manager, size);
    }
    else if (manager->algorithm == WORST_FIT)
    {
        selected_block = find_worst_fit(manager, size);
    }

    if (selected_block == NULL)
    {
        LOG("Не удалось найти подходящий блок для размера %zu\n", size);
        errno = ENOMEM;
        return NULL;
    }

    // Если блок больше необходимого, разделяем его
    if (selected_block->size >= size + sizeof(MemoryBlock) + 1)
    { // +1 для хотя бы одного байта в новом блоке
        MemoryBlock *new_block = (MemoryBlock *)((char *)selected_block + sizeof(MemoryBlock) + size);
        new_block->size = selected_block->size - size - sizeof(MemoryBlock);
        new_block->is_free = 1;
        new_block->next = selected_block->next;
        new_block->prev = selected_block;

        if (selected_block->next != NULL)
        {
            selected_block->next->prev = new_block;
        }

        selected_block->size = size;
        selected_block->is_free = 0;
        selected_block->next = new_block;

        LOG("Разделен блок: новый блок размером %zu байт\n", new_block->size);
    }
    else
    {
        // Блок подходит по размеру
        selected_block->is_free = 0;
        LOG("Выделен блок размером %zu байт без разделения\n", selected_block->size);
    }

    return (void *)((char *)selected_block + sizeof(MemoryBlock));
}

// Освобождение блока памяти
int memory_free_block(MemoryManager *manager, void *ptr)
{
    if (manager == NULL || ptr == NULL)
    {
        errno = EINVAL;
        return -1;
    }

    // Проверка, что ptr находится внутри выделенной области
    if (ptr < manager->memory_start || ptr >= (void *)((char *)manager->memory_start + manager->total_size))
    {
        fprintf(stderr, "Попытка освобождения памяти вне области управления.\n");
        errno = EFAULT;
        return -1;
    }

    MemoryBlock *block = (MemoryBlock *)((char *)ptr - sizeof(MemoryBlock));
    if (block->is_free)
    {
        fprintf(stderr, "Блок уже освобожден.\n");
        errno = EINVAL;
        return -1;
    }

    block->is_free = 1;
    LOG("Освобожден блок размером %zu байт\n", block->size);

    // Слияние с последующим блоком, если он свободен
    if (block->next != NULL && block->next->is_free)
    {
        LOG("Слияние с последующим блоком\n");
        block->size += sizeof(MemoryBlock) + block->next->size;
        block->next = block->next->next;
        if (block->next != NULL)
        {
            block->next->prev = block;
        }
    }

    // Слияние с предыдущим блоком, если он свободен
    if (block->prev != NULL && block->prev->is_free)
    {
        LOG("Слияние с предыдущим блоком\n");
        block->prev->size += sizeof(MemoryBlock) + block->size;
        block->prev->next = block->next;
        if (block->next != NULL)
        {
            block->next->prev = block->prev;
        }
        block = block->prev;
    }

    return 0;
}

// Запись данных в память
int memory_write(MemoryManager *manager, void *ptr, const void *data, size_t size)
{
    if (manager == NULL || ptr == NULL || data == NULL)
    {
        errno = EINVAL;
        return -1;
    }

    // Проверка, что ptr находится внутри выделенной области
    if (ptr < manager->memory_start || ptr >= (void *)((char *)manager->memory_start + manager->total_size))
    {
        fprintf(stderr, "Попытка записи за пределы области управления.\n");
        errno = EFAULT;
        return -1;
    }

    MemoryBlock *block = (MemoryBlock *)((char *)ptr - sizeof(MemoryBlock));
    if (size > block->size)
    {
        // Запись выходит за пределы блока
        fprintf(stderr, "Запись выходит за пределы выделенного блока.\n");
        errno = EFAULT;
        return -1;
    }

    memcpy(ptr, data, size);
    LOG("Записаны данные в блок размером %zu байт\n", size);
    return 0;
}

// Чтение данных из памяти
int memory_read(MemoryManager *manager, void *ptr, void *buffer, size_t size)
{
    if (manager == NULL || ptr == NULL || buffer == NULL)
    {
        errno = EINVAL;
        return -1;
    }

    // Проверка, что ptr находится внутри выделенной области
    if (ptr < manager->memory_start || ptr >= (void *)((char *)manager->memory_start + manager->total_size))
    {
        fprintf(stderr, "Попытка чтения за пределы области управления.\n");
        errno = EFAULT;
        return -1;
    }

    MemoryBlock *block = (MemoryBlock *)((char *)ptr - sizeof(MemoryBlock));
    if (size > block->size)
    {
        // Чтение выходит за пределы блока
        fprintf(stderr, "Чтение выходит за пределы выделенного блока.\n");
        errno = EFAULT;
        return -1;
    }

    memcpy(buffer, ptr, size);
    LOG("Прочитаны данные из блока размером %zu байт\n", size);
    return 0;
}

// Дефрагментация памяти
void memory_defragment(MemoryManager *manager)
{
    if (manager == NULL)
    {
        return;
    }

    MemoryBlock *current = manager->free_list;

    while (current != NULL && current->next != NULL)
    {
        if (current->is_free && current->next->is_free)
        {
            LOG("Дефрагментация: слияние блоков %p и %p\n", (void *)current, (void *)current->next);
            current->size += sizeof(MemoryBlock) + current->next->size;
            current->next = current->next->next;
            if (current->next != NULL)
            {
                current->next->prev = current;
            }
        }
        else
        {
            current = current->next;
        }
    }
    LOG("Дефрагментация завершена.\n");
}

// Очистка менеджера памяти
void memory_manager_destroy(MemoryManager *manager)
{
    if (manager == NULL)
    {
        return;
    }
    free(manager->memory_start);
    manager->memory_start = NULL;
    manager->free_list = NULL;
    manager->total_size = 0;
    LOG("Менеджер памяти уничтожен.\n");
}