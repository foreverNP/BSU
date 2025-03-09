package com.example.project1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GuessingGameTest {
    private GuessingGame game;

    @Before
    public void setup() {
        game = new GuessingGame(100);
    }

    @Test
    public void testInitialState() {
        assertEquals(0, game.getAttempts());
        assertFalse(game.isGameFinished());
    }

    @Test
    public void testGuessTooLow() {
        GuessingGame.GuessResult result = game.makeGuess(50);
        assertEquals(GuessingGame.GuessResult.AHEAD, result);
        assertEquals(1, game.getAttempts());
        assertFalse(game.isGameFinished());
    }

    @Test
    public void testGuessTooHigh() {
        GuessingGame.GuessResult result = game.makeGuess(150);
        assertEquals(GuessingGame.GuessResult.BEHIND, result);
        assertEquals(1, game.getAttempts());
        assertFalse(game.isGameFinished());
    }

    @Test
    public void testGuessCorrect() {
        GuessingGame.GuessResult result = game.makeGuess(100);
        assertEquals(GuessingGame.GuessResult.HIT, result);
        assertEquals(1, game.getAttempts());
        assertTrue(game.isGameFinished());
    }

    @Test
    public void testInvalidGuess() {
        GuessingGame.GuessResult result = game.makeGuess(250);
        assertEquals(GuessingGame.GuessResult.INVALID, result);
        assertEquals(0, game.getAttempts());
    }
}
