CREATE DATABASE TestBDB1
GO
USE TestBDB1;
GO
-- Задание 2.1: Ограничение DEFAULT
-- Добавляем ограничение DEFAULT для столбца, чтобы указать значение по умолчанию, если оно не задано.
CREATE TABLE TestTable1 (
    iID INT DEFAULT 1,
    CONSTRAINT check_iID CHECK (iID is NOT NULL)
);
INSERT INTO TestTable1 VALUES (10);
GO
-- Задание 2.2: Проверка значения NULL
-- Проверяем, что сервер вернет ошибку при попытке вставить NULL в столбец с ограничением CHECK.
UPDATE TestTable1 SET iID=NULL;
GO
-- Задание 2.3: Ограничение CHECK для диапазона дат
-- Добавляем ограничение CHECK, чтобы день рождения соответствовал определенному диапазону.
CREATE TABLE TestTable2 (
    iID INT DEFAULT 1 NOT NULL,
    vcName VARCHAR(50) NOT NULL,
    dBirthDate DATETIME,
    CONSTRAINT CK_birthdate CHECK (dBirthDate > '01-01-1900' AND dBirthDate < GETDATE())
);
GO
-- Задание 2.4: Проверка даты выдачи паспорта
-- Дата выдачи паспорта должна быть больше даты рождения и меньше текущей даты.
CREATE TABLE TestTable4 (
    iID INT DEFAULT 1 NOT NULL,
    vcName VARCHAR(50) NOT NULL,
    dBirthDate DATETIME,
    dDocDate DATETIME,
    CHECK (dDocDate > dBirthDate AND dBirthDate < GETDATE())
);
GO