-- 1. Вывести список студентов, поступивших после 2018 года:
SELECT * FROM Student
WHERE God_postupleniya > 2018;

-- 2. Вывести список студентов, у которых специальность "физика":
SELECT * FROM Student
WHERE Specialnost = 'Физика';

-- 3. Вывести список студентов, у которых специальность не "химия":
SELECT * FROM Student
WHERE Specialnost != 'Химия';

-- 4. Вывести студентов, поступивших после 2019 года по специальности "математика":
SELECT * FROM Student
WHERE God_postupleniya > 2019 AND Specialnost = 'Математика';

-- 5. Вывести студентов, поступивших после 2016 года по специальности "математика" или после 2018 года по специальности "физика":
SELECT * FROM Student
WHERE (God_postupleniya > 2016 AND Specialnost = 'Математика') 
   OR (God_postupleniya > 2018 AND Specialnost = 'Физика');

-- 6. Вывести студентов, поступивших в 2019-2022 гг.:
SELECT * FROM Student
WHERE God_postupleniya BETWEEN 2019 AND 2022;

-- 7. Вывести студентов, родившихся между 01.01.2001 и 09.12.2005:
SELECT * FROM Student
WHERE Data_rozdeniya BETWEEN '2001-01-01' AND '2005-12-09';

-- 8. Вывести студентов из задания 7, отсортированных по фамилии:
SELECT * FROM Student
WHERE Data_rozdeniya BETWEEN '2001-01-01' AND '2005-12-09'
ORDER BY FIO;

-- 9. Вывести студентов, фамилия которых начинается с буквы "М":
SELECT * FROM Student
WHERE FIO LIKE 'М%';

-- 10. Вывести студентов, у которых вторая буква в специальности — "и":
SELECT * FROM Student
WHERE Specialnost LIKE '_и%';

-- 11. Вывести студентов, у которых третья буква фамилии — "л", "е" или "м":
SELECT * FROM Student
WHERE FIO LIKE '__л%' 
   OR FIO LIKE '__е%' 
   OR FIO LIKE '__м%';

-- 12. Вывести фамилии студентов, фамилии которых начинаются с буквы от "М" до "С":
SELECT FIO FROM Student
WHERE FIO BETWEEN 'М' AND 'С';

-- 13. Вывести фамилии студентов, фамилии которых не начинаются с букв от "М" до "С":
SELECT FIO FROM Student
WHERE FIO NOT BETWEEN 'М' AND 'С';

-- 14. Вывести студентов со специальностями "математика" и "физика":
SELECT * FROM Student
WHERE Specialnost IN ('Математика', 'Физика');
