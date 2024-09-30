CREATE DATABASE TestDatabas
ON 
( 
    NAME = TestDatabas_data, 
    FILENAME = '/var/opt/mssql/data/TestDatabas_data.mdf',
    SIZE = 10MB,
    MAXSIZE = 100MB,
    FILEGROWTH = 5MB
)
LOG ON
(
    NAME = TestDatabas_log,
    FILENAME = '/var/opt/mssql/data/TestDatabas_log.ldf',
    SIZE = 10MB,
    FILEGROWTH = 10%
);
