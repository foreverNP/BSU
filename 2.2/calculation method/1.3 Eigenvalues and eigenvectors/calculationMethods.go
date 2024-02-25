package main

import "math"

// вычитание векторов
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

// умножение матриц на скаляр
func multiplyMatrixByScalar(matrix [][]float64, scalar float64) [][]float64 {
	rows := len(matrix)
	cols := len(matrix[0])

	result := make([][]float64, rows)

	for i := 0; i < rows; i++ {
		result[i] = make([]float64, cols)
		for j := 0; j < cols; j++ {
			result[i][j] = matrix[i][j] * scalar
		}
	}

	return result
}

// транспонирует заданную матрицу.
func transposeMatrix(matrix [][]float64) [][]float64 {
	// Определяем количество строк и столбцов в исходной матрице.
	rows := len(matrix)
	cols := len(matrix[0])

	// Создаем новую матрицу с перевернутыми размерами (количество строк становится количеством столбцов и наоборот).
	result := make([][]float64, cols)

	// Инициализируем новую матрицу.
	for i := 0; i < cols; i++ {
		result[i] = make([]float64, rows)
	}

	// Заполняем новую матрицу элементами, транспонированными из исходной матрицы.
	for i := 0; i < rows; i++ {
		for j := 0; j < cols; j++ {
			result[j][i] = matrix[i][j]
		}
	}

	return result
}

func findMaxOffDiagonalElement(A [][]float64) (int, int) {
	maxVal := 0.0
	p, q := 0, 0

	for i := 0; i < len(A)-1; i++ {
		for j := i + 1; j < len(A); j++ {
			if math.Abs(A[i][j]) > maxVal {
				maxVal = math.Abs(A[i][j])
				p = i
				q = j
			}
		}
	}

	return p, q
}

func off(A [][]float64) float64 {
	maxVal := 0.0

	for i := 0; i < len(A)-1; i++ {
		for j := i + 1; j < len(A); j++ {
			maxVal += 2 * A[i][j] * A[i][j]
		}
	}

	return maxVal
}
