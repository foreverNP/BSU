-- 1. Количество учеников по предметам по столбцам
SELECT 'Количество' AS [Количество учеников по предметам], Математика, Физика, Химия
FROM 
    (
        SELECT predmet, fio
        FROM Students
    ) AS SourceTable
    PIVOT
    (
        COUNT(fio)
        FOR predmet IN (Математика, Физика, Химия)
    ) AS PivotTable;

-- 2. Количество учеников для каждой школы по каждому предмету
SELECT ush, Математика, Физика, Химия
FROM (
    SELECT ush, predmet, fio
    FROM Students
) AS SourceTable
PIVOT (
    COUNT(fio) 
    FOR predmet IN ([Математика], [Физика], [Химия])
) AS PivotTable;

-- 3. Фамилии учеников, предметы и школы в один столбец
SELECT fio, [Предмет или школа]
FROM Students
UNPIVOT(
    [Предмет или школа] FOR Значение IN (ush, predmet) 
) unpvt 

