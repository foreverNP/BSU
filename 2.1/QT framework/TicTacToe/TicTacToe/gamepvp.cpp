#include "gamepvp.h"

#include <QLabel>
#include <QPushButton>

GamePVP::GamePVP(int fieldSize_, QMainWindow *menu_, QWidget *parent)
    : QMainWindow(parent),
      menu(menu_),
      fieldSize(fieldSize_),
      buttonsEnabled(fieldSize_ * fieldSize_) {
  ui = new Ui::MainWindow;
  ui->setupUi(this);
  setMinimumSize(800, 450);
  resize(800, 450);

  hLayout = new QHBoxLayout;
  vLayout = new QVBoxLayout;
  gameBoard = new QGridLayout;

  exitMenuButton = new QPushButton(this);
  exitMenuButton->setText("Menu");
  exitMenuButton->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
  connect(exitMenuButton, &QPushButton::clicked, this,
          &GamePVP::exitButtonClick);

  clearButton = new QPushButton(this);
  clearButton->setText("Clear");
  clearButton->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
  connect(clearButton, &QPushButton::clicked, this, &GamePVP::clearButtonClick);

  statusLabel = new QLabel(this);
  statusLabel->setAlignment(Qt::AlignCenter);
  statusLabel->setStyleSheet("QLabel{font: 900 30pt; color: red}");
  statusLabel->setText("X's turn");

  turn = 1;

  for (int i = 0; i < fieldSize; ++i) {
    for (int j = 0; j < fieldSize; ++j) {
      QPushButton *button = new QPushButton(this);
      button->setObjectName(QString("%1,%2").arg(i).arg(j));
      button->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
      button->setStyleSheet(
          "QPushButton{ font: 900 20pt; background-color: white; border: 1px "
          "solid black;"
          "color: white;}"
          "QPushButton:hover {background-color : #cccccc}"
          "QPushButton:pressed {background-color : #808080;}");
      button->setText(" ");
      connect(button, &QPushButton::clicked, this, &GamePVP::handleButtonClick);
      gameBoard->addWidget(button, i, j);
    }
  }

  vLayout->addWidget(statusLabel, 12);
  vLayout->addWidget(clearButton, 3);
  vLayout->addWidget(exitMenuButton, 3);

  hLayout->addLayout(vLayout, 7);
  hLayout->addLayout(gameBoard, 9);

  ui->centralwidget->setLayout(hLayout);
}

GamePVP::~GamePVP() { this->clear(); }

void GamePVP::handleButtonClick() {
  QPushButton *button = qobject_cast<QPushButton *>(sender());
  button->setEnabled(false);
  buttonsEnabled--;

  QString objectName = button->objectName();
  QStringList nameParts = objectName.split(",");
  int row = nameParts[0].toInt();
  int col = nameParts[1].toInt();

  if (turn == 1) {
    button->setStyleSheet(
        "QPushButton{ font: 900 20pt; background-color:  #e6e6e6; border: 0px "
        "#e6e6e6; color: red;}");
    statusLabel->setStyleSheet("QLabel{font: 900 30pt; color: blue}");
    button->setText("X");
    statusLabel->setText("O's turn");
  } else {
    button->setStyleSheet(
        "QPushButton{ font: 900 20pt; background-color:  #e6e6e6; border: 0px "
        "#e6e6e6; color: blue;}");
    statusLabel->setStyleSheet("QLabel{font: 900 30pt; color: red}");
    button->setText("O");
    statusLabel->setText("X's turn");
  }

  if (checkWin(row, col, (turn == 1 ? "X" : "O"))) {
    QString winner = (turn == 1 ? "X" : "O");
    statusLabel->setStyleSheet("QLabel {font: 900 40pt; color: green}");
    statusLabel->setText(QString("%1 wins!").arg(winner));

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
  }

  turn = (turn == 1 ? 2 : 1);
}

void GamePVP::exitButtonClick() {
  menu->show();
  this->clear();
  this->close();
}

void GamePVP::clearButtonClick() {
  for (int i = 0; i < fieldSize; ++i) {
    for (int j = 0; j < fieldSize; ++j) {
      QPushButton *button = dynamic_cast<QPushButton *>(
          gameBoard->itemAtPosition(i, j)->widget());
      button->setText(" ");
      button->setEnabled(true);
      button->setStyleSheet(
          "QPushButton{ font: 900 20pt; background-color: white; border: 1px "
          "solid black;"
          "color: white;}"
          "QPushButton:hover {background-color : #cccccc}"
          "QPushButton:pressed {background-color : #808080;}");
    }
  }

  statusLabel->setStyleSheet("QLabel{font: 900 30pt; color: red}");
  statusLabel->setText("X's turn");
  buttonsEnabled = fieldSize * fieldSize;
  turn = 1;
}

bool GamePVP::checkWin(int row, int col, QString player) {
  int i;
  bool win = true;

  for (i = 0; i < fieldSize; i++) {
    if (dynamic_cast<QPushButton *>(gameBoard->itemAtPosition(row, i)->widget())
            ->text() != player) {
      win = false;
      break;
    }
  }
  if (win) {
    return true;
  }

  win = true;
  for (i = 0; i < fieldSize; i++) {
    if (dynamic_cast<QPushButton *>(gameBoard->itemAtPosition(i, col)->widget())
            ->text() != player) {
      win = false;
      break;
    }
  }
  if (win) {
    return true;
  }

  if (row == col) {
    win = true;
    for (i = 0; i < fieldSize; i++) {
      if (dynamic_cast<QPushButton *>(gameBoard->itemAtPosition(i, i)->widget())
              ->text() != player) {
        win = false;
        break;
      }
    }
    if (win) {
      return true;
    }
  }

  if (row + col == fieldSize - 1) {
    win = true;
    for (i = 0; i < fieldSize; i++) {
      if (dynamic_cast<QPushButton *>(
              gameBoard->itemAtPosition(i, fieldSize - 1 - i)->widget())
              ->text() != player) {
        win = false;
        break;
      }
    }
    if (win) {
      return true;
    }
  }

  return false;
}

void GamePVP::clear() {
  delete ui;
  delete exitMenuButton;
  delete statusLabel;

  for (int i = 0; i < fieldSize; ++i) {
    for (int j = 0; j < fieldSize; ++j) {
      delete gameBoard->itemAtPosition(i, j)->widget();
    }
  }

  delete gameBoard;
  delete vLayout;
  delete hLayout;
}
