#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QBoxLayout>
#include <QComboBox>
#include <QFileDialog>
#include <QMessageBox>
#include <opencv2/opencv.hpp>
#include <QLabel>
#include <statustipfilter.h>

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

enum imageProccess{
    Blur,
    Gaussian,
    Otsu,
    Histogram,
    Adaptive,
    None
};

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private slots:

    void on_open_triggered();

    void on_default_images_triggered();

    void on_blur_triggered();

    void on_gaussian_triggered();

    void on_adaptive_thresholding_triggered();

    void on_otsu_thresholding_triggered();

    void on_histogramm_thresholding_triggered();

private:
    void clearLayout();
    void getSrc(cv::Mat& cvsrc, cv::Mat& cvdst);
    void setData(const cv::Mat& cvsrc, const cv::Mat& cvdst);
    int getHistogramThreshold(const cv::Mat& cvsrc);
    bool srcProccessed = false;
    QHBoxLayout *h = nullptr;
    Ui::MainWindow *ui;
    QString filepath;
    QPixmap src;
    QPixmap dst;
    QLabel *lsrc = nullptr;
    QLabel *ldst = nullptr;
    StatusTipFilter filter = StatusTipFilter();
    imageProccess curProccess = imageProccess::None;
};
#endif // MAINWINDOW_H
