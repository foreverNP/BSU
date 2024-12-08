#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

// Включите ваш файл с менеджером памяти
#include "mmemory.h"

void test_mem_mgr_malloc()
{
    void *ptr = mem_mgr_malloc(100);
    assert(ptr != NULL);
    mem_mgr_free(ptr);
}

void test_mem_mgr_calloc()
{
    void *ptr = mem_mgr_calloc(10, 10);
    assert(ptr != NULL);
    mem_mgr_free(ptr);
}

void test_mem_mgr_realloc()
{
    void *ptr = mem_mgr_malloc(100);
    assert(ptr != NULL);
    void *new_ptr = mem_mgr_realloc(ptr, 200);
    assert(new_ptr != NULL);
    mem_mgr_free(new_ptr);
}

void test_mem_mgr_free()
{
    void *ptr = mem_mgr_malloc(100);
    assert(ptr != NULL);
    mem_mgr_free(ptr);
    assert(mem_mgr_size_get() == 0);
}

void test_mem_mgr_size_get()
{
    void *ptr1 = mem_mgr_malloc(100);
    void *ptr2 = mem_mgr_malloc(200);
    assert(mem_mgr_size_get() == 300);
    mem_mgr_free(ptr1);
    mem_mgr_free(ptr2);
}

void test_mem_mgr_list_print()
{
    void *ptr1 = mem_mgr_malloc(100);
    void *ptr2 = mem_mgr_malloc(200);
    mem_mgr_list_print();
    mem_mgr_free(ptr1);
    mem_mgr_free(ptr2);
}

void test_mem_mgr_write_read()
{
    void *ptr = mem_mgr_malloc(100);
    assert(ptr != NULL);

    char data[] = "Hello, World!";
    mem_mgr_write(ptr, 0, data, sizeof(data));

    char read_data[sizeof(data)];
    mem_mgr_read(ptr, 0, read_data, sizeof(read_data));

    assert(memcmp(data, read_data, sizeof(data)) == 0);

    mem_mgr_free(ptr);
}

int main()
{
    mem_mgr_init();

    test_mem_mgr_malloc();
    test_mem_mgr_calloc();
    test_mem_mgr_realloc();
    test_mem_mgr_free();
    test_mem_mgr_size_get();
    test_mem_mgr_list_print();
    test_mem_mgr_write_read();

    printf("All tests passed!\n");
    return 0;
}