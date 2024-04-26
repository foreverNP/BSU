package main

import (
	"fmt"
	"math"
	"mv2.3/internal/integral"
	"os"
)

const (
	e = 1e-7
	a = 0
	b = math.Pi / 4.0
)

var (
	f = func(x float64) float64 {
		return math.Pow((math.Sin(x)-math.Cos(x))/(math.Sin(x)+math.Cos(x)), 3.0)
	}

	I = math.Log(math.Sqrt2) - 0.5
)

func main() {
	logFileTrapezoid, _ := os.OpenFile("trapezoid.txt", os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)
	logFileSimpson, _ := os.OpenFile("simpson.txt", os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)

	resultTrapezoid := integral.IntegrateTrapezoidal(f, a, b, e, logFileTrapezoid)
	resultSimpson := integral.IntegrateSimpson(f, a, b, e, logFileSimpson)
	resultGaussLegendre := integral.IntegrateGaussLegendre(f, a, b, 4)

	fmt.Printf("Trapezoid res: %.10f\nerror: %.10f\n", resultTrapezoid, math.Abs(I-resultTrapezoid))
	fmt.Printf("Simpson res: %.10f\nerror: %.10f\n", resultSimpson, math.Abs(I-resultSimpson))
	fmt.Printf("GaussLegendre res: %.10f\nerror: %.10f\n", resultGaussLegendre, math.Abs(I-resultGaussLegendre))
}
