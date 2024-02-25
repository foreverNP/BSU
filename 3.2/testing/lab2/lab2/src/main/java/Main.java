
/**
 * Пакет с главным классом программы для демонстрации возможностей класса Trapezium.
 */

import Trapezium.Trapezium;
import Trapezium.Trapezium.TrapeziumSortBy;
import Point.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Главный класс программы, демонстрирующий функциональность класса Trapezium.
 * Показывает создание трапеций, вычисление площади и периметра, сортировку и
 * итерацию.
 *
 * @author Лев Сергиенко
 * @version 1.1
 */
public class Main {
    /**
     * Точка входа в приложение.
     * Демонстрирует различные возможности класса Trapezium.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        System.out.println("===== Демонстрация класса Trapezium =====");

        // 1. Демонстрация разных конструкторов
        demoConstructors();

        // 2. Демонстрация вычисления площади и периметра
        demoAreaAndPerimeter();

        // 3. Демонстрация проверки на трапецию
        demoIsTrapezium();

        // 4. Демонстрация сравнения и сортировки
        demoSortingAndComparison();

        // 5. Демонстрация итератора
        demoIterator();

        // 6. Демонстрация работы с углами
        demoCosAngle();
    }

    /**
     * Демонстрирует различные способы создания объектов Trapezium.
     * Показывает работу конструктора по умолчанию, параметризованного конструктора
     * и конструктора из строкового представления.
     */
    private static void demoConstructors() {
        System.out.println("\n----- Демонстрация конструкторов -----");

        // Конструктор по умолчанию
        Trapezium defaultTrapezium = new Trapezium();
        System.out.println("Трапеция по умолчанию:");
        System.out.println(defaultTrapezium);

        // Параметризованный конструктор
        Point p1 = new Point(0, 0);
        Point p2 = new Point(10, 0);
        Point p3 = new Point(8, 5);
        Point p4 = new Point(2, 5);
        Trapezium customTrapezium = new Trapezium(p1, p2, p3, p4, 255, 0, 0, 0, 255, 0);
        System.out.println("Пользовательская трапеция:");
        System.out.println(customTrapezium);

        // Конструктор из строки
        String trapeziumString = "Border - rgb(128, 0, 128)\n" +
                "Filling - rgb(255, 255, 0)\n" +
                "First coordinate: (1, 1)\n" +
                "Second coordinate: (11, 1)\n" +
                "Third coordinate: (9, 6)\n" +
                "Fourth coordinate: (3, 6)";
        Trapezium fromStringTrapezium = new Trapezium(trapeziumString);
        System.out.println("Трапеция из строки:");
        System.out.println(fromStringTrapezium);

        // Демонстрация геттеров для точек и цветов
        System.out.println("\nДемонстрация геттеров:");
        Point firstPoint = fromStringTrapezium.getP1();
        System.out.println("Первая точка: (" + firstPoint.getX() + ", " + firstPoint.getY() + ")");

        int[] borderColor = fromStringTrapezium.getBorderColor();
        System.out
                .println("Цвет границы RGB: (" + borderColor[0] + ", " + borderColor[1] + ", " + borderColor[2] + ")");

        int[] fillingColor = fromStringTrapezium.getFillingColor();
        System.out.println(
                "Цвет заливки RGB: (" + fillingColor[0] + ", " + fillingColor[1] + ", " + fillingColor[2] + ")");
    }

    /**
     * Демонстрирует методы расчета площади и периметра трапеции.
     * Показывает результаты для разных трапеций.
     */
    private static void demoAreaAndPerimeter() {
        System.out.println("\n----- Демонстрация площади и периметра -----");

        // Создаем правильную трапецию
        Point p1 = new Point(0, 0);
        Point p2 = new Point(10, 0);
        Point p3 = new Point(8, 5);
        Point p4 = new Point(2, 5);
        Trapezium trapezium = new Trapezium(p1, p2, p3, p4, 0, 0, 0, 0, 0, 0);

        // Вычисляем и выводим площадь и периметр
        double area = trapezium.Area();
        double perimeter = trapezium.Perimetr();

        System.out.println("Для трапеции:");
        System.out.println("Координаты: (0,0), (10,0), (8,5), (2,5)");
        System.out.println("Площадь: " + String.format("%.2f", area));
        System.out.println("Периметр: " + String.format("%.2f", perimeter));

        // Создаем другую трапецию с разными размерами
        Point q1 = new Point(0, 0);
        Point q2 = new Point(20, 0);
        Point q3 = new Point(15, 10);
        Point q4 = new Point(5, 10);
        Trapezium largeTrapezium = new Trapezium(q1, q2, q3, q4, 0, 0, 0, 0, 0, 0);

        // Вычисляем и выводим площадь и периметр
        area = largeTrapezium.Area();
        perimeter = largeTrapezium.Perimetr();

        System.out.println("\nДля большей трапеции:");
        System.out.println("Координаты: (0,0), (20,0), (15,10), (5,10)");
        System.out.println("Площадь: " + String.format("%.2f", area));
        System.out.println("Периметр: " + String.format("%.2f", perimeter));
    }

