/* mmemory.h */
#ifndef MMEMORY_H
#define MMEMORY_H

#include <stddef.h>
#include <errno.h>

// Макросы для логирования
#ifdef DEBUG
#include <stdio.h>
#define LOG(fmt, ...) fprintf(stderr, fmt, __VA_ARGS__)
#else
#define LOG(fmt, ...)
#endif

// Перечисление алгоритмов распределения
typedef enum
{
    FIRST_FIT,
    BEST_FIT,
    WORST_FIT
} AllocationAlgorithm;

// Структура блока памяти
typedef struct MemoryBlock
{
    size_t size;
    int is_free;
    struct MemoryBlock *next;
    struct MemoryBlock *prev;
} MemoryBlock;

// Структура менеджера памяти
typedef struct MemoryManager
{
    void *memory_start;
    size_t total_size;
    MemoryBlock *free_list;
    AllocationAlgorithm algorithm;
} MemoryManager;

// Прототипы функций
int memory_manager_init(MemoryManager *manager, size_t size, AllocationAlgorithm algorithm);
void *memory_alloc(MemoryManager *manager, size_t size);
int memory_free_block(MemoryManager *manager, void *ptr);
int memory_write(MemoryManager *manager, void *ptr, const void *data, size_t size);
int memory_read(MemoryManager *manager, void *ptr, void *buffer, size_t size);
void memory_defragment(MemoryManager *manager);
void memory_manager_destroy(MemoryManager *manager);

#endif // MMEMORY_H
