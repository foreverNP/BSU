package com.example.project1;

import java.util.Random;

public class GuessingGame {
    private int secretNumber;
    private int attempts;
    private boolean gameFinished;

    public GuessingGame() {
        startNewGame();
    }

    public GuessingGame(int secretNumber) {
        this.secretNumber = secretNumber;
        this.attempts = 0;
        this.gameFinished = false;
    }

    public void startNewGame() {
        secretNumber = new Random().nextInt(200) + 1;
        attempts = 0;
        gameFinished = false;
    }

    public int getSecretNumber() {
        return secretNumber;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public enum GuessResult {
        AHEAD, BEHIND, HIT, INVALID
    }

    public GuessResult makeGuess(int userGuess) {
        if (userGuess < 0 || userGuess > 200) {
            return GuessResult.INVALID;
        }
        attempts++;
        if (userGuess < secretNumber) {
            return GuessResult.AHEAD;
        } else if (userGuess > secretNumber) {
            return GuessResult.BEHIND;
        } else {
            gameFinished = true;
            return GuessResult.HIT;
        }
    }
}
