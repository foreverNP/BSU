from filesystem import FileSystem


def main():
    """
    Основная функция для демонстрации работы файловой системы.
    """
    # Инициализация файловой системы
    fs = FileSystem(block_size=4, total_blocks=10)

    # Создаём первый файл
    fs.create_file("file1", "HelloWorld")  # 10 символов, займёт 3 блока
    print("Содержимое 'file1':", fs.read_file("file1"))

    # Создаём второй файл
    fs.create_file("file2", "Python")  # 6 символов, займёт 2 блока
    print("Содержимое 'file2':", fs.read_file("file2"))

    # Выводим состояние файловой системы после создания двух файлов
    print("\nСостояние после создания двух файлов:")
    dump = fs.dump()
    print("Files:", dump["files"])
    print("Storage:", dump["storage"])
    print("Free blocks:", dump["free_blocks"])

    # Удаляем первый файл
    fs.delete_file("file1")
    print("\nСостояние после удаления 'file1':")
    dump = fs.dump()
    print("Files:", dump["files"])
    print("Storage:", dump["storage"])
    print("Free blocks:", dump["free_blocks"])

    # Создаём третий файл
    fs.create_file("file3", "Data")  # 4 символа, займёт 1 блок
    print("\nСодержимое 'file3':", fs.read_file("file3"))

    # Выводим финальное состояние файловой системы после создания 'file3'
    print("\nСостояние после создания 'file3':")
    dump = fs.dump()
    print("Files:", dump["files"])
    print("Storage:", dump["storage"])
    print("Free blocks:", dump["free_blocks"])


if __name__ == "__main__":
    main()
