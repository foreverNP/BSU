package interPoly

import (
	"mv2.1/internal/node"
)

// NewtonPolyBuilder строит полином Ньютона на основе заданных узлов интерполяции
// nodes - узлы для интерполяции
// Возвращает полином Ньютона, представленный в виде структуры NewtonPoly
func NewtonPolyBuilder(nodes []node.Node) NewtonPoly {
	// Инициализация полинома Ньютона
	poly := NewtonPoly{
		separatedDif: make([]float64, len(nodes)),
		nodes:        make([]node.Node, len(nodes)),
	}
	// Копирование узлов
	copy(poly.nodes, nodes)

	// Инициализация первых разделенных разностей значениями Y из узлов
	for i := 0; i < len(poly.separatedDif); i++ {
		poly.separatedDif[i] = poly.nodes[i].Y
	}

	// Вычисление разделенных разностей
	for j := 1; j < len(poly.separatedDif); j++ {
		for i := len(poly.separatedDif) - 1; i >= j; i-- {
			poly.separatedDif[i] = (poly.separatedDif[i] - poly.separatedDif[i-1]) / (poly.nodes[i].X - poly.nodes[i-j].X)
		}
	}

	// Возвращение построенного полинома Ньютона
	return poly
}
