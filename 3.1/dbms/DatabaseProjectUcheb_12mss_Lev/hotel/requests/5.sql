USE HotelDB;
GO

-- Задание 21: Создание 5 не обновляемых представлений

-- 1. Представление с информацией об услугах, предоставляемых зданиями
-- Business value: Помогает определить, какие услуги доступны в каждом здании для улучшения сервиса.
CREATE VIEW vw_BuildingServices
AS
SELECT
    b.id AS BuildingID,
    bc.name AS BuildingClass,
    s.name AS ServiceName,
    s.price
FROM
    building_services bs
    JOIN buildings b ON bs.building_id = b.id
    JOIN building_classes bc ON b.class_id = bc.id
    JOIN services s ON bs.service_id = s.id;
GO

SELECT * FROM vw_BuildingServices;
GO

-- 2. Представление с информацией о клиентах и их типах
-- Business value: для целевых маркетинговых кампаний.
CREATE VIEW vw_ClientTypes
AS
SELECT
    c.id AS ClientID,
    c.name AS ClientName,
    ct.name AS ClientType,
    c.phone,
    c.email,
    c.discount,
    c.registration_date
FROM
    clients c
    LEFT JOIN clients_types ct ON c.type_id = ct.id;
GO

SELECT * FROM vw_ClientTypes;
GO

-- 3. Представление с информацией о доходах от бронирований по зданиям
-- Business value: Позволяет анализировать доходы по различным зданиям для финансового планирования.
CREATE VIEW vw_RevenueByBuilding
AS
SELECT
    b.id AS BuildingID,
    bc.name AS BuildingClass,
    SUM(r.price) AS TotalRevenue
FROM
    bookings bk
    INNER JOIN rooms r ON bk.room_id = r.id
    INNER JOIN buildings b ON r.building_id = b.id
    INNER JOIN building_classes bc ON b.class_id = bc.id
WHERE
    bk.status_id = (SELECT id FROM booking_statuses WHERE name = 'Completed')
GROUP BY
    b.id,
    bc.name;
GO

SELECT * FROM vw_RevenueByBuilding;
GO

-- 4. Представление с информацией о клиентах с активными бронированиями
-- Business value: Позволяет идентифицировать активных клиентов для улучшения обслуживания и маркетинга.
CREATE VIEW vw_ActiveClients
AS
SELECT DISTINCT
    c.id AS ClientID,
    c.name AS ClientName,
    c.email,
    c.phone
FROM
    clients c
    JOIN bookings bk ON c.id = bk.booker_id
WHERE
    bk.end_date >= GETDATE();
GO

SELECT * FROM vw_ActiveClients;
GO

-- 5. Представление с информацией о новых клиентах за последний месяц
-- Business value: Помогает отслеживать приток новых клиентов для оценки эффективности маркетинговых мероприятий.
CREATE VIEW vw_NewClientsLastMonth
AS
SELECT
    c.id AS ClientID,
    c.name AS ClientName,
    c.registration_date
FROM
    clients c
WHERE
    c.registration_date >= DATEADD(month, -1, GETDATE());
GO

SELECT * FROM vw_NewClientsLastMonth;
GO

-- Задание 22: Создание 3 обновляемых представлений и применение операторов INSERT, UPDATE, DELETE

-- 1. Обновляемое представление для таблицы clients
-- Business value: Упрощает управление данными клиентов через представление.
CREATE VIEW vw_Clients
AS
SELECT
    id,
    type_id,
    name,
    phone,
    email,
    discount,
    registration_date
FROM
    clients;
GO

INSERT INTO vw_Clients (id, type_id, name, phone, email, discount)
VALUES ('CUST001', 1, 'Иван Иванов', '123-456-7890', 'ivan@example.com', 10.00);
GO

UPDATE vw_Clients
SET phone = '098-765-4321'
WHERE id = 'CUST001';
GO

DELETE FROM vw_Clients
WHERE id = 'CUST001';
GO

-- 2. Обновляемое представление для таблицы building_classes
-- Business value: Упрощает управление классами зданий через представление.
CREATE VIEW vw_BuildingClasses
AS
SELECT
    id,
    name
FROM
    building_classes;
GO

