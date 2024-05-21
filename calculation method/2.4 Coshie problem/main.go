package main

import (
	"mv2.4/diffs"
)

const (
	a  = 2.0
	b  = 3.0
	h  = 0.1
	u0 = 2.0
)

var (
	diffU = func(x float64, u float64) float64 {
		return (1.0 - u*u) / (2.0 * x)
	}

	u = func(x float64) float64 {
		return (3.0*x + 2.0) / (3.0*x - 2.0)
	}

	ddU = func(x float64, u float64) float64 {
		return (1.0 - 2.0*u) / (2.0 * x)
	}
)

// const (
// 	a  = 0.0 // начальное значение x
// 	b  = 1.0 // конечное значение x
// 	h  = 0.1 // шаг интегрирования
// 	y0 = 1.0 // начальное значение y
// )

// var (
// 	diffY = func(x float64, y float64) float64 {
// 		return -15.0 * y
// 	}

// 	yExact = func(x float64) float64 {
// 		return math.Exp(-15.0 * x)
// 	}

// 	ddY = func(x float64, y float64) float64 {
// 		return -15.0
// 	}
// )

func main() {
	task := diffs.NewCoshieTask(diffU, u, a, b, u0, h)

	task.SolveRungeKutta3("runge.txt")
	task.ImplicitTrapezoid("trapezoid.txt", ddU)
	task.AdamsPredictorCorrector3("adams.txt")
}
