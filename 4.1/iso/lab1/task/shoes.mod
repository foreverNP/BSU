param m integer >= 1;
param n integer >= 1;

param A{1..m, 1..n}; # матрица потребления ресурсов
param b{1..m}; # запасы ресурсов
param price{1..n}; # цена (выручка) с единицы

# переменные: целые числа пар каждой модели
var x{1..n} integer >= 0;

# целевая: максимум выручки
maximize Revenue: sum{j in 1..n} price[j] * x[j];

# ресурсные ограничения
s.t. Resource{i in 1..m}:
sum{j in 1..n} A[i,j] * x[j] <= b[i];