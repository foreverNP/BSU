#ifndef MEM_MGR_H
#define MEM_MGR_H

#include <stddef.h>

// Функции выделения и освобождения памяти
void *mem_mgr_malloc(size_t size);
void *mem_mgr_calloc(size_t num, size_t size);
void *mem_mgr_realloc(void *ptr, size_t size);
void mem_mgr_free(void *ptr);

// Функции для чтения и записи данных
void mem_mgr_write(void *ptr, size_t offset, const void *data, size_t size);
void mem_mgr_read(void *ptr, size_t offset, void *data, size_t size);

// Вспомогательные функции
size_t mem_mgr_size_get(void);
char *mem_mgr_list_get(void);
size_t mem_mgr_malloc_max(void);
size_t mem_mgr_hash(const void *data, size_t size);

// Функция инициализации менеджера памяти
void mem_mgr_init(void);

#endif // MEM_MGR_H