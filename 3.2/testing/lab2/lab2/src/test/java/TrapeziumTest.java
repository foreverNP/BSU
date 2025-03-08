import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import Point.Point;
import Trapezium.Trapezium;
import Trapezium.Trapezium.TrapeziumSortBy;

public class TrapeziumTest {
    private Point p1, p2, p3, p4;
    private Trapezium defaultTrapezium;
    private Trapezium customTrapezium;
    private Trapezium stringTrapezium;

    @Before
    public void setUp() {
        p1 = new Point(0, 0);
        p2 = new Point(10, 0);
        p3 = new Point(8, 5);
        p4 = new Point(2, 5);

        defaultTrapezium = new Trapezium();

        customTrapezium = new Trapezium(p1, p2, p3, p4, 255, 0, 0, 0, 255, 0);

        String trapeziumStr = "Border - rgb(128, 0, 128)\n" +
                "Filling - rgb(255, 255, 0)\n" +
                "First coordinate: (1, 1)\n" +
                "Second coordinate: (11, 1)\n" +
                "Third coordinate: (9, 6)\n" +
                "Fourth coordinate: (3, 6)";
        stringTrapezium = new Trapezium(trapeziumStr);
    }

    // ===================== Тесты конструкторов =====================

    // Позитивный тест конструктора по умолчанию
    @Test
    public void testDefaultConstructorPositive() {
        assertNotNull("Объект, созданный конструктором по умолчанию, не должен быть null", defaultTrapezium);
    }

    // Позитивный тест параметризованного конструктора
    @Test
    public void testParameterizedConstructorPositive() {
        assertNotNull("Пользовательский объект не должен быть null", customTrapezium);

        // Проверяем, что первая точка соответствует переданной
        assertEquals("Неверное значение X для первой точки", p1.getX(), customTrapezium.getP1().getX());
        assertEquals("Неверное значение Y для первой точки", p1.getY(), customTrapezium.getP1().getY());

        // Проверяем цвета
        int[] expectedBorder = { 255, 0, 0 };
        int[] expectedFilling = { 0, 255, 0 };
        assertArrayEquals("Цвет границы не соответствует", expectedBorder, customTrapezium.getBorderColor());
        assertArrayEquals("Цвет заливки не соответствует", expectedFilling, customTrapezium.getFillingColor());
    }

    // Позитивный тест конструктора из строки
    @Test
    public void testStringConstructorPositive() {
        assertNotNull("Объект, созданный из строки, не должен быть null", stringTrapezium);

        Point sp1 = stringTrapezium.getP1();
        assertEquals("Неверное значение X для первой точки (строковый конструктор)", 1, sp1.getX());
        assertEquals("Неверное значение Y для первой точки (строковый конструктор)", 1, sp1.getY());
    }

    // Негативный тест: передача null в параметризованный конструктор
    @Test(expected = IllegalArgumentException.class)
    public void testParameterizedConstructorNegativeNullPoint() {
        new Trapezium(null, p2, p3, p4, 255, 255, 255, 255, 255, 255);
    }

    // Негативный тест: некорректный формат строки для конструктора
    @Test(expected = IllegalArgumentException.class)
    public void testStringConstructorNegativeInvalidFormat() {
        new Trapezium("Некорректная строка для создания трапеции");
    }

    // Негативный тест: передача null в конструктор из строки
    @Test(expected = IllegalArgumentException.class)
    public void testStringConstructorNegativeNullInput() {
        new Trapezium((String) null);
    }

    // Негативный тест: передача пустой строки в конструктор
    @Test(expected = IllegalArgumentException.class)
    public void testStringConstructorNegativeEmptyInput() {
        new Trapezium("   ");
    }

    // Негативный тест: неверный компонент цвета
    @Test(expected = IllegalArgumentException.class)
    public void testColorComponentOutOfRange() {
        new Trapezium(p1, p2, p3, p4, 300, 0, 0, 0, 255, 0);
    }

    // ===================== Тесты расчёта площади и периметра =====================

    // Позитивный тест для Area() и Perimetr()
    @Test
    public void testAreaAndPerimeterPositive() {
        double area = customTrapezium.Area();
        double perimeter = customTrapezium.Perimetr();

        // Для точек (0,0), (10,0), (8,5), (2,5) площадь должна быть: ((10+6)/2)*5 = 40
        assertEquals("Неверно вычислена площадь", 40.0, area, 0.01);

        // Периметр – сумма длин сторон:
        double expectedPerimeter = 10 + Math.sqrt(29) + 6 + Math.sqrt(29);
        assertEquals("Неверно вычислен периметр", expectedPerimeter, perimeter, 0.01);
    }

    // Негативный тест: проверка расчёта площади для вырожденной трапеции
    @Test(expected = ArithmeticException.class)
    public void testAreaNegativeDegenerateTrapezium() {
        // Все точки лежат на одной прямой – вырожденный случай
        Point a = new Point(0, 0);
        Point b = new Point(5, 0);
        Point c = new Point(10, 0);
        Point d = new Point(15, 0);
        Trapezium degenerate = new Trapezium(a, b, c, d, 0, 0, 0, 0, 0, 0);
        degenerate.Area();
    }

