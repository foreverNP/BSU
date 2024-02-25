-- 1.1. Количество учеников по предметам по каждому учреждению
SELECT predmet, ush, COUNT(fio) AS kol
FROM Students
GROUP BY predmet, ush;
 
-- 1.2. Количество учеников по предметам по каждому учреждению с промежуточными итогами
SELECT ush, predmet, COUNT(fio) AS kol
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 2.1. Количество учеников по предметам и учреждениям
SELECT predmet, ush, COUNT(fio) AS kol
FROM Students
GROUP BY predmet, ush;

-- 2.2. Количество учеников по предметам и учреждениям с промежуточными итогами
SELECT predmet, ush, COUNT(fio) AS kol
FROM Students
GROUP BY CUBE(predmet, ush);

-- 3. Количество учеников по предметам и учреждениям
SELECT predmet, ush, COUNT(fio) AS kol
FROM Students
GROUP BY GROUPING SETS(predmet, ush);

-- 4. Количество учеников по предметам и учреждениям, NULL заменены текстом
SELECT
    COALESCE(predmet, 'ИТОГО') AS predmet,
    COALESCE(ush, 'Итого') AS ush,
    COUNT(fio) AS kol
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 5. Количество учеников по предметам и учреждениям с заменой NULL в зависимости от группировки
SELECT
    IIF(GROUPING(predmet) = 1, 'ИТОГО', predmet) AS predmet,
    IIF(GROUPING(ush) = 1, 'Итого', ush) AS ush,
    COUNT(fio) AS kol
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 6. Количество учеников по предметам и учреждениям с заменой NULL на текст в итоговых строках
SELECT
    CASE GROUPING_ID(predmet, ush)
            WHEN 1 THEN 'Итого по предметам'
            WHEN 3 THEN 'Итого'
            ELSE ''
        END AS Итого,
    ISNULL(predmet, '') AS predmet,
    ISNULL(ush, '') AS ush,
    COUNT(fio) AS kol
FROM Students
GROUP BY ROLLUP(ush, predmet);