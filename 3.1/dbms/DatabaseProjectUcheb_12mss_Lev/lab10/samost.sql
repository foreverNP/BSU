-- Запрос 1: Вывести список стран и процентное соотношение площади каждой из них к общей площади всех стран мира
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent,
    ROUND(CAST(PL AS FLOAT) * 100 / (SELECT SUM(PL) FROM Tabl_Kontinent$), 3) AS Процент
FROM 
    Tabl_Kontinent$
ORDER BY 
    Процент DESC;

-- Запрос 2: Вывести список стран мира, плотность населения которых больше, чем средняя плотность населения всех стран мира
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    (CAST(KolNas AS FLOAT) / PL) > (SELECT AVG(CAST(KolNas AS FLOAT) / PL) FROM Tabl_Kontinent$);

-- Запрос 3: С помощью подзапроса вывести список европейских стран, население которых меньше 5 млн. чел.
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    (
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
    ) A
WHERE 
    KolNas < 5000000;

-- Запрос 4: Вывести список стран и процентное соотношение их площади к суммарной площади той части мира, где они находятся
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent,
    ROUND(CAST(PL AS FLOAT) * 100 / 
        (
            SELECT SUM(PL) 
            FROM Tabl_Kontinent$ B 
            WHERE A.Kontinent = B.Kontinent
        ), 3) AS Процент
FROM 
    Tabl_Kontinent$ A
ORDER BY 
    Процент DESC;

-- Запрос 5: Вывести список стран мира, площадь которых больше, чем средняя площадь стран той части света, где они находятся
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$ A
WHERE 
    PL > 
    (
        SELECT AVG(PL) 
        FROM Tabl_Kontinent$ B 
        WHERE B.Kontinent = A.Kontinent
    );

-- Запрос 6: Вывести список стран мира, которые находятся в тех частях света, средняя плотность населения которых превышает общемировую
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$ 
WHERE 
    Kontinent IN 
    (
        SELECT 
            Kontinent
        FROM 
            Tabl_Kontinent$
        GROUP BY 
            Kontinent
        HAVING 
            AVG(CAST(KolNas AS FLOAT) / PL) > 
            (
                SELECT AVG(CAST(KolNas AS FLOAT) / PL) FROM Tabl_Kontinent$
            )
    );

-- Запрос 7: Вывести список южноамериканских стран, в которых живет больше людей, чем в любой африканской стране
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = N'Южная Америка'
    AND KolNas > ALL 
    (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Африка'
    );

-- Запрос 8: Вывести список африканских стран, в которых живет больше людей, чем хотя бы в одной южноамериканской стране
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
    AND KolNas > ANY 
    (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Южная Америка'
    );

-- Запрос 9: Если в Африке есть хотя бы одна страна, площадь которой больше 2 млн. кв. км, вывести список всех африканских стран
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
    AND EXISTS 
    (
        SELECT 
            *
        FROM 
            Tabl_Kontinent$
        WHERE 
            Kontinent = N'Африка'
            AND PL > 2000000
    );

-- Запрос 10: Вывести список стран той части света, где находится страна Белоруссия
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    Kontinent = 
    (
        SELECT 
            Kontinent
        FROM 
            Tabl_Kontinent$
        WHERE 
            Nazvanie = N'Белоруссия'
    );

-- Запрос 11: Вывести список стран, население которых не превышает население страны Белоруссия
SELECT 
    Nazvanie,
    Stolica,
    PL,
    KolNas,
    Kontinent
FROM 
    Tabl_Kontinent$
WHERE 
    KolNas <= 
    (
        SELECT 
            KolNas
        FROM 
            Tabl_Kontinent$
        WHERE 
            Nazvanie = N'Белоруссия'
    );

-- Запрос 12: Вывести название страны с наибольшим населением среди стран с наименьшей площадью на каждом континенте
with min_sq as (  
    select
      min(PL) as Country_Min_Square, Kontinent
    from
      Tabl_Kontinent$
    group by
      Kontinent
),

population_cte as (
  select Nazvanie, KolNas
  from Tabl_Kontinent$ c join min_sq m on c.Kontinent = m.Kontinent and c.PL = m.Country_Min_Square
)

select Nazvanie
from population_cte
where KolNas = (select max(KolNas) from population_cte)
