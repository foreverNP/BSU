#ifndef GAMEPVP_H
#define GAMEPVP_H

#include <QMainWindow>
#include <QtWidgets>

#include "ui_mainwindow.h"

class GamePVP : public QMainWindow {
  Q_OBJECT

 public:
  GamePVP(int fieldSize_ = 3, QMainWindow *menu_ = nullptr,
          QWidget *parent = nullptr);
  virtual ~GamePVP();
  void clear();

 protected:
  const int fieldSize;

  Ui::MainWindow *ui;
  QMainWindow *menu;
  QGridLayout *gameBoard;
  QHBoxLayout *hLayout;
  QVBoxLayout *vLayout;
  QPushButton *exitMenuButton;
  QPushButton *clearButton;
  QLabel *statusLabel;

  int turn;
  int buttonsEnabled;
  bool checkWin(int row, int col, QString player);

 protected slots:
  virtual void handleButtonClick();
  void exitButtonClick();
  void clearButtonClick();
};

#endif
