package main

import (
	"bufio"
	"fmt"
	"io/fs"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

const (
	dataDir   = "/var/data"
	resultDir = "/var/result"
)

func main() {
	fmt.Println("Worker1: Начинаю обработку файлов...")

	if err := os.MkdirAll(resultDir, 0755); err != nil {
		fmt.Printf("Ошибка создания каталога %s: %v\n", resultDir, err)
		os.Exit(1)
	}

	resultFile, err := os.Create(filepath.Join(resultDir, "data.txt"))
	if err != nil {
		fmt.Printf("Ошибка создания файла результата: %v\n", err)
		os.Exit(1)
	}
	defer resultFile.Close()

	err = filepath.WalkDir(dataDir, func(path string, d fs.DirEntry, err error) error {
		if err != nil {
			return err
		}

		if d.IsDir() {
			return nil
		}

		if strings.HasSuffix(strings.ToLower(path), ".txt") {
			fmt.Printf("Обрабатываю файл: %s\n", filepath.Base(path))

			file, err := os.Open(path)
			if err != nil {
				fmt.Printf("Ошибка открытия файла %s: %v\n", path, err)
				return err
			}
			defer file.Close()

			// Читаем первую строку
			scanner := bufio.NewScanner(file)
			if scanner.Scan() {
				line := strings.TrimSpace(scanner.Text())
				if _, err := strconv.Atoi(line); err == nil {
					_, err := resultFile.WriteString(line + "\n")
					if err != nil {
						fmt.Printf("Ошибка записи в файл результата: %v\n", err)
						return err
					}
					fmt.Printf("Добавлено число: %s\n", line)
				} else {
					fmt.Printf("Пропускаю нечисловую строку: %s\n", line)
				}
			}

			if err := scanner.Err(); err != nil {
				fmt.Printf("Ошибка чтения файла %s: %v\n", path, err)
				return err
			}
		}

		return nil
	})
	if err != nil {
		fmt.Printf("Ошибка при обходе каталога %s: %v\n", dataDir, err)
		os.Exit(1)
	}

	fmt.Println("Worker1: Обработка завершена успешно!")
	fmt.Printf("Результат сохранен в файл: %s\n", filepath.Join(resultDir, "data.txt"))
}
