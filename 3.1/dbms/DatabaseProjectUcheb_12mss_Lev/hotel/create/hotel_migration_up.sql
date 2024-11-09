USE HotelDB;
GO

-- Создание таблицы building_classes
CREATE TABLE building_classes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(20) UNIQUE NOT NULL
);

-- Создание таблицы buildings
CREATE TABLE buildings (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    class_id INT,
    floors_num INT NOT NULL,
    total_rooms INT NOT NULL,

    CONSTRAINT FK_Building_Class FOREIGN KEY (class_id) REFERENCES building_classes(id)
);

-- Создание таблицы rooms
CREATE TABLE rooms (
    id INT IDENTITY(1,1) PRIMARY KEY,
    building_id INT,
    bed_num INT,
    floor INT NOT NULL, 
    price DECIMAL(10,2) NOT NULL,

    CONSTRAINT CK_RoomType CHECK (bed_num BETWEEN 1 AND 5),
    CONSTRAINT FK_Room_Building FOREIGN KEY (building_id) REFERENCES buildings(id)
);

-- Создание таблицы clients_types
CREATE TABLE clients_types (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    name NVARCHAR(20) UNIQUE
);

-- Создание таблицы clients
CREATE TABLE clients (
    id NVARCHAR(255) PRIMARY KEY,
    type_id INT, 
    name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.0 NOT NULL,
    registration_date DATE DEFAULT GETDATE() NOT NULL,

    CONSTRAINT FK_Client_Type FOREIGN KEY (type_id) REFERENCES clients_types(id)
);

-- Создание таблицы booking_statuses
CREATE TABLE booking_statuses (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    name NVARCHAR(20) UNIQUE
);

-- Создание таблицы bookings
CREATE TABLE bookings (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    room_id INT,
    booker_id NVARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status_id INT,
    booking_date DATE DEFAULT GETDATE() NOT NULL,

    CONSTRAINT FK_Booking_Room FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT FK_Booking_Booker FOREIGN KEY (booker_id) REFERENCES clients(id),
    CONSTRAINT FK_Booking_Status FOREIGN KEY (status_id) REFERENCES booking_statuses(id)
);

-- Создание таблицы service_types
CREATE TABLE service_types (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    name NVARCHAR(20) UNIQUE
);

-- Создание таблицы services
CREATE TABLE services (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    type_id INT,
    name NVARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,

    CONSTRAINT FK_Service_Type FOREIGN KEY (type_id) REFERENCES service_types(id) -- Связь с таблицей service_types
);

-- Промежуточная таблица для связей building_services (услуги, предоставляемые зданиям)
CREATE TABLE building_services (
    building_id INT,
    service_id INT,

    PRIMARY KEY (building_id, service_id),
    CONSTRAINT FK_BuildingService_Building FOREIGN KEY (building_id) REFERENCES buildings(id),
    CONSTRAINT FK_BuildingService_Service FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Таблица для расходов на услуги (expenses)
CREATE TABLE expenses (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    booking_id INT,
    service_id INT,
    amount DECIMAL(10,2) NOT NULL,
    expense_date DATE DEFAULT GETDATE() NOT NULL,

    CONSTRAINT FK_Expense_Booking FOREIGN KEY (booking_id) REFERENCES bookings(id),  -- Связь с таблицей bookings
    CONSTRAINT FK_Expense_Service FOREIGN KEY (service_id) REFERENCES services(id)   -- Связь с таблицей services
);

-- Таблица для жалоб (complaints)
CREATE TABLE complaints (
    id INT IDENTITY(1,1) PRIMARY KEY,           
    booking_id INT,
    description NVARCHAR(MAX),

    CONSTRAINT FK_Complaint_Booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);