INSERT INTO vw_BuildingClasses (name)
VALUES ('Luxury');
GO

UPDATE vw_BuildingClasses
SET name = 'Premium'
WHERE name = 'Luxury';
GO

DELETE FROM vw_BuildingClasses
WHERE name = 'Premium';
GO

-- 3. Обновляемое представление для таблицы building_services
-- Business value: Позволяет управлять услугами, предоставляемыми зданиями, через представление.
CREATE VIEW vw_BuildingServices_Update
AS
SELECT
    building_id,
    service_id
FROM
    building_services;
GO

INSERT INTO vw_BuildingServices_Update (building_id, service_id)
VALUES (1, 2);
GO

DELETE FROM vw_BuildingServices_Update
WHERE building_id = 1 AND service_id = 2;
GO

-- Задание 23: Применение табличных переменных, временных таблиц и обобщенных табличных выражений

-- 1. Табличная переменная: Список услуг с ценой выше среднего
-- Business value:   для временного хранения и анализа дорогих услуг
DECLARE @HighPriceServices TABLE (
    ServiceID INT,
    ServiceName NVARCHAR(255),
    Price DECIMAL(10,2)
);

INSERT INTO @HighPriceServices (ServiceID, ServiceName, Price)
SELECT
    id,
    name,
    price
FROM
    services
WHERE
    price > (SELECT AVG(price) FROM services);

SELECT * FROM @HighPriceServices;
GO

-- 2. Локальная временная таблица: Клиенты с скидкой выше 5%
-- Business value:анализировать клиентов с высокими скидками для целевых маркетинговых кампаний.
CREATE TABLE #HighDiscountClients (
    ClientID NVARCHAR(255),
    ClientName NVARCHAR(255),
    Discount DECIMAL(5,2)
);

INSERT INTO #HighDiscountClients
SELECT
    id,
    name,
    discount
FROM
    clients
WHERE
    discount > 5.00;

SELECT * FROM #HighDiscountClients;
GO

-- 3. Глобальная временная таблица: Общая информация о зданиях и их классах
-- Business value: Предоставляет доступ к информации о зданиях и их классах для анализа всех сессий.
CREATE TABLE ##BuildingInfo (
    BuildingID INT,
    BuildingClass NVARCHAR(20),
    FloorsNum INT,
    TotalRooms INT
);

INSERT INTO ##BuildingInfo
SELECT
    b.id,
    bc.name,
    b.floors_num,
    b.total_rooms
FROM
    buildings b
    INNER JOIN building_classes bc ON b.class_id = bc.id;

SELECT * FROM ##BuildingInfo;
GO

-- 4. Обобщенное табличное выражение (CTE): Занятость комнат в текущем месяце
-- Business value: Упрощает выборку занятости комнат для текущего месяца для отчетности.
WITH CurrentMonthOccupancy AS (
    SELECT
        r.id AS RoomID,
        COUNT(bk.id) AS BookingsCount
    FROM
        rooms r
        LEFT JOIN bookings bk ON r.id = bk.room_id
            AND MONTH(bk.start_date) = MONTH(GETDATE())
            AND YEAR(bk.start_date) = YEAR(GETDATE())
    GROUP BY
        r.id
)

SELECT
    RoomID,
    BookingsCount
FROM
    CurrentMonthOccupancy;
GO

USE HotelDB;
GO

-- Удаление не обновляемых представлений
DROP VIEW IF EXISTS dbo.vw_BuildingServices;
DROP VIEW IF EXISTS dbo.vw_ClientTypes;
DROP VIEW IF EXISTS dbo.vw_RevenueByBuilding;
DROP VIEW IF EXISTS dbo.vw_ActiveClients;
DROP VIEW IF EXISTS dbo.vw_NewClientsLastMonth;

-- Удаление обновляемых представлений
DROP VIEW IF EXISTS dbo.vw_Clients;
DROP VIEW IF EXISTS dbo.vw_BuildingClasses;
DROP VIEW IF EXISTS dbo.vw_BuildingServices_Update;

-- Удаление временных таблиц
DROP TABLE IF EXISTS #HighDiscountClients;
DROP TABLE IF EXISTS ##BuildingInfo;
GO