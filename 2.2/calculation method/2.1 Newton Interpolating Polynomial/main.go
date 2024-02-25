package main

import (
	"fmt"
	"math"
	"mv2.1/internal/interPoly"
	"mv2.1/internal/node"
	"os"
)

const (
	a       = -3.0
	b       = 3.0
	nPoints = 100
)

var (
	f1 = func(x float64) float64 {
		return math.Sin(x)
	}

	f2 = func(x float64) float64 {
		return math.Sqrt(4 + math.Abs(x))
	}
)

func main() {
	var n int

	fmt.Println("Input n: ")
	_, _ = fmt.Scan(&n)

	var (
		// P1 Построение полинома Ньютона для f1 с равноотстоящими узлами
		P1 = interPoly.NewtonPolyBuilder(node.BuildEquidistantNodes(f1, a, b, n))
		// C1 Построение полинома Ньютона для f1 с Чебышевскими узлами
		C1 = interPoly.NewtonPolyBuilder(node.BuildChebyshevNodes(f1, a, b, n))
		// P2 Построение полинома Ньютона для f1 с равноотстоящими узлами
		P2 = interPoly.NewtonPolyBuilder(node.BuildEquidistantNodes(f2, a, b, n))
		// C2 Построение полинома Ньютона для f1 с Чебышевскими узлами
		C2 = interPoly.NewtonPolyBuilder(node.BuildChebyshevNodes(f2, a, b, n))
	)

	// Вывод полиномов Ньютона в консоль
	fmt.Println("P1: ", P1)
	fmt.Println("C1: ", C1)
	fmt.Println()
	fmt.Println("P2: ", P2)
	fmt.Println("C2: ", C2)

	// Сохранение результатов в файлы и вычисление погрешности интерполяции
	saveToFile(fmt.Sprintf("%s_%d_%d.txt", "P1", n, nPoints), P1, f1)
	saveToFile(fmt.Sprintf("%s_%d_%d.txt", "C1", n, nPoints), C1, f1)
	saveToFile(fmt.Sprintf("%s_%d_%d.txt", "P2", n, nPoints), P2, f2)
	saveToFile(fmt.Sprintf("%s_%d_%d.txt", "C2", n, nPoints), C2, f2)
}

// saveToFile сохраняет результаты интерполяции в файл и вычисляет погрешность
func saveToFile(filename string, poly interPoly.NewtonPoly, f func(float64) float64) {
	file, err := os.Create(filename)
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	// Вычисление шага для точек интерполяции
	step := (b - a) / float64(nPoints-1)
	interErr := 0.0

	// Запись точек интерполяции и реальных значений функции в файл
	for i := 0; i < nPoints; i++ {
		x := a + float64(i)*step
		y := poly.Solve(x)
		yReal := f(x)

		// Обновление максимальной погрешности
		interErr = math.Max(interErr, math.Abs(yReal-y))

		//Запись в файл в формате "x y интерполяция y реальное значение"
		_, err := file.WriteString(fmt.Sprintf("%.10f %.10f %.10f\n", x, y, yReal))
		if err != nil {
			fmt.Println("Error writing to file:", err)
			return
		}
	}

	//Запись погрешности интерполяции в файл
	_, err = file.WriteString(fmt.Sprintf("%s %.10f", "", interErr))
	if err != nil {
		fmt.Println("Error writing to file:", err)
		return
	}
}
