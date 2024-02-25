#include "mmemory.h"

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define BLOCK_ARR_SIZE 1000
#define FOR_EACH_BLOCK(block) \
    for ((block) = block_arr; (block) < block_arr + BLOCK_ARR_SIZE; ++(block))

struct block
{
    const void *ptr;
    size_t size;
};

static struct block block_arr[BLOCK_ARR_SIZE];
static size_t block_arr_count;

static struct block *ptr_block_get(const void *ptr)
{
    struct block *ret;
    FOR_EACH_BLOCK(ret)
    if (ret->ptr == ptr)
        return ret;
    return NULL;
}

static struct block *avail_block_get(void)
{
    return block_arr_count < BLOCK_ARR_SIZE ? ptr_block_get(NULL) : NULL;
}

static void block_set(const void *ptr, size_t size)
{
    struct block *block = avail_block_get();
    if (block != NULL)
    {
        block->ptr = ptr;
        block->size = size;
        ++block_arr_count;
        printf("Block set: ptr = %p, size = %zu\n", ptr, size);
    }
    else
    {
        printf("Failed to set block: no available blocks\n");
    }
}

static void block_modify(const void *old_ptr, const void *new_ptr, size_t new_size)
{
    struct block *block = ptr_block_get(old_ptr);
    if (block != NULL)
    {
        block->ptr = new_ptr;
        block->size = new_size;
        printf("Block modified: old_ptr = %p, new_ptr = %p, new_size = %zu\n", old_ptr, new_ptr, new_size);
    }
    else
    {
        block_set(new_ptr, new_size);
    }
}

static void block_clear(const void *ptr)
{
    struct block *block = ptr_block_get(ptr);
    if (block != NULL)
    {
        block->ptr = NULL;
        block->size = 0;
        --block_arr_count;
        printf("Block cleared: ptr = %p\n", ptr);
    }
    else
    {
        printf("Failed to clear block: block not found\n");
    }
}

void *mem_mgr_calloc(size_t num, size_t size)
{
    void *ret;
    ret = calloc(num, size);
    if (ret != NULL)
    {
        block_set(ret, num * size);
    }
    else
    {
        printf("Failed to allocate memory with calloc\n");
    }
    return ret;
}

void *mem_mgr_malloc(size_t size)
{
    void *ret;
    ret = malloc(size);
    if (ret != NULL)
    {
        block_set(ret, size);
    }
    else
    {
        printf("Failed to allocate memory with malloc\n");
    }
    return ret;
}

void *mem_mgr_realloc(void *ptr, size_t size)
{
    void *ret;
    ret = realloc(ptr, size);
    if (size == 0)
    {
        block_clear(ptr);
    }
    else if (ret != NULL)
    {
        block_modify(ptr, ret, size);
    }
    else
    {
        printf("Failed to reallocate memory\n");
    }
    return ret;
}

void mem_mgr_free(void *ptr)
{
    block_clear(ptr);
    free(ptr);
}

size_t mem_mgr_size_get(void)
{
    size_t ret = 0;
    struct block *block;
    if (block_arr_count == 0)
    {
        return 0;
    }
    FOR_EACH_BLOCK(block)
    {
        ret += block->size;
    }
    printf("Total allocated memory size: %zu\n", ret);
    return ret;
}

// Добавьте эту функцию вместо существующей mem_mgr_list_print
char *mem_mgr_list_get(void)
{
    static char list[4096];
    size_t total = 0;
    struct block *block;
    size_t offset = 0;

    offset += snprintf(list + offset, sizeof(list) - offset,
                       "|===========================================|\n");
    offset += snprintf(list + offset, sizeof(list) - offset,
                       "|     Address      |          Size          |\n");

    if (block_arr_count != 0)
    {
        offset += snprintf(list + offset, sizeof(list) - offset,
                           "|-------------------------------------------|\n");
        FOR_EACH_BLOCK(block)
        {
            if (block->ptr != NULL)
            {
                offset += snprintf(list + offset, sizeof(list) - offset,
                                   "| %16p | %20zu B |\n", block->ptr, block->size);
                total += block->size;
            }
            if (offset >= sizeof(list))
                break;
        }
    }
    offset += snprintf(list + offset, sizeof(list) - offset,
                       "|-------------------------------------------|\n");
    offset += snprintf(list + offset, sizeof(list) - offset,
                       "|      Total       | %20zu B |\n", total);
    offset += snprintf(list + offset, sizeof(list) - offset,
                       "|===========================================|\n");

    return list;
}

size_t mem_mgr_malloc_max(void)
{
    size_t low = 0, high = SIZE_MAX, current;
    void *ptr;
    do
    {
        current = (high - low) / 2 + low;
        ptr = malloc(current);
        if (ptr != NULL)
        {
            low = current;
            free(ptr);
        }
        else
        {
            high = current;
        }
    } while (high - low > 1);
    printf("Maximum allocatable memory size: %zu\n", low);
    return low;
}
size_t mem_mgr_hash(const void *data, size_t size)
{
    const unsigned char *ptr = data;
    const unsigned char shift_mask = sizeof(size_t) - 1;
    unsigned char shift = 0;
    size_t ret = 0;
    while (size != 0)
    {
        ret ^= (size_t)*ptr << (shift & shift_mask) * 8;
        ++ptr;
        ++shift;
        --size;
    }
    printf("Hash value: %zu\n", ret);
    return ret;
}

void mem_mgr_init(void)
{
    block_arr_count = 0;
    memset(block_arr, 0, sizeof(block_arr));
    printf("Memory manager initialized\n");
}

void mem_mgr_write(void *ptr, size_t offset, const void *data, size_t size)
{
    if (ptr != NULL && offset + size <= mem_mgr_size_get())
    {
        memcpy((char *)ptr + offset, data, size);
        printf("Data written to ptr = %p, offset = %zu, size = %zu\n", ptr, offset, size);
    }
    else
    {
        printf("Failed to write data: invalid parameters\n");
    }
}

void mem_mgr_read(void *ptr, size_t offset, void *data, size_t size)
{
    if (ptr != NULL && offset + size <= mem_mgr_size_get())
    {
        memcpy(data, (char *)ptr + offset, size);
        printf("Data read from ptr = %p, offset = %zu, size = %zu\n", ptr, offset, size);
    }
    else
    {
        printf("Failed to read data: invalid parameters\n");
    }
}