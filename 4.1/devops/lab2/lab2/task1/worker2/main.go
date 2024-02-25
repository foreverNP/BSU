package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

const (
	dataFile  = "/var/data/data.txt"
	resultDir = "/var/result"
)

func main() {
	fmt.Println("Worker2: Начинаю поиск минимального числа...")

	if err := os.MkdirAll(resultDir, 0755); err != nil {
		fmt.Printf("Ошибка создания каталога %s: %v\n", resultDir, err)
		os.Exit(1)
	}

	file, err := os.Open(dataFile)
	if err != nil {
		fmt.Printf("Ошибка открытия файла %s: %v\n", dataFile, err)
		os.Exit(1)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	minNumber := math.MaxInt64
	var numbers []int

	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		if line == "" {
			continue
		}

		number, err := strconv.Atoi(line)
		if err != nil {
			fmt.Printf("Пропускаю нечисловую строку: %s\n", line)
			continue
		}

		numbers = append(numbers, number)
		if number < minNumber {
			minNumber = number
		}
	}

	if err := scanner.Err(); err != nil {
		fmt.Printf("Ошибка чтения файла: %v\n", err)
		os.Exit(1)
	}

	if len(numbers) == 0 {
		fmt.Println("В файле не найдено ни одного числа!")
		os.Exit(1)
	}

	cube := minNumber * minNumber * minNumber

	fmt.Printf("Найденные числа: %v\n", numbers)
	fmt.Printf("Минимальное число: %d\n", minNumber)
	fmt.Printf("Третья степень минимального числа: %d\n", cube)

	resultFile, err := os.Create(filepath.Join(resultDir, "result.txt"))
	if err != nil {
		fmt.Printf("Ошибка создания файла результата: %v\n", err)
		os.Exit(1)
	}
	defer resultFile.Close()

	_, err = resultFile.WriteString(fmt.Sprintf("%d\n", cube))
	if err != nil {
		fmt.Printf("Ошибка записи в файл результата: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("Результат сохранен в файл: %s\n", filepath.Join(resultDir, "result.txt"))
	fmt.Println("Worker2: Обработка завершена успешно!")
}
