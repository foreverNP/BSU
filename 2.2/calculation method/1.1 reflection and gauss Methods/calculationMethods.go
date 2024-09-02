package main

import "math"

// Скалярное произведение двух векторов
func dotProduct(vector1, vector2 []float64) float64 {
	if len(vector1) != len(vector2) {
		panic("Длины векторов должны совпадать")
	}

	result := 0.0
	for i := 0; i < len(vector1); i++ {
		result += vector1[i] * vector2[i]
	}

	return result
}

// Евклидова норма вектора
func euclideanNorm(vector []float64) float64 {
	result := 0.0
	for i := 0; i < len(vector); i++ {
		result += vector[i] * vector[i]
	}

	return math.Sqrt(result)
}

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

// умножение матриц
func multiplyMatrices(matrix1, matrix2 [][]float64) [][]float64 {
	rows1, cols1 := len(matrix1), len(matrix1[0])
	rows2, cols2 := len(matrix2), len(matrix2[0])

	// Проверяем, можно ли умножить матрицы
	if cols1 != rows2 {
		panic("Невозможно умножить матрицы. Количество столбцов первой матрицы не равно количеству строк второй матрицы.")
	}

	// Создаем новую матрицу результатов
	result := make([][]float64, rows1)
	for i := range result {
		result[i] = make([]float64, cols2)
	}

	// Вычисляем произведение
	for i := 0; i < rows1; i++ {
		for j := 0; j < cols2; j++ {
			for k := 0; k < cols1; k++ {
				result[i][j] += matrix1[i][k] * matrix2[k][j]
			}
		}
	}

	return result
}

// кубическая/строковая норма матрицы
func matrixNorm(matrix [][]float64) float64 {
	var maxSum float64

	for i := range matrix {
		var sum float64
		for j := range matrix[0] {
			sum += math.Abs(matrix[i][j])
		}
		if sum > maxSum {
			maxSum = sum
		}
	}

	return maxSum
}