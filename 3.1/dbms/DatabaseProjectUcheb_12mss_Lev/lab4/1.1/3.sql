-- 3.2. Вывести список всех студентов:
SELECT * FROM Student;

-- 3.3. Вывести ФИО и дату рождения всех студентов:
SELECT FIO, Data_rozdeniya FROM Student;

-- 3.4. Создать вычисляемое поле "Информация" (например, для студента Петрова):
SELECT FIO, Specialnost, 
       'Студент ' + FIO + ', специализация: ' + Specialnost AS Информация
FROM Student;

-- 3.5. Вывести ФИО студентов и номер следующего года после поступления:
SELECT FIO, God_postupleniya + 1 AS Next_Year
FROM Student;

-- 3.6. Вывести список специализаций, убрав дубликаты:
SELECT DISTINCT Specialnost 
FROM Student; 

-- 3.7. Вывести список студентов, отсортированный по возрастанию года поступления:
SELECT * FROM Student
ORDER BY God_postupleniya ASC;

-- 3.8. Вывести список студентов, отсортированный в обратном алфавитном порядке по полю "Spez" и в алфавитном порядке по полю "FIO":
SELECT * FROM Student
ORDER BY Specialnost DESC, FIO ASC;

-- 3.9. Вывести первые две строки из списка студентов, отсортированного по ФИО:
SELECT TOP 2 * FROM Student
ORDER BY FIO ASC;

-- 3.10. Вывести первые 30% строк из списка студентов, отсортированного по возрастанию года поступления:
SELECT TOP 30 PERCENT * FROM Student
ORDER BY God_postupleniya ASC;

-- 3.11. Вывести список студентов с одним из первых четырёх годов поступления:
SELECT TOP 4 WITH TIES * FROM Student ORDER BY God_postupleniya;

-- 3.12. Вывести, начиная с третьего, список студентов, отсортированный по ФИО:
SELECT * FROM Student
ORDER BY FIO ASC
OFFSET 2 ROWS;

-- 3.13. Вывести студентов с третьего по десятого, отсортированных по ФИО:
SELECT * FROM Student
ORDER BY FIO ASC
OFFSET 2 ROWS
FETCH NEXT 8 ROWS ONLY;