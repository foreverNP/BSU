-- 2. Вывести максимальную площадь стран:
SELECT MAX(PL) AS Max_Ploshchad FROM Tabl_Kontinent$;

-- 3. Вывести наименьшее население стран в Африке:
SELECT MIN(KolNas) AS Min_Naselenie FROM Tabl_Kontinent$ WHERE Kontinent = 'Африка';

-- 4. Вывести суммарное население стран Северной и Южной Америки:
SELECT SUM(KolNas) AS Total_Naselenie FROM Tabl_Kontinent$ WHERE Kontinent IN ('Северная Америка', 'Южная Америка');

-- 5. Вывести среднее население стран, кроме европейских, округлить до двух знаков:
SELECT ROUND(AVG(KolNas), 2) AS Avg_Naselenie FROM Tabl_Kontinent$ WHERE Kontinent != 'Европа';

-- 6. Вывести количество стран, название которых начинается с буквы «С»:
SELECT COUNT(*) AS Count_Of_Countries FROM Tabl_Kontinent$ WHERE LEFT(Nazvanie, 1) = 'С';

-- 7. Вывести количество континентов, где есть страны:
SELECT COUNT(DISTINCT Kontinent) AS Kontinent_Count FROM Tabl_Kontinent$;

-- 8. Вывести разницу между населением стран с наибольшим и наименьшим количеством граждан:
SELECT MAX(KolNas) - MIN(KolNas) AS Population_Difference FROM Tabl_Kontinent$;

-- 9. Вывести количество стран на каждом континенте, отсортировать по убыванию:
SELECT Kontinent, COUNT(*) AS Count_Of_Countries FROM Tabl_Kontinent$ GROUP BY Kontinent ORDER BY Count_Of_Countries DESC;

-- 10. Вывести количество стран по первым буквам в названии, отсортировать в алфавитном порядке:
SELECT LEFT(Nazvanie, 1) AS First_Letter, COUNT(*) AS Count_Of_Countries 
FROM Tabl_Kontinent$ 
GROUP BY LEFT(Nazvanie, 1) 
ORDER BY First_Letter;

-- 11. Вывести континенты, где плотность населения больше 100 чел/кв. км:
SELECT Kontinent 
FROM Tabl_Kontinent$ 
GROUP BY Kontinent 
HAVING SUM(KolNas) / SUM(PL) > 100;

-- 12. Прогнозируемое население через 25 лет:
SELECT Kontinent, 
       CASE 
           WHEN Kontinent IN ('Европа', 'Азия') THEN FLOOR(SUM(KolNas) * 1.2)
           WHEN Kontinent IN ('Северная Америка', 'Африка') THEN FLOOR(SUM(KolNas) * 1.5)
           ELSE FLOOR(SUM(KolNas) * 1.7)
       END AS Projected_Population 
FROM Tabl_Kontinent$ 
GROUP BY Kontinent;

-- 13. Вывести континенты, где разница между населением наибольших и наименьших стран не более 1000 раз:
SELECT Kontinent 
FROM Tabl_Kontinent$ 
GROUP BY Kontinent 
HAVING MAX(KolNas) / MIN(KolNas) <= 1000;

-- 14. Вывести количество стран, у которых нет столицы:
SELECT COUNT(*) AS No_Capital_Count FROM Tabl_Kontinent$ WHERE Stolica IS NULL;

-- 15. Вывести количество символов в самых длинных и коротких названиях стран и столиц:
SELECT MAX(LEN(Nazvanie)) AS Max_Country_Length, MIN(LEN(Nazvanie)) AS Min_Country_Length,
       MAX(LEN(Stolica)) AS Max_Capital_Length, MIN(LEN(Stolica)) AS Min_Capital_Length
FROM Tabl_Kontinent$;

-- 16. Вывести континенты со средней плотностью среди стран с площадью более 1 млн кв. км больше 10 чел/кв. км:
SELECT Kontinent 
FROM Tabl_Kontinent$ 
WHERE PL > 1000000 
GROUP BY Kontinent 
HAVING AVG(KolNas / PL) > 10 
ORDER BY AVG(KolNas / PL) DESC;
