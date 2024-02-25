#ifndef GAMEMENU_H
#define GAMEMENU_H

#include <QMainWindow>
#include <QtWidgets>

#include "gamepvp.h"
#include "gamepve.h"
#include "ui_mainwindow.h"

class GameMenu : public QMainWindow {
  Q_OBJECT

 private:
  Ui::MainWindow *ui;

  QPushButton *PVP;
  QPushButton *PVE;
  QComboBox *gameBoardSize;
  QVBoxLayout *layout;

 public:
  GameMenu(QWidget *parent = nullptr);

 private slots:
  void onButtonPVPClicked();
  void onButtonPVEClicked();
};

#endif
