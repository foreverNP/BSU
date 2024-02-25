import os
import shutil

class FileSystem:
    def __init__(self, root_directory):
        self.root_directory = root_directory
        if not os.path.exists(self.root_directory):
            os.makedirs(self.root_directory)

    def _get_path(self, name):
        return os.path.join(self.root_directory, name)

    def create_file(self, name, content=""):
        path = self._get_path(name)
        if os.path.exists(path):
            raise Exception("File already exists")
        with open(path, "w") as f:
            f.write(content)

    def delete_file(self, name):
        path = self._get_path(name)
        if not os.path.exists(path):
            raise Exception("File not found")
        os.remove(path)

    def copy_file(self, source_name, dest_name):
        source_path = self._get_path(source_name)
        dest_path = self._get_path(dest_name)
        if not os.path.exists(source_path):
            raise Exception("Source file not found")
        if os.path.exists(dest_path):
            raise Exception("Destination file already exists")
        shutil.copy2(source_path, dest_path)

    def move_file(self, source_name, dest_name):
        source_path = self._get_path(source_name)
        dest_path = self._get_path(dest_name)
        if not os.path.exists(source_path):
            raise Exception("Source file not found")
        if os.path.exists(dest_path):
            raise Exception("Destination file already exists")
        shutil.move(source_path, dest_path)

    def write_to_file(self, name, content):
        path = self._get_path(name)
        if not os.path.exists(path):
            raise Exception("File not found")
        with open(path, "w") as f:
            f.write(content)

    def read_file(self, name):
        path = self._get_path(name)
        if not os.path.exists(path):
            raise Exception("File not found")
        with open(path, "r") as f:
            return f.read()

    def dump(self):
        dump_data = {}
        for root, dirs, files in os.walk(self.root_directory):
            for file in files:
                file_path = os.path.join(root, file)
                with open(file_path, "r") as f:
                    content = f.read()
                dump_data[file] = {
                    "path": file_path,
                    "content": content
                }
        return dump_data

# Example usage
root_dir = "./virtual_fs"
fs = FileSystem(root_dir)
fs.create_file("file1.txt", "HelloWorld")
fs.write_to_file("file1.txt", "NewContent")
print(fs.read_file("file1.txt"))  # Output: NewContent
fs.create_file("file2.txt", "1234")
fs.copy_file("file2.txt", "file3.txt")
fs.move_file("file3.txt", "file4.txt")
fs.delete_file("file1.txt")
print(fs.dump())