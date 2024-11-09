USE Ucheb_0

-- Задание 2.1.1
-- Вывести объединенный результат выполнения запросов, которые выбирают страны
-- с площадью больше 1 млн. кв. км и с населением больше 100 млн. чел
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 1000000
UNION
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas > 100000000;

-- Задание 2.1.2
-- Вывести объединенный результат выполнения запросов, которые выбирают страны
-- с площадью больше 1 млн. кв. км и с населением больше 100 млн. чел., при этом
-- оставляет дубликаты
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 1000000

UNION ALL

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas > 100000000;


-- Задание 2.1.3
-- Вывести объединенный результат выполнения запросов, которые выбирают
-- европейские страны с плотностью более 300 чел. на кв. км, азиатские страны с
-- плотностью более 200 чел. на кв. км. и африканские страны с плотностью более 150
-- чел. на кв. км. Результат отсортировать по континентам:
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Европа' AND
    CAST(KolNas AS FLOAT) / PL > 300

UNION 

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Азия' AND
    CAST(KolNas AS FLOAT) / PL > 200

UNION 

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Африка' AND
    CAST(KolNas AS FLOAT) / PL > 150

ORDER BY 
    Kontinent;


-- Задание 2.1.4
-- Вывести список стран с площадью больше 1 млн. кв. км, исключить страны с
-- населением больше 1 млн. чел
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 1000000

EXCEPT

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas > 1000000;


-- Задание 2.1.5
-- Вывести список стран с площадью больше 1 млн. кв. км и с населением больше 100
-- млн. чел.
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 1000000

INTERSECT

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas > 100000000;

-- Задание 2.1.6 Самостоятельная работа

-- 1. Вывести объединенный результат выполнения запросов, которые выбирают
-- страны с площадью меньше 500 кв. км и с площадью больше 5 млн. кв. км:
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL < 500

UNION ALL

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 5000000;

-- 2. Вывести список стран с площадью больше 1 млн. кв. км, исключить страны с
-- населением меньше 100 млн. чел.:
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL > 1000000

EXCEPT

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas < 100000000;


-- 3. Вывести список стран с площадью меньше 500 кв. км и с населением меньше
-- 100 тыс. чел.
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    PL < 500

INTERSECT

SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas < 100000;
