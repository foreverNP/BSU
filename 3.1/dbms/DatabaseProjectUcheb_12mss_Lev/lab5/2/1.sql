-- 1. Количество учеников по предметам по столбцам
SELECT 'Количество' AS [Количество учеников по предметам], Математика, Физика, Химия
FROM 
    (
        SELECT predmet, fio
        FROM Studentssss
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
    FROM Studentssss
) AS SourceTable
PIVOT (
    COUNT(fio) 
    FOR predmet IN ([Математика], [Физика], [Химия])
) AS PivotTable;

-- 3. Фамилии учеников, предметы и школы в один столбец
SELECT fio, [Предмет или школа]
FROM Studentssss
UNPIVOT(
    [Предмет или школа] FOR value IN (ush, predmet) 
) unpvt 

-- SELECT <non-pivoted_columns>, [pivoted_column1], [pivoted_column2], ...
-- FROM
-- (
--     SELECT <columns>
--     FROM <table_name>
-- ) AS SourceTable
-- PIVOT
-- (
--     <aggregate_function>(<pivot_column>)
--     FOR <column_to_pivot> IN ([pivoted_column1], [pivoted_column2], ...)
-- ) AS PivotTable;

-- <non-pivoted_columns> — это столбцы, которые не участвуют в операции PIVOT. Они остаются неизменными в результате преобразования.
-- <pivoted_column1>, <pivoted_column2>, ... — это столбцы, которые будут созданы в результате операции PIVOT на основе значений одного из столбцов исходной таблицы.
-- <aggregate_function>(<pivot_column>) — агрегирующая функция (например, SUM, COUNT, AVG), которая применяется к значениям столбца, который мы преобразуем в столбцы (агрегируемые данные).
-- <column_to_pivot> — столбец, который мы преобразуем в новые столбцы (значения которого станут новыми заголовками столбцов).
-- <table_name> — исходная таблица, откуда берутся данные.
-- SourceTable — это имя подзапроса (временной таблицы), из которого берутся данные для обработки.
-- PivotTable — это имя результирующей таблицы, которая будет создана после выполнения операции PIVOT.

-- SELECT <columns>
-- FROM <table>
-- UNPIVOT (
--     <target_value_column> FOR <target_column_name> IN (<column_list>)
-- ) AS <alias>

-- <target_value_column> — столбец, который будет содержать значения исходных столбцов.
-- <target_column_name> — столбец, который будет содержать имена (метки) исходных столбцов.
-- <column_list> — список столбцов, которые мы хотим "свернуть" в строки.
