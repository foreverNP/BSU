USE HotelDB;
GO

INSERT INTO building_classes (name) VALUES 
('1 star'),
('2 star'),
('3 star'),
('4 star'),
('5 star');
GO

INSERT INTO service_types (name) VALUES 
('Entertainment'),
('Food'),
('Sports'),
('Transport'),
('Health');
GO

INSERT INTO clients_types (name) VALUES 
('Individual'),
('Legal Entity');
GO

INSERT INTO booking_statuses (name) VALUES 
('Pending'),
('Confirmed'),
('Cancelled'),
('Completed');
GO

INSERT INTO buildings (class_id, floors_num, total_rooms) VALUES 
(1, 5, 50),
(2, 10, 100),
(3, 15, 150),
(4, 20, 200),
(5, 25, 250),
(1, 6, 60),
(2, 12, 120),
(3, 18, 180),
(4, 22, 220),
(5, 28, 280);
GO

INSERT INTO clients (id, type_id, name, phone, email, discount, registration_date) VALUES 
('JUR001', 2, 'Romashka LLC', '+79001234567', 'contact@romashka.ru', 5.00, '2024-01-15'),
('JUR002', 2, 'Vasilek JSC', '+79002345678', 'info@vasilek.ru', 10.00, '2024-02-20'),
('JUR003', 2, 'Petrov SP', '+79003456789', 'petrov@business.ru', 0.00, '2024-03-10'),
('JUR004', 2, 'Liliya LLC', '+79004567890', 'contact@lilia.ru', 7.50, '2024-04-05'),
('JUR005', 2, 'Tulpan JSC', '+79005678901', 'info@tulpan.ru', 12.00, '2024-05-12'),
('JUR006', 2, 'Sidorov SP', '+79006789012', 'sidorov@business.ru', 0.00, '2024-06-18'),
('JUR007', 2, 'Roza LLC', '+79007890123', 'contact@roza.ru', 8.00, '2024-07-22'),
('JUR008', 2, 'Gerbera JSC', '+79008901234', 'info@gerbera.ru', 15.00, '2024-08-30'),
('JUR009', 2, 'Ivanov SP', '+79009012345', 'ivanov@business.ru', 0.00, '2024-09-05'),
('JUR010', 2, 'Chrysanthemum LLC', '+79000123456', 'contact@khrizantema.ru', 9.50, '2024-10-10');
GO

INSERT INTO clients (id, type_id, name, phone, email, discount, registration_date) VALUES 
('AB1234567', 1, 'Ivan Ivanov', '+79001111111', 'ivan.ivanov@example.com', DEFAULT, '2024-01-20'),
('CD7654321', 1, 'Petr Petrov', '+79002222222', 'petr.petrov@example.com', DEFAULT, '2024-02-15'),
('EF2345678', 1, 'Anna Smirnova', '+79003333333', 'anna.smirnova@example.com', DEFAULT, '2024-03-10'),
('GH8765432', 1, 'Olga Sidorova', '+79004444444', 'olga.sidorova@example.com', DEFAULT, '2024-04-05'),
('IJ3456789', 1, 'Maria Kuznetsova', '+79005555555', 'maria.kuznetsova@example.com', DEFAULT, '2024-05-01'),
('KL9876543', 1, 'Dmitry Volkov', '+79006666666', 'dmitry.volkov@example.com', DEFAULT, '2024-06-15'),
('MN4567890', 1, 'Svetlana Pavlova', '+79007777777', 'svetlana.pavlova@example.com', DEFAULT, '2024-07-10'),
('OP0987654', 1, 'Alexey Morozov', '+79008888888', 'alexey.morozov@example.com', DEFAULT, '2024-08-25'),
('QR5678901', 1, 'Ekaterina Zakharova', '+79009999999', 'ekaterina.zakharova@example.com', DEFAULT, '2024-09-18'),
('ST6789012', 1, 'Sergey Vorobyov', '+79000000000', 'sergey.vorobyev@example.com', DEFAULT, '2024-10-05');
GO

