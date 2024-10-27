USE HotelDB;
GO

-- 3.1. Вывод всех данных из таблиц (например: клиенты, бронирования, комнаты)
SELECT * FROM clients;
SELECT * FROM bookings;
SELECT * FROM rooms;
GO

-- 3.2. Вывод данных клиентов с сортировкой по имени в алфавитном порядке
SELECT id, name, email
FROM clients
ORDER BY name ASC;
GO

-- 3.3. Вывод бронирований с сортировкой  по дате начала (по убыванию) и имени клиента
SELECT name, registration_date
FROM clients 
ORDER BY name DESC, registration_date DESC;
GO

-- 3.4. Вывод уникальных комнат и расчёт цены за одну кровать (вычисляемое поле price_per_bed)
SELECT DISTINCT
    (price / bed_num) AS price_per_bed
FROM rooms;
GO

-- 3.5. Вывод 30% строк из таблицы бронирований
SELECT TOP 30 PERCENT *
FROM bookings;
GO

-- 3.6. Вывод первых 5 строк из таблицы бронирований
SELECT TOP 5 *
FROM bookings;
GO

-- 3.7. Вывод строк с 4 по 7, отсортированных по дате бронирования в обратном порядке
SELECT *
FROM bookings
ORDER BY booking_date DESC
OFFSET 3 ROWS FETCH NEXT 4 ROWS ONLY;
GO

-- 3.8. выражение для сортировки (комнаты по цене на одно спальное место)
SELECT *
FROM rooms
ORDER BY (price / bed_num) DESC;
GO

-- -- 3.9. Запрос на выборку с добавлением SELECT INTO (создание новой таблицы ConfirmedBookings)
-- SELECT *
-- INTO ConfirmedBookings
-- FROM bookings
-- WHERE status_id = 2;
-- GO

-- 4.1. Фильтрация данных: бронирования с продолжительностью больше 4 дней
SELECT *
FROM bookings
WHERE DATEDIFF(day, start_date, end_date) > 4;
GO

-- 4.2. Фильтрация данных: клиенты, зарегистрированные после 1 июня 2024 года
SELECT *
FROM clients
WHERE registration_date > '2024-06-01';
GO

-- 4.3. Фильтрация данных: комнаты с ценой выше 3000 и более чем 3 кроватями
SELECT *
FROM rooms
WHERE price > 3000 AND bed_num > 3;
GO

-- 4.4. Фильтрация данных: услуги с ценой между 1000 и 2000
SELECT *
FROM services
WHERE price BETWEEN 1000 AND 2000;
GO

-- 4.5. Фильтрация данных: клиенты с именем, начинающимся на букву 'А'
SELECT *
FROM clients
WHERE name LIKE 'A%';
GO

-- 4.6. Фильтрация данных: бронирования со статусом "Подтверждено" или "Завершено"
SELECT *
FROM bookings
WHERE status_id IN (2, 4);
GO

-- 4.7. Фильтрация данных: комнаты на 5 этаже или стоимостью менее 2000
SELECT *
FROM rooms
WHERE floor = 5 OR price < 2000;
GO

-- -- 5. Переименование таблицы complaints на customer_complaints
-- EXEC sp_rename 'complaints', 'customer_complaints';
-- GO

-- -- 6. Добавление двух новых записей в таблицы клиентов и комнат
-- INSERT INTO clients (id, type_id, name, phone, email, discount, registration_date) 
-- VALUES ('JUR011', 2, 'Magnolia LLC', '+79001239876', 'info@magnolia.ru', 8.00, '2024-10-12'),
--        ('JUR012', 2, 'Tulip Ltd', '+79001234599', 'contact@tulip.com', 5.50, '2024-10-15');

-- INSERT INTO rooms (building_id, bed_num, floor, price) 
-- VALUES (1, 2, 3, 1550.00), (2, 4, 2, 2050.00);
-- GO

-- -- 7. Изменение структуры таблицы (добавление нового столбца и ограничения)
-- ALTER TABLE clients
-- ADD loyalty_points INT DEFAULT 0;

-- ALTER TABLE clients
-- ADD CONSTRAINT CK_LoyaltyPoints CHECK (loyalty_points >= 0);
-- GO

-- 8.1. Агрегатные функции: сумма всех расходов на услуги
SELECT SUM(amount) AS TotalExpenses
FROM expenses;
GO

-- 8.2. Агрегатные функции: максимальная цена за услугу
SELECT MAX(price) AS MaxServicePrice
FROM services;
GO

-- 8.3. Агрегатные функции: минимальное количество кроватей в комнате
SELECT MIN(bed_num) AS MinBeds
FROM rooms;
GO

-- 8.4. Агрегатные функции: средний доход (через расходы на услуги)
SELECT AVG(amount) AS AvgBookingExpense
FROM expenses;
GO

-- 8.5. Агрегатные функции: подсчет количества бронирований
SELECT COUNT(*) AS BookingCount
FROM bookings;
GO

-- 9.1. Группировка по количеству кроватей и подсчет количества комнат для каждой группы
SELECT bed_num, COUNT(*) AS RoomCount
FROM rooms
GROUP BY bed_num;
GO

-- 9.2. Группировка данных: средняя стоимость комнат по этажам
SELECT floor, AVG(price) AS AvgRoomPrice
FROM rooms
GROUP BY floor;
GO

-- 10.1. Фильтрация групп по статусу бронирований, где количество бронирований больше 5
SELECT status_id, COUNT(*) AS BookingCount
FROM bookings
GROUP BY status_id
HAVING COUNT(*) > 7;
GO

-- 10.2. Фильтрация групп с использованием HAVING: этажи с средней ценой комнат выше 2500
SELECT floor, AVG(price) AS AvgRoomPrice
FROM rooms
GROUP BY floor
HAVING AVG(price) > 2500;
GO

-- 11.1. Использование ROLLUP для суммирования расходов по бронированиям и услугам
SELECT booking_id, service_id, SUM(amount) AS TotalExpense
FROM expenses
GROUP BY ROLLUP(booking_id, service_id);
GO

-- 11.2. Использование CUBE для вычислений по всем возможным комбинациям бронирований и услуг
SELECT booking_id, service_id, SUM(amount) AS TotalExpense
FROM expenses
GROUP BY CUBE(booking_id, service_id);
GO

-- 11.3. Использование GROUPING SETS для выборочной группировки по бронированиям и услугам
SELECT booking_id, service_id, SUM(amount) AS TotalExpense
FROM expenses
GROUP BY GROUPING SETS ((booking_id), (service_id), ());
GO

-- 11.4. Использование OVER для подсчета общего количества бронирований
SELECT COUNT(*) OVER() AS TotalBookings
FROM bookings;
GO

-- 12.1. Количество бронирований по статусу для комнаты
SELECT *
FROM (
    SELECT r.id AS room_id, bs.name AS status_name
    FROM rooms r
    JOIN bookings b ON r.id = b.room_id
    JOIN booking_statuses bs ON b.status_id = bs.id
) AS BookingData
PIVOT (
    COUNT(status_name)
    FOR status_name IN ([confirmed], [cancelled], [pending])
) AS PivotTable;
GO

-- 12.2. Количество этажей и комнат для building
SELECT building_id, attribute, value
FROM (
    SELECT b.id AS building_id, b.floors_num, b.total_rooms
    FROM buildings b
) AS BuildingData
UNPIVOT (
    value FOR attribute IN (floors_num, total_rooms)
) AS UnpivotedBuildingData;
