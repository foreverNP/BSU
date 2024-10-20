-- 17. Минимальная площадь стран:
SELECT MIN(PL) AS Min_Ploshchad FROM Tabl_Kontinent$;

-- 18. Наибольшая по населению страна в Северной и Южной Америке:
SELECT TOP 1 Nazvanie, KolNas 
FROM Tabl_Kontinent$ 
WHERE Kontinent IN ('Северная Америка', 'Южная Америка') 
ORDER BY KolNas DESC;


-- 19. Среднее население стран, округлить до одного знака:
SELECT ROUND(AVG(KolNas), 1) AS Avg_Naselenie FROM Tabl_Kontinent$;

-- 20. Количество стран, название которых заканчивается на «ан», кроме тех, что на «стан»:
SELECT COUNT(*) AS Count_Of_Countries 
FROM Tabl_Kontinent$ 
WHERE Nazvanie LIKE '%ан' AND Nazvanie NOT LIKE '%стан';

-- 21. Количество континентов, где есть страны, название которых начинается с «Р»:
SELECT COUNT(DISTINCT Kontinent) AS Kontinent_Count 
FROM Tabl_Kontinent$ 
WHERE Nazvanie LIKE 'Р%';

-- 22. В сколько раз страна с наибольшей площадью больше, чем с наименьшей:
SELECT MAX(PL) / MIN(PL) AS Area_Ratio FROM Tabl_Kontinent$;

-- 23. Количество стран с населением больше 100 млн на каждом континенте, отсортировать по возрастанию:
SELECT Kontinent, COUNT(*) AS Count_Of_Countries 
FROM Tabl_Kontinent$ 
WHERE KolNas > 100000000 
GROUP BY Kontinent 
ORDER BY Count_Of_Countries ASC;

-- 24. Количество стран по количеству букв в названии:
SELECT LEN(Nazvanie) AS Name_Length, COUNT(*) AS Count_Of_Countries 
FROM Tabl_Kontinent$ 
GROUP BY LEN(Nazvanie) 
ORDER BY Count_Of_Countries DESC;

-- 25. Прогнозируемое население мира через 20 лет, увеличится на 10%:
SELECT Kontinent, FLOOR(SUM(KolNas) * 1.1) AS Projected_Population 
FROM Tabl_Kontinent$ 
GROUP BY Kontinent;

-- 26. Континенты, где разница по площади между наибольшими и наименьшими странами не более 10,000 раз:
SELECT Kontinent 
FROM Tabl_Kontinent$ 
GROUP BY Kontinent 
HAVING MAX(PL) / MIN(PL) <= 10000;

-- 27. Средняя длина названий африканских стран:
SELECT AVG(LEN(Nazvanie)) AS Avg_Name_Length 
FROM Tabl_Kontinent$ 
WHERE Kontinent = 'Африка';

-- 28. Континенты, у которых средняя плотность среди стран с населением более 1 млн чел. больше 30 чел/кв. км:
SELECT Kontinent 
FROM Tabl_Kontinent$ 
WHERE KolNas > 1000000 
GROUP BY Kontinent 
HAVING AVG(KolNas / PL) > 30;