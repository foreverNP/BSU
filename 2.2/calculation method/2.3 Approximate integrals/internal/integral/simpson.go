package integral

import (
	"fmt"
	"math"
	"os"
)

// rungeErrorSimpson вычисляет оценку погрешности методом Рунге для формулы Симпсона
func rungeErrorSimpson(q2, q float64) float64 {
	return (q2 - q) / 15
}

// simpsonRule вычисляет приближенное значение интеграла функции f от a до b с помощью кф Симпсона
func simpsonRule(f integrand, a, b float64, n int) float64 {
	h := (b - a) / float64(n)
	sum := 0.0

	// Вычисляем сумму значений функции f в точках x с соответствующими коэффициентами
	for i := 1; i <= n-1; i++ {
		x := a + float64(i)*h
		coeff := 0.0

		if i%2 == 0 {
			coeff = 2.0
		} else {
			coeff = 4.0
		}

		sum += coeff * f(x)
	}

	return (h / 3.0) * (f(a) + f(b) + sum)
}

// IntegrateSimpson вычисляет приближенное значение интеграла функции f от a до b с заданной точностью e
// с помощью кф Симпсона и метода Рунге для оценки погрешности
func IntegrateSimpson(f integrand, a, b float64, e float64, logFile *os.File) float64 {
	n := 2
	prevResult := 0.0
	result := simpsonRule(f, a, b, n)

	// Вычисляем приближенное значение интеграла с заданной точностью e с помощью метода Рунге
	for math.Abs(rungeErrorSimpson(result, prevResult)) > e {
		fmt.Fprintf(logFile, "h = %.10f, Q = %.10f, R = %.10f, n = %d\n",
			(b-a)/float64(n), result, math.Abs(rungeErrorSimpson(result, prevResult)), n)

		prevResult = result
		n *= 2
		result = simpsonRule(f, a, b, n)
	}

	// Записываем результаты в лог-файл
	fmt.Fprintf(logFile, "h = %.10f, Q = %.10f, R = %.10f, n = %d\n",
		(b-a)/float64(n), result, math.Abs(rungeErrorSimpson(result, prevResult)), n)

	return result
}