    /**
     * Демонстрирует метод isTrapezium для проверки,
     * является ли четырехугольник действительно трапецией.
     * Показывает результаты для трапеции и произвольного четырехугольника.
     */
    private static void demoIsTrapezium() {
        System.out.println("\n----- Демонстрация проверки на трапецию -----");

        // Создаем трапецию (две параллельные стороны)
        Point p1 = new Point(0, 0);
        Point p2 = new Point(10, 0); // Основание параллельно оси X
        Point p3 = new Point(8, 5);
        Point p4 = new Point(2, 5); // Верхняя сторона параллельна оси X
        Trapezium trapezium = new Trapezium(p1, p2, p3, p4, 0, 0, 0, 0, 0, 0);

        // Проверяем и выводим результат
        boolean isTrapezium = trapezium.isTrapezium();
        System.out.println("Фигура с координатами (0,0), (10,0), (8,5), (2,5)");
        System.out.println("Является трапецией: " + isTrapezium);

        // Создаем произвольный четырехугольник (не трапецию)
        Point q1 = new Point(0, 0);
        Point q2 = new Point(10, 0);
        Point q3 = new Point(12, 7); // Нарушаем параллельность
        Point q4 = new Point(2, 5);
        Trapezium notTrapezium = new Trapezium(q1, q2, q3, q4, 0, 0, 0, 0, 0, 0);

        // Проверяем и выводим результат
        isTrapezium = notTrapezium.isTrapezium();
        System.out.println("\nФигура с координатами (0,0), (10,0), (12,7), (2,5)");
        System.out.println("Является трапецией: " + isTrapezium);
    }

    /**
     * Демонстрирует функциональность сравнения и сортировки трапеций.
     * Показывает использование различных критериев сортировки.
     */
    private static void demoSortingAndComparison() {
        System.out.println("\n----- Демонстрация сравнения и сортировки -----");

        // Создаем список трапеций
        List<Trapezium> trapeziums = new ArrayList<>();

        // Добавляем трапеции с разными параметрами
        trapeziums.add(new Trapezium(
                new Point(0, 0),
                new Point(10, 0),
                new Point(8, 5),
                new Point(2, 5),
                255, 0, 0, // красная граница
                200, 200, 200 // серая заливка
        ));

        trapeziums.add(new Trapezium(
                new Point(5, 5),
                new Point(15, 5),
                new Point(13, 10),
                new Point(7, 10),
                0, 255, 0, // зеленая граница
                100, 100, 100 // темно-серая заливка
        ));

        trapeziums.add(new Trapezium(
                new Point(-5, -5),
                new Point(5, -5),
                new Point(3, 0),
                new Point(-3, 0),
                0, 0, 255, // синяя граница
                50, 50, 50 // очень темно-серая заливка
        ));

        // Выводим исходный список
        System.out.println("Исходный список трапеций:");
        printTrapeziumList(trapeziums);

        // Демонстрация различных критериев сортировки

        // 1. Сортировка по X-координате первой точки
        Trapezium.setSortBy(TrapeziumSortBy.firstX);
        Collections.sort(trapeziums);
        System.out.println("\nСортировка по X-координате первой точки:");
        printTrapeziumList(trapeziums);

        // 2. Сортировка по Y-координате второй точки
        Trapezium.setSortBy(TrapeziumSortBy.secondY);
        Collections.sort(trapeziums);
        System.out.println("\nСортировка по Y-координате второй точки:");
        printTrapeziumList(trapeziums);

        // 3. Сортировка по красной компоненте цвета границы
        Trapezium.setSortBy(TrapeziumSortBy.R_border);
        Collections.sort(trapeziums);
        System.out.println("\nСортировка по красной компоненте цвета границы:");
        printTrapeziumList(trapeziums);

        // 4. Демонстрация компаратора
        Collections.sort(trapeziums, Trapezium.getComparator(TrapeziumSortBy.B_filling));
        System.out.println("\nСортировка с использованием компаратора по синей компоненте заливки:");
        printTrapeziumList(trapeziums);
    }

