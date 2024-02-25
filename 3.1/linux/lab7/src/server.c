#include "mmemory.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

// Порт сервера
#define PORT 8080
#define BUFFER_SIZE 4096

// Функции обработки различных запросов
void handle_malloc(int client_socket, char *params);
void handle_calloc(int client_socket, char *params);
void handle_realloc(int client_socket, char *params);
void handle_free(int client_socket, char *params);
void handle_size_get(int client_socket);
void handle_list_print(int client_socket);
void handle_hash(int client_socket, char *params);

// Функция для отправки HTTP-ответа
void send_response(int client_socket, const char *status, const char *content_type, const char *body)
{
    char response[BUFFER_SIZE];
    int length = snprintf(response, sizeof(response),
                          "HTTP/1.1 %s\r\n"
                          "Content-Type: %s\r\n"
                          "Content-Length: %zu\r\n"
                          "Access-Control-Allow-Origin: *\r\n"
                          "Connection: close\r\n"
                          "\r\n"
                          "%s",
                          status, content_type, strlen(body), body);
    send(client_socket, response, length, 0);
}

int main()
{
    int server_fd, new_socket;
    struct sockaddr_in address;
    int addrlen = sizeof(address);
    char buffer[BUFFER_SIZE] = {0};

    // Инициализация менеджера памяти
    mem_mgr_init();

    // 1. Создание сокета
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0)
    {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    // 2. Настройка адреса
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // 3. Привязка сокета к адресу
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0)
    {
        perror("Bind failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    // 4. Прослушивание входящих соединений
    if (listen(server_fd, 10) < 0)
    {
        perror("Listen failed");
        close(server_fd);
        exit(EXIT_FAILURE);
    }

    printf("HTTP server is listening on port %d...\n", PORT);

    // 5. Основной цикл обработки соединений
    while (1)
    {
        printf("Waiting for a connection...\n");

        // Принятие соединения
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t *)&addrlen)) < 0)
        {
            perror("Accept failed");
            continue;
        }

        // Чтение HTTP-запроса
        int read_size = read(new_socket, buffer, BUFFER_SIZE - 1);
        if (read_size < 0)
        {
            perror("Read failed");
            close(new_socket);
            continue;
        }
        buffer[read_size] = '\0';
        printf("Request received:\n%s\n", buffer);

        // Простая обработка запроса
        char method[8];
        char path[256];
        sscanf(buffer, "%s %s", method, path);

        // Разбор пути и параметров
        char *query = strchr(path, '?');
        if (query != NULL)
        {
            *query = '\0';
            query++;
        }

        if (strcmp(method, "GET") == 0)
        {
            if (strcmp(path, "/malloc") == 0)
            {
                handle_malloc(new_socket, query);
            }
            else if (strcmp(path, "/calloc") == 0)
            {
                handle_calloc(new_socket, query);
            }
            else if (strcmp(path, "/realloc") == 0)
            {
                handle_realloc(new_socket, query);
            }
            else if (strcmp(path, "/free") == 0)
            {
                handle_free(new_socket, query);
            }
            else if (strcmp(path, "/size") == 0)
            {
                handle_size_get(new_socket);
            }
            else if (strcmp(path, "/list") == 0)
            {
                handle_list_print(new_socket);
            }
            else if (strcmp(path, "/hash") == 0)
            {
                handle_hash(new_socket, query);
            }
            else
            {
                const char *body = "Endpoint not found";
                send_response(new_socket, "404 Not Found", "text/plain", body);
            }
        }
        else
        {
            const char *body = "Method not supported";
            send_response(new_socket, "405 Method Not Allowed", "text/plain", body);
        }

        // Очистка буфера
        memset(buffer, 0, sizeof(buffer));
        // Закрытие соединения
        close(new_socket);
    }

    close(server_fd);
    return 0;
}

// Обработчик /malloc?size=NUMBER
void handle_malloc(int client_socket, char *params)
{
    size_t size = 0;
    if (params != NULL)
    {
        sscanf(params, "size=%zu", &size);
    }

    if (size == 0)
    {
        const char *body = "Invalid size parameter";
        send_response(client_socket, "400 Bad Request", "text/plain", body);
        return;
    }

    void *ptr = mem_mgr_malloc(size);
    if (ptr != NULL)
    {
        char body[64];
        snprintf(body, sizeof(body), "Allocated at address: %p", ptr);
        send_response(client_socket, "200 OK", "text/plain", body);
    }
    else
    {
        const char *body = "Memory allocation failed";
        send_response(client_socket, "500 Internal Server Error", "text/plain", body);
    }
}

