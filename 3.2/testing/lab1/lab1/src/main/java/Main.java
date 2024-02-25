import Trapezium.Trapezium;
import Point.Point;

import java.util.Arrays;

/**
 * Основной класс приложения для демонстрации функционала класса Trapezium.
 * Включает примеры создания трапеций, работы с итератором, сортировкой,
 * вычисления характеристик и парсинга данных.
 *
 * @author Лев Сергиенко
 * @version 1.0
 */
public class Main {
    /**
     * Точка входа в программу. Выполняет:
     * 1. Создание трапеции с заданными параметрами
     * 2. Демонстрацию работы итератора по точкам трапеции
     * 3. Расчёт и вывод периметра и площади
     * 4. Создание массива трапеций и их сортировку по заданному критерию
     * 5. Тестирование функционала парсинга строкового представления трапеции
     * 
     * @param args Аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация трапеции с произвольными координатами и параметрами
        Trapezium f = new Trapezium(
                new Point(0, 0),
                new Point(-1, 1),
                new Point(-3, 1),
                new Point(-4, 0),
                2, 25, 67, 89, 90, 12);

        // Демонстрация перебора точек трапеции через итератор
        System.out.println("Точки трапеции:");
        for (var point : f) {
            System.out.println(point);
        }
        System.out.println("*****************************************************************");

        // Вывод полной информации о трапеции
        System.out.println("Информация о трапеции:\n" + f);

        // Расчёт и вывод основных геометрических характеристик
        System.out.println("Периметр: " + f.Perimetr());
        System.out.println("Площадь: " + f.Area());

        // Создание массива трапеций с разными параметрами для демонстрации сортировки
        Trapezium[] trapeziums = new Trapezium[5];
        for (int i = 0; i < 5; i++) {
            trapeziums[i] = new Trapezium(
                    new Point(i, 0),
                    new Point(i - 1, 1),
                    new Point(i - 3, 1),
                    new Point(i - 4, 0),
                    2 * i, 25, 72 - i, 89, 90, 12);
        }

        // Настройка критерия сортировки по цвету заливки
        Trapezium.setSortBy(Trapezium.TrapeziumSortBy.B_filling);

        // Сортировка массива с использованием заданного критерия
        Arrays.sort(trapeziums);

        // Вывод отсортированного массива трапеций
        System.out.println("\nОтсортированные трапеции:");
        for (Trapezium t : trapeziums) {
            System.out.println(t);
        }

        // Проверка работы парсера: преобразование строки в объект Trapezium
        Trapezium parsedTrapezium = new Trapezium();
        boolean parseSuccess = parsedTrapezium.parse(trapeziums[0].toString());

        System.out.println("\nРезультат парсинга:");
        System.out.println(parseSuccess ? parsedTrapezium : "Ошибка парсинга");
    }
}