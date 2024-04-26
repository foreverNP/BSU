package integral

import (
	"fmt"
	"math"
	"os"
)

// integrand представляет функцию, которую необходимо интегрировать
type integrand func(float64) float64

// rungeErrorTrapezoid вычисляет оценку погрешности методом Рунге
func rungeErrorTrapezoid(q2, q float64) float64 {
	return (q2 - q) / 3
}

// trapezoidalRule вычисляет приближенное значение интеграла функции f от a до b
func trapezoidalRule(f integrand, a, b float64, n int) float64 {
	h := (b - a) / float64(n)
	sum := 0.0

	// Вычисляем сумму значений функции f в точках x
	for i := 1; i <= n-1; i++ {
		x := a + float64(i)*h
		sum += f(x)
	}

	return 0.5 * h * (f(a) + 2*sum + f(b))
}

// IntegrateTrapezoidal вычисляет приближенное значение интеграла функции f от a до b с заданной точностью e
// с помощью кф трапеций и метода Рунге для оценки погрешности
func IntegrateTrapezoidal(f integrand, a, b float64, e float64, logFile *os.File) float64 {
	n := 2
	prevResult := 0.0
	result := trapezoidalRule(f, a, b, n)

	// Вычисляем приближенное значение интеграла с заданной точностью e с помощью метода Рунге
	for math.Abs(rungeErrorTrapezoid(result, prevResult)) > e {
		fmt.Fprintf(logFile, "h = %.10f, Q = %.10f, R = %.10f, n = %d\n",
			(b-a)/float64(n), result, math.Abs(rungeErrorTrapezoid(result, prevResult)), n)

		prevResult = result
		n *= 2
		result = trapezoidalRule(f, a, b, n)
	}

	// Записываем результаты в лог-файл
	fmt.Fprintf(logFile, "h = %.10f, Q = %.10f, R = %.10f, n = %d\n",
		(b-a)/float64(n), result, math.Abs(rungeErrorTrapezoid(result, prevResult)), n)

	return result
}
