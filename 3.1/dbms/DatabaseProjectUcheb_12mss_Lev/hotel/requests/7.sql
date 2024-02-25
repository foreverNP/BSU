USE HotelDB;
GO

-- AFTER INSERT ТРИГГЕРЫ 

-- 27.1. AFTER INSERT триггер на таблицу clients
-- При добавлении клиента-физлица присваивать ему приветственную скидку 5% если discount еще 0.00.
DROP TRIGGER IF EXISTS trg_AfterInsert_Clients;
GO
CREATE TRIGGER trg_AfterInsert_Clients
ON clients
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE c
    SET c.discount = 5.00
    FROM clients c
    JOIN inserted i ON i.id = c.id
    JOIN clients_types ct ON ct.id = i.type_id
    WHERE ct.name = 'Individual' AND c.discount = 0.00;
END;
GO

INSERT INTO clients (id, type_id, name, phone, email, discount, registration_date) VALUES 
('TEST1', 1, 'Romashka LLC', '+79001234567', 'contact@romashka.ru', 0.00, '2024-01-15');

SELECT id, discount FROM clients WHERE id = 'TEST1';

DELETE FROM clients WHERE id = 'TEST1';

-- 27.2 AFTER UPDATE
-- Описание запроса: При обновлении статуса бронирования на 'Completed' (status_id=4), увеличивает рейтинг клиента.
-- Business value: Вознаграждает клиентов за завершенные бронирования, поощряя их к дальнейшим действиям.
DROP TRIGGER IF EXISTS trg_AfterUpdate_Bookings;
GO
CREATE TRIGGER trg_AfterUpdate_Bookings
ON bookings
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE c
    SET c.discount = CASE 
                        WHEN c.discount + 1 <= 20 THEN c.discount + 1 
                        ELSE 20 
                     END
    FROM clients c
    JOIN inserted i ON i.booker_id = c.id
    JOIN deleted d ON d.id = i.id
    WHERE i.status_id = 4 AND d.status_id <> 4;
END;
GO

UPDATE bookings
SET status_id = 4
WHERE id = 1;
GO

SELECT id, discount FROM clients WHERE id = 'JUR001';
GO

-- 27.3 AFTER DELETE
-- Описание запроса: При удалении клиента, записывает информацию о клиенте в таблицу archived_clients.
-- Business value: Позволяет сохранять информацию о удаленных клиентах для анализа
DROP TABLE IF EXISTS archived_clients;
GO
CREATE TABLE archived_clients (
    id NVARCHAR(255) PRIMARY KEY,
    type_id INT, 
    name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.0 NOT NULL,
    registration_date DATE DEFAULT GETDATE() NOT NULL,
);
GO

DROP TRIGGER IF EXISTS trg_AfterDelete_Clients;
GO
CREATE TRIGGER trg_AfterDelete_Clients
ON clients
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO archived_clients (id, type_id, name, phone, email, discount, registration_date)
    SELECT id, type_id, name, phone, email, discount, registration_date
    FROM deleted;
END;
GO

INSERT INTO clients (id, type_id, name, phone, email, discount, registration_date) VALUES 
('TEST1', 1, 'Romashka LLC', '+79001234567', 'contact@romashka.ru', 0.00, '2024-01-15');

DELETE FROM clients
WHERE id = 'TEST1';
GO

SELECT * FROM archived_clients;
GO

-- INSTEAD OF-ТРИГГЕРЫ 

DROP VIEW IF EXISTS vw_BookingClient;
GO
CREATE VIEW vw_BookingClient AS
SELECT b.id AS booking_id, b.start_date, b.end_date, b.status_id
FROM bookings b
GO

-- INSTEAD OF INSERT

-- Задание
-- 28.1 INSTEAD OF INSERT
-- Описание запроса: При вставке нового номера, если цена меньше 500, устанавливает минимальную цену 500.
-- Business value: Обеспечивает соблюдение минимальных цен для номеров.

DROP TRIGGER IF EXISTS trg_InsteadOfInsert_Rooms;
GO

CREATE TRIGGER trg_InsteadOfInsert_Rooms
ON rooms
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO rooms (building_id, bed_num, floor, price)
    SELECT building_id, bed_num, floor, 
           CASE WHEN price < 500 THEN 500 ELSE price END
    FROM inserted;
END;
GO

INSERT INTO rooms (building_id, bed_num, floor, price)
VALUES (1, 5, 2, 400.00);
GO

SELECT TOP 1 * FROM rooms ORDER BY id DESC;
GO

-- 28.2 INSTEAD OF UPDATE триггер на представление vw_BookingClient
-- Описание запроса: При обновлении дат бронирования через представление проверяет, что start_date <= end_date.
-- Business value: Обеспечивает корректность данных о периодах бронирований.

DROP TRIGGER IF EXISTS trg_InsteadOfUpdate_vw_BookingClient;
GO

