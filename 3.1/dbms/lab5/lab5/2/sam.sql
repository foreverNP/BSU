-- 4. Средний балл учеников по школам в столбцы
SELECT *
FROM (
    SELECT predmet, ush, AVG(ball) AS AvgBall
    FROM Students
    GROUP BY predmet, ush
) AS SourceTable
PIVOT (
    AVG(AvgBall)
    FOR ush IN ([Лицей], [Гимназия])
) AS PivotTable;

-- 5. Средний балл учеников по школам в столбцы и по предметам в строки
SELECT *
FROM (
    SELECT predmet, ush, AVG(ball) AS AvgBall
    FROM Students
    GROUP BY predmet, ush
) AS SourceTable
PIVOT (
    AVG(AvgBall)
    FOR ush IN ([Лицей], [Гимназия])
) AS PivotTable;

-- 6. Названия предметов, фамилии учеников и школы в один столбец
SELECT CONCAT(predmet, ' - ', fio, ' - ', ush) AS CombinedColumn
FROM Students;

-- 7.1 Создать таблицу для премий сотрудников
CREATE TABLE test_table_pivot (
    fio VARCHAR(50) NULL,
    god INT NULL,
    summa FLOAT NULL
);

-- 7.2 Ввод данных в таблицу test_table_pivot
INSERT INTO test_table_pivot (fio, god, summa) VALUES
('Иванов', 2020, 5000),
('Петров', 2020, 4500),
('Иванов', 2021, 5200),
('Петров', 2021, 4700),
('Иванов', 2022, 5300),
('Петров', 2022, 4800);

-- 7.3 Премии по годам с использованием PIVOT
SELECT fio, [2020], [2021], [2022]
FROM (
    SELECT fio, god, summa
    FROM test_table_pivot
) AS SourceTable
PIVOT (
    SUM(summa)
    FOR god IN ([2020], [2021], [2022])
) AS PivotTable;
