"use strict";

/*
 * Load the model data for Lab9. We load into DOM the
 * property lab9models.exampleModel a function that returns an object with the
 * following property:
 *    name:  A string with name.
 *
 * See README.md for information on how to access it.
 */
var lab9models;

if (lab9models === undefined) {
  lab9models = {};
}

lab9models.exampleModel = function () {
  return {
    name: "Lev Sergienko",
  };
};
