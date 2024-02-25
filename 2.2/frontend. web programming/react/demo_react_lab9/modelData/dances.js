"use strict";

/*
 * Load the model data for Lab9. We load into the property
 * lab9models.dancesModel a function that returns an array of strings with the
 * names of the dances.
 *
 * 
 */

var lab9models;

if (lab9models === undefined) {
  lab9models = {};
}

lab9models.dancesModel = function () {
  return [
    "Танго",
    "Вальс",
    "Фокстрот",
    "Квикстеп",
    "Самба",
    "Ча-ча-ча",
    "Румба",
    "Пасодобль",
    "Джайв",
    "Сальса",
    "Меренге",
    "Бачата",
    "Реггетон",
    "Хип-хоп",
    "Брейк-данс",
    "Диско",
    "Тверк",
    "Хастл",
    "Ламбада",
    "Форро",
    "Кизомба",
    "Зук",
    "Танго аргентино",
    "Милонга",
    "Вальс аргентино",
    "Милонга",    
  ];
};
