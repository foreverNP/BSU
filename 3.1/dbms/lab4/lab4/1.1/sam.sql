-- 4.1. Вывести ФИО, специализацию и дату рождения всех студентов:
SELECT FIO, Specialnost, Data_rozdeniya FROM Student;

-- 4.2. Создать вычисляемое поле "О поступлении":
SELECT FIO, ' поступил в ' + CAST(God_postupleniya AS NVARCHAR) AS О_поступлении 
FROM Student;

-- 4.3. Вывести ФИО студентов и вычисляемое поле "Через 5 лет после поступления":
SELECT FIO, God_postupleniya + 5 AS Через_5_лет 
FROM Student;

-- 4.4. Вывести список годов поступления, убрав дубликаты:
SELECT DISTINCT God_postupleniya FROM Student;

-- 4.5. Вывести список студентов, отсортированный по убыванию даты рождения:
SELECT * FROM Student
ORDER BY Data_rozdeniya DESC;

-- 4.6. Вывести список студентов, отсортированный в обратном алфавитном порядке специализаций, по убыванию года поступления, и в алфавитном порядке ФИО:
SELECT * FROM Student
ORDER BY Specialnost DESC, God_postupleniya DESC, FIO ASC;

-- 4.7. Вывести первую строку из списка студентов, отсортированного в обратном алфавитном порядке ФИО:
SELECT TOP 1 * FROM Student
ORDER BY FIO DESC;

-- 4.8. Вывести фамилию студента, который раньше всех поступил:
SELECT TOP 1 FIO FROM Student
ORDER BY God_postupleniya ASC;

-- 4.9. Вывести первые 10% строк из списка студентов, отсортированного по алфавиту:
SELECT TOP 10 PERCENT * FROM Student
ORDER BY FIO ASC;

-- 4.10. Вывести список студентов с одним из первых пяти годов поступления:
SELECT TOP 5 WITH TIES * FROM Student ORDER BY God_postupleniya;

-- 4.11. Вывести, начиная с пятого, список студентов, отсортированный по дате рождения:
SELECT * FROM Student
ORDER BY Data_rozdeniya ASC
OFFSET 4 ROWS;

-- 4.12. Вывести 7-ю строку из списка студентов:
SELECT * FROM Student
ORDER BY FIO ASC
OFFSET 6 ROWS
FETCH NEXT 1 ROW ONLY;

-- 4.13. Вывести с 5 по 9 строки:
SELECT * FROM Student
ORDER BY FIO ASC
OFFSET 4 ROWS
FETCH NEXT 5 ROWS ONLY;