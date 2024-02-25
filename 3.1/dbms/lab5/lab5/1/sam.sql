-- Самостоятельная работа

-- 1. Максимальный балл учеников по каждому предмету по каждой школе и промежуточные итоги
SELECT 
    ush, 
    predmet, 
    MAX(ball) AS MaxBall
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 2. Минимальный балл учеников по учреждениям и предметам с промежуточными итогами
SELECT 
    ush, 
    predmet, 
    MIN(ball) AS MinBall
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 3. Средний балл учеников по учреждениям и предметам
SELECT 
    ush, 
    predmet, 
    AVG(ball) AS AvgBall
FROM Students
GROUP BY ush, predmet;

-- 4. Количество учеников по учреждениям и предметам с заменой NULL значений на текст и промежуточные итоги
SELECT 
    COALESCE(ush, 'Итого по всем учреждениям') AS ush, 
    COALESCE(predmet, 'Итого по всем предметам') AS predmet, 
    COUNT(fio) AS StudentCount
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 5. Суммарный балл учеников по учреждениям и предметам с заменой NULL значений на текст и промежуточные итоги
SELECT 
    IIF(GROUPING(ush) = 1, 'Все учреждения', ush) AS ush, 
    IIF(GROUPING(predmet) = 1, 'Все предметы', predmet) AS predmet, 
    SUM(ball) AS TotalBall
FROM Students
GROUP BY ROLLUP(ush, predmet);

-- 6. Максимальный балл учеников по учреждениям и предметам с заменой NULL значений в зависимости от уровней группировки
SELECT 
    CASE GROUPING_ID(ush, predmet)
        WHEN 1 THEN 'Итого по предметам'
        WHEN 3 THEN 'Итого по всем данным'
        ELSE ush
    END AS ush, 
    ISNULL(predmet, 'Все предметы') AS predmet, 
    MAX(ball) AS MaxBall
FROM Students
GROUP BY ROLLUP(ush, predmet);
