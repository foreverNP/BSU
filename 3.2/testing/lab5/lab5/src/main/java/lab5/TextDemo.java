package lab5;

public class TextDemo {
    public static void main(String[] args) {
        System.out.println("=== Text Class Демонстрация ===");

        // Демонстрация разных конструкторов
        System.out.println("\n=== Конструкторы ===");

        // Пустой конструктор
        Text emptyText = new Text();
        System.out.println("Пустой текст создан. Количество предложений: " + emptyText.getSentenceCount());

        // Конструктор с массивом предложений
        String[] sentences = { "Первое предложение.", "Второе предложение.", "Третье предложение." };
        Text arrayText = new Text(sentences);
        System.out.println("Текст создан из массива. Количество предложений: " + arrayText.getSentenceCount());

        // Конструктор копирования
        Text copiedText = new Text(arrayText);
        System.out.println("Текст скопирован. Количество предложений: " + copiedText.getSentenceCount());

        // Демонстрация добавления предложений
        System.out.println("\n=== Добавление предложений ===");
        Text text = new Text();
        text.addSentence("Это первое предложение.");
        System.out.println("После добавления первого предложения: " + text);
        text.addSentence("Это второе предложение.");
        System.out.println("После добавления второго предложения: " + text);

        // Демонстрация вставки предложения
        System.out.println("\n=== Вставка предложения ===");
        text.insertSentence(1, "Это вставленное предложение между первым и вторым.");
        System.out.println("После вставки предложения: " + text);
        System.out.println("Получение предложения по индексу 1: " + text.getSentence(1));

        // Демонстрация удаления предложения
        System.out.println("\n=== Удаление предложения ===");
        text.deleteSentence(1);
        System.out.println("После удаления вставленного предложения: " + text);

        // Подсчет слов и букв
        System.out.println("\n=== Подсчет слов и букв ===");
        Text countingText = new Text();
        countingText.addSentence("Здесь пять слов в предложении.");
        countingText.addSentence("А тут еще четыре слова.");
        System.out.println("Текст: " + countingText);
        System.out.println("Количество предложений: " + countingText.getSentenceCount());
        System.out.println("Количество слов: " + countingText.getWordCount());
        System.out.println("Количество букв: " + countingText.getLetterCount());

        // Сравнение текстов
        System.out.println("\n=== Сравнение текстов ===");
        Text text1 = new Text();
        text1.addSentence("Текст для сравнения.");

        Text text2 = new Text();
        text2.addSentence("Текст для сравнения.");

        Text text3 = new Text();
        text3.addSentence("Другой текст.");

        System.out.println("Текст 1: " + text1);
        System.out.println("Текст 2: " + text2);
        System.out.println("Текст 3: " + text3);
        System.out.println("Текст 1 равен Тексту 2? " + text1.equals(text2));
        System.out.println("Текст 1 равен Тексту 3? " + text1.equals(text3));

        // Демонстрация обработки исключений
        System.out.println("\n=== Обработка исключений ===");
        try {
            Text exceptionText = new Text();
            exceptionText.getSentence(0);
        } catch (IndexOutOfBoundsException e) {
            System.out
                    .println("Поймано исключение при попытке доступа к несуществующему предложению: " + e.getMessage());
        }

        try {
            Text exceptionText = new Text();
            exceptionText.deleteSentence(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Поймано исключение при попытке удалить несуществующее предложение: " + e.getMessage());
        }
    }
}