// Обработчик /calloc?num=NUMBER&size=NUMBER
void handle_calloc(int client_socket, char *params)
{
    size_t num = 0, size = 0;
    if (params != NULL)
    {
        sscanf(params, "num=%zu&size=%zu", &num, &size);
    }

    if (num == 0 || size == 0)
    {
        const char *body = "Invalid parameters";
        send_response(client_socket, "400 Bad Request", "text/plain", body);
        return;
    }

    void *ptr = mem_mgr_calloc(num, size);
    if (ptr != NULL)
    {
        char body[64];
        snprintf(body, sizeof(body), "Allocated at address: %p", ptr);
        send_response(client_socket, "200 OK", "text/plain", body);
    }
    else
    {
        const char *body = "Memory allocation failed";
        send_response(client_socket, "500 Internal Server Error", "text/plain", body);
    }
}

// Обработчик /realloc?ptr=ADDRESS&size=NUMBER
void handle_realloc(int client_socket, char *params)
{
    void *ptr = NULL;
    size_t size = 0;

    if (params != NULL)
    {
        char ptr_str[32] = {0};
        // Изменяем формат строки, чтобы считывать ptr до символа &
        int scanned = sscanf(params, "ptr=%31[^&]&size=%zu", ptr_str, &size);

        if (scanned == 2)
        {
            ptr = (void *)strtoull(ptr_str, NULL, 16);
        }
        else
        {
            // Если парсинг не удался, устанавливаем size в 0 для обработки ошибки
            size = 0;
        }
    }

    if (ptr == NULL || size == 0)
    {
        const char *body = "Invalid parameters";
        // Логирование
        printf("Invalid parameters. ptr: %p, size: %zu\n", ptr, size);
        send_response(client_socket, "400 Bad Request", "text/plain", body);
        return;
    }

    void *new_ptr = mem_mgr_realloc(ptr, size);
    if (new_ptr != NULL)
    {
        char body[64];
        snprintf(body, sizeof(body), "Reallocated to address: %p", new_ptr);
        send_response(client_socket, "200 OK", "text/plain", body);
    }
    else
    {
        const char *body = "Memory reallocation failed";
        send_response(client_socket, "500 Internal Server Error", "text/plain", body);
    }
}

// Обработчик /free?ptr=ADDRESS
void handle_free(int client_socket, char *params)
{
    void *ptr = NULL;
    if (params != NULL)
    {
        char ptr_str[32];
        sscanf(params, "ptr=%s", ptr_str);
        ptr = (void *)strtoull(ptr_str, NULL, 16);
    }

    if (ptr == NULL)
    {
        const char *body = "Invalid pointer parameter";
        send_response(client_socket, "400 Bad Request", "text/plain", body);
        return;
    }

    mem_mgr_free(ptr);
    const char *body = "Memory freed successfully";
    send_response(client_socket, "200 OK", "text/plain", body);
}

// Обработчик /size
void handle_size_get(int client_socket)
{
    size_t size = mem_mgr_size_get();
    char body[64];
    snprintf(body, sizeof(body), "Total allocated memory size: %zu bytes", size);
    send_response(client_socket, "200 OK", "text/plain", body);
}

// Обработчик /list
void handle_list_print(int client_socket)
{
    char *list = mem_mgr_list_get();
    send_response(client_socket, "200 OK", "text/plain", list);
}

// Обработчик /hash?data=STRING&size=NUMBER
void handle_hash(int client_socket, char *params)
{
    const char *data = NULL;
    size_t size = 0;
    if (params != NULL)
    {
        char data_str[256] = {0};
        size = 0;
        if (sscanf(params, "data=%255[^&]&size=%zu", data_str, &size) == 2)
        {
            data = data_str;
        }
        else
        {
            printf("Failed to parse parameters: %s\n", params);
        }
    }

    if (data == NULL || size == 0)
    {
        const char *body = "Invalid parameters";
        send_response(client_socket, "400 Bad Request", "text/plain", body);
        return;
    }

    size_t hash = mem_mgr_hash(data, size);
    char body[64];
    snprintf(body, sizeof(body), "Hash value: %zu", hash);
    send_response(client_socket, "200 OK", "text/plain", body);
}
