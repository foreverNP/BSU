from filesystem import FileSystem


def run_tests():
    print("\nЗапуск тестов...")

    # Инициализация файловой системы
    fs = FileSystem(block_size=4, total_blocks=10)

    # Тест 1: Чтение несуществующего файла
    try:
        fs.read_file("nonexistent")
    except Exception as e:
        print("Тест 1 Passed: Чтение несуществующего файла вызывает исключение:", e)
    else:
        print("Тест 1 Failed: Исключение не было вызвано")

    # Тест 2: Создание файла с уже существующим именем
    fs.create_file("file1", "Data")
    try:
        fs.create_file("file1", "Duplicate")
    except Exception as e:
        print(
            "Тест 2 Passed: Создание файла с существующим именем вызывает исключение:",
            e,
        )
    else:
        print("Тест 2 Failed: Исключение не было вызвано")

    # Тест 3: Удаление несуществующего файла
    try:
        fs.delete_file("nonexistent")
    except Exception as e:
        print("Тест 3 Passed: Удаление несуществующего файла вызывает исключение:", e)
    else:
        print("Тест 3 Failed: Исключение не было вызвано")

    # Тест 4: Создание файла, требующего больше блоков, чем доступно
    try:
        fs.create_file("bigfile", "A" * 50)  # Требует больше блоков, чем доступно
    except Exception as e:
        print(
            "Тест 4 Passed: Создание большого файла вызывает исключение при недостатке места:",
            e,
        )
    else:
        print("Тест 4 Failed: Исключение не было вызвано")

    print("Тесты завершены.")


if __name__ == "__main__":
    run_tests()
