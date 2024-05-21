package diffs

import (
	"fmt"
	"log"
	"math"
	"os"
)

const (
	e = 1e-7 // Допустимая погрешность для метода Ньютона
)

// Правая часть дифференциального уравнения
type diffEquation func(x float64, u float64) float64

// Функция u(x)
type equation func(x float64) float64

type CoshieTask struct {
	diffU diffEquation // Правая часть дифференциального уравнения
	u     equation     // Функция u(x)

	a  float64
	b  float64
	u0 float64 // Начальное условие

	h float64 // Шаг
}

func NewCoshieTask(diffU diffEquation, u equation, a, b, u0, h float64) CoshieTask {
	return CoshieTask{
		diffU: diffU,
		u:     u,
		a:     a,
		b:     b,
		u0:    u0,
		h:     h,
	}
}

// SolveRungeKutta3 решает дифференциальное уравнение методом Рунге-Кутта 3-го порядка
// и записывает результат в файл с именем fileName
func (t CoshieTask) SolveRungeKutta3(fileName string) {
	file, err := os.OpenFile(fileName, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	yi := t.u0
	yih2 := t.u0
	maxErr := 0.0
	Rh := 0.0

	fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", t.a, t.u(t.a), yi)

	k := 1
	for xi := t.a; xi < t.b; xi += t.h {
		yi1 := rungeKutta3(t.diffU, xi, yi, t.h)

		fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", xi+t.h, t.u(xi+t.h), yi1)

		maxErr = max(math.Abs(t.u(xi+t.h)-yi1), maxErr)
		// Считаем погрешность методом Рунге для шага 2h
		if k%2 == 0 {
			yih2 = rungeKutta3(t.diffU, xi-t.h, yih2, t.h*2.0)
			Rh = max(math.Abs(yih2-yi1)/7.0, Rh)
		}

		k++
		yi = yi1
	}
	fmt.Fprintf(file, "err			= %.10f\n", maxErr)
	fmt.Fprintf(file, "Rh			= %.10f\n", Rh)
	fmt.Fprintf(file, "|Rh - err|	= %.10f\n", math.Abs(Rh-maxErr))
}

// Основная логика метода Рунге-Кутта 3-го порядка
func rungeKutta3(diffU diffEquation, xi, yi, h float64) float64 {
	var (
		k1 = diffU(xi, yi)
		k2 = diffU(xi+1.0/3.0*h, yi+1.0/3.0*k1*h)
		k3 = diffU(xi+2.0/3.0*h, yi+2.0/3.0*k2*h)
	)

	return yi + h*(1.0/4.0*k1+3.0/4.0*k3)
}

// ImplicitTrapezoid решает дифференциальное уравнение неявным методом трапеций
// ddU - производная правой части дифференциального уравнения для метода Ньютона
func (t CoshieTask) ImplicitTrapezoid(fileName string, ddU diffEquation) {
	file, err := os.OpenFile(fileName, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	yi := t.u0
	yih2 := t.u0
	maxErr := 0.0
	Rh := 0.0

	fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", t.a, t.u(t.a), yi)

	k := 1
	for xi := t.a; xi < t.b; xi += t.h {
		yi1 := implicitTrapezoid(t.diffU, ddU, xi, yi, t.h)

		fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", xi+t.h, t.u(xi+t.h), yi1)

		maxErr = max(math.Abs(t.u(xi+t.h)-yi1), maxErr)
		// Считаем погрешность методом Рунге для шага 2h
		if k%2 == 0 {
			yih2 = implicitTrapezoid(t.diffU, ddU, xi-t.h, yih2, t.h*2.0)
			Rh = max(math.Abs(yih2-yi1)/3.0, Rh)
		}

		yi = yi1
		k++
	}
	fmt.Fprintf(file, "err			= %.10f\n", maxErr)
	fmt.Fprintf(file, "Rh			= %.10f\n", Rh)
	fmt.Fprintf(file, "|Rh - err|	= %.10f\n", math.Abs(Rh-maxErr))
}

// Основная логика неявного метода трапеций
func implicitTrapezoid(diffU diffEquation, ddU diffEquation, xi, yi, h float64) float64 {
	return newtonNonlinear(diffU, ddU, xi, yi, h)
}

// newtonNonlinear решает нелинейное уравнение методом Ньютона
// diffU - правая часть дифференциального уравнения
// ddU - производная правой части дифференциального уравнения
func newtonNonlinear(diffU diffEquation, ddU diffEquation, xi, yi, h float64) float64 {
	var (
		y           = yi
		hh          = h / 2.0
		xi1         = xi + h
		precomputed = yi + hh*diffU(xi, yi)
	)

	for {
		yNext := y - (y-precomputed-hh*diffU(xi1, y))/(1.0-hh*ddU(xi1, y))
		if math.Abs(yNext-y) < e {
			return yNext
		}
		y = yNext
	}
}

// AdamsPredictorCorrector3 решает дифференциальное уравнение предиктор-корректорным методом Адамса 3-го порядка
func (t CoshieTask) AdamsPredictorCorrector3(fileName string) {
	file, err := os.OpenFile(fileName, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	yi_2 := t.u0
	yi_1 := rungeKutta3(t.diffU, t.a, yi_2, t.h)   // находим y[i-1] методом Рунге-Кутта 3-го порядка
	yi := rungeKutta3(t.diffU, t.a+t.h, yi_1, t.h) // находим y[i] методом Рунге-Кутта 3-го порядка
	maxErr := 0.0

	fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", t.a, t.u(t.a), yi_2)
	fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", t.a+t.h, t.u(t.a+t.h), yi_1)
	fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", t.a+2.0*t.h, t.u(t.a+2.0*t.h), yi)

	maxErr = max(math.Abs(t.u(t.a+t.h)-yi_1), math.Abs(t.u(t.a+2*t.h)-yi))

	for xi := t.a + 2.0*t.h; xi <= t.b; xi += t.h {
		yi1 := explicitAdams3(t.diffU, xi, yi, yi_1, yi_2, t.h)
		yi1 = implicitAdams3(t.diffU, xi, yi1, yi, yi_1, t.h)

		fmt.Fprintf(file, "i = %.10f u = %.10f y = %.10f\n", xi+t.h, t.u(xi+t.h), yi1)
		maxErr = max(math.Abs(t.u(xi+t.h)-yi1), maxErr)

		yi_2 = yi_1
		yi_1 = yi
		yi = yi1
	}
	fmt.Fprintf(file, "err			= %.10f", maxErr)
}

// Явный метод Адамса 3-го порядка
// yi_1 - y[i-1], yi_2 - y[i-2]
func explicitAdams3(diffU diffEquation, xi, yi, yi_1, yi_2, h float64) float64 {
	return yi + h/12.0*(23.0*diffU(xi, yi)-16.0*diffU(xi-h, yi_1)+5.0*diffU(xi-2*h, yi_2))
}

// Неявный метод Адамса 3-го порядка
// yi1 - y[i+1], yi_1 - y[i-1]
func implicitAdams3(diffU diffEquation, xi, yi1, yi, yi_1, h float64) float64 {
	return yi + h/12.0*(5.0*diffU(xi+h, yi1)+8.0*diffU(xi, yi)-diffU(xi-h, yi_1))
}
