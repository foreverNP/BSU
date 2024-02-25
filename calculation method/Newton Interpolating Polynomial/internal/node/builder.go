package node

import (
	"math"
)

// BuildEquidistantNodes строит равноотстоящие узлы
// f - интерполируемая функция, a и b - границы интервала, n - степнь многочлена
// Возвращает массив узлов Node, представляющих точки интерполяции
func BuildEquidistantNodes(f func(float64) float64, a float64, b float64, n int) []Node {
	// Создаем слайс для хранения узлов
	nodes := make([]Node, n+1)
	// Вычисляем шаг между узлами
	h := (b - a) / float64(n)

	// Заполняем массив узлами, где X - равномерно распределенные точки, Y - значение функции в этих точках
	for i := 0; i <= n; i++ {
		x := a + float64(i)*h
		nodes[i] = Node{X: x, Y: f(x)}
	}

	// Возвращаем массив узлов для использования при интерполяции
	return nodes
}

// BuildChebyshevNodes строит чебышѐвские узлы
// Возвращает массив узлов Node, представляющих точки интерполяции
func BuildChebyshevNodes(f func(float64) float64, a float64, b float64, n int) []Node {
	// Создаем слайс для хранения узлов
	nodes := make([]Node, n+1)

	// Заполняем массив узлами, где X - точки, определенные методом Чебышева
	for i := 0; i <= n; i++ {
		x := (a+b)/2 + (b-a)/2*math.Cos(math.Pi*(2*float64(i)+1)/(2*float64(n)+2))
		nodes[i] = Node{X: x, Y: f(x)}
	}

	return nodes
}
