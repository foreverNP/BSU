-- 2 Создание триггера trig1 для добавления записи
DROP TRIGGER IF EXISTS trig1;
GO

CREATE TRIGGER trig1
ON Student
FOR INSERT
AS
BEGIN
    PRINT N'Запись добавлена';
END;
GO

-- Проверка работы триггера trig1
INSERT INTO Student (FIO, Data, spez, godpost)
VALUES (N'Тестовый Студент', '2000-01-01', N'Биология', 2025);
GO

-- 3. Создание триггера trig2 для предотвращения удаления записей
DROP TRIGGER IF EXISTS trig2;
GO

CREATE TRIGGER trig2
ON Student
INSTEAD OF DELETE
AS
BEGIN
    PRINT N'Нельзя удалить данные';
END;
GO

-- Проверка работы триггера trig2
DELETE FROM Student WHERE FIO = N'Тестовый Студент';
GO

-- 4. Создание триггера trig3 для архивации удаляемых записей
DROP TRIGGER IF EXISTS trig3;
GO

CREATE TRIGGER trig3
ON Student
FOR DELETE
AS
BEGIN
    INSERT INTO Студент_Архив (FIO, Data, spez, godpost, Удалено)
    SELECT FIO, Data, spez, godpost, GETDATE() AS Удалено
    FROM DELETED;
END;
GO

-- Проверка работы триггера trig3
DELETE FROM Student WHERE FIO = N'Тестовый Студент';
GO

SELECT * FROM Студент_Архив;
GO