-- Задание 1: Создание базы данных и таблицы
CREATE DATABASE productsdb;
GO
USE productsdb;
GO
-- Создаем таблицу Product
CREATE TABLE Product (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(30) NOT NULL,
    Proizvod NVARCHAR(20) NOT NULL,
    ProductCount INT DEFAULT 0,
    Price MONEY NOT NULL
);
-- Добавляем первую запись
INSERT INTO Product VALUES ('iPhone 7', 'Apple', 5, 5100);
GO

-- Задание 2: Указание конкретных столбцов при вставке
INSERT INTO Product (Name, Price, Proizvod) VALUES ('iPhone 6', 6100, 'Apple')
GO
-- Задание 3: Добавление нескольких строк
INSERT INTO Product (Name, Proizvod, ProductCount, Price)
VALUES 
    ('iPhone 6', 'Apple', 3, 36000), 
    ('Galaxy S8', 'Samsung', 2, 46000), 
    ('Galaxy S8 Plus', 'Samsung', 1, 56000);
GO
-- Задание 4: Использование значения по умолчанию
INSERT INTO Product (Name, Proizvod, ProductCount, Price)
VALUES ('Mi6', 'Xiaomi', DEFAULT, 28000);
GO
-- Просмотр данных в таблице Product после всех вставок
SELECT * FROM Product;
GO
