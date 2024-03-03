var danceDescriptions = {};

var error404 = "404 not found";

function addValue(key, value) {
    danceDescriptions[key] = value;
}

function deleteValue(key) {
    delete danceDescriptions[key];
}

function getValueInfo(key) {
    return danceDescriptions[key] ? danceDescriptions[key] : error404;
}

function listValues() {
    let result = "";
    for (let key in danceDescriptions) {
        result += `${key}: ${danceDescriptions[key]}\n`;
    }
    return result;
}