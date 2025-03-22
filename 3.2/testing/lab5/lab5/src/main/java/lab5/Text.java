package lab5;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

public class Text {
    private List<String> sentences;

    /**
     * Конструктор по умолчанию - создает пустой текст
     */
    public Text() {
        this.sentences = new ArrayList<>();
    }

    /**
     * Конструктор с массивом предложений
     * 
     * @param sentences Массив предложений для инициализации текста
     */
    public Text(String[] sentences) {
        this.sentences = new ArrayList<>(Arrays.asList(sentences));
    }

    /**
     * Конструктор копирования - создает новый текст как копию другого
     * 
     * @param other Текст для копирования
     */
    public Text(Text other) {
        this.sentences = new ArrayList<>(other.sentences);
    }

    /**
     * Добавить предложение в конец текста
     * 
     * @param sentence Предложение для добавления
     */
    public void addSentence(String sentence) {
        sentences.add(sentence);
    }

    /**
     * Удалить предложение по указанному индексу
     * 
     * @param index Индекс предложения для удаления
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public void deleteSentence(int index) {
        if (index < 0 || index >= sentences.size()) {
            throw new IndexOutOfBoundsException("Недопустимый индекс предложения: " + index);
        }
        sentences.remove(index);
    }

    /**
     * Вставить предложение по указанному индексу
     * 
     * @param index    Позиция для вставки предложения
     * @param sentence Предложение для вставки
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public void insertSentence(int index, String sentence) {
        if (index < 0 || index > sentences.size()) {
            throw new IndexOutOfBoundsException("Недопустимый индекс предложения: " + index);
        }
        sentences.add(index, sentence);
    }

    /**
     * Получить количество предложений в тексте
     * 
     * @return Количество предложений
     */
    public int getSentenceCount() {
        return sentences.size();
    }

    /**
     * Получить предложение по указанному индексу
     * 
     * @param index Индекс предложения
     * @return Предложение
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public String getSentence(int index) {
        if (index < 0 || index >= sentences.size()) {
            throw new IndexOutOfBoundsException("Недопустимый индекс предложения: " + index);
        }
        return sentences.get(index);
    }

    /**
     * Подсчитать общее количество слов в тексте
     * 
     * @return Количество слов
     */
    public int getWordCount() {
        int count = 0;
        for (String sentence : sentences) {
            // Разделяем по пробелам и считаем непустые части
            String[] words = sentence.split("\\s+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Подсчитать общее количество букв в тексте
     * 
     * @return Количество букв (алфавитных символов)
     */
    public int getLetterCount() {
        int count = 0;
        for (String sentence : sentences) {
            for (char c : sentence.toCharArray()) {
                if (Character.isLetter(c)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Сравнить два текста на равенство
     * 
     * @param obj Объект для сравнения
     * @return true, если тексты содержат одинаковые предложения, иначе false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Text other = (Text) obj;
        return Objects.equals(sentences, other.sentences);
    }

    /**
     * Получить строковое представление текста
     * 
     * @return Все предложения, объединенные вместе
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String sentence : sentences) {
            sb.append(sentence);
            if (!sentence.endsWith(" ")) {
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}