INSERT INTO rooms (building_id, bed_num, floor, price) VALUES 
(1, 1, 1, 1000.00),
(1, 2, 2, 1500.00),
(1, 3, 3, 2000.00),
(1, 4, 4, 2500.00),
(1, 5, 5, 3000.00),
(2, 1, 1, 1100.00),
(2, 2, 2, 1600.00),
(2, 3, 3, 2100.00),
(2, 4, 4, 2600.00),
(2, 5, 5, 3100.00),
(3, 1, 1, 1200.00),
(3, 2, 2, 1700.00),
(3, 3, 3, 2200.00),
(3, 4, 4, 2700.00),
(3, 5, 5, 3200.00),
(4, 1, 1, 1300.00),
(4, 2, 2, 1800.00),
(4, 3, 3, 2300.00),
(4, 4, 4, 2800.00),
(4, 5, 5, 3300.00),
(5, 1, 1, 1400.00),
(5, 2, 2, 1900.00),
(5, 3, 3, 2400.00),
(5, 4, 4, 2900.00),
(5, 5, 5, 3400.00),
(6, 1, 1, 1050.00),
(6, 2, 2, 1550.00),
(6, 3, 3, 2050.00),
(6, 4, 4, 2550.00),
(6, 5, 5, 3050.00),
(7, 1, 1, 1150.00),
(7, 2, 2, 1650.00),
(7, 3, 3, 2150.00),
(7, 4, 4, 2650.00),
(7, 5, 5, 3150.00),
(8, 1, 1, 1250.00),
(8, 2, 2, 1750.00),
(8, 3, 3, 2250.00),
(8, 4, 4, 2750.00),
(8, 5, 5, 3250.00),
(9, 1, 1, 1350.00),
(9, 2, 2, 1850.00),
(9, 3, 3, 2350.00),
(9, 4, 4, 2850.00),
(9, 5, 5, 3350.00),
(10, 1, 1, 1450.00),
(10, 2, 2, 1950.00),
(10, 3, 3, 2450.00),
(10, 4, 4, 2950.00),
(10, 5, 5, 3450.00);
GO

INSERT INTO services (type_id, name, price) VALUES 
(1, 'Cinema Session', 500.00),
(1, 'Concert', 1500.00),
(1, 'Theatrical Performance', 2000.00),
(1, 'Exhibition', 800.00),
(1, 'Master Class', 1200.00),
(2, 'Breakfast', 300.00),
(2, 'Lunch', 700.00),
(2, 'Dinner', 900.00),
(2, 'Buffet', 200.00),
(2, 'Special Menu', 1000.00),
(3, 'Gym', 400.00),
(3, 'Yoga', 350.00),
(3, 'Swimming', 500.00),
(3, 'Fitness Class', 450.00),
(3, 'Basketball', 600.00),
(4, 'Car Rental', 2000.00),
(4, 'Airport Transfer', 1500.00),
(4, 'Taxi', 500.00),
(4, 'Bicycle Rental', 800.00),
(4, 'Boat Rental', 2500.00),
(5, 'Massage', 1200.00),
(5, 'Spa Procedures', 2500.00),
(5, 'Medical Examination', 1800.00),
(5, 'Doctor Consultation', 1000.00),
(5, 'Physiotherapy', 1600.00);
GO

INSERT INTO building_services (building_id, service_id) VALUES 
(1, 1), (1, 6), (1, 11),
(2, 2), (2, 7), (2, 12),
(3, 3), (3, 8), (3, 13),
(4, 4), (4, 9), (4, 14),
(5, 5), (5, 10), (5, 15),
(6, 16), (6, 17), (6, 18),
(7, 19), (7, 20), (7, 21),
(8, 22), (8, 23), (8, 24),
(9, 25), (9, 1), (9, 2),
(10, 3), (10, 4), (10, 5);
GO

INSERT INTO bookings (room_id, booker_id, start_date, end_date, status_id) VALUES 
(1, 'JUR001', '2024-11-01', '2024-11-05', 2),
(2, 'JUR002', '2024-11-03', '2024-11-07', 1),
(3, 'JUR003', '2024-11-05', '2024-11-10', 3),
(4, 'JUR004', '2024-11-07', '2024-11-12', 2),
(5, 'JUR005', '2024-11-09', '2024-11-14', 4),
(6, 'JUR006', '2024-11-11', '2024-11-16', 2),
(7, 'JUR007', '2024-11-13', '2024-11-18', 1),
(8, 'JUR008', '2024-11-15', '2024-11-20', 3),
(9, 'JUR009', '2024-11-17', '2024-11-22', 2),
(10, 'JUR010', '2024-11-19', '2024-11-24', 4),
(11, 'JUR001', '2024-11-21', '2024-11-26', 2),
(12, 'JUR002', '2024-11-23', '2024-11-28', 1),
(13, 'JUR003', '2024-11-25', '2024-11-30', 3),
(14, 'JUR004', '2024-11-27', '2024-12-02', 2),
(15, 'JUR005', '2024-11-29', '2024-12-04', 4),
(16, 'JUR006', '2024-12-01', '2024-12-06', 2),
(17, 'JUR007', '2024-12-03', '2024-12-08', 1),
(18, 'JUR008', '2024-12-05', '2024-12-10', 3),
(19, 'JUR009', '2024-12-07', '2024-12-12', 2),
(20, 'JUR010', '2024-12-09', '2024-12-14', 4);
GO

