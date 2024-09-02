#ifndef GAMEPVE_H
#define GAMEPVE_HF

#include "gamepvp.h"

using std::pair;

class GamePVE : public GamePVP {
  Q_OBJECT
 public:
  GamePVE(int fieldSize_ = 3, QMainWindow *menu_ = nullptr,
          QWidget *parent = nullptr);
  ~GamePVE() {}

 private:
  int minimaxAlgo(bool isMaximizing, int depth);
  pair<int, int> findBestMove();

  int checkWin(QString player);

 private slots:
  void handleButtonClick() override;
};

#endif
