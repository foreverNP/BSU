-- 1. Создание таблицы с данными о сотрудниках, отделах, годах и суммах заработка
CREATE TABLE Employees (
    fio NVARCHAR(100),
    department NVARCHAR(100),
    year INT,
    salary DECIMAL(10, 2)
);

-- 2. Ввод данных
INSERT INTO Employees (fio, department, year, salary) VALUES
('Иванов', 'Отдел продаж', 2020, 50000),
('Петров', 'Отдел продаж', 2021, 60000),
('Сидоров', 'Отдел маркетинга', 2020, 55000),
('Кузнецов', 'Отдел маркетинга', 2021, 62000),
('Смирнов', 'Отдел IT', 2020, 70000),
('Федоров', 'Отдел IT', 2021, 72000);
