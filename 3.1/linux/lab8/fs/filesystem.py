class FileSystem:
    def __init__(self, block_size=4, total_blocks=100):
        """
        Инициализирует файловую систему.

        :param block_size: Размер одного блока в символах.
        :param total_blocks: Общее количество блоков в файловой системе.
        """
        self.block_size = block_size
        self.total_blocks = total_blocks
        self.storage = [None] * total_blocks  # Storage for blocks
        self.files = {}  # Dictionary for files {name: (first_block, size)}
        self.free_blocks = list(range(total_blocks))  # Free blocks

    def _allocate_blocks(self, content):
        """
        Выделяет необходимое количество блоков для хранения содержимого.

        :param content: Содержимое файла.
        :return: Список номеров выделенных блоков.
        :raises Exception: Если недостаточно свободных блоков.
        """
        required_blocks = -(-len(content) // self.block_size)  # Ceiling division
        if len(self.free_blocks) < required_blocks:
            raise Exception("Not enough free space")
        allocated = []
        for _ in range(required_blocks):
            block = self.free_blocks.pop(0)  # Получаем первый свободный блок
            allocated.append(block)
        return allocated

    def create_file(self, name, content=""):
        """
        Создает новый файл с заданным именем и содержимым.

        :param name: Имя файла.
        :param content: Содержимое файла.
        :raises Exception: Если файл с таким именем уже существует.
        """
        if name in self.files:
            raise Exception("File already exists")
        blocks = self._allocate_blocks(content)
        self.files[name] = (blocks[0], len(content))

        # Link blocks and store content
        for i, block in enumerate(blocks):
            start = i * self.block_size
            end = start + self.block_size
            block_content = content[start:end]

            # If not the last block, link to the next block
            next_block = blocks[i + 1] if i + 1 < len(blocks) else None
            self.storage[block] = (block_content, next_block)

    def delete_file(self, name):
        """
        Удаляет файл по его имени, освобождая занятые блоки.

        :param name: Имя файла для удаления.
        :raises Exception: Если файл не найден.
        """
        if name not in self.files:
            raise Exception("File not found")
        first_block, _ = self.files[name]
        current_block = first_block
        while current_block is not None:
            # Get the next block reference from current block metadata
            _, next_block = self.storage[current_block]
            self.free_blocks.append(current_block)  # Mark block as free
            # Do not clear block content
            current_block = next_block
        del self.files[name]
        # Keep free blocks sorted for consistency
        self.free_blocks.sort()

    def read_file(self, name):
        """
        Читает содержимое файла по его имени.

        :param name: Имя файла для чтения.
        :return: Содержимое файла.
        :raises Exception: Если файл не найден.
        """
        if name not in self.files:
            raise Exception("File not found")
        first_block, size = self.files[name]
        content = []
        current_block = first_block
        while current_block is not None:
            block_content, next_block = self.storage[current_block]
            content.append(block_content)  # Collect only the content part
            current_block = next_block  # Move to the next block
        return "".join(content)[:size]

    def dump(self):
        """
        Возвращает текущее состояние файловой системы для отладки.

        :return: Словарь с информацией о файлах, хранилище и свободные блоки.
        """
        # Represent storage content as readable strings
        readable_storage = []
        for block in self.storage:
            if block is None:
                readable_storage.append(None)
            else:
                content, next_block = block
                readable_storage.append(f"{content} -> {next_block}")
        return {
            "files": self.files,
            "storage": readable_storage,
            "free_blocks": self.free_blocks,
        }
