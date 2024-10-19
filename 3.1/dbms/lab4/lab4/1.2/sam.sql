-- 1. Выборка с объединением столбцов и применением арифметических операций
SELECT 
    Name + ' ' + ProductNumber AS ProductDescription, 
    ListPrice * SafetyStockLevel AS TotalValue 
FROM Production.Product
WHERE SafetyStockLevel > 10;

-- 2. Использование псевдонимов для столбцов и сортировка
SELECT 
    Name AS ProductName, 
    ListPrice, 
    ListPrice * SafetyStockLevel AS StockValue 
FROM Production.Product
ORDER BY StockValue DESC;

-- 3. Использование сложных выражений в сортировке
SELECT 
    Name, 
    StandardCost, 
    (StandardCost + ListPrice) / 2 AS AvgPrice 
FROM Production.Product
ORDER BY AvgPrice DESC;

-- 4. Создание новой таблицы с помощью SELECT INTO
SELECT 
    ProductSubcategoryID, 
    SUM(ListPrice) AS TotalListPrice 
INTO Production.ProductSummary
FROM Production.Product
GROUP BY ProductSubcategoryID;