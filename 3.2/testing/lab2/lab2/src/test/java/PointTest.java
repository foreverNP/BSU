import static org.junit.Assert.*;

import org.junit.Test;
import Point.Point;

public class PointTest {

    // Тест конструктора по умолчанию
    @Test
    public void testDefaultConstructor() {
        Point p = new Point();
        assertEquals("X координата по умолчанию должна быть 0", 0, p.getX());
        assertEquals("Y координата по умолчанию должна быть 0", 0, p.getY());
    }

    // Тест параметризованного конструктора
    @Test
    public void testParameterizedConstructor() {
        Point p = new Point(3, 4);
        assertEquals("Неверное значение X", 3, p.getX());
        assertEquals("Неверное значение Y", 4, p.getY());
    }

    // Тест сеттеров и геттеров
    @Test
    public void testSettersAndGetters() {
        Point p = new Point();
        p.setX(10);
        p.setY(20);
        assertEquals("Неверное значение X после установки", 10, p.getX());
        assertEquals("Неверное значение Y после установки", 20, p.getY());
    }

    // Тест метода equals и hashCode
    @Test
    public void testEquals() {
        Point p1 = new Point(5, 7);
        Point p2 = new Point(5, 7);
        Point p3 = new Point(7, 5);
        assertEquals("Точки с одинаковыми координатами должны быть равны", p1, p2);
        assertNotEquals("Точки с разными координатами не должны быть равны", p1, p3);
    }

    @Test
    public void testEqualsSameObject() {
        Point p = new Point(5, 7);
        assertTrue("Ожидается, что точка равна самой себе", p.equals(p));
    }

    @Test
    public void testEqualsNull() {
        Point p = new Point(5, 7);
        assertFalse("Ожидается, что точка не равна null", p.equals(null));
    }

    @Test
    public void testEqualsDifferentType() {
        Point p = new Point(5, 7);
        Object o = new Object();
        assertFalse("Ожидается, что точка не равна объекту другого типа", p.equals(o));
    }

    // Тест метода distanceTo для корректного расчета расстояния
    @Test
    public void testDistanceTo() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);
        double distance = p1.distanceTo(p2);
        assertEquals("Расстояние между точками (0,0) и (3,4) должно быть 5.0", 5.0, distance, 0.001);
    }

    // Негативный тест: передача null в метод distanceTo
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceToWithNull() {
        Point p = new Point(1, 2);
        p.distanceTo(null);
    }

    // Тест метода toString
    @Test
    public void testToString() {
        Point p = new Point(8, 9);
        assertEquals("Неверное строковое представление точки", "(8, 9)", p.toString());
    }
}
