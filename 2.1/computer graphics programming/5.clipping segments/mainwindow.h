#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "plotarea.h"

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
    void on_segments_clicked();

    void on_poly_clicked();

    void on_spinBox_valueChanged(int arg1);

private:
    Ui::MainWindow *ui;
    PlotArea *area;
    QString segmentsPath = "segments.txt";
    QString polygonPath = "polygon.txt";
    void ProcessSegments();
    void ProcessPoly();
    void convex(const std::vector<QPointF> &polygon, const std::vector<std::pair<QPointF, QPointF>> &data);
    int getCode(qreal x, qreal y) const;
    int getCode(const QPointF &p) const;
};
#endif // MAINWINDOW_H
