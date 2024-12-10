-- Задание 23: Создание 4 хранимых процедур с входными параметрами

-- 1. Получение информации о клиенте по его ID
-- Business value: Позволяет быстро получать подробную информацию о клиенте для улучшения обслуживания и управления клиентской базой.

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

-- Применение процедуры GetClientByID
EXEC GetClientByID @ClientID = 'JUR001';
GO

-- 2. Получение списка доступных комнат в указанном здании и на определенном этаже
-- Business value: Упрощает процесс бронирования, предоставляя актуальную информацию о доступных комнатах.

DROP PROC IF EXISTS GetAvailableRooms;
GO
CREATE PROC GetAvailableRooms
    @BuildingID INT,
    @Floor INT
AS
BEGIN
    SELECT 
        r.id AS [Номер комнаты],
        r.bed_num AS [Количество спальных мест],
        r.price AS [Цена за ночь]
    FROM 
        rooms r
    WHERE 
        r.building_id = @BuildingID
        AND r.floor = @Floor
        AND r.id NOT IN (
            SELECT room_id FROM bookings 
            WHERE @Floor BETWEEN start_date AND end_date
        );
END
GO

-- Применение процедуры GetAvailableRooms
EXEC GetAvailableRooms @BuildingID = 1, @Floor = 2;
GO

-- 3. Получение списка бронирований за указанный период
-- Business value: Позволяет менеджерам отслеживать загрузку гостиницы и планировать ресурсы.

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

-- Применение процедуры GetBookingsByDateRange
EXEC GetBookingsByDateRange @StartDate = '2024-12-01', @EndDate = '2024-12-31';
GO

-- 4. Получение списка расходов по конкретному бронированию
-- Business value: Позволяет анализировать затраты, связанные с каждым бронированием, для финансового контроля и оптимизации.

DROP PROC IF EXISTS GetExpensesByBooking;
GO
CREATE PROC GetExpensesByBooking
    @BookingID INT
AS
BEGIN
    SELECT 
        e.id AS [ID расхода],
        s.name AS [Услуга],
        e.amount AS [Сумма],
        e.expense_date AS [Дата расхода]
    FROM 
        expenses e
        JOIN services s ON e.service_id = s.id
    WHERE 
        e.booking_id = @BookingID;
END
GO

-- Применение процедуры GetExpensesByBooking
EXEC GetExpensesByBooking @BookingID = 1;
GO

---------------------------------------------------

-- Задание 24: Создание 4 хранимых процедур с выходными параметрами

-- 1. Получение общего количества бронирований для клиента
-- Business value: Позволяет оценивать активность клиентов и их вклад в бизнес.

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

-- Применение процедуры GetTotalBookingsByClient
DECLARE @Total INT;
EXEC GetTotalBookingsByClient @ClientID = 'JUR001', @TotalBookings = @Total OUTPUT;
SELECT @Total AS [Общее количество бронирований];
GO

-- 2. Получение общей суммы расходов по бронированию
-- Business value: Позволяет контролировать финансовые показатели и планировать бюджет.

DROP PROC IF EXISTS GetTotalExpensesByBooking;
GO
CREATE PROC GetTotalExpensesByBooking
    @BookingID INT,
    @TotalExpenses DECIMAL(10,2) OUTPUT
AS
BEGIN
    SELECT 
        @TotalExpenses = SUM(amount)
    FROM 
        expenses
    WHERE 
        booking_id = @BookingID;
END
GO

-- Применение процедуры GetTotalExpensesByBooking
DECLARE @Expenses DECIMAL(10,2);
EXEC GetTotalExpensesByBooking @BookingID = 1, @TotalExpenses = @Expenses OUTPUT;
SELECT @Expenses AS [Общая сумма расходов];
GO

-- 3. Получение средней цены номера в здании
-- Business value: Помогает в анализе ценовой политики и конкурентоспособности.

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

-- Применение процедуры GetAverageRoomPriceByBuilding
DECLARE @AvgPrice DECIMAL(10,2);
EXEC GetAverageRoomPriceByBuilding @BuildingID = 1, @AveragePrice = @AvgPrice OUTPUT;
SELECT @AvgPrice AS [Средняя цена номера];
GO

-- 4. Получение общего количества клиентов по типу
-- Business value: Позволяет сегментировать клиентскую базу для таргетированных маркетинговых кампаний.

DROP PROC IF EXISTS GetTotalClientsByType;
GO
CREATE PROC GetTotalClientsByType
    @ClientType NVARCHAR(20),
    @TotalClients INT OUTPUT
AS
BEGIN
    SELECT 
        @TotalClients = COUNT(*)
    FROM 
        clients c
        JOIN clients_types ct ON c.type_id = ct.id
    WHERE 
        ct.name = @ClientType;
END
GO

-- Применение процедуры GetTotalClientsByType
DECLARE @ClientsCount INT;
EXEC GetTotalClientsByType @ClientType = 'Legal Entity', @TotalClients = @ClientsCount OUTPUT;
SELECT @ClientsCount AS [Общее количество клиентов типа 'Legal Entity'];
GO

---------------------------------------------------

-- Задание 25: Создание 4 определяемых пользователем скалярных функций

-- 1. Вычисление продолжительности бронирования в днях
-- Business value: Позволяет быстро оценить длительность пребывания клиентов для аналитики и планирования.

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

-- Применение функции CalculateBookingDuration
SELECT 
    id AS [ID бронирования],
    dbo.CalculateBookingDuration(start_date, end_date) AS [Продолжительность (дни)]
