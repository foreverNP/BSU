-- 13. Написать по 2 запроса на пересечение, разность и объединение таблиц.

-- ***** Запросы на пересечение (INTERSECT) *****

-- Запрос 1: Найти клиентов, которые сделали бронирования и подали жалобы.
-- Business value: позволяет выявить клиентов, которые были недовольны своим пребыванием, для улучшения сервиса.
SELECT c.id, c.name, c.email
FROM clients c
JOIN bookings b ON c.id = b.booker_id

INTERSECT

SELECT c.id, c.name, c.email
FROM clients c
JOIN bookings b ON c.id = b.booker_id
JOIN complaints comp ON b.id = comp.booking_id;

-- Запрос 2: Найти номера, которые в данный момент забронированы и на которые поступали жалобы.
-- Business value: помогает выявить проблемные номера, требующие внимания.
SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r
JOIN bookings b ON r.id = b.room_id
WHERE GETDATE() BETWEEN b.start_date AND b.end_date

INTERSECT

SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r
JOIN bookings b ON r.id = b.room_id
JOIN complaints c ON b.id = c.booking_id;

-- Запрос 3: Найти клиентов, которые забронировали номера и приобрели дополнительные услуги.
-- Business value: выявляет ценных клиентов для предложения им специальных акций.
SELECT c.id, c.name, c.email
FROM clients c
JOIN bookings b ON c.id = b.booker_id

INTERSECT

SELECT c.id, c.name, c.email
FROM clients c
JOIN bookings b ON c.id = b.booker_id
JOIN expenses e ON b.id = e.booking_id;

-- ***** Запросы на разность (EXCEPT) *****

-- Запрос 1: Найти номера, на которые никогда не поступали жалобы.
-- Business value: идентификация номеров высокого качества.
SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r

EXCEPT

SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r
JOIN bookings b ON r.id = b.room_id
JOIN complaints c ON b.id = c.booking_id;

-- Запрос 2: Найти здания, которые не предлагают услугу с id = X. например, 'Theatrical Performance'
-- Business value: расширить предложение услуг в этих зданиях.
SELECT b.id, b.class_id, b.floors_num, b.total_rooms
FROM buildings b

EXCEPT

SELECT b.id, b.class_id, b.floors_num, b.total_rooms
FROM buildings b
JOIN building_services bs ON b.id = bs.building_id
WHERE bs.service_id = 3;

-- ***** Запросы на объединение (UNION) *****

-- Запрос 1: Найти номера, которые либо в данный момент забронированы, либо на них поступали жалобы.
-- Business value: мониторинг номеров, требующих внимания.
SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r
JOIN bookings b ON r.id = b.room_id
WHERE GETDATE() BETWEEN b.start_date AND b.end_date

UNION

SELECT r.id, r.building_id, r.floor, r.price
FROM rooms r
JOIN bookings b ON r.id = b.room_id
JOIN complaints c ON b.id = c.booking_id;

-- Запрос 2: Найти бронирования со статусом "Pending" или "Confirmed".
-- Business value: управление текущими и предстоящими бронированиями.
SELECT b.id, b.room_id, b.booker_id, b.start_date, b.end_date, b.status_id, b.booking_date
FROM bookings b
JOIN booking_statuses bs ON b.status_id = bs.id
WHERE bs.name = 'Pending'

UNION

SELECT b.id, b.room_id, b.booker_id, b.start_date, b.end_date, b.status_id, b.booking_date
FROM bookings b
JOIN booking_statuses bs ON b.status_id = bs.id
WHERE bs.name = 'Confirmed';

-- 14. Написать 4 запроса с использованием подзапросов, используя операторы сравнения, IN, ANY|SOME и ALL, предикат EXISTS.

-- Запрос 1: Найти клиентов со скидкой выше средней.
-- Business value: идентификация клиентов с особыми условиями.
SELECT c.id, c.name, c.discount
FROM clients c
WHERE c.discount > (SELECT AVG(discount) FROM clients);

-- Запрос 2: Найти самые дорогие номера в каждом здании.
-- Business value: анализ ценовой политики по зданиям.
SELECT r1.id, r1.building_id, r1.price
FROM rooms r1
WHERE r1.price >= ALL (
    SELECT r2.price FROM rooms r2
    WHERE r2.building_id = r1.building_id
);

-- Запрос 3: Найти клиентов, которые бронировали номера с количеством кроватей выше среднего.
-- Business value: разделение клиентов по предпочтениям.
SELECT c.id, c.name
FROM clients c
JOIN bookings b ON c.id = b.booker_id
JOIN rooms r ON b.room_id = r.id
WHERE r.bed_num > (SELECT AVG(bed_num) FROM rooms);

-- Запрос 4: Найти здания, предлагающие все типы услуг.
-- Business value: 
SELECT b.id, b.class_id
FROM buildings b
WHERE NOT EXISTS (
    SELECT * FROM service_types st
    WHERE NOT EXISTS (
        SELECT * FROM building_services bs
        JOIN services s ON bs.service_id = s.id
        WHERE bs.building_id = b.id AND s.type_id = st.id
    )
);

-- Запрос 5: Найти клиентов, которые не делали бронирований за последние 6 месяцев.
-- Business value: целевая аудитория для акций по возврату клиентов.
SELECT c.id, c.name
FROM clients c
WHERE NOT EXISTS (
    SELECT * FROM bookings b
    WHERE b.booker_id = c.id
      AND b.booking_date >= DATEADD(month, -6, GETDATE())
);

-- Запрос 6: Найти клиентов, которые зарегистрированы в определенные дни недели (например, только по понедельникам, средам и пятницам).
-- Business value: для маркетинговых кампаний и акций.
SELECT c.id, c.name, c.email, c.registration_date
FROM clients c
WHERE DATENAME(WEEKDAY, c.registration_date) IN ('Monday', 'Wednesday', 'Friday');