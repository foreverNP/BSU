USE Ucheb_0
GO

-- Запрос 1: Вывести список стран и процентное соотношение их населения к суммарному населению мира
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent,
    ROUND(CAST(KolNas AS FLOAT) * 100 / 
          (SELECT SUM(KolNas) FROM Tabl_Kontinent$), 3) AS Процент
FROM 
    Tabl_Kontinent$
ORDER BY 
    Процент DESC;

-- Запрос 2: Вывести список стран мира, население которых больше, чем среднее население всех стран мира
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas > (SELECT AVG(KolNas) FROM Tabl_Kontinent$);

-- Запрос 3: С помощью подзапроса вывести список африканских стран, население которых больше 50 млн. чел
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    (SELECT 
         Nazvanie,
         Stolica,
         PL,
         KolNas,
         Kontinent
     FROM 
         Tabl_Kontinent$
     WHERE 
         Nazvanie = 'Африка') A
WHERE 
    KolNas > 50000000;

-- Запрос 4: Вывести список стран и процентное соотношение их населения к суммарному населению к той части мира, где они находятся
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent,
    ROUND(CAST(KolNas AS FLOAT) * 100 / 
          (SELECT SUM(KolNas) 
           FROM Tabl_Kontinent$ Б 
           WHERE A.Kontinent = Б.Kontinent), 3) AS Процент
FROM 
    Tabl_Kontinent$ A
ORDER BY 
    Процент DESC;

-- Запрос 5: Вывести список стран мира, население которых больше, чем среднее население стран в той части света, где они находятся
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$ A
WHERE 
    KolNas > (SELECT AVG(KolNas) 
              FROM Tabl_Kontinent$ Б 
              WHERE Б.Kontinent = A.Kontinent);

-- Запрос 6: Вывести список стран мира, которые находятся в тех частях света, среднее население которых больше, чем общемировое
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$ 
WHERE 
    Kontinent IN (
        SELECT 
            Kontinent
        FROM 
            Tabl_Kontinent$
        GROUP BY 
            Kontinent
        HAVING 
            AVG(KolNas) > (SELECT AVG(KolNas) FROM Tabl_Kontinent$)
    );

-- Запрос 7: Вывести список азиатских стран, население которых больше, чем в любой европейской стране
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Азия'
    AND KolNas > ALL (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Европа'
    );

-- Запрос 8: Вывести список европейских стран, население которых больше, чем население хотя бы одной южноамериканской страны
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Европа'
    AND KolNas > ANY (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Южная Америка'
    );

-- Запрос 9: Если в Африке есть хотя бы одна страна, население которой больше 100 млн. чел., вывести список всех африканских стран
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Африка'
    AND EXISTS (
        SELECT 
            *
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Африка'
            AND KolNas > 100000000
    );

-- Запрос 10: Вывести список стран в той части света, где находится страна "Ангола"
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = (
        SELECT 
            Kontinent
        FROM 
            Tabl_Kontinent$
        WHERE 
            Nazvanie = N'Ангола'
    );

-- Запрос 11: Вывести список стран, население которых не превышает население страны "Ангола"
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas <= (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Nazvanie = N'Ангола'
    );

-- Запрос 12: Вывести название страны с наибольшим населением среди стран с наименьшим населением на каждом континенте
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas = (
        SELECT 
            MAX(Мин_Нас)
        FROM 
            (SELECT 
                 MIN(KolNas) AS Мин_Нас
             FROM 
                 Tabl_Kontinent$
             GROUP BY 
                 Kontinent) A
    );