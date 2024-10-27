
-- 1. Найти все товары, производителем которых является компания Samsung:
SELECT * FROM Products
WHERE Manufacturer = 'Samsung';

-- 2. Найти все товары, у которых цена больше 45,000:
SELECT * FROM Products
WHERE Price > 45000;

-- 3. Найти все товары, у которых совокупная стоимость больше 200,000 (Price * ProductCount > 200000):
SELECT * FROM Products
WHERE Price * ProductCount > 200000;

-- 4. Найти все товары, у которых производитель Samsung и цена больше 50,000:
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' AND Price > 50000;

-- 5. Найти все товары, у которых производитель Samsung или цена больше 50,000:
SELECT * FROM Products
WHERE Manufacturer = 'Samsung' OR Price > 50000;

-- 6. Найти все товары, у которых производитель не Samsung:
SELECT * FROM Products
WHERE Manufacturer != 'Samsung';

-- 7. Найти товары, у которых на складе больше 2 единиц и цена больше 30,000, либо производитель Samsung:
SELECT * FROM Products
WHERE (ProductCount > 2 AND Price > 30000) OR Manufacturer = 'Samsung';

-- 8. Найти товары, у которых производитель Samsung, Xiaomi или Huawei:
SELECT * FROM Products
WHERE Manufacturer IN ('Samsung', 'Xiaomi', 'Huawei');

-- 9. Найти товары, у которых цена от 20,000 до 40,000:
SELECT * FROM Products
WHERE Price BETWEEN 20000 AND 40000;

-- 10. Найти товары, запасы которых на определенную сумму (Price * ProductCount) находятся в диапазоне 100,000 - 200,000:
SELECT * FROM Products
WHERE Price * ProductCount BETWEEN 100000 AND 200000;