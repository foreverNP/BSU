-- 1. Создать Хранимую процедуру для вывода информации о сервере, о базе данных и о текущем пользователе, и вызвать её
DROP PROC IF EXISTS PROC1;
GO
CREATE PROC PROC1
AS
BEGIN
    SELECT 
        @@Servername AS Сервер,
        @@Version AS ВерсияСУБД,
        Db_Name() AS БазаДанных,
        User AS ПользовательБД,
        System_User AS СистемныйПользователь
END
GO
-- Вызов процедуры PROC1
EXEC PROC1
GO

-- 2. Напишите хранимую процедуру, которая возвращает количество стран, содержащих в названии заданную букву, и вызовите её
DROP PROC IF EXISTS PROC2;
GO
CREATE PROC PROC2
    @Буква AS NCHAR(1),
    @Количество AS INT OUTPUT
AS
BEGIN
    SELECT 
        @Количество = COUNT(*)
    FROM 
        Tabl_Kontinent$
    WHERE 
        CHARINDEX(@Буква, Nazvanie) > 0
END
GO

-- Вызов процедуры PROC2
DECLARE @K AS INT
DECLARE @Б AS NCHAR(1)
SET @Б = N'y'
EXECUTE PROC2 @Б, @K OUTPUT
SELECT @K AS [Количество стран]
GO


-- 3. Напишите хранимую процедуру для вывода трёх стран с наименьшей площадью в заданной части света = Европа, вызовите её
DROP PROC IF EXISTS PROC3;
GO
CREATE PROC PROC3
    @Конт AS NVARCHAR(50) = N'Европа'
AS
BEGIN
    SELECT TOP 3 
        Nazvanie,
        Stolica,
        PL,
        KolNas,
        Kontinent
    FROM 
        Tabl_Kontinent$
    WHERE 
        Kontinent = @Конт
    ORDER BY 
        PL
END
GO

-- Вызов процедуры PROC3
EXECUTE PROC3 DEFAULT
GO


---------------------------------------------------
-- 1. Напишите функцию для вывода списка стран с площадью в интервале заданных значений, и вызовите её
DROP FUNCTION IF EXISTS Fun1;
GO
CREATE FUNCTION Fun1
(
    @A1 AS FLOAT,
    @B1 AS FLOAT
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
        PL BETWEEN @A1 AND @B1
)
GO
-- Вызов функции Fun1
SELECT * FROM dbo.Fun1(10, 1000)
GO


-- 2. Напишите функцию для вывода столицы данной страны, и вызовите её
DROP FUNCTION IF EXISTS Fun2
GO
CREATE FUNCTION Fun2
(
    @Страна AS NVARCHAR(50)
)
RETURNS NVARCHAR(50)
AS
BEGIN
    DECLARE @S AS NVARCHAR(50)
    SELECT 
        @S = Stolica
    FROM 
        Tabl_Kontinent$
    WHERE 
        Nazvanie = @Страна
    RETURN @S
END
GO

-- Вызов функции Fun2
SELECT dbo.Fun2(N'Австрия')
GO


-- 3. Напишите функцию для вычисления плотности населения, и вызовите её
DROP FUNCTION IF EXISTS Fun3
GO
CREATE FUNCTION Fun3
(
    @Население AS INT,
    @Площадь AS FLOAT
)
RETURNS FLOAT
AS
BEGIN
    DECLARE @P AS FLOAT
    SET @P = ROUND(CAST(@Население AS FLOAT) / @Площадь, 2)
    RETURN @P
END
GO

-- Вызов функции Fun3
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent,
    dbo.Fun3(KolNas, PL) AS Плотность
FROM 
    Tabl_Kontinent$
GO
