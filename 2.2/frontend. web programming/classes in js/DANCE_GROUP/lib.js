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