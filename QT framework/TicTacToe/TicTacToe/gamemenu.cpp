#include "gamemenu.h"

GameMenu::GameMenu(QWidget *parent)
    : QMainWindow(parent), ui(new Ui::MainWindow) {
  ui->setupUi(this);

  setWindowTitle("Tic Tac Toe");

  layout = new QVBoxLayout;
  layout->setAlignment(Qt::AlignCenter);
  ui->centralwidget->setLayout(layout);

  QLabel *gameNameLabel = new QLabel("Tic Tac Toe");
  QLabel *sizeChooseLabel = new QLabel("Choose size");
  gameNameLabel->setAlignment(Qt::AlignCenter);
  sizeChooseLabel->setAlignment(Qt::AlignCenter);
  gameNameLabel->setMaximumHeight(200);
  sizeChooseLabel->setMaximumHeight(50);
  gameNameLabel->setStyleSheet("font: 900 20pt; color: rgb(0, 0, 0)");

  PVP = new QPushButton("Player vs Player", this);
  PVE = new QPushButton("Player vs Bot", this);
  gameBoardSize = new QComboBox(this);

  PVP->setMaximumSize(400, 200);
  PVP->setMinimumSize(180, 50);
  PVE->setMaximumSize(400, 200);
  PVE->setMinimumSize(180, 50);
  PVP->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
  PVE->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
  PVP->setStyleSheet(
      "QPushButton{ border-radius: 20; background-color: rgb(255, 255, 255); "
      "color: black;}"
      "QPushButton:hover {background-color : #cccccc}"
      "QPushButton:pressed {background-color : #808080;}");
  PVE->setStyleSheet(
      "QPushButton{ border-radius: 20; background-color: rgb(255, 255, 255); "
      "color: black;}"
      "QPushButton:hover {background-color : #cccccc}"
      "QPushButton:pressed {background-color : #808080;}");

  for (int i = 3; i <= 15; i++) {
    gameBoardSize->addItem(QString::number(i));
  }

  layout->addStretch();
  layout->addWidget(gameNameLabel);
  layout->addStretch();
  layout->addWidget(PVP);
  layout->addWidget(PVE);
  layout->addStretch();
  layout->addWidget(sizeChooseLabel);
  layout->addWidget(gameBoardSize);
  layout->addStretch();

  connect(PVP, &QPushButton::clicked, this, &GameMenu::onButtonPVPClicked);
  connect(PVE, &QPushButton::clicked, this, &GameMenu::onButtonPVEClicked);
}

void GameMenu::onButtonPVPClicked() {
  GamePVP *gamePVP = new GamePVP(gameBoardSize->currentIndex() + 3, this);
  this->close();
  gamePVP->show();
}

void GameMenu::onButtonPVEClicked() {
  GamePVE *gamePVE = new GamePVE(3, this);
  this->close();
  gamePVE->show();
}
