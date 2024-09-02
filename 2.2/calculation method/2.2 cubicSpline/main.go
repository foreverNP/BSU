package main

import (
	"fmt"
	"math"
	"mv2.2/internal/node"
	"mv2.2/internal/spline"
	"os"
)

const (
	a      = -3.0
	b      = 3.0
	N      = 15
	points = 100
)

var (
	f = func(x float64) float64 {
		return math.Sin(x)
	}

	Df = func(x float64) float64 {
		return math.Cos(x)
	}
)

func main() {
	spl := spline.New(Df, node.BuildEquidistantNodes(f, a, b, N))

	saveToFile("spline", spl, f)
}

// saveToFile сохраняет результаты интерполяции в файл и вычисляет погрешность
func saveToFile(filename string, spl spline.CubicSpline, f func(float64) float64) {
	file, err := os.Create(filename)
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	// Вычисление шага для точек интерполяции
	step := (b - a) / points
	interErr := 0.0

	// Запись точек интерполяции и реальных значений функции в файл
	for i := 0; i <= points; i++ {
		x := a + float64(i)*step
		y, err := spl.Solve(x)

		if err != nil {
			panic(err)
		}

		yReal := f(x)

		// Обновление максимальной погрешности
		interErr = math.Max(interErr, math.Abs(yReal-y))

		//Запись в файл в формате "x y интерполяция y реальное значение"
		_, err = file.WriteString(fmt.Sprintf("%.2f %.10f %.10f %.15f\n", x, y, yReal, math.Abs(y-yReal)))
		if err != nil {
			fmt.Println("Error writing to file:", err)
			return
		}
	}

	//Запись погрешности интерполяции в файл
	_, err = file.WriteString(fmt.Sprintf("%.15f", interErr))
	if err != nil {
		fmt.Println("Error writing to file:", err)
		return
	}
}
