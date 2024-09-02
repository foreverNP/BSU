class TLocalStorage {
    error404 = "404 not found";

    constructor() {
        this.localStorage = window.localStorage;
    }

    reset() {
        this.localStorage.clear();
    }

    addValue(key, value) {
        this.localStorage.setItem(key, value);
    }

    getValue(key) {
        return this.localStorage.getItem(key) ? this.localStorage.getItem(key) : this.error404;
    }

    deleteValue(key) {
        this.localStorage.removeItem(key);
    }

    getKeys() {
        return Object.keys(this.localStorage);
    }

    listValues() {
        let result = "";
        for (let i = 0; i < this.localStorage.length; i++) {
            let key = this.localStorage.key(i);
            let value = this.localStorage.getItem(key);
            result += `${key}: ${value}\n`;
        }
        return result;
    }
}

class THashStorage {
    #storage = {};
    error404 = "404 not found";

    reset() {
        this.#storage = {};
    }

    addValue(key, value) {
        this.#storage[key] = value;
    }

    getValue(key) {
        return this.#storage[key] ? this.#storage[key] : this.error404;
    }

    deleteValue(key) {
        delete this.#storage[key];
    }

    getKeys() {
        return Object.keys(this.#storage);
    }

    listValues() {
        let result = "";
        for (let key in this.#storage) {
            result += `${key}: ${this.#storage[key]}\n`;
        }
        return result;
    }
}