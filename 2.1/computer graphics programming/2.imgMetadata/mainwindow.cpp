#include "mainwindow.h"
#include "ui_mainwindow.h"

#include <QDir>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent), ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    ui->tableWidget->horizontalHeader()->setSectionResizeMode(QHeaderView::Stretch);
    ui->tableWidget->setEditTriggers(QAbstractItemView::NoEditTriggers);
    ui->checkBox->setToolTip("Если установлен, то при открытии директории текующие данные очищаются");

    QObject::connect(ui->tableWidget->horizontalHeader(), &QHeaderView::sectionClicked, this, &MainWindow::sortByColumn);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::on_pushButton_clicked()
{
    QString filter = "Images (*.jpg *.gif *.tif *.bmp *.png *.pcx *.BMP)";
    QList<QUrl> files = QFileDialog::getOpenFileUrls(nullptr, "Выберите файл(ы)", QDir::homePath(), filter);

    updateInfo(files);
}

void MainWindow::on_pushButton_2_clicked()
{
    QString folderPath = QFileDialog::getExistingDirectory(nullptr, "Выберите папку", QDir::homePath());

    if (!folderPath.isEmpty())
    {
        if (ui->checkBox->isChecked())
        {
            ui->tableWidget->setRowCount(0);
        }

        QDir dir(folderPath);
        QStringList filters;
        filters << "*.jpg"
                << "*.gif"
                << "*.tif"
                << "*.bmp"
                << "*.png"
                << "*.pcx"
                << "*.BMP";
        dir.setNameFilters(filters);

        QList<QUrl> files;
        for (const QFileInfo &fileInfo : dir.entryInfoList())
        {
            files.append(QUrl::fromLocalFile(fileInfo.filePath()));
        }

        updateInfo(files);
    }
}

void MainWindow::sortByColumn(int logicalIndex)
{
    Qt::SortOrder currentOrder = ui->tableWidget->horizontalHeader()->sortIndicatorOrder();
    if (currentOrder == Qt::DescendingOrder)
    {
        ui->tableWidget->sortItems(logicalIndex, Qt::AscendingOrder);
    }
    else
    {
        ui->tableWidget->sortItems(logicalIndex, Qt::DescendingOrder);
    }
}

void MainWindow::updateInfo(QList<QUrl> files)
{
    for (int i = 0; i < files.size(); i++)
    {
        int row = ui->tableWidget->rowCount();
        ui->tableWidget->insertRow(row);

        QString fileName = "", resolution = "", type = "";
        QFile temp(files[i].toLocalFile());
        QImage im(files[i].toLocalFile());
        QImageWriter a(files[i].toLocalFile());
        int compression = a.compression();

        if (temp.fileName().lastIndexOf('.') == -1)
        {
            ui->tableWidget->setRowCount(ui->tableWidget->rowCount() - 1);
            continue;
        }

        for (int i = temp.fileName().lastIndexOf('.'); i < temp.fileName().size(); i++)
        {
            type.append(temp.fileName()[i]);
        }

        if (type == ".jpg")
        {
            compression = im.text("JPEGQuality").toInt();
        }
        else if (type == ".tif")
        {
            compression = im.text("Compression").toInt();
        }
        else if (type == ".png")
        {
            compression = im.text("CompressionLevel").toInt();
        }

        resolution = QString::number((int)(im.dotsPerMeterX() * 0.0254)) + "x" + QString::number((int)(im.dotsPerMeterY() * 0.0254));

        for (int i = temp.fileName().lastIndexOf('/') + 1; i < temp.fileName().lastIndexOf('.'); i++)
        {
            fileName.append(temp.fileName()[i]);
        }

        ui->tableWidget->setItem(row, 0, new QTableWidgetItem(fileName));
        ui->tableWidget->setItem(row, 1, new QTableWidgetItem(QString::number(im.size().width()) + "x" + QString::number(im.size().height())));
        ui->tableWidget->setItem(row, 2, new QTableWidgetItem(resolution));
        ui->tableWidget->setItem(row, 3, new QTableWidgetItem(QString::number(im.bitPlaneCount())));
        ui->tableWidget->setItem(row, 4, new QTableWidgetItem(QString::number(compression)));
    }
}
