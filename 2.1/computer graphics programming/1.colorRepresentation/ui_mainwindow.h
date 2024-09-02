/********************************************************************************
** Form generated from reading UI file 'mainwindow.ui'
**
** Created by: Qt User Interface Compiler version 6.5.0
**
** WARNING! All changes made in this file will be lost when recompiling UI file!
********************************************************************************/

#ifndef UI_MAINWINDOW_H
#define UI_MAINWINDOW_H

#include <QtCore/QVariant>
#include <QtWidgets/QApplication>
#include <QtWidgets/QComboBox>
#include <QtWidgets/QDoubleSpinBox>
#include <QtWidgets/QLabel>
#include <QtWidgets/QMainWindow>
#include <QtWidgets/QMenuBar>
#include <QtWidgets/QPushButton>
#include <QtWidgets/QSlider>
#include <QtWidgets/QStatusBar>
#include <QtWidgets/QWidget>

QT_BEGIN_NAMESPACE

class Ui_MainWindow
{
public:
    QWidget *centralwidget;
    QPushButton *pushButton;
    QLabel *label_2;
    QDoubleSpinBox *doubleSpinBox;
    QDoubleSpinBox *doubleSpinBox_2;
    QDoubleSpinBox *doubleSpinBox_3;
    QDoubleSpinBox *doubleSpinBox_4;
    QLabel *label_3;
    QLabel *label_4;
    QLabel *label_5;
    QLabel *label_6;
    QLabel *label_7;
    QLabel *label_8;
    QDoubleSpinBox *doubleSpinBox_5;
    QLabel *label_9;
    QDoubleSpinBox *doubleSpinBox_6;
    QLabel *label_10;
    QDoubleSpinBox *doubleSpinBox_7;
    QDoubleSpinBox *doubleSpinBox_8;
    QLabel *label_11;
    QLabel *label_12;
    QLabel *label_13;
    QDoubleSpinBox *doubleSpinBox_9;
    QLabel *label_14;
    QDoubleSpinBox *doubleSpinBox_10;
    QLabel *label_15;
    QDoubleSpinBox *doubleSpinBox_11;
    QDoubleSpinBox *doubleSpinBox_12;
    QLabel *label_16;
    QComboBox *comboBox_2;
    QComboBox *comboBox_3;
    QComboBox *comboBox_4;
    QSlider *horizontalSlider;
    QSlider *horizontalSlider_2;
    QSlider *horizontalSlider_3;
    QSlider *horizontalSlider_4;
    QSlider *horizontalSlider_5;
    QSlider *horizontalSlider_6;
    QSlider *horizontalSlider_7;
    QSlider *horizontalSlider_8;
    QSlider *horizontalSlider_9;
    QSlider *horizontalSlider_10;
    QSlider *horizontalSlider_11;
    QSlider *horizontalSlider_12;
    QMenuBar *menubar;
    QStatusBar *statusbar;

