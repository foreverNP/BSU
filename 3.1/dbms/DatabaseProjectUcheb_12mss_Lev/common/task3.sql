CREATE DATABASE TestBD1
GO
USE TestBD1;
GO
-- Создаем таблицу Klient, как указано в практическом задании:
CREATE TABLE Klient (
    Id INT, 
    Age INT,
    NName NVARCHAR(20), 
    Fio NVARCHAR(20),
    Email VARCHAR(30),
    Phone VARCHAR(20)
);
GO
-- Первичный ключ на уровне столбца:
CREATE TABLE Klient_PR_K1 (
    Id INT PRIMARY KEY, 
    Age INT, 
    NName NVARCHAR(20), 
    Fio NVARCHAR(20),
    Email VARCHAR(30),
    Phone VARCHAR(20)
);
GO
-- Первичный ключ на уровне таблицы:
CREATE TABLE Klient_PR_K_T (
    Id INT , 
    Age INT, 
    NName NVARCHAR(20), 
    Fio NVARCHAR(20),
    Email VARCHAR(30),
    Phone VARCHAR(20),
    PRIMARY KEY (Id)
);
GO
-- Создаем таблицу Zakaz_L с составным ключом:
CREATE TABLE Zakaz_L (
    Zakaz INT,
    Product INT,
    kol_vo int,
    Cena Money,
    PRIMARY KEY (Zakaz, Product)
);
GO
-- Создаем таблицу с автоинкрементируемым столбцом:
CREATE TABLE Klient_PR_K_ID (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Age INT, 
    NName NVARCHAR(20), 
    Fio NVARCHAR(20),
    Email VARCHAR(30),
    Phone VARCHAR(20)
);
GO
-- Уникальные ключи на уровне столбцов:
CREATE TABLE Klient_PR_K_ID_UN (
    KlientId INT IDENTITY(1,1) PRIMARY KEY,
    Email VARCHAR(30) UNIQUE,
    Phone VARCHAR(20) UNIQUE
);
GO
-- Уникальные ключи на уровне таблицы:
CREATE TABLE Klient_PR_K_ID_UN1 (
    KlientId INT IDENTITY(1,1) PRIMARY KEY,
    Email VARCHAR(30),
    Phone VARCHAR(20),
    CONSTRAINT UQ_Email UNIQUE (Email),
    CONSTRAINT UQ_Phone UNIQUE (Phone)
);
GO
-- Создаем таблицу с значениями по умолчанию:
CREATE TABLE Klient_PR_K_ID_UN_Def (
    KlientId INT IDENTITY(1,1) PRIMARY KEY,
    Age INT DEFAULT 18,
    Email VARCHAR(30) UNIQUE,
    Phone VARCHAR(20) UNIQUE
);
GO
-- Создаем таблицу с ограничениями CHECK:
CREATE TABLE Klient_PR_K_ID_UN_Def_CH (
    KlientId INT IDENTITY(1,1) PRIMARY KEY,
    Age INT CHECK (Age >= 0 AND Age <= 100),
    Email VARCHAR(30) CHECK (Email != ''),
    Phone VARCHAR(20) CHECK (Phone != '')
);
GO
-- Создаем две связанные таблицы через внешний ключ:
-- Таблица Klient_VKK:
CREATE TABLE Klient_VKK (
    KlientId INT PRIMARY KEY,
    FirstName NVARCHAR(20),
    LastName NVARCHAR(20)
);
GO
-- Таблица Zakaz_VKK:
CREATE TABLE Zakaz_VKK (
    ZakazId INT PRIMARY KEY IDENTITY(1,1),
    KlientId INT,
    OrderDate DATE,
    FOREIGN KEY (KlientId) REFERENCES Klient_VKK(KlientId) ON DELETE CASCADE ON UPDATE CASCADE
);
GO
