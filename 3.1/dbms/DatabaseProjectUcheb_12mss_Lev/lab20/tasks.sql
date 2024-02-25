-- 1. Создание двух новых логинов с разными серверными ролями
USE master;
GO

-- Логин LoginA с ролью dbcreator
CREATE LOGIN LoginA WITH PASSWORD = 'StrongP@ssw0rdA!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
ALTER SERVER ROLE dbcreator ADD MEMBER LoginA;
GO

-- Логин LoginB с ролью securityadmin
CREATE LOGIN LoginB WITH PASSWORD = 'StrongP@ssw0rdB!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
ALTER SERVER ROLE securityadmin ADD MEMBER LoginB;
GO

-- 2. Создание двух новых логинов средствами T-SQL
-- Логин LoginC с ролью securityadmin
CREATE LOGIN LoginC WITH PASSWORD = 'StrongP@ssw0rdC!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
ALTER SERVER ROLE securityadmin ADD MEMBER LoginC;
GO

-- Логин LoginD с ролью processadmin
CREATE LOGIN LoginD WITH PASSWORD = 'StrongP@ssw0rdD!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
ALTER SERVER ROLE processadmin ADD MEMBER LoginD;
GO

-- 3. Создание тестового логина для проверки действий
CREATE LOGIN LoginTest WITH PASSWORD = 'StrongP@ssw0rdTest!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
GO
DROP LOGIN LoginTest 

-- 4. Создание серверной роли и предоставление разрешений
CREATE SERVER ROLE MyServerRole;
GO

GRANT CREATE ANY DATABASE TO MyServerRole;
GO

GRANT ALTER ANY LOGIN TO MyServerRole;
GO

-- 5. Добавление LoginC в серверную роль MyServerRole
ALTER SERVER ROLE MyServerRole ADD MEMBER LoginC;
GO

-- 6. Исключение LoginC из роли bulkadmin
ALTER SERVER ROLE bulkadmin DROP MEMBER LoginC;
GO

-- 7. Удаление серверной роли MyServerRole
DROP SERVER ROLE MyServerRole;
GO

-- 8. Удаление логина LoginD
DROP LOGIN LoginD;
GO

-- 9. Создание базы данных TestDB (если отсутствует) и пользователей для оставшихся логинов
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'TestDB')
BEGIN
    CREATE DATABASE TestDB;
END;
GO

USE TestDB;
GO

-- Создание пользователей
CREATE USER UserA FOR LOGIN LoginA WITH DEFAULT_SCHEMA = dbo;
GO

CREATE USER UserB FOR LOGIN LoginB WITH DEFAULT_SCHEMA = dbo;
GO

CREATE USER UserC FOR LOGIN LoginC WITH DEFAULT_SCHEMA = dbo;
GO

-- Назначение ролей для пользователей
ALTER ROLE db_datareader ADD MEMBER UserB;
GO

ALTER ROLE db_datawriter ADD MEMBER UserC;
GO

-- 10. Создание пользовательской роли уровня базы данных и предоставление прав
CREATE ROLE MyDbRole;
GO

GRANT SELECT ON SCHEMA::dbo TO MyDbRole;
GO

GRANT INSERT ON SCHEMA::dbo TO MyDbRole;
GO

GRANT UPDATE ON SCHEMA::dbo TO MyDbRole;
GO

-- 11. Добавление UserB в пользовательскую роль MyDbRole
ALTER ROLE MyDbRole ADD MEMBER UserB;
GO

-- 12. Проверка прав для UserB выполняется вручную через подключение

-- 13. Удаление пользователя UserA
DROP USER UserA;
GO

-- 14. Создание схемы и объектов в ней
CREATE SCHEMA MySchema AUTHORIZATION MyDbRole;
GO

CREATE TABLE MySchema.Table1 (ID INT IDENTITY(1,1) PRIMARY KEY, Name NVARCHAR(100));
GO

CREATE TABLE MySchema.Table2 (ID INT IDENTITY(1,1) PRIMARY KEY, Description NVARCHAR(255));
GO

CREATE TABLE MySchema.Table3 (ID INT IDENTITY(1,1) PRIMARY KEY, Value DECIMAL(10,2));
GO

CREATE VIEW MySchema.View1 AS SELECT ID, Name FROM MySchema.Table1;
GO

CREATE VIEW MySchema.View2 AS SELECT ID, Description FROM MySchema.Table2;
GO

-- Назначение прав
GRANT SELECT ON MySchema.Table1 TO UserB;
GO

GRANT INSERT ON MySchema.Table1 TO UserC;
GO

DENY SELECT ON MySchema.View1 TO UserC;
GO

GRANT SELECT, UPDATE ON MySchema.View2 TO UserB;
GO

-- 15. Переназначение владельца схемы
ALTER AUTHORIZATION ON SCHEMA::MySchema TO UserB;
GO

-- 16. Проверка прав выполняется вручную через подключение

-- 17. Удаление схемы и объектов
DROP VIEW MySchema.View1;
DROP VIEW MySchema.View2;
GO

DROP TABLE MySchema.Table1;
DROP TABLE MySchema.Table2;
DROP TABLE MySchema.Table3;
GO

DROP SCHEMA MySchema;
GO

-- 18. Применение инструкций GRANT, DENY, REVOKE
CREATE ROLE MyCustomRole;
GO

GRANT SELECT, INSERT ON SCHEMA::dbo TO MyCustomRole;
GO

GRANT EXECUTE ON SCHEMA::dbo TO MyCustomRole;
GO

DENY DELETE ON SCHEMA::dbo TO MyCustomRole;
GO

-- Создание логина и пользователя для MyCustomRole
CREATE LOGIN LoginX WITH PASSWORD = 'StrongP@ssw0rdX!', CHECK_POLICY = ON, CHECK_EXPIRATION = ON;
GO

CREATE USER UserX FOR LOGIN LoginX WITH DEFAULT_SCHEMA = dbo;
GO

ALTER ROLE MyCustomRole ADD MEMBER UserX;
GO

-- 19. Членство в роли db_owner и запрет DDL для UserC
ALTER ROLE db_owner ADD MEMBER UserB;
ALTER ROLE db_owner ADD MEMBER UserC;
GO

DENY CREATE TABLE TO UserC;
GO

-- 20. Удаление всех объектов, кроме одной базы данных
USE TestDB;
GO

DROP USER UserB;
DROP USER UserC;
DROP USER UserX;
GO

DROP ROLE MyDbRole;
DROP ROLE MyCustomRole;
GO

USE master;
GO

DROP LOGIN LoginA;
DROP LOGIN LoginB;
DROP LOGIN LoginC;
DROP LOGIN LoginX;
GO
