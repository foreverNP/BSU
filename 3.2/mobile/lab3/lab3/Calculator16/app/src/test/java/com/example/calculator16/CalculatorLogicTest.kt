package com.example.calculator16

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorLogicTest {

    private val calculator = CalculatorLogic()

    @Test
    fun testConvertAreaUK() {
        // 1 m² * 1.19599 = 1.19599, форматирование "#.##" должно дать "1.2"
        val input = "1.0"
        val expected = "1.2"
        val actual = calculator.convertArea(input, isUK = true)
        assertEquals("Конвертация для UK неверна", expected, actual)
    }

    @Test
    fun testConvertAreaUS() {
        // 1 m² * 10.7639 = 10.7639, форматирование "#.##" должно дать "10.76"
        val input = "1.0"
        val expected = "10.76"
        val actual = calculator.convertArea(input, isUK = false)
        assertEquals("Конвертация для US неверна", expected, actual)
    }

    @Test
    fun testToggleSign_positiveToNegative() {
        val input = "123"
        val expected = "-123"
        val actual = calculator.toggleSign(input)
        assertEquals("Смена знака с положительного на отрицательный не работает", expected, actual)
    }

    @Test
    fun testToggleSign_negativeToPositive() {
        val input = "-123"
        val expected = "123"
        val actual = calculator.toggleSign(input)
        assertEquals("Смена знака с отрицательного на положительный не работает", expected, actual)
    }

    @Test
    fun testToggleSign_empty() {
        val input = ""
        val expected = ""
        val actual = calculator.toggleSign(input)
        assertEquals("Пустая строка должна оставаться пустой", expected, actual)
    }
}
