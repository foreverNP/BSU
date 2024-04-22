package main

import (
	"flag"
	"fmt"
	"math"
	"time"
)

const N = 15

// Метод Гаусса
func gaussMethod(A [N][N]float64, B [N]float64) []float64 {
	X := make([]float64, N)

	for i := 0; i < N-1; i++ {
		for j := i + 1; j < N; j++ {
			l := A[j][i] / A[i][i]
			B[j] = B[j] - l*B[i]

			A[j][i] = 0
			for k := i + 1; k < N; k++ {
				A[j][k] = A[j][k] - l*A[i][k]
			}
		}
	}

	//Обратный ход
	for i := N - 1; i >= 0; i-- {
		for j := N - 1; j > i; j-- {
			B[i] = B[i] - X[j]*A[i][j]
		}
		X[i] = B[i] / A[i][i]
	}

	return X
}

// Метод отражений
func reflectionMethod(A [N][N]float64, B [N]float64) ([]float64, [][]float64, [][]float64) {
	X := make([]float64, N)
	Q := make([][]float64, N)
	for i := range Q {
		Q[i] = make([]float64, N)
	}
	for i := range Q { // Q = I
		Q[i][i] = 1
	}

	for i := 0; i < N-1; i++ {
		a := make([]float64, N-i)  // i-ый вектор-столбец элементов от i до N
		ai := make([]float64, N-i) // вектор-столбец, который хотим получить на месте i-ого, ai[0] = euclideanNorm(a), остальные - 0

		for j := 0; j < N-i; j++ {
			a[j] = A[i+j][i]
		}

		ai[0] = euclideanNorm(a)

		w := subtractVectors(a, ai) // искомый вектор нормали преобразования w
		norm := euclideanNorm(w)
		if norm != 0 {
			for i, value := range w {
				w[i] = value / norm
			}
		}

		// Замена i-ого столбца
		for j := i; j < N; j++ {
			A[j][i] = ai[j-i]
		}

		// Вычисление столбоцов от i+1 до N, в каждом меняются элементы с от i+1 до N
		for j := i + 1; j < N; j++ {
			for k := 0; k < N-i; k++ {
				ai[k] = A[k+i][j]
			}

			pr := dotProduct(w, ai)
			for k := i; k < N; k++ {
				A[k][j] = A[k][j] - 2*w[k-i]*pr
			}
		}

		/////////////////////////////////////////////////////////////////////

		// Вычисление изменненого вектора свободных членов
		pr := dotProduct(w, B[i:])
		for j := i; j < N; j++ {
			B[j] = B[j] - 2*w[j-i]*pr
		}

		/////////////////////////////////////////////////////////////////////

		// Вычислние матрицы преобразования
		Qi := make([][]float64, N-i)
		for j := range Qi {
			Qi[j] = make([]float64, N-i)
		}
		for j := range Qi { // Qi = I
			Qi[j][j] = 1
		}
		for j := 0; j < N-i; j++ {
			for k := 0; k < N-i; k++ {
				Qi[j][k] = Qi[j][k] - 2*w[j]*w[k] // I - 2wwT
			}
		}

		QPart := make([][]float64, N) // Подматрица матрицы Q Nx(N-i) последних N-i столбцов
		for j := range QPart {
			for k := 0; k < N-i; k++ {
				QPart[j] = append(QPart[j], Q[j][k+i])
			}
		}

		Qi = multiplyMatrices(QPart, Qi)

		for j := range Q { // Замена последних N-i столбцов на  QPart * Qi
			for k := 0; k < N-i; k++ {
				Q[j][k+i] = Qi[j][k]
			}
		}
	}

	//Обратный ход
	for i := N - 1; i >= 0; i-- {
		for j := N - 1; j > i; j-- {
			B[i] = B[i] - X[j]*A[i][j]
		}
		X[i] = B[i] / A[i][i]
	}

	// R = A
	R := make([][]float64, N)
	for i := range R {
		for j := range R {
			R[i] = append(R[i], A[i][j])
		}
	}

	return X, R, Q
}

func main() {
	var A [N][N]float64
	var B [N]float64

	/////////////////////////////////////////////////////////////////////

	//Генерация исходных значений
	for i := 0; i < N; i++ {
		for j := 0; j < N; j++ {
			if i == j {
				A[i][j] = float64(5 * (i + 1))
			} else {
				A[i][j] = -(float64(i+1) + math.Sqrt(float64(j+1)))
			}
		}
		B[i] = 3.0 * math.Sqrt(float64(i+1))
	}

	/////////////////////////////////////////////////////////////////////

	st1 := time.Now()
	Xg := gaussMethod(A, B)
	t1 := time.Since(st1)

	st2 := time.Now()
	Xr, R, Q := reflectionMethod(A, B)
	t2 := time.Since(st2)

	/////////////////////////////////////////////////////////////////////

	AXg := make([]float64, N) //Произведение A*Xg
	AXr := make([]float64, N) //Произведение A*Xr

	for i := 0; i < N; i++ {
		for j := 0; j < N; j++ {
			AXg[i] += A[i][j] * Xg[j]
			AXr[i] += A[i][j] * Xr[j]
		}
	}

	QR := multiplyMatrices(Q, R)
	//Отнимаем от QR матрицу A
	for i := range QR {
		for j := range QR[i] {
			QR[i][j] -= A[i][j]
		}
	}

	/////////////////////////////////////////////////////////////////////

	//Вывод

	verbose := flag.Bool("p", false, "Включить подробный вывод")
	flag.Parse()  // Парсинг флагов командной строки
	if *verbose { // Если флаг -p указан, выводим дополнительную информацию
		fmt.Print("A = ")
		for _, row := range A {
			fmt.Print("\t")
			fmt.Printf("%.4f\n", row)
		}
		fmt.Printf("%s\t%.4f\n", "B = ", B)
		fmt.Printf("%s\t%.4f\n", "Xg = ", Xg)
		fmt.Printf("%s\t%.4f\n", "Xr = ", Xr)

		fmt.Print("Q = ")
		for _, row := range Q {
			fmt.Print("\t")
			fmt.Printf("%.4f\n", row)
		}
		fmt.Print("R = ")
		for _, row := range R {
			fmt.Print("\t")
			fmt.Printf("%.4f\n", row)
		}
	}

	fmt.Printf("%s\t%.32f\n", "||AXg - B|| = ", euclideanNorm(subtractVectors(AXg, B[:])))
	fmt.Printf("%s\t%.32f\n", "||AXr - B|| = ", euclideanNorm(subtractVectors(AXr, B[:])))
	fmt.Printf("%s\t%.32f\n", "||QR - A|| = ", matrixNorm(QR))

	fmt.Printf("%-31s%s\n", "Gauss method time = ", t1)
	fmt.Printf("%-31s%s\n", "Reflection method time(+ QR) = ", t2)
}
