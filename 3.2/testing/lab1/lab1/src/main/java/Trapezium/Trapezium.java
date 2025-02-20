/**
 * Пакет, содержащий класс Trapezium для работы с трапециями.
 */
package Trapezium;

import Figure.Figure;
import Point.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Класс, представляющий трапецию в двумерном пространстве.
 * Расширяет абстрактный класс Figure и реализует интерфейсы для сравнения и
 * перебора.
 *
 * @author Лев Сергиенко
 * @version 1.0
 */
public class Trapezium extends Figure implements Comparable<Trapezium>, Iterable<String>, Comparator<Trapezium> {
    private Point p1, p2, p3, p4;
    private Border border;
    private Filling filling;
    private static TrapeziumSortBy sortBy = TrapeziumSortBy.firstX;

    /**
     * Устанавливает глобальный критерий сортировки для всех экземпляров трапеций.
     *
     * @param value Критерий сортировки из перечисления TrapeziumSortBy.
     */
    public static void setSortBy(TrapeziumSortBy value) {
        sortBy = value;
    }

    /**
     * Перечисление критериев сортировки трапеций.
     * Включает координаты точек и компоненты цветов границы и заливки.
     */
    public enum TrapeziumSortBy {
        firstX, secondX, thirdX, fourthX, firstY, secondY, thirdY, fourthY,
        R_border, G_border, B_border, R_filling, G_filling, B_filling
    }

    /**
     * Внутренний класс для хранения и управления цветом границы трапеции.
     * Использует RGB-модель цвета.
     */
    private static class Border {
        private final int R_border;
        private final int G_border;
        private final int B_border;

        /**
         * Создает объект цвета границы с указанными RGB-компонентами.
         *
         * @param r Интенсивность красного цвета (0-255).
         * @param g Интенсивность зеленого цвета (0-255).
         * @param b Интенсивность синего цвета (0-255).
         * @throws IllegalArgumentException Если любое значение компоненты цвета вне
         *                                  диапазона 0-255.
         */
        public Border(int r, int g, int b) {
            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                throw new IllegalArgumentException("Неверный цвет!");
            }
            this.R_border = r;
            this.G_border = g;
            this.B_border = b;
        }

        /**
         * Возвращает красную компоненту цвета границы.
         *
         * @return Значение красной компоненты (0-255).
         */
        public int getR() {
            return R_border;
        }

        /**
         * Возвращает зеленую компоненту цвета границы.
         *
         * @return Значение зеленой компоненты (0-255).
         */
        public int getG() {
            return G_border;
        }

