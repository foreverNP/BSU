package integral

// Узлы и веса для квадратурной формулы Гаусса-Лежандра для n = 3, 4, 5
var gaussLegendreNodes = map[int]struct {
	nodes   []float64
	weights []float64
}{
	3: {
		nodes:   []float64{-0.7745966692414834, 0.0, 0.7745966692414834},
		weights: []float64{0.5555555555555556, 0.8888888888888888, 0.5555555555555556},
	},
	4: {
		nodes:   []float64{-0.33998104358485626, -0.86113631159405257, 0.86113631159405257, 0.33998104358485626},
		weights: []float64{0.65214515486254614, 0.34785484513745386, 0.34785484513745386, 0.65214515486254614},
	},
	5: {
		nodes:   []float64{-0.5384693101056831, -0.906179845938664, 0.0, 0.906179845938664, 0.5384693101056831},
		weights: []float64{0.47862867049936646, 0.2369268850561891, 0.5688888888888889, 0.2369268850561891, 0.47862867049936646},
	},
}

// Преобразование узлов и весов для отрезка [a, b]
func transformNodes(nodes, weights []float64, a, b float64) ([]float64, []float64) {
	newNodes := make([]float64, len(nodes))
	newWeights := make([]float64, len(weights))

	copy(newNodes, nodes)
	copy(newWeights, weights)

	for i := range nodes {
		newNodes[i] = (a+b)/2.0 + (b-a)/2.0*nodes[i]
		newWeights[i] *= (b - a) / 2.0
	}

	return newNodes, newWeights
}

// IntegrateGaussLegendre вычисляет приближенное значение интеграла функции f от a до b
// с помощью квадратурной формулы Гаусса-Лежандра с n узлами
func IntegrateGaussLegendre(f integrand, a, b float64, n int) float64 {
	if _, ok := gaussLegendreNodes[n]; !ok {
		panic("integral: unknown number of nodes for Gauss-Legendre quadrature")
	}

	nodes, weights := transformNodes(gaussLegendreNodes[n].nodes, gaussLegendreNodes[n].weights, a, b)

	sum := 0.0
	for i := range nodes {
		sum += weights[i] * f(nodes[i])
	}

	return sum
}
