from filesystem import FileSystem
import time


def run_benchmarks():
    """
    Выполняет простые бенчмарки для оценки производительности файловой системы.
    """
    print("\nЗапуск бенчмарков...")

    # Инициализация файловой системы с увеличенным числом блоков
    fs = FileSystem(block_size=4, total_blocks=5000)

    # Бенчмарк: создание файлов
    start_time = time.time()
    for i in range(500):  # Создаем 500 файлов
        filename = f"file_{i}"
        content = "Data" * 10  # 40 символов, 10 блоков
        fs.create_file(filename, content)
    creation_time = time.time() - start_time
    print(f"Создание 500 файлов заняло {creation_time:.4f} секунд.")

    # Бенчмарк: чтение файлов
    start_time = time.time()
    for i in range(500):
        filename = f"file_{i}"
        fs.read_file(filename)
    reading_time = time.time() - start_time
    print(f"Чтение 500 файлов заняло {reading_time:.4f} секунд.")

    # Бенчмарк: удаление файлов
    start_time = time.time()
    for i in range(500):
        filename = f"file_{i}"
        fs.delete_file(filename)
    deletion_time = time.time() - start_time
    print(f"Удаление 500 файлов заняло {deletion_time:.4f} секунд.")

    print("Бенчмарки завершены.")


if __name__ == "__main__":
    run_benchmarks()