        /**
         * Возвращает синюю компоненту цвета границы.
         *
         * @return Значение синей компоненты (0-255).
         */
        public int getB() {
            return B_border;
        }
    }

    /**
     * Вычисляет периметр трапеции, суммируя длины всех сторон.
     * Использует евклидово расстояние между точками.
     *
     * @return Периметр трапеции в единицах длины.
     */
    @Override
    public double Perimetr() {
        double per = Math.sqrt(
                (p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY())) +
                Math.sqrt((p3.getX() - p2.getX()) * (p3.getX() - p2.getX())
                        + (p3.getY() - p2.getY()) * (p3.getY() - p2.getY()))
                +
                Math.sqrt((p4.getX() - p3.getX()) * (p4.getX() - p3.getX())
                        + (p4.getY() - p3.getY()) * (p4.getY() - p3.getY()))
                +
                Math.sqrt((p1.getX() - p4.getX()) * (p1.getX() - p4.getX())
                        + (p1.getY() - p4.getY()) * (p1.getY() - p4.getY()));
        return per;
    }

    /**
     * Вычисляет площадь трапеции по формуле Брахмагупты.
     * Применима для любого четырехугольника, заданного координатами вершин.
     *
     * @return Площадь трапеции в квадратных единицах.
     */
    @Override
    public double Area() {
        double area = Math.sqrt((Perimetr() / 2 - Math.sqrt(
                (p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY())))
                *
                (Perimetr() / 2 - Math.sqrt((p3.getX() - p2.getX()) * (p3.getX() - p2.getX())
                        + (p3.getY() - p2.getY()) * (p3.getY() - p2.getY())))
                *
                (Perimetr() / 2 - Math.sqrt((p4.getX() - p3.getX()) * (p4.getX() - p3.getX())
                        + (p4.getY() - p3.getY()) * (p4.getY() - p3.getY())))
                *
                (Perimetr() / 2 - Math.sqrt((p1.getX() - p4.getX()) * (p1.getX() - p4.getX())
                        + (p1.getY() - p4.getY()) * (p1.getY() - p4.getY()))));
        return area;
    }

    /**
     * Вычисляет синус угла между двумя линиями, определенными четырьмя точками.
     * Используется формула скалярного произведения векторов.
     *
     * @param p1 Начальная точка первой линии.
     * @param p2 Конечная точка первой линии.
     * @param p3 Начальная точка второй линии.
     * @param p4 Конечная точка второй линии.
     * @return Синус угла между линиями.
     */
    public double sin(Point p1, Point p2, Point p3, Point p4) {
        return (double) (Math.abs(p1.getX() - p2.getX()) * Math.abs(p3.getX() - p4.getX())
                + Math.abs(p1.getY() - p2.getY()) * Math.abs(p3.getY() - p4.getY()))
                / Math.sqrt(Math.pow(Math.abs(p1.getX() - p2.getX()), 2) + Math.pow(Math.abs(p1.getY() - p2.getY()), 2))
                * Math.sqrt(
                        Math.pow(Math.abs(p3.getX() - p4.getX()), 2) + Math.pow(Math.abs(p3.getY() - p4.getY()), 2));
    }

    /**
     * Внутренний класс для хранения и управления цветом заливки трапеции.
     * Использует RGB-модель цвета.
     */
    private static class Filling {
        private final int R_filling;
        private final int G_filling;
        private final int B_filling;

        /**
         * Создает объект цвета заливки с указанными RGB-компонентами.
         *
         * @param r Интенсивность красного цвета (0-255).
         * @param g Интенсивность зеленого цвета (0-255).
         * @param b Интенсивность синего цвета (0-255).
         * @throws IllegalArgumentException Если любое значение компоненты цвета вне
         *                                  диапазона 0-255.
         */
        public Filling(int r, int g, int b) {
            if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                throw new IllegalArgumentException("Неверный цвет!");
            }
            this.R_filling = r;
            this.G_filling = g;
            this.B_filling = b;
        }

        /**
         * Возвращает красную компоненту цвета заливки.
         *
         * @return Значение красной компоненты (0-255).
         */
        public int getR() {
            return R_filling;
        }

        /**
         * Возвращает зеленую компоненту цвета заливки.
         *
         * @return Значение зеленой компоненты (0-255).
         */
        public int getG() {
            return G_filling;
        }

        /**
         * Возвращает синюю компоненту цвета заливки.
         *
         * @return Значение синей компоненты (0-255).
         */
        public int getB() {
            return B_filling;
        }
    }

    /**
     * Конструктор по умолчанию.
     * Создает трапецию с точками в начале координат и черными цветами границы и
     * заливки.
     */
    public Trapezium() {
        p1 = new Point();
        p2 = new Point();
        p3 = new Point();
        p4 = new Point();
        border = new Border(0, 0, 0);
        filling = new Filling(0, 0, 0);
    }

    /**
     * Параметризованный конструктор.
     * Создает трапецию с заданными координатами вершин и цветами.
     *
     * @param P1  Первая вершина трапеции.
     * @param P2  Вторая вершина трапеции.
     * @param P3  Третья вершина трапеции.
     * @param P4  Четвертая вершина трапеции.
     * @param R_b Красная компонента цвета границы.
     * @param G_b Зеленая компонента цвета границы.
     * @param B_b Синяя компонента цвета границы.
     * @param R_f Красная компонента цвета заливки.
     * @param G_f Зеленая компонента цвета заливки.
     * @param B_f Синяя компонента цвета заливки.
     */
    public Trapezium(Point P1, Point P2, Point P3, Point P4, int R_b, int G_b, int B_b, int R_f, int G_f, int B_f) {
        p1 = P1;
        p2 = P2;
        p3 = P3;
        p4 = P4;
        border = new Border(R_b, G_b, B_b);
        filling = new Filling(R_f, G_f, B_f);
    }

    /**
     * Конструктор из строкового представления.
     * Инициализирует трапецию, разбирая строку в определенном формате.
     *
     * @param data Строковое представление трапеции.
     * @throws IllegalArgumentException Если формат строки некорректен или не
     *                                  удалось разобрать данные.
     */
    public Trapezium(String data) {
        if (!parse(data)) {
            throw new IllegalArgumentException("Ошибка разбора!");
        }
    }

    /**
     * Разбирает строковое представление трапеции.
     * Извлекает цвета и координаты из строки определенного формата.
     *
     * @param str Строковое представление трапеции для разбора.
     * @return true, если разбор успешен, false в противном случае.
     */
    public boolean parse(String str) {
        if (!("Border - rgb((.*))\n" +
                "Filling - rgb((.*))\n" +
                "First coordinate: ((.*))\n" +
                "Second coordinate: ((.*))\n" +
                "Third coordinate: ((.*))\n" +
                "Fourth coordinate: ((.*))\n").matches(str)) {
            return false;
        }

        String[] parts = str.split("\n");

        int r1 = Integer.parseInt(parts[0].substring(13, parts[0].indexOf(',')));
        int g1 = Integer.parseInt(
                parts[0].substring(parts[0].indexOf(',') + 2, parts[0].indexOf(',', parts[0].indexOf(',') + 1)));
        int b1 = Integer.parseInt(parts[0].substring(parts[0].indexOf(',', parts[0].indexOf(',') + 1) + 2,
                parts[0].indexOf(')', parts[0].indexOf(',', parts[0].indexOf(',') + 1) + 1)));

        int r2 = Integer.parseInt(parts[1].substring(14, parts[1].indexOf(',')));
        int g2 = Integer.parseInt(
                parts[1].substring(parts[1].indexOf(',') + 2, parts[1].indexOf(',', parts[1].indexOf(',') + 1)));
        int b2 = Integer.parseInt(parts[1].substring(parts[1].indexOf(',', parts[1].indexOf(',') + 1) + 2,
                parts[1].indexOf(')', parts[1].indexOf(',', parts[1].indexOf(',') + 1) + 1)));
        border = new Border(r1, g1, b1);
        filling = new Filling(r2, g2, b2);

        int px1 = Integer.parseInt(parts[2].substring(19, parts[2].indexOf(',')));
        int py1 = Integer.parseInt(
                parts[2].substring(parts[2].indexOf(',') + 2, parts[2].indexOf(')', parts[2].indexOf(',') + 1)));
        p1 = new Point(px1, py1);

        int px2 = Integer.parseInt(parts[3].substring(20, parts[3].indexOf(',')));
        int py2 = Integer.parseInt(
                parts[3].substring(parts[3].indexOf(',') + 2, parts[3].indexOf(')', parts[3].indexOf(',') + 1)));
        p2 = new Point(px2, py2);

        int px3 = Integer.parseInt(parts[4].substring(19, parts[4].indexOf(',')));
        int py3 = Integer.parseInt(
                parts[4].substring(parts[4].indexOf(',') + 2, parts[4].indexOf(')', parts[4].indexOf(',') + 1)));
        p3 = new Point(px3, py3);

        int px4 = Integer.parseInt(parts[5].substring(20, parts[5].indexOf(',')));
        int py4 = Integer.parseInt(
                parts[5].substring(parts[5].indexOf(',') + 2, parts[5].indexOf(')', parts[5].indexOf(',') + 1)));
        p4 = new Point(px4, py4);

        return true;
    }

    /**
     * Реализация метода compareTo интерфейса Comparable.
     * Сравнивает трапеции по выбранному критерию сортировки.
     *
     * @param entry Трапеция для сравнения с текущей.
     * @return Отрицательное число, если текущая трапеция меньше;
     *         положительное, если больше; ноль, если равны.
     * @throws NoSuchElementException Если выбран неподдерживаемый критерий
     *                                сортировки.
     */
    @Override
    public int compareTo(Trapezium entry) {
        switch (sortBy) {
            case firstX:
                return this.p1.getX() - entry.p1.getX();
            case secondX:
                return this.p2.getX() - entry.p2.getX();
            case thirdX:
                return this.p3.getX() - entry.p3.getX();
            case fourthX:
                return this.p4.getX() - entry.p4.getX();
            case firstY:
                return this.p1.getY() - entry.p1.getY();
            case secondY:
                return this.p2.getY() - entry.p2.getY();
            case thirdY:
                return this.p3.getY() - entry.p3.getY();
            case fourthY:
                return this.p4.getY() - entry.p4.getY();
            case R_border:
                return this.border.R_border - entry.border.R_border;
            case G_border:
                return this.border.G_border - entry.border.G_border;
            case B_border:
                return this.border.B_border - entry.border.B_border;
            case R_filling:
                return this.filling.R_filling - entry.filling.R_filling;
            case G_filling:
                return this.filling.G_filling - entry.filling.G_filling;
            case B_filling:
                return this.filling.B_filling - entry.filling.B_filling;
            default:
                throw new NoSuchElementException();
        }
    }

    /**
     * Реализация метода compare интерфейса Comparator.
     * Делегирует сравнение методу compareTo().
     *
     * @param entry  Первая трапеция для сравнения.
     * @param entry1 Вторая трапеция для сравнения.
     * @return Результат сравнения двух трапеций.
     */
    @Override
    public int compare(Trapezium entry, Trapezium entry1) {
        return entry.compareTo(entry1);
    }

    /**
     * Реализация метода iterator интерфейса Iterable.
     * Предоставляет последовательный доступ к свойствам трапеции в виде строк.
     *
     * @return Итератор по строковым представлениям свойств трапеции.
     */
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int iterator_idx = -1;

            @Override
            public boolean hasNext() {
                return iterator_idx < 10;
            }

            @Override
            public String next() {
                if (hasNext()) {
                    switch (++iterator_idx) {
                        case 0 -> {
                            return Integer.toString(border.getR());
                        }
                        case 1 -> {
                            return Integer.toString(border.getG());
                        }
                        case 2 -> {
                            return Integer.toString(border.getB());
                        }
                        case 3 -> {
                            return Integer.toString(filling.getR());
                        }
                        case 4 -> {
                            return Integer.toString(filling.getG());
                        }
                        case 5 -> {
                            return Integer.toString(filling.getB());
                        }
                        case 6 -> {
                            return p1.getX() + " " + p1.getY();
                        }
                        case 7 -> {
                            return p2.getX() + " " + p2.getY();
                        }
                        case 8 -> {
                            return p3.getX() + " " + p3.getY();
                        }
                        case 9 -> {
                            return p4.getX() + " " + p4.getY();
                        }
                    }
                }
                return "";
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Возвращает строковое представление трапеции.
     * Включает информацию о цветах границы, заливки и координатах всех вершин.
     *
     * @return Форматированная строка с данными трапеции.
     */
    @Override
    public String toString() {
        return "Border - rgb(" + border.getR() + ", " + border.getG() + ", " + border.getB() + ")\n"
                + "Filling - rgb(" + filling.getR() + ", " + filling.getG() + ", " + filling.getB() + ")\n"
                + "First coordinate: (" + p1.getX() + ", " + p1.getY() + ")\n"
                + "Second coordinate: (" + p2.getX() + ", " + p2.getY() + ")\n"
                + "Third coordinate: (" + p3.getX() + ", " + p3.getY() + ")\n"
                + "Fourth coordinate: (" + p4.getX() + ", " + p4.getY() + ")\n";
    }
}