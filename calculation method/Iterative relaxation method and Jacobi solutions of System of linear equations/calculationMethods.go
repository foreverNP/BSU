package main

import "math"

// Вычитание двух векторов
func subtractVectors(vector1, vector2 []float64) []float64 {
	if len(vector1) != len(vector2) {
		panic("Длины векторов должны совпадать")
	}

	result := make([]float64, len(vector1))

	for i := 0; i < len(vector1); i++ {
		result[i] = vector1[i] - vector2[i]
	}

	return result
}

// проверка на диагональное преобладание
func isDiagonallyDominant(matrix [][]float64) bool {
	for i := 0; i < len(matrix); i++ {
		sum := 0.0

		for j := 0; j < len(matrix[0]); j++ {
			if i != j {
				sum += math.Abs(matrix[i][j])
			}
		}

		if matrix[i][i] <= sum {
			return false
		}
	}

	return true
}

// Маскимальная по модулю разность элементво вектора
func maxAbsoluteDifference(vec1, vec2 []float64) float64 {
	return uniformNorm(subtractVectors(vec1, vec2))
}

// Равномерная норма вектора
func uniformNorm(vector []float64) float64 {
	max := math.Abs(vector[0])
	for i := 1; i < len(vector); i++ {
		absValue := math.Abs(vector[i])
		if absValue > max {
			max = absValue
		}
	}
	return max
}
