/**
 * Пакет, содержащий класс Point для представления точек в двумерном пространстве.
 */
package Point;

/**
 * Класс, представляющий точку в двумерном пространстве.
 * Содержит координаты X и Y, методы для работы с ними, а также дополнительные
 * методы для сравнения, вычисления расстояния и строкового представления.
 * 
 * @author Лев Сергиенко
 * @version 1.1
 */
public class Point {
    private int x, y;

    /**
     * Конструктор по умолчанию.
     * Инициализирует точку в начале координат (0, 0).
     */
    public Point() {
        this(0, 0);
    }

    /**
     * Параметризованный конструктор.
     * Создает точку с заданными координатами.
     *
     * @param x Координата X точки.
     * @param y Координата Y точки.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Возвращает координату X.
     *
     * @return Значение X.
     */
    public int getX() {
        return x;
    }

    /**
     * Возвращает координату Y.
     *
     * @return Значение Y.
     */
    public int getY() {
        return y;
    }

    /**
     * Устанавливает новое значение для координаты X.
     *
     * @param x Новое значение X.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Устанавливает новое значение для координаты Y.
     *
     * @param y Новое значение Y.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Вычисляет евклидово расстояние до другой точки.
     *
     * @param other Другая точка. Не должна быть null.
     * @return Расстояние до другой точки.
     * @throws IllegalArgumentException если other равен null.
     */
    public double distanceTo(Point other) {
        if (other == null) {
            throw new IllegalArgumentException("Другой объект Point не должен быть null");
        }
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Переопределенный метод equals для сравнения точек.
     *
     * @param o Объект для сравнения.
     * @return true, если объекты представляют одинаковые координаты, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    /**
     * Возвращает строковое представление точки.
     *
     * @return Строка в формате "(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
