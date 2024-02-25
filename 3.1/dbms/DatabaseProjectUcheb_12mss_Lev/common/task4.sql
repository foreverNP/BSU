CREATE DATABASE TestBD2
GO
USE TestBD2;
GO
-- Задание 1: Создание таблицы Klient_Alt
-- Проверяем, существует ли таблица, если да - удаляем, и создаем заново.
IF OBJECT_ID('dbo.Klient_Alt', 'U') IS NOT NULL
    DROP TABLE dbo.Klient_Alt;
CREATE TABLE Klient_Alt (
    Id INT PRIMARY KEY, 
    Age INT, 
    NName NVARCHAR(20), 
    Fio NVARCHAR(20),
    Email VARCHAR(30),
    Phone VARCHAR(20)
);
GO
-- Задание 2: Добавление нового столбца Address
-- Добавляем новый столбец Address, который не может содержать NULL и имеет значение по умолчанию.
ALTER TABLE Klient_Alt
ADD Address NVARCHAR(50) DEFAULT 'нет' NOT NULL;
GO
-- Задание 3: Удаление столбца Address
-- Удаляем столбец Address
ALTER TABLE Klient_Alt
DROP COLUMN Address;
GO
-- Задание 4: Добавление столбца Phone
-- Восстанавливаем столбец Phone, который будет хранить номера телефонов.
ALTER TABLE Klient_Alt
DROP COLUMN Phone;
ALTER TABLE Klient_Alt
ADD Phone VARCHAR(20);
GO
-- Задание 5: Изменение типа данных столбца Name
-- Изменяем тип данных столбца Name на NVARCHAR(20).
ALTER TABLE Klient_Alt
ALTER COLUMN NName NVARCHAR(20);
GO
-- Задание 6: Добавление ограничения CHECK для столбца Age
-- Добавляем ограничение CHECK, чтобы в столбце Age были только значения > 21.
ALTER TABLE Klient_Alt WITH NOCHECK
ADD CHECK (Age > 21);
GO
-- Задание 7: Добавление внешнего ключа и первичного ключа
-- Создаем таблицу Zakaz и добавляем внешний ключ на столбец KlientId.
CREATE TABLE Zakaz (
    Id INT IDENTITY(1,1),
    KlientId INT,
    CreatedAt DATE
);
-- Добавляем внешний ключ, который ссылается на таблицу Klient.
ALTER TABLE Zakaz
ADD CONSTRAINT FK_Zakaz_Klient FOREIGN KEY (KlientId) REFERENCES Klient_Alt(Id);
GO
-- Задание 8: Удаление внешнего ключа
-- Удаляем внешнее ключевое ограничение из таблицы Zakaz.
ALTER TABLE Zakaz
DROP CONSTRAINT FK_Zakaz_Klient;
GO
-- Задание 9: Переименование таблицы Klient_Alt в Klient_Updated
-- Переименовываем таблицу Klient_Alt в Klient_Updated.
EXEC sp_rename 'Klient_Alt', 'Klient_Updated';