INSERT INTO bookings (room_id, booker_id, start_date, end_date, status_id) VALUES 
(21, 'AB1234567', '2024-11-01', '2024-11-03', 2),
(22, 'CD7654321', '2024-11-04', '2024-11-07', 1),
(23, 'EF2345678', '2024-11-08', '2024-11-11', 2),
(24, 'GH8765432', '2024-11-12', '2024-11-15', 4),
(25, 'IJ3456789', '2024-11-16', '2024-11-19', 2),
(26, 'KL9876543', '2024-11-20', '2024-11-23', 1),
(27, 'MN4567890', '2024-11-24', '2024-11-27', 3),
(28, 'OP0987654', '2024-11-28', '2024-12-01', 2),
(29, 'QR5678901', '2024-12-02', '2024-12-05', 4),
(30, 'ST6789012', '2024-12-06', '2024-12-09', 2);
GO

INSERT INTO expenses (booking_id, service_id, amount) VALUES 
(1, 1, 500.00),
(1, 6, 300.00),
(1, 11, 400.00),
(2, 2, 1500.00),
(2, 7, 700.00),
(2, 12, 350.00),
(3, 3, 2000.00),
(3, 8, 800.00),
(3, 13, 500.00),
(4, 4, 800.00),
(4, 9, 200.00),
(4, 14, 450.00),
(5, 5, 1200.00),
(5, 10, 1000.00),
(5, 15, 600.00),
(6, 16, 2000.00),
(6, 17, 1500.00),
(6, 18, 500.00),
(7, 19, 800.00),
(7, 20, 2500.00),
(8, 21, 1200.00),
(8, 22, 2500.00),
(8, 23, 1800.00),
(9, 24, 1000.00),
(9, 25, 1600.00),
(10, 1, 500.00),
(10, 2, 1500.00),
(10, 3, 2000.00),
(11, 4, 800.00),
(11, 5, 1200.00),
(11, 6, 300.00),
(12, 7, 700.00),
(12, 8, 800.00),
(12, 9, 200.00),
(13, 10, 1000.00),
(13, 11, 400.00),
(13, 12, 350.00),
(14, 13, 500.00),
(14, 14, 450.00),
(14, 15, 600.00),
(15, 16, 2000.00),
(15, 17, 1500.00),
(15, 18, 500.00),
(16, 19, 800.00),
(16, 20, 2500.00),
(16, 21, 1200.00),
(17, 22, 2500.00),
(17, 23, 1800.00),
(17, 24, 1000.00),
(18, 25, 1600.00),
(18, 1, 500.00),
(18, 2, 1500.00),
(19, 3, 2000.00),
(19, 4, 800.00),
(19, 5, 1200.00),
(20, 6, 300.00),
(20, 7, 700.00),
(20, 8, 800.00);
GO

INSERT INTO expenses (booking_id, service_id, amount) VALUES 
(21, 1, 500.00), 
(21, 6, 300.00),
(22, 2, 1500.00), 
(22, 7, 700.00),
(23, 3, 2000.00),
(23, 8, 800.00),
(24, 4, 800.00),  
(24, 9, 200.00),
(25, 5, 1200.00),
(25, 10, 1000.00),
(26, 16, 2000.00), 
(26, 17, 1500.00),
(26, 18, 500.00),
(27, 19, 800.00), 
(27, 20, 2500.00),
(28, 21, 1200.00), 
(28, 22, 2500.00),
(28, 23, 1800.00),
(29, 24, 1000.00),
(29, 25, 1600.00),
(30, 1, 500.00),
(30, 2, 1500.00),
(30, 3, 2000.00);
GO

INSERT INTO complaints (booking_id, description) VALUES 
(1, 'The room was dirty on arrival.'),
(2, 'Noisy neighbor disturbed sleep.'),
(3, 'Broken air conditioner.'),
(4, 'No hot water.'),
(5, 'Late checkout was not allowed.'),
(6, 'Poor internet connection.'),
(7, 'Unprofessional service.'),
(8, 'Musty smell in the room.'),
(9, 'Not enough towels.'),
(10, 'Unsatisfactory furniture condition.'),
(11, 'Parking problems.'),
(12, 'Need for additional repairs.'),
(13, 'Room cleaning was late.'),
(14, 'Inconvenient bed position.'),
(15, 'Lighting problems.'),
(16, 'Not enough power sockets.'),
(17, 'Poor soundproofing.'),
(18, 'Inconvenient shower.'),
(19, 'Heating problems.'),
(20, 'No bathrobes in the bathroom.');
GO
