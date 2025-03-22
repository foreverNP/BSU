import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import lab5.*;

public class TextTest {
    private Text text;

    @Before
    public void setUp() {
        text = new Text();
    }

    @Test
    public void testEmptyTextHasZeroSentences() {
        assertEquals(0, text.getSentenceCount());
    }

    @Test
    public void testAddSentence() {
        text.addSentence("Это тестовое предложение.");
        assertEquals(1, text.getSentenceCount());
        assertEquals("Это тестовое предложение.", text.getSentence(0));
    }

    @Test
    public void testDeleteSentence() {
        text.addSentence("Первое предложение.");
        text.addSentence("Второе предложение.");
        text.deleteSentence(0);
        assertEquals(1, text.getSentenceCount());
        assertEquals("Второе предложение.", text.getSentence(0));
    }

    @Test
    public void testInsertSentence() {
        text.addSentence("Первое предложение.");
        text.addSentence("Третье предложение.");
        text.insertSentence(1, "Второе предложение.");
        assertEquals(3, text.getSentenceCount());
        assertEquals("Второе предложение.", text.getSentence(1));
    }

    @Test
    public void testWordCount() {
        text.addSentence("Это тестовое предложение.");
        text.addSentence("Еще одно тестовое предложение.");
        assertEquals(7, text.getWordCount());
    }

    @Test
    public void testLetterCount() {
        text.addSentence("АБВ.");
        text.addSentence("ГДЕ.");
        assertEquals(6, text.getLetterCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testDeleteSentenceInvalidIndex() {
        text.deleteSentence(0);
    }

    @Test
    public void testTextEquality() {
        Text text1 = new Text();
        text1.addSentence("Тестовое предложение.");

        Text text2 = new Text();
        text2.addSentence("Тестовое предложение.");

        Text text3 = new Text();
        text3.addSentence("Другое предложение.");

        assertTrue(text1.equals(text2));
        assertFalse(text1.equals(text3));
    }

    @Test
    public void testConstructorWithSentences() {
        String[] sentences = { "Первое.", "Второе." };
        Text textWithSentences = new Text(sentences);

        assertEquals(2, textWithSentences.getSentenceCount());
        assertEquals("Первое.", textWithSentences.getSentence(0));
        assertEquals("Второе.", textWithSentences.getSentence(1));
    }

    @Test
    public void testConstructorWithText() {
        Text originalText = new Text();
        originalText.addSentence("Оригинальное предложение.");

        Text copiedText = new Text(originalText);
        assertEquals(1, copiedText.getSentenceCount());
        assertEquals("Оригинальное предложение.", copiedText.getSentence(0));
    }

    @Test
    public void testToString() {
        text.addSentence("First sentence.");
        text.addSentence("Second sentence.");
        assertEquals("First sentence. Second sentence.", text.toString());
    }
}
