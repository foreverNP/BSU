from flask import Flask, session, request, jsonify, send_from_directory
from flask_session import Session
from filesystem import FileSystem
import os

app = Flask(__name__, static_folder="static")

# Конфигурация сессий Flask
app.config["SECRET_KEY"] = "supersecret"
app.config["SESSION_TYPE"] = "filesystem"  # Хранение сессий на сервере
app.config["SESSION_FILE_DIR"] = "./flask_session/"  # Директория для хранения сессий
app.config["SESSION_COOKIE_NAME"] = "session"
app.session_cookie_name = app.config["SESSION_COOKIE_NAME"]

# Инициализация расширения Flask-Session
Session(app)


def get_filesystem():
    """
    Получает объект FileSystem из сессии или создает новый, если его нет.
    """
    if "filesystem" not in session:
        # Инициализация новой файловой системы с дефолтными параметрами
        fs = FileSystem(block_size=4, total_blocks=100)
        session["filesystem"] = {
            "block_size": fs.block_size,
            "total_blocks": fs.total_blocks,
            "storage": fs.storage,
            "files": fs.files,
            "free_blocks": fs.free_blocks,
        }
    else:
        fs_data = session["filesystem"]
        fs = FileSystem(
            block_size=fs_data["block_size"], total_blocks=fs_data["total_blocks"]
        )
        fs.storage = fs_data["storage"]
        fs.files = fs_data["files"]
        fs.free_blocks = fs_data["free_blocks"]
    return fs


def save_filesystem(fs):
    """
    Сохраняет состояние FileSystem обратно в сессию.
    """
    session["filesystem"] = {
        "block_size": fs.block_size,
        "total_blocks": fs.total_blocks,
        "storage": fs.storage,
        "files": fs.files,
        "free_blocks": fs.free_blocks,
    }


@app.route("/")
def index():
    """
    Обслуживает клиентский HTML-файл.
    """
    return send_from_directory(app.static_folder, "client.html")


@app.route("/create_file", methods=["POST"])
def create_file():
    """
    Endpoint для создания файла.

    Ожидает JSON с полями:
    - name: имя файла
    - content: содержимое файла
    """
    data = request.get_json()
    name = data.get("name")
    content = data.get("content", "")

    if not name:
        return jsonify({"status": "error", "message": "Имя файла обязательно."}), 400

    fs = get_filesystem()
    try:
        fs.create_file(name, content)
        save_filesystem(fs)
        return jsonify(
            {"status": "success", "message": f"Файл '{name}' успешно создан."}
        ), 201
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 400


@app.route("/delete_file", methods=["POST"])
def delete_file():
    """
    Endpoint для удаления файла.

    Ожидает JSON с полем:
    - name: имя файла
    """
    data = request.get_json()
    name = data.get("name")

    if not name:
        return jsonify({"status": "error", "message": "Имя файла обязательно."}), 400

    fs = get_filesystem()
    try:
        fs.delete_file(name)
        save_filesystem(fs)
        return jsonify(
            {"status": "success", "message": f"Файл '{name}' успешно удален."}
        ), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 400


@app.route("/read_file", methods=["GET"])
def read_file():
    """
    Endpoint для чтения файла.

    Ожидает параметр запроса:
    - name: имя файла
    """
    name = request.args.get("name")
    if not name:
        return jsonify(
            {"status": "error", "message": "Параметр 'name' обязателен."}
        ), 400

    fs = get_filesystem()
    try:
        content = fs.read_file(name)
        return jsonify({"status": "success", "content": content}), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 400


@app.route("/dump", methods=["GET"])
def dump():
    """
    Endpoint для получения состояния файловой системы.
    """
    fs = get_filesystem()
    state = fs.dump()
    return jsonify({"status": "success", "state": state}), 200


@app.route("/list_files", methods=["GET"])
def list_files():
    """
    Endpoint для получения списка файлов.
    """
    fs = get_filesystem()
    files = list(fs.files.keys())
    return jsonify({"status": "success", "files": files}), 200


@app.route("/set_parameters", methods=["POST"])
def set_parameters():
    """
    Endpoint для изменения параметров файловой системы.

    Ожидает JSON с полями:
    - block_size: новый размер блока (целое число)
    - total_blocks: новое общее количество блоков (целое число)
    """
    data = request.get_json()
    new_block_size = data.get("block_size")
    new_total_blocks = data.get("total_blocks")

    # Проверка наличия необходимых параметров
    if new_block_size is None or new_total_blocks is None:
        return jsonify(
            {
                "status": "error",
                "message": "Необходимо указать 'block_size' и 'total_blocks'.",
            }
        ), 400

    # Проверка типов данных
    if not isinstance(new_block_size, int) or not isinstance(new_total_blocks, int):
        return jsonify(
            {
                "status": "error",
                "message": "'block_size' и 'total_blocks' должны быть целыми числами.",
            }
        ), 400

    if new_block_size < 1 or new_total_blocks < 1:
        return jsonify(
            {
                "status": "error",
                "message": "'block_size' и 'total_blocks' должны быть положительными числами.",
            }
        ), 400

    try:
        new_fs = FileSystem(block_size=new_block_size, total_blocks=new_total_blocks)
        save_filesystem(new_fs)
        return jsonify(
            {
                "status": "success",
                "message": "Параметры файловой системы успешно изменены",
            }
        ), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 400


@app.route("/get_parameters", methods=["GET"])
def get_parameters():
    """
    Endpoint для получения текущих параметров файловой системы.
    """
    fs = get_filesystem()
    params = {
        "block_size": fs.block_size,
        "total_blocks": fs.total_blocks,
    }
    return jsonify({"status": "success", "parameters": params}), 200


if __name__ == "__main__":
    # Убедитесь, что директория для сессий существует
    os.makedirs("./flask_session/", exist_ok=True)
    app.run(host="0.0.0.0", port=5000, debug=True)
