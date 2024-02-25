CREATE DATABASE HotelDB
ON PRIMARY 
(
    NAME = 'HotelData',                              -- Логическое имя файла данных
    FILENAME = '/var/opt/mssql/data/HotelData.mdf',  -- Расположение файла внутри контейнера
    SIZE = 10MB,                                     -- Начальный размер файла
    MAXSIZE = 50MB,                                  -- Максимальный размер файла
    FILEGROWTH = 5MB                                 -- Приращение файла
)
LOG ON
(
    NAME = 'HotelLog',                              -- Логическое имя журнала транзакций
    FILENAME = '/var/opt/mssql/data/HotelLog.ldf',  -- Расположение файла журнала внутри контейнера
    SIZE = 5MB,                                     -- Начальный размер файла
    MAXSIZE = 25MB,                                 -- Максимальный размер файла
    FILEGROWTH = 5MB                                -- Приращение журнала
);
GO
