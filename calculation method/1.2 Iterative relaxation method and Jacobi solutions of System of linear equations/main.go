package main

import (
	"fmt"
	"math"
)

const N = 15
const e = 1.0e-8
const Kmax = 100

// Итерационны метод Якоби решения СЛАУ
func jacobiMethod(A [][]float64, B []float64) ([]float64, int) {
	X1 := make([]float64, len(B))
	X2 := make([]float64, len(B))

	copy(X1, B)

	K := 0
	for ; K < Kmax; K++ {
		for i := 0; i < len(B); i++ {
			sum := 0.0
			for j := 0; j < len(B); j++ {
				if i != j {
					sum += A[i][j] * X1[j]
				}
			}
			X2[i] = (1.0 / A[i][i]) * (B[i] - sum)
		}

		if maxAbsoluteDifference(X2, X1) < e {
			K++
			break
		}

		copy(X1, X2)
	}

	return X2, K
}

// Итерационны метод релаксации решения СЛАУ(при w == 1 превращается в метод Гаусса – Зейделя)
func relaxationMethod(A [][]float64, B []float64, w float64) ([]float64, int) {
	if w <= 0 || w >= 2 {
		panic("Неверный весовой коэффициент!")
	}

	X1 := make([]float64, len(B))
	X2 := make([]float64, len(B))

	copy(X1, B)
	copy(X2, B)

	K := 0
	for ; K < Kmax; K++ {
		for i := 0; i < len(B); i++ {
			sum := 0.0
			for j := 0; j < len(B); j++ {
				if i != j {
					sum += A[i][j] * X2[j]
				}
			}
			X2[i] = (1.0-w)*X2[i] + (w/A[i][i])*(B[i]-sum)
		}

		if maxAbsoluteDifference(X2, X1) < e {
			K++
			break
		}

		copy(X1, X2)
	}

	return X2, K
}

func main() {
	A := make([][]float64, N)
	B := make([]float64, N)
	exactSolution := make([]float64, N)

	//Генерация значений точного решение
	for i := 0; i < N; i++ {
		exactSolution[i] = 9.0 + float64(i)
	}
	//Генерация исходных матриц
	for i := 0; i < N; i++ {
		A[i] = make([]float64, N)

		for j := 0; j < N; j++ {
			if i == j {
				A[i][j] = 11.0 * math.Sqrt(float64(i+1))
			} else {
				A[i][j] = 0.001 * (float64(i+1) / float64(j+1))
			}
			B[i] += A[i][j] * exactSolution[j]
		}
	}

	//////////////////////////////////////////////////////////////////////

	//Проверяем диагональное преобладание
	if !isDiagonallyDominant(A) {
		panic("Not diagonally dominant")
	}

	XJacobi, KJacobi := jacobiMethod(A, B)
	XGaussSeidel, KGaussSeidel := relaxationMethod(A, B, 1)
	XRelaxation5, KRelaxation5 := relaxationMethod(A, B, 0.5)
	XRelaxation15, KRelaxation15 := relaxationMethod(A, B, 1.5)

	//////////////////////////////////////////////////////////////////////

	//Вывод
	fmt.Print("A = ")
	for _, row := range A {
		fmt.Print("\t")
		fmt.Printf("%.5f\n", row)
	}
	fmt.Printf("%s\t%.4f\n", "B = ", B)

	fmt.Println("\nJacobi Method:")
	fmt.Printf("%s\t%.4f\n", "X = ", XJacobi)
	fmt.Printf("%s\t%d\n", "K = ", KJacobi)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XJacobi, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XJacobi, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nGauss-Seidel Method:")
	fmt.Printf("%s\t%.4f\n", "X = ", XGaussSeidel)
	fmt.Printf("%s\t%d\n", "K = ", KGaussSeidel)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XGaussSeidel, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XGaussSeidel, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nRelaxation Method(w = 0.5):")
	fmt.Printf("%s\t%.4f\n", "X = ", XRelaxation5)
	fmt.Printf("%s\t%d\n", "K = ", KRelaxation5)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XRelaxation5, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XRelaxation5, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nRelaxation Method(w = 1.5):")
	fmt.Printf("%s\t%.4f\n", "X = ", XRelaxation15)
	fmt.Printf("%s\t%d\n", "K = ", KRelaxation15)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XRelaxation15, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XRelaxation15, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\n//////////////////////////////////////////////////////////////////////")
	//////////////////////////////////////////////////////////////////////

	//Модификация исходных значений
	for i := 1; i < N; i++ {
		sum := 0.0
		for j := 0; j < N; j++ {
			if i != j {
				sum += math.Abs(A[i][j])
			}
		}
		A[i][i] = sum
	}

	B = make([]float64, N)
	for i := 0; i < N; i++ {
		for j := 0; j < N; j++ {
			B[i] += A[i][j] * exactSolution[j]
		}
	}

	//////////////////////////////////////////////////////////////////////

	XJacobi, KJacobi = jacobiMethod(A, B)
	XGaussSeidel, KGaussSeidel = relaxationMethod(A, B, 1)
	XRelaxation5, KRelaxation5 = relaxationMethod(A, B, 0.5)
	XRelaxation15, KRelaxation15 = relaxationMethod(A, B, 1.5)

	//////////////////////////////////////////////////////////////////////

	//Вывод
	fmt.Print("A = ")
	for _, row := range A {
		fmt.Print("\t")
		fmt.Printf("%.5f\n", row)
	}
	fmt.Printf("%s\t%.4f\n", "B = ", B)

	fmt.Println("\nJacobi Method:")
	fmt.Printf("%s\t%.4f\n", "X = ", XJacobi)
	fmt.Printf("%s\t%d\n", "K = ", KJacobi)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XJacobi, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XJacobi, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nGauss-Seidel Method:")
	fmt.Printf("%s\t%.4f\n", "X = ", XGaussSeidel)
	fmt.Printf("%s\t%d\n", "K = ", KGaussSeidel)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XGaussSeidel, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XGaussSeidel, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nRelaxation Method(w = 0.5):")
	fmt.Printf("%s\t%.4f\n", "X = ", XRelaxation5)
	fmt.Printf("%s\t%d\n", "K = ", KRelaxation5)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XRelaxation5, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XRelaxation5, exactSolution))/uniformNorm(exactSolution))

	fmt.Println("\nRelaxation Method(w = 1.5):")
	fmt.Printf("%s\t%.4f\n", "X = ", XRelaxation15)
	fmt.Printf("%s\t%d\n", "K = ", KRelaxation15)
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| = ", uniformNorm(subtractVectors(XRelaxation15, exactSolution)))
	fmt.Printf("%s\t%.32f\n", "||X - Exact solution|| / ||Exact solution|| = ",
		uniformNorm(subtractVectors(XRelaxation15, exactSolution))/uniformNorm(exactSolution))
}
