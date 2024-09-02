#include "gamepve.h"

GamePVE::GamePVE(int fieldSize_, QMainWindow* menu_, QWidget* parent)
    : GamePVP(fieldSize_, menu_, parent) {}

int GamePVE::minimaxAlgo(bool isMaximizing, int depth) {
  int score = checkWin("O") - checkWin("X");
  if (score == 10 || score == -10 || buttonsEnabled == 0) {
    return score - depth;
  }

  if (isMaximizing) {
    int bestScore = -1000;
    for (int i = 0; i < fieldSize; i++) {
      for (int j = 0; j < fieldSize; j++) {
        if (dynamic_cast<QPushButton*>(
                gameBoard->itemAtPosition(i, j)->widget())
                ->text() == " ") {
          dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->setText("O");
          buttonsEnabled--;

          int currentScore = minimaxAlgo(false, depth + 1);

          dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->setText(" ");
          buttonsEnabled++;

          bestScore = std::max(bestScore, currentScore);
        }
      }
    }

    return bestScore;
  } else {
    int bestScore = 1000;
    for (int i = 0; i < fieldSize; i++) {
      for (int j = 0; j < fieldSize; j++) {
        if (dynamic_cast<QPushButton*>(
                gameBoard->itemAtPosition(i, j)->widget())
                ->text() == " ") {
          dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->setText("X");
          buttonsEnabled--;

          int currentScore = minimaxAlgo(true, depth + 1);

          dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->setText(" ");
          buttonsEnabled++;

          bestScore = std::min(bestScore, currentScore);
        }
      }
    }

    return bestScore;
  }
}

pair<int, int> GamePVE::findBestMove() {
  int bestScore = -1000;
  pair<int, int> bestMove = {-1, -1};

  for (int i = 0; i < fieldSize; i++) {
    for (int j = 0; j < fieldSize; j++) {
      if (dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->text() == " ") {
        dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
            ->setText("O");
        buttonsEnabled--;

        int currentScore = minimaxAlgo(false, 0);

        dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
            ->setText(" ");
        buttonsEnabled++;

        if (currentScore > bestScore) {
          bestScore = currentScore;
          bestMove = {i, j};
        }
      }
    }
  }

  return bestMove;
}

int GamePVE::checkWin(QString player) {
  int i, j;
  bool win;

  for (i = 0; i < fieldSize; i++) {
    win = true;

    for (j = 0; j < fieldSize; j++) {
      if (dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, j)->widget())
              ->text() != player) {
        win = false;
        break;
      }
    }

    if (win) {
      return 10;
    }
  }

  for (i = 0; i < fieldSize; i++) {
    win = true;

    for (j = 0; j < fieldSize; j++) {
      if (dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(j, i)->widget())
              ->text() != player) {
        win = false;
        break;
      }
    }

    if (win) {
      return 10;
    }
  }

  win = true;
  for (i = 0; i < fieldSize; i++) {
    if (dynamic_cast<QPushButton*>(gameBoard->itemAtPosition(i, i)->widget())
            ->text() != player) {
      win = false;
      break;
    }
  }
  if (win) {
    return 10;
  }

  win = true;
  for (i = 0; i < fieldSize; i++) {
    if (dynamic_cast<QPushButton*>(
            gameBoard->itemAtPosition(i, fieldSize - 1 - i)->widget())
            ->text() != player) {
      win = false;
      break;
    }
  }
  if (win) {
    return 10;
  }

  return 0;
}

void GamePVE::handleButtonClick() {
  QPushButton* button = qobject_cast<QPushButton*>(sender());
  button->setEnabled(false);

  button->setStyleSheet(
      "QPushButton{ font: 900 20pt; background-color:  #e6e6e6; border: 0px "
      "#e6e6e6; color: red;}");
  button->setText("X");
  buttonsEnabled--;

  if (checkWin("X") == 10) {
    statusLabel->setStyleSheet("QLabel {font: 900 40pt; color: green}");
    statusLabel->setText("You wins!");

    for (int i = 0; i < fieldSize; ++i) {
      for (int j = 0; j < fieldSize; ++j) {
        gameBoard->itemAtPosition(i, j)->widget()->setEnabled(false);
      }
    }

    return;
  }

  if (buttonsEnabled == 0) {
    statusLabel->setStyleSheet("QLabel{font: 900 40pt; color: black}");
    statusLabel->setText("Draw!");

    return;
  }

  pair<int, int> bestBotMove = findBestMove();
  button = dynamic_cast<QPushButton*>(
      gameBoard->itemAtPosition(bestBotMove.first, bestBotMove.second)
          ->widget());

  button->setStyleSheet(
      "QPushButton{ font: 900 20pt; background-color:  #e6e6e6; border: "
      "0px "
      "#e6e6e6; color: blue;}");
  button->setEnabled(false);
  button->setText("O");
  statusLabel->setStyleSheet("QLabel{font: 900 30pt; color: red}");
  statusLabel->setText("X's turn");
  buttonsEnabled--;

  if (checkWin("O") == 10) {
    statusLabel->setStyleSheet("QLabel {font: 900 40pt; color: green}");
    statusLabel->setText("Bot wins!");

    for (int i = 0; i < fieldSize; ++i) {
      for (int j = 0; j < fieldSize; ++j) {
        gameBoard->itemAtPosition(i, j)->widget()->setEnabled(false);
      }
    }
  }
}