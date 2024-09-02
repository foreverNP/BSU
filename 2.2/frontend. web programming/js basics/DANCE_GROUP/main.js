function addDance() {
    let key = prompt("Введите название танца:");
    let value = prompt("Введите описание танца:");

    if (key == "" || value == "" || key == null || value == null) {
        error("Поля не должны быть пустыми!");
        return;
    }

    if (getValueInfo(key) != error404) {
        error("Танец уже существует в базе!");
        return;
    }

    addValue(key, value);
    print("Танец добавлен успешно!", 'green');
}

function deleteDance() {
    let key = prompt("Введите название танца, информацию о котором хотите удалить:");

    if (key == "" || key == null) {
        error("Поля не должны быть пустыми!");
        return;
    }

    if (getValueInfo(key) == error404) {
        error("Такого танца нет в базе!");
        return;
    }

    deleteValue(key);
    print("Удалено!", 'green');
}

function getDanceInfo() {
    let key = prompt("Введите название танца, информацию о котором хотите получить:");

    if (key == "" || key == null) {
        error("Поля не должны быть пустыми!");
        return;
    }

    print(getValueInfo(key), 'black');
}

function listAllDances() {
    print(listValues(), 'black');
}

function print(str, color) {
    document.querySelector('.console-style').style.color = color;
    document.querySelector('.console-style').textContent = str;
    console.log(str);
}

function error(str) {
    print(str, 'red');
}