FROM 
    bookings;
GO

-- 2. Определение VIP-клиента на основе скидки
-- Business value: Позволяет автоматически классифицировать клиентов для предоставления специальных предложений.

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

-- Применение функции IsVIPClient
SELECT 
    id AS [ID клиента],
    name AS [Имя],
    discount AS [Скидка],
    dbo.IsVIPClient(discount) AS [VIP]
FROM 
    clients;
GO

-- 3. Вычисление общей стоимости бронирования
-- Business value: Обеспечивает точный расчет стоимости бронирования для выставления счетов клиентам.

DROP FUNCTION IF EXISTS dbo.CalculateTotalBookingCost;
GO
CREATE FUNCTION dbo.CalculateTotalBookingCost
(
    @RoomPrice DECIMAL(10,2),
    @Duration INT,
    @ServiceCost DECIMAL(10,2)
)
RETURNS DECIMAL(10,2)
AS
BEGIN
    RETURN (@RoomPrice * @Duration) + @ServiceCost;
END
GO

-- Применение функции CalculateTotalBookingCost
SELECT 
    b.id AS [ID бронирования],
    r.price AS [Цена номера],
    dbo.CalculateBookingDuration(b.start_date, b.end_date) AS [Длительность (дни)],
    ISNULL(SUM(e.amount), 0) AS [Стоимость услуг],
    dbo.CalculateTotalBookingCost(r.price, dbo.CalculateBookingDuration(b.start_date, b.end_date), ISNULL(SUM(e.amount), 0)) AS [Общая стоимость]
FROM 
    bookings b
    JOIN rooms r ON b.room_id = r.id
    LEFT JOIN expenses e ON b.id = e.booking_id
GROUP BY 
    b.id, r.price, b.start_date, b.end_date;
GO

-- 4. Получение скидки клиента на основе типа клиента
-- Business value: Автоматизирует предоставление скидок клиентам в зависимости от их типа, улучшая клиентский сервис.

DROP FUNCTION IF EXISTS dbo.GetClientDiscountByType;
GO
CREATE FUNCTION dbo.GetClientDiscountByType
(
    @ClientType NVARCHAR(20)
)
RETURNS DECIMAL(5,2)
AS
BEGIN
    DECLARE @Discount DECIMAL(5,2);
    IF @ClientType = 'Legal Entity'
        SET @Discount = 10.00;
    ELSE IF @ClientType = 'Individual'
        SET @Discount = 5.00;
    ELSE
        SET @Discount = 0.00;
    RETURN @Discount;
END
GO

-- Применение функции GetClientDiscountByType
SELECT 
    c.id AS [ID клиента],
    c.name AS [Имя],
    ct.name AS [Тип клиента],
    dbo.GetClientDiscountByType(ct.name) AS [Скидка по типу]
FROM 
    clients c
    JOIN clients_types ct ON c.type_id = ct.id;
GO

---------------------------------------------------

-- Задание 26: Создание 4 определяемых пользователем функций, возвращающих табличное значение

-- 1. Список активных бронирований
-- Business value: Позволяет быстро получать актуальные бронирования для управления загрузкой гостиницы.

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

-- Применение функции GetActiveBookings
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

-- Применение функции GetServicesByBooking
SELECT * FROM dbo.GetServicesByBooking(@BookingID = 1);
GO

-- 3. Список клиентов, сделавших бронирования в определенный период
-- Business value: Помогает в анализе клиентской активности и планировании маркетинговых мероприятий.

DROP FUNCTION IF EXISTS dbo.GetClientsByBookingPeriod;
GO
CREATE FUNCTION dbo.GetClientsByBookingPeriod
(
    @StartDate DATE,
    @EndDate DATE
)
RETURNS TABLE
AS
RETURN
(
    SELECT DISTINCT 
        c.id AS [ID клиента],
        c.name AS [Имя],
        c.email AS [Электронная почта],
        c.phone AS [Телефон]
    FROM 
        bookings b
        JOIN clients c ON b.booker_id = c.id
    WHERE 
        b.start_date >= @StartDate
        AND b.end_date <= @EndDate
);
GO

-- Применение функции GetClientsByBookingPeriod
SELECT * FROM dbo.GetClientsByBookingPeriod(@StartDate = '2024-12-01', @EndDate = '2024-12-31');
GO

-- 4. Список доступных комнат на заданный период
-- Business value: Облегчает процесс поиска свободных номеров для клиентов, повышая уровень обслуживания.

DROP FUNCTION IF EXISTS dbo.GetAvailableRoomsByDate;
GO
CREATE FUNCTION dbo.GetAvailableRoomsByDate
(
    @StartDate DATE,
    @EndDate DATE
)
RETURNS TABLE
AS
RETURN
(
    SELECT 
        r.id AS [Номер комнаты],
        r.bed_num AS [Количество спальных мест],
        r.floor AS [Этаж],
        r.price AS [Цена за ночь],
        b.name AS [Класс здания]
    FROM 
        rooms r
        JOIN buildings b ON r.building_id = b.id
    WHERE 
        r.id NOT IN (
            SELECT room_id FROM bookings 
            WHERE 
                (@StartDate <= end_date) 
                AND (@EndDate >= start_date)
        )
);
GO

-- Применение функции GetAvailableRoomsByDate
SELECT * FROM dbo.GetAvailableRoomsByDate(@StartDate = '2024-12-20', @EndDate = '2024-12-25');
GO
