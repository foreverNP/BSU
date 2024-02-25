
#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QColorDialog>
#include "colors.h"

QT_BEGIN_NAMESPACE
namespace Ui
{
    class MainWindow;
}
QT_END_NAMESPACE

class MainWindow : public QMainWindow

{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:
    void on_pushButton_clicked();
    void on_comboBox_2_activated(int index);
    void on_comboBox_3_activated(int index);
    void on_comboBox_4_activated(int index);

    void valueChangedSpin1(double);
    void valueChangedSpin2(double);
    void valueChangedSpin3(double);

    void valueChangedSlider1(int);
    void valueChangedSlider2(int);
    void valueChangedSlider3(int);

private:
    Ui::MainWindow *ui;
    ColorSystem *first;
    ColorSystem *second;
    ColorSystem *third;
    QColor mainColor;

    void updateColor();

    void setFirst(QString);
    void setSecond(QString);
    void setThird(QString);

    void offSignals(bool);

    void set1GroupSliders(bool, double, double, double, double, double, double);
    void set2GroupSliders(bool, double, double, double, double, double, double);
    void set3GroupSliders(bool, double, double, double, double, double, double);
};

#endif // MAINWINDOW_H
