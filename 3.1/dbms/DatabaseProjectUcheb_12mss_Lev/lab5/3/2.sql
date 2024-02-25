-- 3.1. Сумма расхода на оплату труда по отделам и годам
SELECT department, year, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY department, year;

-- 3.2. Общий расход по каждому отделу и годам с использованием ROLLUP
SELECT department, year, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY ROLLUP(department, year);

-- 3.3. Общий итог по отделам
SELECT department, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY ROLLUP(department);

-- 3.4. Общий итог по годам
SELECT year, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY ROLLUP(year);

-- 4.1. Общий расход по отделам и годам с использованием CUBE
SELECT department, year, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY CUBE(department, year);

-- 5.1. Общий расход по отделам и годам с использованием GROUPING SETS
SELECT department, year, SUM(salary) AS TotalSalary
FROM Employees
GROUP BY GROUPING SETS ((department, year), (department), (year), ());

-- 6.1. Пример с использованием функции GROUPING
SELECT 
    department, 
    year, 
    SUM(salary) AS TotalSalary,
    GROUPING(department) AS IsDepartmentSummary,
    GROUPING(year) AS IsYearSummary
FROM Employees
GROUP BY ROLLUP(department, year);