CREATE TRIGGER trg_InsteadOfUpdate_vw_BookingClient
ON vw_BookingClient
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM inserted 
        WHERE start_date > end_date
    )
    BEGIN
        RAISERROR('start_date cannot be after end_date.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    UPDATE b
    SET b.start_date = i.start_date,
        b.end_date = i.end_date,
        b.status_id = i.status_id
    FROM bookings b
    JOIN inserted i ON b.id = i.booking_id;
END;
GO

UPDATE vw_BookingClient
SET start_date = '2024-12-21', end_date = '2024-12-26'
WHERE booking_id = 1;
GO

SELECT * FROM bookings WHERE id = 1;
GO

BEGIN TRY
    UPDATE vw_BookingClient
    SET start_date = '2024-12-28', end_date = '2024-12-25'
    WHERE booking_id = 1;
END TRY
BEGIN CATCH
    PRINT ERROR_MESSAGE();
END CATCH
GO

-- INSTEAD OF DELETE

-- 28.3 INSTEAD OF DELETE
-- Описание запроса: Разрешает удаление расходов только если они были сделаны более 30 дней назад.
-- Business value: Предотвращает удаление актуальных расходов

DROP TRIGGER IF EXISTS trg_InsteadOfDelete_Expenses;
GO
CREATE TRIGGER trg_InsteadOfDelete_Expenses
ON expenses
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM deleted 
        WHERE DATEDIFF(DAY, expense_date, GETDATE()) <= 30
    )
    BEGIN
        RAISERROR('Cannot delete expenses made within the last 30 days.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    DELETE e
    FROM expenses e
    JOIN deleted d ON e.id = d.id;
END;
GO


BEGIN TRY
    DELETE FROM expenses
    WHERE id = 1;
END TRY
BEGIN CATCH
    PRINT ERROR_MESSAGE();
END CATCH
GO

-- ТРАНЗАКЦИИ

-- 29.1 Транзакция создания нового бронирования и обновления скидки клиента
-- Описание запроса: Создает новое бронирование и одновременно обновляет скидку клиента в рамках одной транзакции.
-- Business value: Обеспечивает согласованность данных при выполнении связанных операций
BEGIN TRAN AddBookingAndUpdateDiscount

DECLARE @client_id NVARCHAR(255) = 'AB1234567';
DECLARE @room_id INT = 2;

INSERT INTO bookings (room_id, booker_id, start_date, end_date, status_id)
VALUES (@room_id, @client_id, '2024-12-20', '2024-12-25', 1);

UPDATE clients
SET discount = CASE 
                WHEN discount < 10 THEN discount + 1 
                ELSE discount 
               END
WHERE id = @client_id;

COMMIT TRAN AddBookingAndUpdateDiscount;
GO

SELECT * FROM bookings WHERE booker_id = 'AB1234567' ORDER BY id DESC;
GO

SELECT id, name, discount FROM clients WHERE id = 'AB1234567';
GO

-- 29.2 Транзакция добавления нового клиента и бронирования для него
-- Описание запроса: Добавляет нового клиента и создает для него бронирование в рамках одной транзакции
BEGIN TRAN AddClientAndBooking

DECLARE @newClientId NVARCHAR(255) = 'NEW123';
DECLARE @room_id INT = 3;

INSERT INTO clients (id, type_id, name, phone, email)
VALUES (@newClientId, 1, 'New Client', '+79991234567', 'newclient@example.com');

INSERT INTO bookings (room_id, booker_id, start_date, end_date, status_id)
VALUES (@room_id, @newClientId, '2024-12-26', '2024-12-28', 1);

COMMIT TRAN AddClientAndBooking;
GO

SELECT * FROM clients WHERE id = 'NEW123';
GO

SELECT * FROM bookings WHERE booker_id = 'NEW123';
GO

-- ТРАНЗАКЦИИ С ОБРАБОТКОЙ ОШИБОК

-- 30.1 Транзакция вставки услуги с несуществующим типом 
-- Описание запроса: Пытается вставить услугу с несуществующим type_id, обрабатывает ошибку внешнего ключа.
BEGIN TRAN InsertServiceWithInvalidType
BEGIN TRY
    INSERT INTO services (type_id, name, price) VALUES (9999, 'Invalid Service', 500.00);
    COMMIT TRAN InsertServiceWithInvalidType;
END TRY
BEGIN CATCH
    ROLLBACK TRAN InsertServiceWithInvalidType;
    PRINT 'Error in InsertServiceWithInvalidType transaction: ' + ERROR_MESSAGE();
END CATCH
GO

-- 30.2 Транзакция добавления бронирования с end_date < start_date
-- Описание запроса: Пытается добавить бронирование, где end_date раньше start_date, проверяет логические ограничения.
BEGIN TRAN InsertInvalidBookingDates
BEGIN TRY
    DECLARE @room_id INT = 5;
    DECLARE @client_id NVARCHAR(255) = 'EF2345678';
    DECLARE @start_date DATE = '2024-12-10';
    DECLARE @end_date DATE = '2024-12-05';

    INSERT INTO bookings (room_id, booker_id, start_date, end_date, status_id)
    VALUES (@room_id, @client_id, @start_date, @end_date, 1);

    IF (@end_date < @start_date)
    BEGIN
        RAISERROR('Invalid booking period: end_date is earlier than start_date.', 16, 1);
        ROLLBACK TRAN InsertInvalidBookingDates;
        RETURN;
    END

    COMMIT TRAN InsertInvalidBookingDates;
END TRY
BEGIN CATCH
    ROLLBACK TRAN InsertInvalidBookingDates;
    PRINT 'Error in InsertInvalidBookingDates transaction: ' + ERROR_MESSAGE();
END CATCH
GO

SELECT * FROM bookings WHERE room_id = 5;