    void setupUi(QMainWindow *MainWindow)
    {
        if (MainWindow->objectName().isEmpty())
            MainWindow->setObjectName("MainWindow");
        MainWindow->resize(800, 600);
        MainWindow->setMinimumSize(QSize(800, 600));
        MainWindow->setMaximumSize(QSize(800, 600));
        centralwidget = new QWidget(MainWindow);
        centralwidget->setObjectName("centralwidget");
        pushButton = new QPushButton(centralwidget);
        pushButton->setObjectName("pushButton");
        pushButton->setGeometry(QRect(300, 40, 160, 160));
        label_2 = new QLabel(centralwidget);
        label_2->setObjectName("label_2");
        label_2->setGeometry(QRect(80, 290, 71, 21));
        doubleSpinBox = new QDoubleSpinBox(centralwidget);
        doubleSpinBox->setObjectName("doubleSpinBox");
        doubleSpinBox->setGeometry(QRect(80, 320, 91, 29));
        doubleSpinBox->setDecimals(0);
        doubleSpinBox->setMaximum(255.000000000000000);
        doubleSpinBox_2 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_2->setObjectName("doubleSpinBox_2");
        doubleSpinBox_2->setGeometry(QRect(80, 380, 91, 29));
        doubleSpinBox_2->setDecimals(0);
        doubleSpinBox_2->setMaximum(255.000000000000000);
        doubleSpinBox_3 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_3->setObjectName("doubleSpinBox_3");
        doubleSpinBox_3->setGeometry(QRect(80, 440, 91, 29));
        doubleSpinBox_3->setDecimals(0);
        doubleSpinBox_3->setMaximum(255.000000000000000);
        doubleSpinBox_4 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_4->setObjectName("doubleSpinBox_4");
        doubleSpinBox_4->setEnabled(false);
        doubleSpinBox_4->setGeometry(QRect(80, 500, 91, 29));
        label_3 = new QLabel(centralwidget);
        label_3->setObjectName("label_3");
        label_3->setGeometry(QRect(50, 320, 20, 20));
        label_4 = new QLabel(centralwidget);
        label_4->setObjectName("label_4");
        label_4->setGeometry(QRect(50, 380, 20, 20));
        label_5 = new QLabel(centralwidget);
        label_5->setObjectName("label_5");
        label_5->setGeometry(QRect(50, 440, 20, 20));
        label_6 = new QLabel(centralwidget);
        label_6->setObjectName("label_6");
        label_6->setGeometry(QRect(50, 500, 20, 20));
        label_7 = new QLabel(centralwidget);
        label_7->setObjectName("label_7");
        label_7->setGeometry(QRect(310, 440, 20, 20));
        label_8 = new QLabel(centralwidget);
        label_8->setObjectName("label_8");
        label_8->setGeometry(QRect(340, 290, 71, 21));
        doubleSpinBox_5 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_5->setObjectName("doubleSpinBox_5");
        doubleSpinBox_5->setGeometry(QRect(340, 320, 91, 29));
        doubleSpinBox_5->setDecimals(3);
        doubleSpinBox_5->setMaximum(100.000000000000000);
        label_9 = new QLabel(centralwidget);
        label_9->setObjectName("label_9");
        label_9->setGeometry(QRect(310, 500, 20, 20));
        doubleSpinBox_6 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_6->setObjectName("doubleSpinBox_6");
        doubleSpinBox_6->setGeometry(QRect(340, 440, 91, 29));
        doubleSpinBox_6->setDecimals(3);
        doubleSpinBox_6->setMinimum(-128.000000000000000);
        doubleSpinBox_6->setMaximum(128.000000000000000);
        label_10 = new QLabel(centralwidget);
        label_10->setObjectName("label_10");
        label_10->setGeometry(QRect(310, 380, 20, 20));
        doubleSpinBox_7 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_7->setObjectName("doubleSpinBox_7");
        doubleSpinBox_7->setGeometry(QRect(340, 380, 91, 29));
        doubleSpinBox_7->setDecimals(3);
        doubleSpinBox_7->setMinimum(-128.000000000000000);
        doubleSpinBox_7->setMaximum(128.000000000000000);
        doubleSpinBox_8 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_8->setObjectName("doubleSpinBox_8");
        doubleSpinBox_8->setEnabled(false);
        doubleSpinBox_8->setGeometry(QRect(340, 500, 91, 29));
        label_11 = new QLabel(centralwidget);
        label_11->setObjectName("label_11");
        label_11->setGeometry(QRect(310, 320, 20, 20));
        label_12 = new QLabel(centralwidget);
        label_12->setObjectName("label_12");
        label_12->setGeometry(QRect(570, 440, 20, 20));
        label_13 = new QLabel(centralwidget);
        label_13->setObjectName("label_13");
        label_13->setGeometry(QRect(600, 290, 71, 21));
        doubleSpinBox_9 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_9->setObjectName("doubleSpinBox_9");
        doubleSpinBox_9->setGeometry(QRect(600, 320, 91, 29));
        doubleSpinBox_9->setDecimals(3);
        doubleSpinBox_9->setMaximum(100.000000000000000);
        label_14 = new QLabel(centralwidget);
        label_14->setObjectName("label_14");
        label_14->setGeometry(QRect(570, 500, 20, 20));
        doubleSpinBox_10 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_10->setObjectName("doubleSpinBox_10");
        doubleSpinBox_10->setGeometry(QRect(600, 440, 91, 29));
        doubleSpinBox_10->setDecimals(3);
        doubleSpinBox_10->setMaximum(100.000000000000000);
        label_15 = new QLabel(centralwidget);
        label_15->setObjectName("label_15");
        label_15->setGeometry(QRect(570, 380, 20, 20));
        doubleSpinBox_11 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_11->setObjectName("doubleSpinBox_11");
        doubleSpinBox_11->setGeometry(QRect(600, 380, 91, 29));
        doubleSpinBox_11->setDecimals(3);
        doubleSpinBox_11->setMaximum(100.000000000000000);
        doubleSpinBox_12 = new QDoubleSpinBox(centralwidget);
        doubleSpinBox_12->setObjectName("doubleSpinBox_12");
        doubleSpinBox_12->setGeometry(QRect(600, 500, 91, 29));
        doubleSpinBox_12->setDecimals(3);
        doubleSpinBox_12->setMaximum(100.000000000000000);
        label_16 = new QLabel(centralwidget);
        label_16->setObjectName("label_16");
        label_16->setGeometry(QRect(570, 320, 20, 20));
        comboBox_2 = new QComboBox(centralwidget);
        comboBox_2->addItem(QString());
        comboBox_2->addItem(QString());
        comboBox_2->addItem(QString());
        comboBox_2->addItem(QString());
        comboBox_2->addItem(QString());
        comboBox_2->addItem(QString());
        comboBox_2->setObjectName("comboBox_2");
        comboBox_2->setGeometry(QRect(80, 250, 91, 28));
        comboBox_3 = new QComboBox(centralwidget);
        comboBox_3->addItem(QString());
        comboBox_3->addItem(QString());
        comboBox_3->addItem(QString());
        comboBox_3->addItem(QString());
        comboBox_3->addItem(QString());
        comboBox_3->addItem(QString());
        comboBox_3->setObjectName("comboBox_3");
        comboBox_3->setGeometry(QRect(340, 250, 91, 28));
        comboBox_4 = new QComboBox(centralwidget);
        comboBox_4->addItem(QString());
        comboBox_4->addItem(QString());
        comboBox_4->addItem(QString());
        comboBox_4->addItem(QString());
        comboBox_4->addItem(QString());
        comboBox_4->addItem(QString());
        comboBox_4->setObjectName("comboBox_4");
        comboBox_4->setGeometry(QRect(600, 250, 91, 28));
        horizontalSlider = new QSlider(centralwidget);
        horizontalSlider->setObjectName("horizontalSlider");
        horizontalSlider->setGeometry(QRect(40, 350, 160, 18));
        horizontalSlider->setMaximum(255);
        horizontalSlider->setOrientation(Qt::Horizontal);
        horizontalSlider_2 = new QSlider(centralwidget);
        horizontalSlider_2->setObjectName("horizontalSlider_2");
        horizontalSlider_2->setGeometry(QRect(40, 410, 160, 18));
        horizontalSlider_2->setMaximum(255);
        horizontalSlider_2->setOrientation(Qt::Horizontal);
        horizontalSlider_3 = new QSlider(centralwidget);
        horizontalSlider_3->setObjectName("horizontalSlider_3");
        horizontalSlider_3->setGeometry(QRect(40, 470, 160, 18));
        horizontalSlider_3->setMaximum(255);
        horizontalSlider_3->setOrientation(Qt::Horizontal);
        horizontalSlider_4 = new QSlider(centralwidget);
        horizontalSlider_4->setObjectName("horizontalSlider_4");
        horizontalSlider_4->setGeometry(QRect(40, 530, 160, 18));
        horizontalSlider_4->setMaximum(100);
        horizontalSlider_4->setOrientation(Qt::Horizontal);
        horizontalSlider_5 = new QSlider(centralwidget);
        horizontalSlider_5->setObjectName("horizontalSlider_5");
        horizontalSlider_5->setGeometry(QRect(300, 410, 160, 18));
        horizontalSlider_5->setMinimum(-128);
        horizontalSlider_5->setMaximum(128);
        horizontalSlider_5->setOrientation(Qt::Horizontal);
        horizontalSlider_6 = new QSlider(centralwidget);
        horizontalSlider_6->setObjectName("horizontalSlider_6");
        horizontalSlider_6->setGeometry(QRect(300, 530, 160, 18));
        horizontalSlider_6->setMaximum(100);
        horizontalSlider_6->setOrientation(Qt::Horizontal);
        horizontalSlider_7 = new QSlider(centralwidget);
        horizontalSlider_7->setObjectName("horizontalSlider_7");
        horizontalSlider_7->setGeometry(QRect(300, 470, 160, 18));
        horizontalSlider_7->setMinimum(-128);
        horizontalSlider_7->setMaximum(128);
        horizontalSlider_7->setOrientation(Qt::Horizontal);
        horizontalSlider_8 = new QSlider(centralwidget);
        horizontalSlider_8->setObjectName("horizontalSlider_8");
        horizontalSlider_8->setGeometry(QRect(300, 350, 160, 18));
        horizontalSlider_8->setMinimum(0);
        horizontalSlider_8->setMaximum(100);
        horizontalSlider_8->setOrientation(Qt::Horizontal);
        horizontalSlider_9 = new QSlider(centralwidget);
        horizontalSlider_9->setObjectName("horizontalSlider_9");
        horizontalSlider_9->setGeometry(QRect(560, 410, 160, 18));
        horizontalSlider_9->setMaximum(100);
        horizontalSlider_9->setOrientation(Qt::Horizontal);
        horizontalSlider_10 = new QSlider(centralwidget);
        horizontalSlider_10->setObjectName("horizontalSlider_10");
        horizontalSlider_10->setGeometry(QRect(560, 530, 160, 18));
        horizontalSlider_10->setMaximum(100);
        horizontalSlider_10->setOrientation(Qt::Horizontal);
        horizontalSlider_11 = new QSlider(centralwidget);
        horizontalSlider_11->setObjectName("horizontalSlider_11");
        horizontalSlider_11->setGeometry(QRect(560, 470, 160, 18));
        horizontalSlider_11->setMaximum(100);
        horizontalSlider_11->setOrientation(Qt::Horizontal);
        horizontalSlider_12 = new QSlider(centralwidget);
        horizontalSlider_12->setObjectName("horizontalSlider_12");
        horizontalSlider_12->setGeometry(QRect(560, 350, 160, 18));
        horizontalSlider_12->setMaximum(100);
        horizontalSlider_12->setOrientation(Qt::Horizontal);
        MainWindow->setCentralWidget(centralwidget);
        menubar = new QMenuBar(MainWindow);
        menubar->setObjectName("menubar");
        menubar->setGeometry(QRect(0, 0, 800, 25));
        MainWindow->setMenuBar(menubar);
        statusbar = new QStatusBar(MainWindow);
        statusbar->setObjectName("statusbar");
        MainWindow->setStatusBar(statusbar);

        retranslateUi(MainWindow);

        QMetaObject::connectSlotsByName(MainWindow);
    } // setupUi

