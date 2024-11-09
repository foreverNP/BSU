#include <iostream>
#include <fstream>
#include <cstring>
#include <fcntl.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <semaphore.h>
#include <chrono>

#define SHM_SIZE 4096       // размер разделяемой памяти
#define SHM_NAME "/channel" // имя разделяемой памяти
#define BUF_SIZE 1024       // размер буфера канала

struct Channel
{
    char buffer[BUF_SIZE]; // буфер для данных
    size_t size;           // размер данных в буфере
};

// Функция создания канала
void create_channel(Channel *&channel, int &shm_fd, sem_t *&empty, sem_t *&full)
{
    // Создание разделяемой памяти
    shm_fd = shm_open(SHM_NAME, O_CREAT | O_RDWR, 0666);
    ftruncate(shm_fd, sizeof(Channel));
    channel = (Channel *)mmap(nullptr, sizeof(Channel), PROT_READ | PROT_WRITE, MAP_SHARED, shm_fd, 0);

    // Инициализация начальных значений канала
    channel->size = 0;

    // Создание семафоров
    empty = sem_open("/sem_empty", O_CREAT, 0666, 1); // "пустой" семафор
    full = sem_open("/sem_full", O_CREAT, 0666, 0);   // "полный" семафор
}

// Функция удаления канала
void delete_channel(Channel *channel, int shm_fd, sem_t *empty, sem_t *full)
{
    munmap(channel, sizeof(Channel));
    close(shm_fd);
    shm_unlink(SHM_NAME);
    sem_close(empty);
    sem_close(full);
    sem_unlink("/sem_empty");
    sem_unlink("/sem_full");
}

// Функция записи данных в канал
void send(Channel *channel, sem_t *empty, sem_t *full, const char *data, size_t size)
{
    sem_wait(empty); // ждем, пока буфер не освободится
    memcpy(channel->buffer, data, size);
    channel->size = size;
    sem_post(full); // сигнализируем о доступных данных
}

// Функция чтения данных из канала
void receive(Channel *channel, sem_t *empty, sem_t *full, char *buffer, size_t &size)
{
    sem_wait(full); // ждем, пока данные не будут доступны
    size = channel->size;
    memcpy(buffer, channel->buffer, size);
    channel->size = 0;
    sem_post(empty); // освобождаем буфер
}

// Процесс чтения
void reader_process(const char *source_file, Channel *channel, sem_t *empty, sem_t *full)
{
    std::ifstream src(source_file, std::ios::binary);
    if (!src)
    {
        perror("Failed to open source file");
        exit(1);
    }

    char data[BUF_SIZE];
    while (src)
    {
        src.read(data, BUF_SIZE);
        std::streamsize bytes = src.gcount();
        if (bytes > 0)
        {
            send(channel, empty, full, data, bytes);
        }
    }
    src.close();
    send(channel, empty, full, "", 0); // отправка сигнала завершения
    exit(0);
}

// Процесс записи
void writer_process(const char *target_file, Channel *channel, sem_t *empty, sem_t *full)
{
    std::ofstream dest(target_file, std::ios::binary);
    if (!dest)
    {
        perror("Failed to open target file");
        exit(1);
    }

    char data[BUF_SIZE];
    size_t bytes;
    while (true)
    {
        receive(channel, empty, full, data, bytes);
        if (bytes == 0)
            break; // завершение передачи
        dest.write(data, bytes);
    }
    dest.close();
    exit(0);
}

int main(int argc, char *argv[])
{
    if (argc != 3)
    {
        std::cerr << "Usage: " << argv[0] << " <source_file> <target_file>\n";
        return 1;
    }

    const char *source_file = argv[1];
    const char *target_file = argv[2];

    Channel *channel;
    int shm_fd;
    sem_t *empty, *full;
    create_channel(channel, shm_fd, empty, full);

    pid_t reader_pid = fork();
    if (reader_pid == 0)
    {
        reader_process(source_file, channel, empty, full);
    }

    pid_t writer_pid = fork();
    if (writer_pid == 0)
    {
        writer_process(target_file, channel, empty, full);
    }

    // Ожидание завершения дочерних процессов
    int reader_status, writer_status;
    waitpid(reader_pid, &reader_status, 0);
    waitpid(writer_pid, &writer_status, 0);

    if (WIFEXITED(reader_status) && WEXITSTATUS(reader_status) != 0)
    {
        std::cerr << "Reader process failed\n";
    }
    if (WIFEXITED(writer_status) && WEXITSTATUS(writer_status) != 0)
    {
        std::cerr << "Writer process failed\n";
    }

    delete_channel(channel, shm_fd, empty, full);

    return 0;
}