-- Задание 23: Создание 2 хранимых процедур с входными параметрами

-- 1. Получение информации о клиенте по его ID
-- Business value: быстро получать подробную информацию о клиенте

DROP PROC IF EXISTS GetClientByID;
GO
CREATE PROC GetClientByID
    @ClientID NVARCHAR(255)
AS
BEGIN
    SELECT 
        id AS [Идентификатор клиента],
        name AS [Имя],
        phone AS [Телефон],
        email AS [Электронная почта],
        discount AS [Скидка],
        registration_date AS [Дата регистрации]
    FROM 
        clients
    WHERE 
        id = @ClientID;
END
GO

EXEC GetClientByID @ClientID = 'JUR001';
GO

-- 2. Получение списка бронирований за указанный период
-- Business value: отслеживать загрузку гостиницы и планировать ресурсы.

DROP PROC IF EXISTS GetBookingsByDateRange;
GO
CREATE PROC GetBookingsByDateRange
    @StartDate DATE,
    @EndDate DATE
AS
BEGIN
    SELECT 
        b.id AS [ID бронирования],
        c.name AS [Клиент],
        r.id AS [Номер комнаты],
        b.start_date AS [Дата начала],
        b.end_date AS [Дата окончания],
        bs.name AS [Статус бронирования]
    FROM 
        bookings b
        JOIN clients c ON b.booker_id = c.id
        JOIN rooms r ON b.room_id = r.id
        JOIN booking_statuses bs ON b.status_id = bs.id
    WHERE 
        b.start_date >= @StartDate
        AND b.end_date <= @EndDate;
END
GO

EXEC GetBookingsByDateRange @StartDate = '2024-12-01', @EndDate = '2024-12-31';
GO

---------------------------------------------------

-- Задание 24: Создание 2 хранимых процедур с выходными параметрами

-- 1. Получение общего количества бронирований для клиента
-- Business value: оценивать активность клиентов

DROP PROC IF EXISTS GetTotalBookingsByClient;
GO
CREATE PROC GetTotalBookingsByClient
    @ClientID NVARCHAR(255),
    @TotalBookings INT OUTPUT
AS
BEGIN
    SELECT 
        @TotalBookings = COUNT(*)
    FROM 
        bookings
    WHERE 
        booker_id = @ClientID;
END
GO

DECLARE @Total INT;
EXEC GetTotalBookingsByClient @ClientID = 'JUR001', @TotalBookings = @Total OUTPUT;
SELECT @Total AS [Общее количество бронирований];
GO

-- 2. Получение средней цены номера в здании
-- Business value: Помогает в анализе
DROP PROC IF EXISTS GetAverageRoomPriceByBuilding;
GO
CREATE PROC GetAverageRoomPriceByBuilding
    @BuildingID INT,
    @AveragePrice DECIMAL(10,2) OUTPUT
AS
BEGIN
    SELECT 
        @AveragePrice = AVG(price)
    FROM 
        rooms
    WHERE 
        building_id = @BuildingID;
END
GO

DECLARE @AvgPrice DECIMAL(10,2);
EXEC GetAverageRoomPriceByBuilding @BuildingID = 1, @AveragePrice = @AvgPrice OUTPUT;
SELECT @AvgPrice AS [Средняя цена номера];
GO

---------------------------------------------------

-- Задание 25: Создание 2 определяемых пользователем скалярных функций

-- 1. Вычисление продолжительности бронирования в днях
-- Business value: оценить длительность пребывания клиентов для аналитики и планирования.

DROP FUNCTION IF EXISTS dbo.CalculateBookingDuration;
GO
CREATE FUNCTION dbo.CalculateBookingDuration
(
    @StartDate DATE,
    @EndDate DATE
)
RETURNS INT
AS
BEGIN
    RETURN DATEDIFF(DAY, @StartDate, @EndDate);
END
GO

SELECT 
    id AS [ID бронирования],
    dbo.CalculateBookingDuration(start_date, end_date) AS [Продолжительность (дни)]
FROM 
    bookings;
GO

-- 2. Определение VIP-клиента на основе скидки
-- Business value: автоматически классифицировать клиентов для предоставления специальных предложений.

DROP FUNCTION IF EXISTS dbo.IsVIPClient;
GO
CREATE FUNCTION dbo.IsVIPClient
(
    @Discount DECIMAL(5,2)
)
RETURNS BIT
AS
BEGIN
    RETURN CASE 
        WHEN @Discount >= 10.00 THEN 1
        ELSE 0
    END;
END
GO

SELECT 
    id AS [ID клиента],
    name AS [Имя],
    discount AS [Скидка],
    dbo.IsVIPClient(discount) AS [VIP]
FROM 
    clients;
GO

---------------------------------------------------

-- Задание 26: Создание 2 определяемых пользователем функций, возвращающих табличное значение

-- 1. Список активных бронирований
-- Business value: быстро получать актуальные бронирования для управления загрузкой гостиницы.

DROP FUNCTION IF EXISTS dbo.GetActiveBookings;
GO
CREATE FUNCTION dbo.GetActiveBookings()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        b.id AS [ID бронирования],
        c.name AS [Клиент],
        r.id AS [Номер комнаты],
        b.start_date AS [Дата начала],
        b.end_date AS [Дата окончания],
        bs.name AS [Статус]
    FROM 
        bookings b
        JOIN clients c ON b.booker_id = c.id
        JOIN rooms r ON b.room_id = r.id
        JOIN booking_statuses bs ON b.status_id = bs.id
    WHERE 
        GETDATE() BETWEEN b.start_date AND b.end_date
);
GO

SELECT * FROM dbo.GetActiveBookings();
GO

-- 2. Список услуг, используемых в конкретном бронировании
-- Business value: Обеспечивает детальный отчет о дополнительных услугах, использованных клиентами.
DROP FUNCTION IF EXISTS dbo.GetServicesByBooking;
GO
CREATE FUNCTION dbo.GetServicesByBooking
(
    @BookingID INT
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        s.name AS [Услуга],
        e.amount AS [Сумма],
        e.expense_date AS [Дата расхода]
    FROM 
        expenses e
        JOIN services s ON e.service_id = s.id
    WHERE 
        e.booking_id = @BookingID
);
GO

SELECT * FROM dbo.GetServicesByBooking(1);
GO
