param N >= 1 integer; # Количество вышек
param M >= 1 integer; # Количество заводов

param a{1..N} >= 0; # Производительность вышек
param b{1..M} >= 0; # Производительность заводов
param c{1..N, 1..M} >= 0; # Стоимость транспортировки

var flow_source{1..N} >= 0; # Поток от источника к вышкам
var flow_sink{1..M} >= 0; # Поток от заводов к стоку
var flow{1..N, 1..M} >= 0; # Поток от вышек к заводам

# Целевая функция: минимизация стоимости транспортировки
minimize total_cost:
sum{i in 1..N, j in 1..M} c[i,j] * flow[i,j];

# Ограничение на производительность вышек
subject to well_capacity{i in 1..N}:
flow_source[i] <= a[i];

# Ограничение на производительность заводов
subject to factory_capacity{j in 1..M}:
flow_sink[j] <= b[j];

# Баланс потока в вышках
subject to well_balance{i in 1..N}:
flow_source[i] = sum{j in 1..M} flow[i,j];

# Баланс потока в заводах
subject to factory_balance{j in 1..M}:
sum{i in 1..N} flow[i,j] = flow_sink[j];

# Общий поток должен быть максимальным = min(sum(a), sum(b))
subject to max_flow:
sum{i in 1..N} flow_source[i] = min(sum{i in 1..N} a[i], sum{j in 1..M} b[j]);