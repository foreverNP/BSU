/**
 * Пакет, содержащий класс Trapezium для работы с трапециями.
 */
package Trapezium;

import Figure.Figure;
import Point.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс, представляющий трапецию в двумерном пространстве.
 * Расширяет абстрактный класс Figure и реализует интерфейсы для сравнения и
 * перебора.
 *
 * @author Лев Сергиенко
 * @version 1.1
 */
public class Trapezium extends Figure implements Comparable<Trapezium>, Iterable<String> {
    private final Point p1, p2, p3, p4;
    private final Border border;
    private final Filling filling;
    private static TrapeziumSortBy sortBy = TrapeziumSortBy.firstX;

    /**
     * Устанавливает глобальный критерий сортировки для всех экземпляров трапеций.
     *
     * @param value Критерий сортировки из перечисления TrapeziumSortBy.
     */
    public static void setSortBy(TrapeziumSortBy value) {
        if (value == null) {
            throw new IllegalArgumentException("Критерий сортировки не может быть null");
        }
        sortBy = value;
    }

    /**
     * Возвращает текущий критерий сортировки.
     *
     * @return Текущий критерий сортировки.
     */
    public static TrapeziumSortBy getSortBy() {
        return sortBy;
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
            validateColorComponent(r, "Red");
            validateColorComponent(g, "Green");
            validateColorComponent(b, "Blue");

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
            validateColorComponent(r, "Red");
            validateColorComponent(g, "Green");
            validateColorComponent(b, "Blue");

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
     * Проверяет, что компонента цвета находится в допустимом диапазоне.
     * 
     * @param value         Значение компоненты цвета для проверки.
     * @param componentName Название компоненты для сообщения об ошибке.
     * @throws IllegalArgumentException Если значение вне диапазона 0-255.
     */
    private static void validateColorComponent(int value, String componentName) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                    componentName + " компонента цвета должна быть в диапазоне 0-255, получено: " + value);
        }
    }

    /**
     * Вычисляет евклидово расстояние между двумя точками.
     * 
     * @param p1 Первая точка.
     * @param p2 Вторая точка.
     * @return Расстояние между точками.
     */
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    /**
     * Вычисляет периметр трапеции, суммируя длины всех сторон.
     * Использует евклидово расстояние между точками.
     *
     * @return Периметр трапеции в единицах длины.
     */
    @Override
    public double Perimetr() {
        return distance(p1, p2) + distance(p2, p3) + distance(p3, p4) + distance(p4, p1);
    }

    /**
     * Вычисляет площадь трапеции по формуле для четырехугольника.
     * Применима для любого четырехугольника, заданного координатами вершин.
     *
     * @return Площадь трапеции в квадратных единицах.
     */
    @Override
    public double Area() {
        if (!isTrapezium()) {
            throw new ArithmeticException("Фигура не является трапецией");
        }
        // Используем формулу площади четырехугольника через полупериметр и стороны
        double s = Perimetr() / 2;
        double a = distance(p1, p2);
        double b = distance(p2, p3);
        double c = distance(p3, p4);
        double d = distance(p4, p1);

        // Вычисляем диагональ для формулы Брахмагупты
        double diagonal = distance(p1, p3);

        // Используем формулу Герона для двух треугольников, образованных диагональю
        double s1 = (a + b + diagonal) / 2;
        double s2 = (c + d + diagonal) / 2;

        double area1 = Math.sqrt(s1 * (s1 - a) * (s1 - b) * (s1 - diagonal));
        double area2 = Math.sqrt(s2 * (s2 - c) * (s2 - d) * (s2 - diagonal));

        double result = area1 + area2; // суммарная площадь
        if (result == 0.0) {
            throw new ArithmeticException("Площадь трапеции не может быть равна нулю");
        }
        return result;
    }

    /**
     * Вычисляет косинус угла между двумя векторами.
     * Используется формула скалярного произведения векторов.
     *
     * @param v1start Начальная точка первого вектора.
     * @param v1end   Конечная точка первого вектора.
     * @param v2start Начальная точка второго вектора.
     * @param v2end   Конечная точка второго вектора.
     * @return Косинус угла между векторами.
     */
    public double cosAngle(Point v1start, Point v1end, Point v2start, Point v2end) {
        if (v1start == null || v1end == null || v2start == null || v2end == null) {
            throw new IllegalArgumentException("Точки не могут быть null");
        }
        // Вычисляем компоненты векторов
        double v1x = v1end.getX() - v1start.getX();
        double v1y = v1end.getY() - v1start.getY();
        double v2x = v2end.getX() - v2start.getX();
        double v2y = v2end.getY() - v2start.getY();

        // Скалярное произведение
        double dotProduct = v1x * v2x + v1y * v2y;

        // Длины векторов
        double v1Length = Math.sqrt(v1x * v1x + v1y * v1y);
        double v2Length = Math.sqrt(v2x * v2x + v2y * v2y);

        // Косинус угла
        return dotProduct / (v1Length * v2Length);
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
        // Проверяем, что переданные точки не null
        if (P1 == null || P2 == null || P3 == null || P4 == null) {
            throw new IllegalArgumentException("Точки не могут быть null");
        }

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
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой");
        }

        try {
            // Используем регулярные выражения для более надежного парсинга
            Pattern borderPattern = Pattern.compile("Border - rgb\\((\\d+), (\\d+), (\\d+)\\)");
            Pattern fillingPattern = Pattern.compile("Filling - rgb\\((\\d+), (\\d+), (\\d+)\\)");
            Pattern p1Pattern = Pattern.compile("First coordinate: \\(([-\\d]+), ([-\\d]+)\\)");
            Pattern p2Pattern = Pattern.compile("Second coordinate: \\(([-\\d]+), ([-\\d]+)\\)");
            Pattern p3Pattern = Pattern.compile("Third coordinate: \\(([-\\d]+), ([-\\d]+)\\)");
            Pattern p4Pattern = Pattern.compile("Fourth coordinate: \\(([-\\d]+), ([-\\d]+)\\)");

            Matcher borderMatcher = borderPattern.matcher(data);
            Matcher fillingMatcher = fillingPattern.matcher(data);
            Matcher p1Matcher = p1Pattern.matcher(data);
            Matcher p2Matcher = p2Pattern.matcher(data);
            Matcher p3Matcher = p3Pattern.matcher(data);
            Matcher p4Matcher = p4Pattern.matcher(data);

            if (!borderMatcher.find() || !fillingMatcher.find() || !p1Matcher.find() ||
                    !p2Matcher.find() || !p3Matcher.find() || !p4Matcher.find()) {
                throw new IllegalArgumentException("Неверный формат входной строки");
            }

            int borderR = Integer.parseInt(borderMatcher.group(1));
            int borderG = Integer.parseInt(borderMatcher.group(2));
            int borderB = Integer.parseInt(borderMatcher.group(3));

            int fillingR = Integer.parseInt(fillingMatcher.group(1));
            int fillingG = Integer.parseInt(fillingMatcher.group(2));
            int fillingB = Integer.parseInt(fillingMatcher.group(3));

            int p1x = Integer.parseInt(p1Matcher.group(1));
            int p1y = Integer.parseInt(p1Matcher.group(2));

            int p2x = Integer.parseInt(p2Matcher.group(1));
            int p2y = Integer.parseInt(p2Matcher.group(2));

            int p3x = Integer.parseInt(p3Matcher.group(1));
            int p3y = Integer.parseInt(p3Matcher.group(2));

            int p4x = Integer.parseInt(p4Matcher.group(1));
            int p4y = Integer.parseInt(p4Matcher.group(2));

            this.border = new Border(borderR, borderG, borderB);
            this.filling = new Filling(fillingR, fillingG, fillingB);
            this.p1 = new Point(p1x, p1y);
            this.p2 = new Point(p2x, p2y);
            this.p3 = new Point(p3x, p3y);
            this.p4 = new Point(p4x, p4y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка при парсинге числовых значений: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка разбора строки: " + e.getMessage());
        }
    }

    /**
     * Реализация метода compareTo интерфейса Comparable.
     * Сравнивает трапеции по выбранному критерию сортировки.
     *
     * @param other Трапеция для сравнения с текущей.
     * @return Отрицательное число, если текущая трапеция меньше;
     *         положительное, если больше; ноль, если равны.
     * @throws NoSuchElementException Если выбран неподдерживаемый критерий
     *                                сортировки.
     */
    @Override
    public int compareTo(Trapezium other) {
        if (other == null) {
            return 1; // Null всегда меньше любого объекта
        }

        return switch (sortBy) {
            case firstX -> Integer.compare(this.p1.getX(), other.p1.getX());
            case secondX -> Integer.compare(this.p2.getX(), other.p2.getX());
            case thirdX -> Integer.compare(this.p3.getX(), other.p3.getX());
            case fourthX -> Integer.compare(this.p4.getX(), other.p4.getX());
            case firstY -> Integer.compare(this.p1.getY(), other.p1.getY());
            case secondY -> Integer.compare(this.p2.getY(), other.p2.getY());
            case thirdY -> Integer.compare(this.p3.getY(), other.p3.getY());
            case fourthY -> Integer.compare(this.p4.getY(), other.p4.getY());
            case R_border -> Integer.compare(this.border.R_border, other.border.R_border);
            case G_border -> Integer.compare(this.border.G_border, other.border.G_border);
            case B_border -> Integer.compare(this.border.B_border, other.border.B_border);
            case R_filling -> Integer.compare(this.filling.R_filling, other.filling.R_filling);
            case G_filling -> Integer.compare(this.filling.G_filling, other.filling.G_filling);
            case B_filling -> Integer.compare(this.filling.B_filling, other.filling.B_filling);
        };
    }

    /**
     * Создает компаратор для сравнения трапеций по указанному критерию.
     * 
     * @param criteria Критерий сортировки.
     * @return Компаратор для сравнения трапеций.
     */
    public static Comparator<Trapezium> getComparator(TrapeziumSortBy criteria) {
        return (t1, t2) -> {
            if (t1 == null && t2 == null)
                return 0;
            if (t1 == null)
                return -1;
            if (t2 == null)
                return 1;

            TrapeziumSortBy oldSortBy = sortBy;
            sortBy = criteria;
            int result = t1.compareTo(t2);
            sortBy = oldSortBy;
            return result;
        };
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
            private int index = 0;
            private final String[] properties = {
                    Integer.toString(border.getR()),
                    Integer.toString(border.getG()),
                    Integer.toString(border.getB()),
                    Integer.toString(filling.getR()),
                    Integer.toString(filling.getG()),
                    Integer.toString(filling.getB()),
                    p1.getX() + " " + p1.getY(),
                    p2.getX() + " " + p2.getY(),
                    p3.getX() + " " + p3.getY(),
                    p4.getX() + " " + p4.getY()
            };

            @Override
            public boolean hasNext() {
                return index < properties.length;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет больше элементов");
                }
                return properties[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Операция удаления не поддерживается");
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
        return String.format(
                "Border - rgb(%d, %d, %d)%n" +
                        "Filling - rgb(%d, %d, %d)%n" +
                        "First coordinate: (%d, %d)%n" +
                        "Second coordinate: (%d, %d)%n" +
                        "Third coordinate: (%d, %d)%n" +
                        "Fourth coordinate: (%d, %d)%n",
                border.getR(), border.getG(), border.getB(),
                filling.getR(), filling.getG(), filling.getB(),
                p1.getX(), p1.getY(),
                p2.getX(), p2.getY(),
                p3.getX(), p3.getY(),
                p4.getX(), p4.getY());
    }

    /**
     * Проверяет, является ли четырехугольник действительно трапецией
     * (имеет две параллельные стороны).
     *
     * @return true, если фигура является трапецией, false в противном случае.
     */
    public boolean isTrapezium() {
        // Вычисляем векторы для всех сторон
        int v1x = p2.getX() - p1.getX();
        int v1y = p2.getY() - p1.getY();
        int v2x = p3.getX() - p2.getX();
        int v2y = p3.getY() - p2.getY();
        int v3x = p4.getX() - p3.getX();
        int v3y = p4.getY() - p3.getY();
        int v4x = p1.getX() - p4.getX();
        int v4y = p1.getY() - p4.getY();

        // Проверяем параллельность противоположных сторон
        boolean parallel1And3 = areVectorsParallel(v1x, v1y, v3x, v3y);
        boolean parallel2And4 = areVectorsParallel(v2x, v2y, v4x, v4y);

        // Трапеция должна иметь хотя бы одну пару параллельных сторон
        return parallel1And3 || parallel2And4;
    }

    /**
     * Проверяет, параллельны ли два вектора.
     *
     * @param v1x x-компонента первого вектора
     * @param v1y y-компонента первого вектора
     * @param v2x x-компонента второго вектора
     * @param v2y y-компонента второго вектора
     * @return true, если векторы параллельны, иначе false
     */
    private boolean areVectorsParallel(int v1x, int v1y, int v2x, int v2y) {
        // Векторы параллельны, если их векторное произведение равно 0
        return v1x * v2y - v1y * v2x == 0;
    }

    /**
     * Возвращает первую вершину трапеции.
     * 
     * @return Первая вершина.
     */
    public Point getP1() {
        return p1;
    }

    /**
     * Возвращает вторую вершину трапеции.
     * 
     * @return Вторая вершина.
     */
    public Point getP2() {
        return p2;
    }

    /**
     * Возвращает третью вершину трапеции.
     * 
     * @return Третья вершина.
     */
    public Point getP3() {
        return p3;
    }

    /**
     * Возвращает четвертую вершину трапеции.
     * 
     * @return Четвертая вершина.
     */
    public Point getP4() {
        return p4;
    }

    /**
     * Возвращает цвет границы в формате RGB массива.
     * 
     * @return Массив из трех элементов [R, G, B].
     */
    public int[] getBorderColor() {
        return new int[] { border.getR(), border.getG(), border.getB() };
    }

    /**
     * Возвращает цвет заливки в формате RGB массива.
     * 
     * @return Массив из трех элементов [R, G, B].
     */
    public int[] getFillingColor() {
        return new int[] { filling.getR(), filling.getG(), filling.getB() };
    }
}