    // ===================== Тесты метода isTrapezium() =====================

    // Позитивный тест: фигура является трапецией
    @Test
    public void testIsTrapeziumPositive() {
        assertTrue("Фигура должна быть трапецией", customTrapezium.isTrapezium());
    }

    // Негативный тест: фигура не является трапецией
    @Test
    public void testIsTrapeziumNegative() {
        Trapezium notTrapezium = new Trapezium(
                new Point(0, 0),
                new Point(10, 0),
                new Point(12, 7), // нарушена параллельность сторон
                new Point(2, 5),
                0, 0, 0, 0, 0, 0);
        assertFalse("Фигура не должна распознаваться как трапеция", notTrapezium.isTrapezium());
    }

    // ===================== Тесты метода cosAngle() =====================

    // Позитивный тест для cosAngle
    @Test
    public void testCosAnglePositive() {
        double cosAngle = customTrapezium.cosAngle(p1, p2, p1, p4);

        double expected = 2 / Math.sqrt(29);
        assertEquals("Неверно вычислен косинус угла", expected, cosAngle, 0.01);
    }

    // Негативный тест для cosAngle: передача null
    @Test(expected = IllegalArgumentException.class)
    public void testCosAngleNegativeNullParameter() {
        customTrapezium.cosAngle(null, p2, p1, p4);
    }

    // ===================== Тесты итератора (iterator()) =====================

    // Позитивный тест для итератора – проверяем количество возвращаемых свойств
    @Test
    public void testIteratorPositive() {
        Iterator<String> iterator = customTrapezium.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        assertEquals("Неверное число элементов, возвращаемых итератором", 10, count);
    }

    // Негативный тест для итератора – вызов next() после исчерпания элементов
    @Test(expected = NoSuchElementException.class)
    public void testIteratorNegativeNoSuchElement() {
        Iterator<String> iterator = customTrapezium.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        // Повторный вызов next() должен привести к исключению
        iterator.next();
    }

    // ===================== Тесты сортировки и компаратора =====================

    // Позитивный тест сортировки списка трапеций
    @Test
    public void testSortingPositive() {
        List<Trapezium> list = new ArrayList<>();
        Trapezium t1 = new Trapezium(new Point(0, 0), new Point(10, 0),
                new Point(8, 5), new Point(2, 5), 255, 0, 0, 0, 0, 0);
        Trapezium t2 = new Trapezium(new Point(5, 5), new Point(15, 5),
                new Point(13, 10), new Point(7, 10), 0, 255, 0, 0, 0, 0);
        list.add(t2);
        list.add(t1);

        // Сортировка по X-координате первой точки
        Trapezium.setSortBy(TrapeziumSortBy.firstX);
        Collections.sort(list);
        assertEquals("Сортировка по первой точке: неверное значение X",
                0, list.get(0).getP1().getX());

        // Сортировка по Y-координате второй точки
        Trapezium.setSortBy(TrapeziumSortBy.secondY);
        Collections.sort(list);

        assertTrue("Сортировка по второй точке: неверное сравнение Y",
                list.get(0).getP2().getY() <= list.get(1).getP2().getY());
    }

    // Негативный тест: установка режима сортировки в null
    @Test(expected = IllegalArgumentException.class)
    public void testSortingNegativeSetSortByNull() {
        Trapezium.setSortBy(null);
    }

    // Позитивный тест: проверка compareTo() для одинаковых трапеций
    @Test
    public void testCompareToCoverage() {
        Trapezium sameAsCustom = new Trapezium(p1, p2, p3, p4, 255, 0, 0, 0, 255, 0);
        assertEquals("При сравнении идентичных трапеций результат должен быть 0",
                0, customTrapezium.compareTo(sameAsCustom));
    }

    // Позитивный тест: проверка получения компаратора
    @Test
    public void testGetComparatorCoverage() {
        assertNotNull("Метод getComparator() не должен возвращать null",
                Trapezium.getComparator(TrapeziumSortBy.firstX));
    }

    // Позитивный тест: проверка получения текущего критерия сортировки
    @Test
    public void testGetSortByCoverage() {
        Trapezium.setSortBy(TrapeziumSortBy.secondX);
        assertEquals("После установки SortBy значение должно совпадать",
                TrapeziumSortBy.secondX, Trapezium.getSortBy());
    }

    // ===================== Тесты метода toString() =====================

    // Позитивный тест: проверка формата выводимой строки
    @Test
    public void testToStringMethodCoverage() {
        String str = customTrapezium.toString();
        assertNotNull("Результат toString() не должен быть null", str);
        assertTrue("Строка должна содержать данные о границе", str.contains("Border - rgb(255, 0, 0)"));
        assertTrue("Строка должна содержать данные о заливке", str.contains("Filling - rgb(0, 255, 0)"));
        assertTrue("Строка должна содержать координаты первой точки (0, 0)", str.contains("First coordinate: (0, 0)"));
    }
}
