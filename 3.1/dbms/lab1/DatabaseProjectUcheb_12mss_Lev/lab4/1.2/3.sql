-- 1. Выборка с выражениями
SELECT 
    ProductName + ' ' + Manufacturer AS ModelName, 
    Price, 
    Price * ProductCount AS TotalSum 
FROM Products;

-- 2. Псевдонимы столбцов
SELECT 
    ProductName + ' ' + Manufacturer AS ModelName, 
    Price, 
    Price * ProductCount AS TotalSum 
FROM Products;

-- 3. Выборка с добавлением
SELECT 
    ProductName + ' ' + Manufacturer AS ModelName, 
    Price 
INTO ProductSummary 
FROM Products;

-- 4. Сортировка
-- 4.1. Сортировка по столбцу ProductName

-- Сортировка по возрастанию:
SELECT * FROM Products
ORDER BY ProductName ASC;
-- Сортировка по убыванию:
SELECT * FROM Products
ORDER BY ProductName DESC;
-- Сортировка по нескольким столбцам:
SELECT * FROM Products
ORDER BY Manufacturer ASC, ProductName ASC;

-- 4.2. Сортировка по псевдониму
SELECT 
    ProductName + ' ' + Manufacturer AS ModelName, 
    Price, 
    Price * ProductCount AS TotalSum 
FROM Products
ORDER BY ModelName ASC;

-- 4.3. Сортировка с использованием сложных выражений
SELECT 
    ProductName, 
    Manufacturer, 
    Price, 
    ProductCount, 
    Price * ProductCount AS TotalSum
FROM Products
ORDER BY Price * ProductCount DESC;
