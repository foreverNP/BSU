const SERVER_URL = "http://localhost:8080";

function mallocMemory() {
    const size = document.getElementById("malloc-size").value;
    if (!size) {
        alert("Введите размер");
        return;
    }
    fetch(`${SERVER_URL}/malloc?size=${size}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("malloc-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function callocMemory() {
    const num = document.getElementById("calloc-num").value;
    const size = document.getElementById("calloc-size").value;
    if (!num || !size) {
        alert("Введите количество и размер");
        return;
    }
    fetch(`${SERVER_URL}/calloc?num=${num}&size=${size}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("calloc-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function reallocMemory() {
    const ptr = document.getElementById("realloc-ptr").value;
    const size = document.getElementById("realloc-size").value;
    if (!ptr || !size) {
        alert("Введите адрес указателя и новый размер");
        return;
    }
    fetch(`${SERVER_URL}/realloc?ptr=${ptr}&size=${size}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("realloc-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function freeMemory() {
    const ptr = document.getElementById("free-ptr").value;
    if (!ptr) {
        alert("Введите адрес указателя");
        return;
    }
    fetch(`${SERVER_URL}/free?ptr=${ptr}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("free-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function getTotalSize() {
    fetch(`${SERVER_URL}/size`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("size-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function getList() {
    fetch(`${SERVER_URL}/list`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("list-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}

function getHash() {
    const data = document.getElementById("hash-data").value;
    const size = document.getElementById("hash-size").value;
    if (!data || !size) {
        alert("Введите данные и размер");
        return;
    }
    fetch(`${SERVER_URL}/hash?data=${encodeURIComponent(data)}&size=${size}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById("hash-result").innerText = data;
        })
        .catch(error => {
            console.error("Error:", error);
        });
}
