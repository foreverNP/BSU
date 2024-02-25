package main

import (
	"fmt"
	"math"
)

const N = 10
const eps = 1.0e-6

func jacobiMethod(A [][]float64) ([][]float64, []float64, []float64, int) {
	// Инициализация единичной матрицы Q и копии матрицы A
	At := make([][]float64, len(A))
	Ak := make([][]float64, len(A))
	Q := make([][]float64, len(A))
	Qk := make([][]float64, len(A))
	for i := range Q {
		Q[i] = make([]float64, len(A))
		Qk[i] = make([]float64, len(A))
		At[i] = make([]float64, len(A))
		Ak[i] = make([]float64, len(A))

		Q[i][i] = 1.0
		Qk[i][i] = 1.0

		for j := range Q {
			At[i][j] = A[i][j]
		}
	}

	counter := 0
	for m, e := findMaxOffDiagonalElement(At); off(At) > eps; m, e = findMaxOffDiagonalElement(At) {
		a := At[m][m]
		b := At[e][e]

		z := (b - a) / 2.0 / At[m][e]

		t := 0.0

		if z == 0 {
			t = 1
		} else {
			t = math.Abs(z) / z / (math.Abs(z) + math.Sqrt(z*z+1))
		}

		c := 1 / math.Sqrt(1+t*t)
		s := t * c

		Ak[m][e] = 0
		Ak[e][m] = 0

		Ak[m][m] = At[m][m] - t*At[m][e]
		Ak[e][e] = At[e][e] + t*At[m][e]
		for i := 0; i < len(A); i++ {
			if m != i && e != i {
				Ak[i][m] = At[m][i] - s*(At[e][i]+s/(1+c)*At[m][i])
				Ak[m][i] = Ak[i][m]

				Ak[i][e] = At[e][i] + s*(At[m][i]-s/(1+c)*At[e][i])
				Ak[e][i] = Ak[i][e]
			}
		}
		for i := 0; i < len(A); i++ {
			At[i][m] = Ak[m][i]
			At[m][i] = Ak[m][i]

			At[i][e] = Ak[e][i]
			At[e][i] = Ak[e][i]
		}

		// Обновляем матрицу Q
		for i := 0; i < len(A); i++ {
			Qk[i][m] = c*Q[i][m] - s*Q[i][e]
			Qk[i][e] = s*Q[i][m] + c*Q[i][e]
		}

		for i := 0; i < len(A); i++ {
			Q[i][m] = Qk[i][m]
			Q[i][e] = Qk[i][e]
		}

		counter++
	}

	// Получаем собственные значения из диагонали матрицы A
	eigenvalues := make([]float64, len(A))
	for i := 0; i < len(A); i++ {
		eigenvalues[i] = At[i][i]
	}

	Q = transposeMatrix(Q)

	errors := make([]float64, len(A))
	for i := 0; i < len(A); i++ {
		xi := make([][]float64, 1)
		xi[0] = Q[i]
		xi = transposeMatrix(xi)

		errors[i] = euclideanNorm(subtractVectors(transposeMatrix(multiplyMatrices(A, xi))[0], transposeMatrix(multiplyMatrixByScalar(xi, eigenvalues[i]))[0]))
	}

	return Q, eigenvalues, errors, counter
}

func powerMethod(A [][]float64) ([]float64, float64, float64, int) {
	y := make([][]float64, N)
	u := make([][]float64, N)
	counter := 0

	for i := 0; i < N; i++ {
		y[i] = make([]float64, 1)
		u[i] = make([]float64, 1)
	}
	y[0][0] = 1
	u[0][0] = 1

	h := dotProduct(transposeMatrix(u)[0], transposeMatrix(multiplyMatrices(A, u))[0])

	for euclideanNorm(subtractVectors(transposeMatrix(multiplyMatrices(A, u))[0], transposeMatrix(multiplyMatrixByScalar(u, h))[0])) > eps {
		y = multiplyMatrices(A, u)
		u = multiplyMatrixByScalar(y, 1/euclideanNorm(transposeMatrix(y)[0]))
		h = dotProduct(transposeMatrix(u)[0], transposeMatrix(multiplyMatrices(A, u))[0])
		counter++
	}

	return transposeMatrix(u)[0], h, euclideanNorm(subtractVectors(transposeMatrix(multiplyMatrices(A, u))[0], transposeMatrix(multiplyMatrixByScalar(u, h))[0])), counter
}

func main() {
	A := [][]float64{
		{3, 0, 1},
		{0, 2, -2},
		{1, -2, 2},
	}

	_, v, _, _ := jacobiMethod(A)

	fmt.Println(v)
}