    void retranslateUi(QMainWindow *MainWindow)
    {
        MainWindow->setWindowTitle(QCoreApplication::translate("MainWindow", "MainWindow", nullptr));
        pushButton->setText(QString());
        label_2->setText(QCoreApplication::translate("MainWindow", "RGB", nullptr));
        label_3->setText(QCoreApplication::translate("MainWindow", "R", nullptr));
        label_4->setText(QCoreApplication::translate("MainWindow", "G", nullptr));
        label_5->setText(QCoreApplication::translate("MainWindow", "B", nullptr));
        label_6->setText(QCoreApplication::translate("MainWindow", "-", nullptr));
        label_7->setText(QCoreApplication::translate("MainWindow", "B", nullptr));
        label_8->setText(QCoreApplication::translate("MainWindow", "LAB", nullptr));
        label_9->setText(QCoreApplication::translate("MainWindow", "-", nullptr));
        label_10->setText(QCoreApplication::translate("MainWindow", "A", nullptr));
        label_11->setText(QCoreApplication::translate("MainWindow", "L", nullptr));
        label_12->setText(QCoreApplication::translate("MainWindow", "Y", nullptr));
        label_13->setText(QCoreApplication::translate("MainWindow", "CMYK", nullptr));
        label_14->setText(QCoreApplication::translate("MainWindow", "K", nullptr));
        label_15->setText(QCoreApplication::translate("MainWindow", "M", nullptr));
        label_16->setText(QCoreApplication::translate("MainWindow", "C", nullptr));
        comboBox_2->setItemText(0, QCoreApplication::translate("MainWindow", "RGB", nullptr));
        comboBox_2->setItemText(1, QCoreApplication::translate("MainWindow", "CMYK", nullptr));
        comboBox_2->setItemText(2, QCoreApplication::translate("MainWindow", "HSV", nullptr));
        comboBox_2->setItemText(3, QCoreApplication::translate("MainWindow", "HSL", nullptr));
        comboBox_2->setItemText(4, QCoreApplication::translate("MainWindow", "XYZ", nullptr));
        comboBox_2->setItemText(5, QCoreApplication::translate("MainWindow", "LAB", nullptr));

        comboBox_3->setItemText(0, QCoreApplication::translate("MainWindow", "LAB", nullptr));
        comboBox_3->setItemText(1, QCoreApplication::translate("MainWindow", "RGB", nullptr));
        comboBox_3->setItemText(2, QCoreApplication::translate("MainWindow", "CMYK", nullptr));
        comboBox_3->setItemText(3, QCoreApplication::translate("MainWindow", "HSV", nullptr));
        comboBox_3->setItemText(4, QCoreApplication::translate("MainWindow", "HSL", nullptr));
        comboBox_3->setItemText(5, QCoreApplication::translate("MainWindow", "XYZ", nullptr));

        comboBox_4->setItemText(0, QCoreApplication::translate("MainWindow", "CMYK", nullptr));
        comboBox_4->setItemText(1, QCoreApplication::translate("MainWindow", "RGB", nullptr));
        comboBox_4->setItemText(2, QCoreApplication::translate("MainWindow", "HSV", nullptr));
        comboBox_4->setItemText(3, QCoreApplication::translate("MainWindow", "HSL", nullptr));
        comboBox_4->setItemText(4, QCoreApplication::translate("MainWindow", "XYZ", nullptr));
        comboBox_4->setItemText(5, QCoreApplication::translate("MainWindow", "LAB", nullptr));

    } // retranslateUi

};

namespace Ui {
    class MainWindow: public Ui_MainWindow {};
} // namespace Ui

QT_END_NAMESPACE

#endif // UI_MAINWINDOW_H
