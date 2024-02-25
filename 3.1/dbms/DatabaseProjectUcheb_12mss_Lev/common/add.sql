-- =========================================
-- БАЗА ДАННЫХ И ТАБЛИЦЫ
-- =========================================

-- Создание базы данных
CREATE DATABASE hotel_management;
GO

-- Переход к базе данных
USE hotel_management;
GO

-- Создание таблиц с основными полями и типами данных
CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY,
    LastName NVARCHAR(50),
    FirstName NVARCHAR(50),
    Patronymic NVARCHAR(50),
    PassportData NVARCHAR(20),
    Comment NVARCHAR(100)
);

CREATE TABLE Rooms (
    RoomID INT PRIMARY KEY,
    RoomNumber INT,
    Capacity INT,
    ComfortLevel NVARCHAR(20),
    Price DECIMAL(10, 2)
);

CREATE TABLE CheckIn (
    CheckInID INT PRIMARY KEY,
    CustomerID INT FOREIGN KEY REFERENCES Customers(CustomerID),
    RoomID INT FOREIGN KEY REFERENCES Rooms(RoomID),
    CheckInDate DATE,
    CheckOutDate DATE,
    Note NVARCHAR(100)
);

-- =========================================
-- ЗАПОЛНЕНИЕ ТАБЛИЦ
-- =========================================
-- Вставка данных
INSERT INTO Customers (CustomerID, LastName, FirstName, Patronymic, PassportData, Comment)
VALUES (1, 'Ivanov', 'Ivan', 'Ivanovich', '1234 567890', 'Regular guest');

INSERT INTO Rooms (RoomID, RoomNumber, Capacity, ComfortLevel, Price)
VALUES (1, 101, 2, 'Luxury', 5000.00);

INSERT INTO CheckIn (CheckInID, CustomerID, RoomID, CheckInDate, CheckOutDate, Note)
VALUES (1, 1, 1, '2024-01-01', '2024-01-10', 'Winter vacation');

-- =========================================
-- ОСНОВНЫЕ ЗАПРОСЫ
-- =========================================

-- 1. Выборка всех данных из таблиц
SELECT * FROM Customers;
SELECT * FROM Rooms;
SELECT * FROM CheckIn;

-- 2. INNER JOIN для получения данных из нескольких таблиц
-- Пример: Список клиентов с информацией об их номерах
SELECT c.LastName, c.FirstName, r.RoomNumber, r.ComfortLevel, ch.CheckInDate, ch.CheckOutDate
FROM Customers c
INNER JOIN CheckIn ch ON c.CustomerID = ch.CustomerID
INNER JOIN Rooms r ON ch.RoomID = r.RoomID;

-- 3. LEFT JOIN для включения всех данных из одной таблицы и только совпадающих строк из другой
-- Пример: Список всех клиентов с данными о номерах (даже если клиент сейчас не проживает)
SELECT c.LastName, c.FirstName, r.RoomNumber, ch.CheckInDate, ch.CheckOutDate
FROM Customers c
LEFT JOIN CheckIn ch ON c.CustomerID = ch.CustomerID
LEFT JOIN Rooms r ON ch.RoomID = r.RoomID;

-- =========================================
-- УСЛОВИЯ ФИЛЬТРАЦИИ
-- =========================================

-- 4. Фильтрация данных по условию
-- Пример: Номера с ценой выше 3000
SELECT RoomNumber, ComfortLevel, Price
FROM Rooms
WHERE Price > 3000;

-- 5. BETWEEN для диапазона значений
-- Пример: Список номеров с ценой от 2000 до 5000
SELECT RoomNumber, Price
FROM Rooms
WHERE Price BETWEEN 2000 AND 5000;

-- 6. Сортировка данных
-- Пример: Сортировка клиентов по фамилии в алфавитном порядке
SELECT LastName, FirstName, Patronymic
FROM Customers
ORDER BY LastName ASC;

-- =========================================
-- ГРУППИРОВКА И АГРЕГАЦИЯ
-- =========================================

-- 7. GROUP BY с агрегатными функциями (COUNT, AVG, MAX, MIN, SUM)
-- Пример: Подсчет количества клиентов в каждом уровне комфортности
SELECT ComfortLevel, COUNT(*) AS RoomCount
FROM Rooms
GROUP BY ComfortLevel;

-- 8. MAX - самый дорогой номер
SELECT MAX(Price) AS MostExpensiveRoom
FROM Rooms;

-- 9. GROUP BY с HAVING - условие для групп
-- Пример: Найти уровни комфортности с количеством номеров более 1
SELECT ComfortLevel, COUNT(*) AS RoomCount
FROM Rooms
GROUP BY ComfortLevel
HAVING COUNT(*) > 1;

-- =========================================
-- ПОДЗАПРОСЫ
-- =========================================

-- 10. Подзапрос для нахождения самого дорогого номера в таблице
SELECT RoomNumber, Price
FROM Rooms
WHERE Price = (SELECT MAX(Price) FROM Rooms);

-- =========================================
-- PIVOT и UNPIVOT (СВОДНЫЕ ТАБЛИЦЫ)
-- =========================================

-- 11. PIVOT - Поворот данных, например, для подсчета количества проживаний клиентов по месяцу
-- Здесь представим CheckIn как временную таблицу для демонстрации
SELECT * 
FROM (
    SELECT CustomerID, MONTH(CheckInDate) AS Month
    FROM CheckIn
) AS SourceTable
PIVOT (
    COUNT(CustomerID) 
    FOR Month IN ([1], [2], [3], [4], [5], [6], [7], [8], [9], [10], [11], [12])
) AS PivotTable;

-- 12. UNPIVOT - Пример, преобразование колонок в строки
-- Пример: Предположим, что у нас есть таблица с годами, которые мы хотим "распаковать" в строки
SELECT CustomerID, Month, StayCount
FROM PivotTable
UNPIVOT (StayCount FOR Month IN ([1], [2], [3], [4], [5], [6], [7], [8], [9], [10], [11], [12])) AS UnpivotTable;

-- =========================================
-- ТЕКСТОВЫЕ ФУНКЦИИ
-- =========================================

-- 13. Преобразование строки в верхний регистр
SELECT UPPER(LastName) AS UpperLastName
FROM Customers;

-- =========================================
-- АЛТЕРНАТИВНЫЕ ОПЕРАЦИИ
-- =========================================

-- 14. CASE для условной логики
-- Пример: Присваиваем категории по цене
SELECT RoomNumber, Price,
    CASE 
        WHEN Price < 2000 THEN 'Economy'
        WHEN Price BETWEEN 2000 AND 4000 THEN 'Standard'
        ELSE 'Luxury'
    END AS PriceCategory
FROM Rooms;

-- =========================================
-- ОБНОВЛЕНИЕ И УДАЛЕНИЕ ДАННЫХ
-- =========================================

-- 15. UPDATE - Обновление данных
-- Пример: Обновить комментарий для клиента
UPDATE Customers
SET Comment = 'VIP client'
WHERE LastName = 'Ivanov';

-- 16. DELETE - Удаление записей
-- Пример: Удалить запись о проживании
DELETE FROM CheckIn
WHERE CheckInID = 1;