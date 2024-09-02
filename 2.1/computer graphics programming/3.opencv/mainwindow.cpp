#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QDebug>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    menuBar()->installEventFilter(&filter);
}

MainWindow::~MainWindow()
{
    delete ui;
    delete h;
}

void MainWindow::clearLayout()
{
    delete lsrc;
    delete ldst;
    delete h;
    h = nullptr;
    lsrc = nullptr;
    ldst = nullptr;
}

void MainWindow::getSrc(cv::Mat& cvsrc, cv::Mat& cvdst)
{
    if (!filepath.isEmpty())
    {
        cvsrc = cv::imread(filepath.toStdString(), cv::IMREAD_ANYDEPTH);
    }
    else
    {
        QImage img;
        switch(curProccess)
        {
            case imageProccess::Blur:
                img = QImage(":/new/images/images/blur.bmp");
                cvsrc = cv::Mat(img.height(), img.width(), CV_8U, img.bits(), img.bytesPerLine()).clone();
                break;
            case imageProccess::Gaussian:
                img = QImage(":/new/images/images/gaussian.bmp");
                cvsrc = cv::Mat(img.height(), img.width(), CV_8U, img.bits(), img.bytesPerLine()).clone();
                break;
            case imageProccess::Histogram:
                img = QImage(":/new/images/images/histogram.bmp");
                img = img.convertToFormat(QImage::Format_Grayscale8);
                cvsrc = cv::Mat(img.height(), img.width(), CV_8U, img.bits(), img.bytesPerLine()).clone();
                break;
            case imageProccess::Otsu:
                img = QImage(":/new/images/images/otsu.bmp");
                cvsrc = cv::Mat(img.height(), img.width(), CV_8U, img.bits(), img.bytesPerLine()).clone();
                break;
            case imageProccess::Adaptive:
                img = QImage(":/new/images/images/adaptive.jpg");
                img.convertTo(QImage::Format_Grayscale8);
                img = img.scaled(400,400);
                cvsrc = cv::Mat(img.height(), img.width(), CV_8U, img.bits(), img.bytesPerLine()).clone();
                break;
            case imageProccess::None:

                break;
        };
    }
    cvdst = cvsrc.clone();
}

void MainWindow::setData(const cv::Mat& cvsrc, const cv::Mat& cvdst)
{
    srcProccessed = true;
    clearLayout();
    QImage im1((uchar*) cvsrc.data, cvsrc.cols, cvsrc.rows, cvsrc.step, QImage::Format_Grayscale8);
    QImage im2((uchar*) cvdst.data, cvdst.cols, cvdst.rows, cvdst.step, QImage::Format_Grayscale8);
    h = new QHBoxLayout(centralWidget());
    src.convertFromImage(im1.scaled(400, 400));
    dst.convertFromImage(im2.scaled(400, 400));
    lsrc = new QLabel(this), ldst = new QLabel(this);
    lsrc -> setPixmap(src);
    lsrc -> setScaledContents(true);
    ldst -> setPixmap(dst);
    ldst -> setScaledContents(true);
    h -> addWidget(lsrc);
    h -> addWidget(ldst);
}

int MainWindow::getHistogramThreshold(const cv::Mat& cvsrc)
{
    int prev = 0, cur = 10;
    const int eps = 5;
    int u[2];

    int hist[256] = {0};
    for(int i = 0; i < cvsrc.rows; ++i)
        for(int j = 0; j < cvsrc.cols; ++j)
        {
            ++hist[cvsrc.at<uchar>(i,j)];
        }

    long long sums[256], cnts[256];
    sums[0] = 0;
    cnts[0] = hist[0];
    for (int i = 1; i < 256; ++i)
    {
        sums[i] = sums[i - 1] + hist[i] * i;
        cnts[i] = cnts[i - 1] + hist[i];
    }

    while(std::abs<int>(cur - prev) > eps)
    {
        u[0] = sums[cur] / (std::max(cnts[cur], 1LL));
        u[1] = (sums[255] - sums[cur]) / (std::max(cnts[255] - cnts[cur], 1LL));
        prev = cur;
        cur = (u[0] + u[1]) / 2;
    }
    return cur;
}

void MainWindow::on_open_triggered()
{
    filepath = QFileDialog::getOpenFileName(this, "Открыть файл", "/home", "Images (*.jpg *.bmp *.png *.jpeg)");
    if (!filepath.isEmpty())
    {
        ui -> default_images -> setChecked(false);
        ui -> default_images -> setEnabled(true);
        clearLayout();
        statusBar() -> showMessage("Выбран файл: " + filepath);
    }
}

void MainWindow::on_default_images_triggered()
{
    filepath = "";
    clearLayout();
    srcProccessed = false;
    ui -> default_images -> setEnabled(false);
}


void MainWindow::on_blur_triggered()
{
    curProccess = imageProccess::Blur;
    cv::Mat cvdst, cvsrc;
    getSrc(cvsrc, cvdst);
    cv::blur(cvsrc, cvdst, cv::Size(3, 3));
    setData(cvsrc, cvdst);
}


void MainWindow::on_gaussian_triggered()
{
    curProccess = imageProccess::Gaussian;
    cv::Mat cvdst, cvsrc;
    getSrc(cvsrc, cvdst);
    cv::GaussianBlur(cvsrc, cvdst, cv::Size(5, 5), 0, 0);
    setData(cvsrc, cvdst);
}


void MainWindow::on_adaptive_thresholding_triggered()
{
    curProccess = imageProccess::Adaptive;
    cv::Mat cvdst, cvsrc;
    getSrc(cvsrc, cvdst);
    cv::adaptiveThreshold(cvsrc, cvdst, 255, cv::AdaptiveThresholdTypes::ADAPTIVE_THRESH_MEAN_C, cv::ThresholdTypes::THRESH_BINARY, 7, 0);
    setData(cvsrc, cvdst);
}


void MainWindow::on_otsu_thresholding_triggered()
{
    curProccess = imageProccess::Otsu;
    cv::Mat cvdst, cvsrc;
    getSrc(cvsrc, cvdst);
    cv::threshold(cvsrc, cvdst, 0, 255, cv::THRESH_BINARY + cv::THRESH_OTSU);
    setData(cvsrc, cvdst);
}


void MainWindow::on_histogramm_thresholding_triggered()
{
    curProccess = imageProccess::Histogram;
    cv::Mat cvdst, cvsrc;
    getSrc(cvsrc, cvdst);
    cv::threshold(cvsrc, cvdst, getHistogramThreshold(cvsrc), 255, cv::THRESH_BINARY);
    setData(cvsrc, cvdst);
}

