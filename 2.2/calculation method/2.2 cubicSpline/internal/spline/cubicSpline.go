package spline

import (
	"errors"
	"mv2.2/internal/equationsSolving"
	"mv2.2/internal/node"
)

// CubicSpline представляет кубическую сплайн-интерполяцию для заданного набора узлов.
type CubicSpline struct {
	nodes  []node.Node // Слайс узлов для интерполяции.
	coeffs []float64   // Коэффициенты для кубической сплайн-интерполяции.
}

// New создает новую кубическую сплайн-интерполяцию на основе заданной производной и набора узлов.
func New(Df func(float64) float64, nodes []node.Node) CubicSpline {
	// Создаем слайсы для матриц A и B для решения СЛАУ.
	var (
		A = make([][]float64, len(nodes))
		B = make([][]float64, len(nodes))
	)

	// Заполняем матрицы A и B для всех узлов, кроме первого и последнего.
	for i := 1; i < len(nodes)-1; i++ {
		A[i] = make([]float64, len(nodes))
		B[i] = make([]float64, 1)

		A[i][i-1] = (nodes[i].X - nodes[i-1].X) / 6
		A[i][i] = (nodes[i+1].X - nodes[i-1].X) / 3
		A[i][i+1] = (nodes[i+1].X - nodes[i].X) / 6

		B[i][0] = (nodes[i+1].Y-nodes[i].Y)/(nodes[i+1].X-nodes[i].X) - (nodes[i].Y-nodes[i-1].Y)/(nodes[i].X-nodes[i-1].X)
	}

	// Заполняем матрицы A и B для первого узла.
	A[0] = make([]float64, len(nodes))
	B[0] = make([]float64, 1)

	A[0][0] = (nodes[1].X - nodes[0].X) / 3
	A[0][1] = A[0][0] / 2
	B[0][0] = (nodes[1].Y-nodes[0].Y)/(nodes[1].X-nodes[0].X) - Df(nodes[0].X)

	// Заполняем матрицы A и B для последнего узла.
	A[len(nodes)-1] = make([]float64, len(nodes))
	B[len(nodes)-1] = make([]float64, 1)

	A[len(nodes)-1][len(nodes)-2] = (nodes[len(nodes)-1].X - nodes[len(nodes)-2].X) / 6
	A[len(nodes)-1][len(nodes)-1] = A[len(nodes)-1][len(nodes)-2] * 2
	B[len(nodes)-1][0] = Df(nodes[len(nodes)-1].X) - (nodes[len(nodes)-1].Y-nodes[len(nodes)-2].Y)/(nodes[len(nodes)-1].X-nodes[len(nodes)-2].X)

	// Решаем СЛАУ с помощью метода прогонки и возвращаем результирующую кубическую сплайн-интерполяцию.
	return CubicSpline{
		nodes:  nodes,
		coeffs: equationsSolving.SolveTridiagonal(A, B),
	}
}

// Solve вычисляет значение кубического сплайна в точке x.
func (cs CubicSpline) Solve(x float64) (float64, error) {
	// Ищем индекс интервала, в который попадает x
	i := -1
	for k := 1; k < len(cs.nodes); k++ {
		if x <= cs.nodes[k].X {
			i = k
			break
		}
	}

	// Проверяем, что x находится в пределах заданных узлов данных
	if i == -1 {
		return 0, errors.New("the argument is out of range")
	}

	// Расчет значений для кубического сплайна
	h := cs.nodes[i].X - cs.nodes[i-1].X
	result := cs.coeffs[i-1]*(cs.nodes[i].X-x)*(cs.nodes[i].X-x)*(cs.nodes[i].X-x)/(6*h) +
		cs.coeffs[i]*(x-cs.nodes[i-1].X)*(x-cs.nodes[i-1].X)*(x-cs.nodes[i-1].X)/(6*h) +
		(cs.nodes[i-1].Y-h*h/6*cs.coeffs[i-1])*(cs.nodes[i].X-x)/h +
		(cs.nodes[i].Y-h*h/6*cs.coeffs[i])*(x-cs.nodes[i-1].X)/h

	return result, nil
}
