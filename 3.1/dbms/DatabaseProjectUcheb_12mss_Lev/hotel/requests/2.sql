USE HotelDB;
GO

-- INNER JOIN

-- 1. Список бронирований с подробной информацией о номерах.
-- Business value: Полезно для анализа занятости и доходов по различным категориям номеров.
SELECT bookings.id AS BookingID, rooms.id AS RoomID, rooms.price, bookings.start_date, bookings.end_date
FROM bookings
INNER JOIN rooms ON bookings.room_id = rooms.id;

-- 2. Список услуг, предоставленных в каждом здании.
-- Business value: Помогает оценить разнообразие услуг в каждом здании и их влияние на привлечение клиентов.
SELECT buildings.id AS BuildingID, services.name AS ServiceName
FROM building_services
INNER JOIN buildings ON building_services.building_id = buildings.id
INNER JOIN services ON building_services.service_id = services.id;

-- LEFT JOIN

-- 1. Получение списка всех услуг с информацией о зданиях, где они предлагаются (если применимо).
-- Business value: Помогает анализировать распространенность услуг и возможное улучшение ассортимента.
SELECT services.name AS ServiceName, buildings.id AS BuildingID
FROM services
LEFT JOIN building_services ON services.id = building_services.service_id
LEFT JOIN buildings ON building_services.building_id = buildings.id;

-- 2. Список всех клиентов с информацией о бронированиях и статусе бронирования (если есть).
-- Business value: Полезен для анализа активности клиентов и улучшения качества сервиса.
SELECT clients.name AS ClientName, bookings.id AS BookingID, booking_statuses.name AS Status
FROM clients
LEFT JOIN bookings ON clients.id = bookings.booker_id
LEFT JOIN booking_statuses ON bookings.status_id = booking_statuses.id;

-- RIGHT JOIN

-- 1. Получение списка всех зданий и услуг, предоставляемых в каждом здании.
-- Business value: Позволяет видеть ассортимент услуг в разных зданиях, что упрощает управление инфраструктурой.
SELECT buildings.id AS BuildingID, services.name AS ServiceName
FROM services
RIGHT JOIN building_services ON services.id = building_services.service_id
RIGHT JOIN buildings ON building_services.building_id = buildings.id;

-- 2. Получение всех клиентов с типом клиента (если он присвоен).
-- Business value: Позволяет увидеть категории клиентов, которые нуждаются в уточнении типов для улучшения таргетинга.
SELECT clients.id AS ClientID, clients.name, clients_types.name AS ClientType
FROM clients_types
RIGHT JOIN clients ON clients_types.id = clients.type_id;

-- FULL OUTER JOIN

-- 1. Список всех бронирований и расходов на них (включая незакрепленные расходы и бронирования).
-- Business value: Полезно для комплексного анализа всех финансовых транзакций, связанных с бронированиями.
SELECT bookings.id AS BookingID, expenses.expense_date, expenses.amount
FROM bookings
FULL OUTER JOIN expenses ON bookings.id = expenses.booking_id;

-- 2. Список всех услуг и всех зданий с предоставлением услуг или их отсутствием.
-- Business value: Полный список услуг и их доступность по зданиям, что помогает в планировании.
SELECT services.name AS ServiceName, buildings.id AS BuildingID
FROM services
FULL OUTER JOIN building_services ON services.id = building_services.service_id
FULL OUTER JOIN buildings ON building_services.building_id = buildings.id;

-- CROSS JOIN

-- 1. Получение всех возможных комбинаций клиентов и жалоб.
-- Business value: Полезно для анализа взаимодействий с клиентами по вопросам жалоб.
SELECT clients.name AS ClientName, complaints.description AS Complaint
FROM clients
CROSS JOIN complaints;

-- 2. Комбинации всех клиентов и всех услуг.
-- Business value: Анализ интересов клиентов к различным услугам для дальнейших рекомендаций.
SELECT clients.name AS ClientName, services.name AS ServiceName
FROM clients
CROSS JOIN services;