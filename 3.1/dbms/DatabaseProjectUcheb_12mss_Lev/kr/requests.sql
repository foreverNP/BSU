-- CREATE
CREATE TABLE Customers_kr
(
    id INT PRIMARY KEY,
    lastName NVARCHAR(50),
    firstName NVARCHAR(50) NOT NULL,
    middleName NVARCHAR(50),
    addres NVARCHAR(20) NOT NULL,
    phoneNumber NVARCHAR(100) NOT NULL
);

CREATE TABLE Marshruts_kr
(
    id INT PRIMARY KEY,
    country NVARCHAR(50) NOT NULL,
    climat NVARCHAR(50) NOT NULL,
    dlit_days INT NOT NULL,
    hotel NVARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE Putevki_kr
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    marshrut_id INT,
    client_id INT,
    num INT NOT NULL,
    outgo_date DATE DEFAULT GETDATE() NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.0 NOT NULL,

    CONSTRAINT FK_Customer FOREIGN KEY (client_id) REFERENCES Customers_kr(id),
    CONSTRAINT FK_Marsh FOREIGN KEY (marshrut_id) REFERENCES Marshruts_kr(id)
);

-- Insert
INSERT INTO Customers_kr
    (id, lastName, firstName, middleName, addres, phoneNumber)
VALUES
    (1, 'Aastname1', 'firstname1', 'middleName1', 'address1', '+1231221431241'),
    (2, 'Bastname2', 'firstname2', 'middleName2', 'address2', '+1231221431241'),
    (3, 'Sastname3', 'firstname3', 'middleName3', 'address3', '+1231221431241'),
    (4, 'Xastname4', 'firstname4', 'middleName4', 'address4', '+1231221431241'),
    (5, 'Pastname5', 'firstname5', 'middleName5', 'address5', '+1231221431241');

INSERT INTO Marshruts_kr
    (id, country, climat, dlit_days, hotel, price)
VALUES
    (1, 'country1', 'climat1', 2, 'hotel1', 1123.32),
    (2, 'country1', 'climat2', 1, 'hotel2', 3123.32),
    (3, 'country2', 'climat3', 3, 'hotel3', 423.22),
    (4, 'country1', 'climat4', 1, 'hotel4', 1123.02),
    (5, 'country3', 'climat5', 12, 'hotel5', 321.12);

INSERT INTO Putevki_kr
    (marshrut_id, client_id, num, outgo_date, discount)
VALUES
    (1, 5, 1, '2024-10-15', 0.12),
    (1, 2, 2, '2024-10-13', 0.02),
    (2, 4, 3, '2024-03-15', 0.11),
    (3, 3, 2, '2024-03-15', 0.0),
    (4, 2, 2, '2024-02-15', 0.0),
    (5, 1, 1, '2024-01-15', 0.0);

-- SELECT
-- a. Запрос «Исходные данные» – выдает все данные из всех таблиц,
-- представляя их в удобной для восприятия форме при этом исключить
-- повторение первичных ключевых полей из главных таблиц; 
SELECT c.id AS clientID, c.firstName, c.lastName, c.middleName, c.phoneNumber, c.addres, p.id AS PutevkaID, p.num AS TicketNum, p.outgo_date, m.id AS MarsrutID, 
m.country, m.climat,m.dlit_days,m.price,m.hotel
FROM Customers_kr c JOIN Putevki_kr p on c.id = p.client_id JOIN Marshruts_kr m ON p.marshrut_id = m.id;  

-- b. Запрос "Клиенты и маршруты ", который выдает список клиентов, выбравших заданный маршрут(например мершрут 1)
SELECT *
FROM Customers_kr JOIN Putevki_kr ON Customers_kr.id = Putevki_kr.client_id
WHERE marshrut_id = 1;

-- c. Запрос "Алфавитный список", который выдает список фамилий в указанном буквенном диапазоне.
-- Начальную и конечную буквы диапазона выбрать самостоятельно(l-a). Отсортировать список в алфавитном порядке. 
SELECT lastName
FROM Customers_kr
WHERE lastName LIKE '[A-B]%'
ORDER BY lastName;

-- d. Запрос "Список с условием": все маршруты для заданной страны.()
SELECT *
FROM Marshruts_kr
WHERE country = 'country1';

-- e. Запрос " Путевки и клиенты", который выдает список путевок, которые были куплены данным клиентом(client_id = 2)
SELECT c.id AS ClientID, c.lastName, c.phoneNumber AS clientPhone, p.id AS PutevkaId, p.num AS TicketNum, p.outgo_date
FROM Putevki_kr p
    JOIN Customers_kr c on p.client_id = c.id
WHERE p.client_id = 2;

-- f. Запрос с расчетами – найти самый длительный маршрут.
SELECT *
FROM Marshruts_kr
WHERE dlit_days = (SELECT MAX(dlit_days)
FROM Marshruts_kr);

-- р. Запрос с групповой операцией – найти среднюю стоимость маршрута.
SELECT AVG(price) AS averagePRice
FROM Marshruts_kr;