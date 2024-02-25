package interPoly

import (
	"fmt"
	"mv2.1/internal/node"
)

// NewtonPoly представляет интерполяционный полином Ньютона.
type NewtonPoly struct {
	nodes        []node.Node // Узлы интерполяции
	separatedDif []float64   // Разделенные разности
}

// Solve вычисляет значение интерполяционного полинома Ньютона в точке x.
func (n NewtonPoly) Solve(x float64) float64 {
	result := n.separatedDif[0]
	temp := 1.0

	// Вычисляем значение интерполяционного полинома в точке x
	for i := 1; i < len(n.nodes); i++ {
		temp *= x - n.nodes[i-1].X
		result += n.separatedDif[i] * temp
	}

	return result
}

// String возвращает строковое представление интерполяционного полинома Ньютона.
func (n NewtonPoly) String() string {
	polyString := fmt.Sprintf("%.3f", n.separatedDif[0])

	// Формируем строку полинома с использованием разделенных разностей и узлов интерполяции
	for i := 1; i < len(n.separatedDif); i++ {
		polyString += " + "

		polyString += fmt.Sprintf("%.3f", n.separatedDif[i])

		for j := 0; j < i; j++ {
			polyString += fmt.Sprintf(" * (x - %.3f)", n.nodes[j].X)
		}
	}

	return polyString
}
