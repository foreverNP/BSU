USE HotelDB;
GO

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- 1. Найти номера, которые стоят меньше указанной максимальной цены.
-- Business value: Определение доступных номеров для клиентов с ограниченным бюджетом.
DECLARE @MaxPrice DECIMAL(10,2) = 3000.0;

SELECT id AS Номер, building_id AS Здание, bed_num AS Кровати, floor AS Этаж, price AS Цена
FROM rooms
WHERE price < @MaxPrice;

-- 2. Найти бронирования в определённый период времени.
-- Business value: Получение информации о загруженности отеля.
DECLARE @StartDate DATE = '2024-11-07';
DECLARE @EndDate DATE = '2024-11-24';

SELECT id AS Бронирование, room_id AS Номер, booker_id AS Клиент, start_date AS Начало, end_date AS Конец, status_id AS Статус, booking_date AS ДатаБронирования
FROM bookings
WHERE start_date >= @StartDate AND end_date <= @EndDate;
------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- IF-ELSE
-- 1. проверкa доступности номера.
-- Business value: Предотвращение двойного бронирования.
DECLARE @RoomID INT = 1;
DECLARE @StartDateIF DATE = '2024-11-07';
DECLARE @EndDateIF DATE = '2024-11-24';

IF NOT EXISTS (
    SELECT 1 
    FROM bookings
    WHERE room_id = @RoomID
      AND start_date <= @EndDateIF
      AND end_date >= @StartDateIF
)
BEGIN
    PRINT N'Номер свободен для выбранныз дат.';
END
ELSE
BEGIN
    PRINT N'Номер недоступен для выбранных дат.';
END;

-- WHILE
-- 2.  Обновить цены всех номеров на 3-м этаже, увеличив их на 10%.
-- Business value: Позволяет автоматически корректировать цены для отдельных этажей.
DECLARE @Room_ID INT = (SELECT MIN(id) FROM rooms WHERE floor = 3); 
DECLARE @MaxRoomID INT = (SELECT MAX(id) FROM rooms WHERE floor = 3);

WHILE @Room_ID IS NOT NULL AND @Room_ID <= @MaxRoomID
BEGIN
    UPDATE rooms
    SET price = price * 1.10
    WHERE id = @Room_ID;

    PRINT N'Обновлена цена для номера с ID: ' + CAST(@Room_ID AS NVARCHAR);

    SET @Room_ID = (SELECT MIN(id) FROM rooms WHERE id > @Room_ID AND floor = 3);
END;

-- TRY
-- 3. Обработка деления на ноль при расчете скидки
-- Business value: Обеспечение стабильности при расчете скидок.
DECLARE @TotalAmount DECIMAL(10,2) = 500.00;
DECLARE @DiscountRate DECIMAL(5,2) = 0.00;
DECLARE @DiscountAmount DECIMAL(10,2);

BEGIN TRY
    SET @DiscountAmount = @TotalAmount / @DiscountRate;
    PRINT N'Сумма скидки: ' + CAST(@DiscountAmount AS VARCHAR);
END TRY
BEGIN CATCH
    PRINT N'Ошибка при расчете скидки: ' + ERROR_MESSAGE();
END CATCH;

------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- Написать 2 запроса на применение функций времени и даты

-- 1. Рассчитать продолжительность каждого бронирования в днях
-- Business value: Анализ средней продолжительности бронирования 
SELECT 
    id AS БронированиеID, 
    start_date AS ДатаНачала, 
    end_date AS ДатаОкончания,
    DATEDIFF(day, start_date, end_date) AS Продолжительность_дней
FROM 
    bookings;

-- 2. Выбрать бронирования, сделанные за последние 30 дней
-- Business value: Анализ активности клиентов и трендов бронирований.
SELECT 
    id AS БронированиеID, 
    booker_id AS КлиентID, 
    booking_date AS ДатаБронирования
FROM 
    bookings
WHERE 
    booking_date >= DATEADD(day, -30, GETDATE());

-- Написать 4 запросов по строковым функциям

-- 1. Извлечь первые 3 символа из номера телефона клиента
-- Business value: Создание региональных группировок или идентификаторов.
SELECT 
    id AS КлиентID, 
    phone AS Телефон, 
    LEFT(phone, 3) AS Код_Оператора
FROM 
    clients;

-- 2. Конкатенировать имя и фамилию клиента
-- Business value: Создание полного имени для отчетов и коммуникаций.
SELECT 
    id AS КлиентID, 
    name AS Имя,
    phone AS Телефон,
    email AS Email,
    name + ' (' + phone + ')' AS КонтактнаяИнформация
FROM 
    clients;

-- 3. Удалить пробелы из начала и конца имени клиента
-- Business value: Очистка данных для предотвращения ошибок при обработке.
SELECT 
    id AS КлиентID, 
    name AS ИсходноеИмя,
    TRIM(name) AS ОчищенноеИмя
FROM 
    clients;

-- 4. Извлечь домен из email клиента
-- Business value: Анализ доменов для определения источников клиентов или сегментации.
SELECT 
    id AS КлиентID, 
    email AS Email,
    SUBSTRING(email, CHARINDEX('@', email) + 1, LEN(email)) AS Домен
FROM 
    clients;

-- Написать 4 запросов по числовым функциям
-- 1. Округлить цену номера до ближайшего целого числа
-- Business value: Упрощение ценовой информации для маркетинговых материалов.
SELECT 
    id AS НомерID, 
    price AS ИсходнаяЦена, 
    ROUND(price, 0) AS ОкругленнаяЦена
FROM 
    rooms;

-- 2. Вычислить квадратный корень из цены номера
-- Business value: Проведение статистического анализа цен для выявления трендов.
SELECT 
    id AS НомерID, 
    price AS Цена, 
    SQRT(price) AS КвадратныйКорень
FROM 
    rooms;

-- 3. Получить округленное вверх значение цены номера
-- Business value: Формирование ценовых категорий или уровней.
SELECT 
    id AS НомерID, 
    price AS Цена, 
    CEILING(price) AS ОкругленнаяВверхЦена
FROM 
    rooms;

-- 4. Получить округленное вниз значение цены номера
-- Business value: Анализ минимальных ценовых порогов.
SELECT 
    id AS НомерID, 
    price AS Цена, 
    FLOOR(price) AS ОкругленнаяВнизЦена
FROM 
    rooms;
