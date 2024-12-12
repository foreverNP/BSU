-- 1. Создать хранимую процедуру для вывода данных всех стран и вызвать её
DROP PROC IF EXISTS PROC_AllCountries;
GO

CREATE PROC PROC_AllCountries
AS
BEGIN
    SELECT *
    FROM Tabl_Kontinent$;
END
GO

-- Вызов процедуры PROC_AllCountries
EXEC PROC_AllCountries;
GO

-- 2. Создать хранимую процедуру, которая принимает число и возвращает количество цифр через параметр OUTPUT
DROP PROC IF EXISTS PROC_CountDigits;
GO

CREATE PROC PROC_CountDigits
    @Number BIGINT,       
    @DigitCount INT OUTPUT
AS
BEGIN
    IF @Number IS NULL
    BEGIN
        SET @DigitCount = 0;
    END
    ELSE
    BEGIN
        -- Преобразуем число в строку и считаем длину строки
        SET @DigitCount = LEN(CAST(ABS(@Number) AS VARCHAR(20)));
    END
END
GO

-- Вызов процедуры PROC_CountDigits
DECLARE @InputNumber BIGINT = 123456;
DECLARE @NumberOfDigits INT;

EXEC PROC_CountDigits @Number = @InputNumber, @DigitCount = @NumberOfDigits OUTPUT;

SELECT @NumberOfDigits AS [Количество цифр];
GO

-- 3. Создать хранимую процедуру для создания таблицы «TestTabl» и заполнения её странами, названия которых начинаются с буквы «С»
DROP PROC IF EXISTS PROC_CreateTestTabl;
GO

CREATE PROC PROC_CreateTestTabl
AS
BEGIN
    -- Проверка и удаление таблицы, если она уже существует
    DROP TABLE IF EXISTS dbo.TestTabl;

    -- Создание новой таблицы TestTabl
    CREATE TABLE dbo.TestTabl
    (
        Nazvanie NVARCHAR(100),
        Stolica NVARCHAR(100),
        PL FLOAT,
        KolNas BIGINT,
        Kontinent NVARCHAR(50)
    );

    -- Заполнение таблицы TestTabl странами, названия которых начинаются с 'С'
    INSERT INTO dbo.TestTabl (Nazvanie, Stolica, PL, KolNas, Kontinent)
    SELECT Nazvanie, Stolica, PL, KolNas, Kontinent
    FROM Tabl_Kontinent$
    WHERE Nazvanie LIKE N'С%';
END
GO

-- Вызов процедуры PROC_CreateTestTabl
EXEC PROC_CreateTestTabl;
GO

-- Проверка содержимого таблицы TestTabl
SELECT * FROM dbo.TestTabl;
GO

---------------------------------------------------------------
-- 1. Создать функцию для возврата списка стран с площадью меньше заданного числа и вызвать её
DROP FUNCTION IF EXISTS dbo.Fun_CountriesBelowArea;
GO

CREATE FUNCTION dbo.Fun_CountriesBelowArea
(
    @MaxArea FLOAT
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        Nazvanie,
        Stolica,
        PL,
        KolNas,
        Kontinent
    FROM 
        Tabl_Kontinent$
    WHERE 
        PL < @MaxArea
);
GO

SELECT * FROM dbo.Fun_CountriesBelowArea(100000);
GO

-- 2. Создать функцию для возврата таблицы с названием страны и плотностью населения и вызвать её
DROP FUNCTION IF EXISTS dbo.Fun_PopulationDensity;
GO

CREATE FUNCTION dbo.Fun_PopulationDensity()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        Nazvanie,
        ROUND(CAST(KolNas AS FLOAT) / NULLIF(PL, 0), 2) AS PopulationDensity
    FROM 
        Tabl_Kontinent$
);
GO

SELECT * FROM dbo.Fun_PopulationDensity();
GO