    /**
     * Вспомогательный метод для вывода списка трапеций.
     * 
     * @param trapeziums Список трапеций для вывода.
     */
    private static void printTrapeziumList(List<Trapezium> trapeziums) {
        for (int i = 0; i < trapeziums.size(); i++) {
            Trapezium t = trapeziums.get(i);
            System.out.printf(
                    "Трапеция %d: верхний левый угол (%d, %d), цвет границы RGB(%d, %d, %d)%n",
                    i + 1,
                    t.getP1().getX(), t.getP1().getY(),
                    t.getBorderColor()[0], t.getBorderColor()[1], t.getBorderColor()[2]);
        }
    }

    /**
     * Демонстрирует работу итератора для перебора свойств трапеции.
     * Показывает последовательный доступ к данным трапеции.
     */
    private static void demoIterator() {
        System.out.println("\n----- Демонстрация итератора -----");

        // Создаем трапецию с известными параметрами
        Trapezium trapezium = new Trapezium(
                new Point(0, 0),
                new Point(10, 0),
                new Point(8, 5),
                new Point(2, 5),
                255, 128, 64, // оранжевая граница
                32, 128, 255 // голубая заливка
        );

        System.out.println("Перебор свойств трапеции через итератор:");

        // Получаем итератор и перечисляем свойства
        Iterator<String> iterator = trapezium.iterator();
        String[] propertyNames = {
                "R компонента границы",
                "G компонента границы",
                "B компонента границы",
                "R компонента заливки",
                "G компонента заливки",
                "B компонента заливки",
                "Координаты точки 1",
                "Координаты точки 2",
                "Координаты точки 3",
                "Координаты точки 4"
        };

        int index = 0;
        while (iterator.hasNext()) {
            String value = iterator.next();
            System.out.println(propertyNames[index] + ": " + value);
            index++;
        }

        // Демонстрация цикла for-each (используется итератор неявно)
        System.out.println("\nИспользование цикла for-each:");
        index = 0;
        for (String property : trapezium) {
            System.out.println(propertyNames[index] + ": " + property);
            index++;
        }
    }

    /**
     * Демонстрирует метод вычисления косинуса угла между векторами.
     * Показывает различные случаи углов в трапеции.
     */
    private static void demoCosAngle() {
        System.out.println("\n----- Демонстрация вычисления косинуса угла -----");

        // Создаем трапецию
        Point p1 = new Point(0, 0);
        Point p2 = new Point(10, 0);
        Point p3 = new Point(8, 5);
        Point p4 = new Point(2, 5);
        Trapezium trapezium = new Trapezium(p1, p2, p3, p4, 0, 0, 0, 0, 0, 0);

        // Вычисляем косинусы углов
        double cosAngle1 = trapezium.cosAngle(p1, p2, p1, p4);
        double cosAngle2 = trapezium.cosAngle(p2, p3, p2, p1);
        double cosAngle3 = trapezium.cosAngle(p3, p4, p3, p2);
        double cosAngle4 = trapezium.cosAngle(p4, p1, p4, p3);

        // Вычисляем углы в градусах (арккосинус)
        double angle1 = Math.toDegrees(Math.acos(cosAngle1));
        double angle2 = Math.toDegrees(Math.acos(cosAngle2));
        double angle3 = Math.toDegrees(Math.acos(cosAngle3));
        double angle4 = Math.toDegrees(Math.acos(cosAngle4));

        System.out.println("Трапеция с координатами (0,0), (10,0), (8,5), (2,5)");
        System.out.println("Косинус угла 1: " + String.format("%.4f", cosAngle1) +
                " (угол: " + String.format("%.1f", angle1) + "°)");
        System.out.println("Косинус угла 2: " + String.format("%.4f", cosAngle2) +
                " (угол: " + String.format("%.1f", angle2) + "°)");
        System.out.println("Косинус угла 3: " + String.format("%.4f", cosAngle3) +
                " (угол: " + String.format("%.1f", angle3) + "°)");
        System.out.println("Косинус угла 4: " + String.format("%.4f", cosAngle4) +
                " (угол: " + String.format("%.1f", angle4) + "°)");

        // Проверка на прямой угол (косинус равен 0)
        Point r1 = new Point(0, 0);
        Point r2 = new Point(10, 0);
        Point r3 = new Point(10, 5);
        Point r4 = new Point(0, 5);
        Trapezium rectangle = new Trapezium(r1, r2, r3, r4, 0, 0, 0, 0, 0, 0);

        double cornerCos = rectangle.cosAngle(r1, r2, r1, r4);
        double cornerAngle = Math.toDegrees(Math.acos(cornerCos));

        System.out.println("\nДля прямоугольника:");
        System.out.println("Косинус прямого угла: " + String.format("%.8f", cornerCos));
        System.out.println("Угол в градусах: " + String.format("%.1f", cornerAngle) + "°");
    }
}