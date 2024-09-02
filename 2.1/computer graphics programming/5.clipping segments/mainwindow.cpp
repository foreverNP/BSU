#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QGridLayout>
#include <fstream>
#include <QMessageBox>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent), ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    setStyleSheet("background-color: white;");
    QGridLayout *g = new QGridLayout(centralWidget());

    area = new PlotArea();

    g->addWidget(area, 0, 0, 10, 4);
    g->addWidget(ui->clippingText, 0, 4, 1, 1);
    g->addWidget(ui->segments, 1, 4, 1, 1);
    g->addWidget(ui->poly, 2, 4, 1, 1);
    g->addWidget(ui->sizeLabel, 3, 4, 1, 1);
    g->addWidget(ui->spinBox, 4, 4, 1, 1);

    centralWidget()->setLayout(g);

    setMinimumSize({600, 600});
    setWindowTitle("Отсечения");
}

int MainWindow::getCode(qreal x, qreal y) const
{
    int ans = 0;
    QPoint p1 = area->getClippingWindowP1();
    QPoint p2 = area->getClippingWindowP2();
    if (y > p1.y())
    {
        ans |= 8;
    }
    if (y < p2.y())
    {
        ans |= 4;
    }
    if (x > p2.x())
    {
        ans |= 2;
    }
    if (x < p1.x())
    {
        ans |= 1;
    }
    return ans;
}

int MainWindow::getCode(const QPointF &p) const
{
    return getCode(p.x(), p.y());
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::ProcessSegments()
{
    std::ifstream fin(segmentsPath.toStdString());

    if (!fin)
    {
        QMessageBox::warning(nullptr, "Ошибка", "Файл segments.txt в папке исполняемого файла не найден");
        return;
    }

    int n;
    fin >> n;
    qreal x1, x2, y1, y2;
    std::vector<std::pair<QPointF, QPointF>> data;

    for (int i = 0; i < n; ++i)
    {
        fin >> x1 >> y1 >> x2 >> y2;
        data.push_back({QPointF(x1, y1), QPointF(x2, y2)});
    }
    fin >> x1 >> y1 >> x2 >> y2;
    area->SetClippingWindow(QPoint(x1, y1), QPoint(x2, y2));
    fin.close();

    for (int i = 0; i < n; ++i)
    {
        x1 = data[i].first.x();
        y1 = data[i].first.y();
        x2 = data[i].second.x();
        y2 = data[i].second.y();
        int code1 = getCode(x1, y1);
        int code2 = getCode(x2, y2);
        if (code1 == 0 && code2 == 0)
        {
            // inside
            area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(x2, y2), Qt::blue});
        }
        else if ((code1 & code2) != 0)
        {
            // outside
            area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(x2, y2), Qt::red});
        }
        else
        {
            qreal A = y2 - y1;
            qreal B = x1 - x2;
            qreal C = -A * x1 - B * y1;
            qreal xmin = area->getClippingWindowP1().x();
            qreal ymax = area->getClippingWindowP1().y();
            qreal xmax = area->getClippingWindowP2().x();
            qreal ymin = area->getClippingWindowP2().y();
            while (code1 != 0 || code2 != 0)
            {
                if (code1 == 0)
                {
                    std::swap(x1, x2);
                    std::swap(y1, y2);
                    std::swap(code1, code2);
                }
                if (code1 & 1)
                {
                    // leftside xmin
                    qreal y = (-C - A * xmin) / B;
                    area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(xmin, y), Qt::red});
                    x1 = xmin;
                    y1 = y;
                }
                else if (code1 & 2)
                {
                    // rightside xmax
                    qreal y = (-C - A * xmax) / B;
                    area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(xmax, y), Qt::red});
                    x1 = xmax;
                    y1 = y;
                }
                else if (code1 & 4)
                {
                    // bottomside ymin
                    qreal x = (-C - B * ymin) / A;
                    area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(x, ymin), Qt::red});
                    x1 = x;
                    y1 = ymin;
                }
                else
                {
                    // topside ymax
                    qreal x = (-C - B * ymax) / A;
                    area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(x, ymax), Qt::red});
                    x1 = x;
                    y1 = ymax;
                }
                code1 = getCode(x1, y1);
            }
            area->AddLineSegment(LineSegmentData{QPointF(x1, y1), QPointF(x2, y2), Qt::blue});
        }
    }
}

void MainWindow::ProcessPoly()
{
    std::ifstream finPol(polygonPath.toStdString());
    std::ifstream finSeg(segmentsPath.toStdString());

    if (!finPol)
    {
        QMessageBox::warning(nullptr, "Ошибка", "Файл polygon.txt в папке исполняемого файла не найден");
        return;
    }
    if (!finSeg)
    {
        QMessageBox::warning(nullptr, "Ошибка", "Файл segments.txt в папке исполняемого файла не найден");
        return;
    }

    int n;
    int x1, y1, x2, y2;

    std::vector<QPointF> polygon;
    std::vector<std::pair<QPointF, QPointF>> data;

    finPol >> n;
    if( n < 3)
    {
        QMessageBox::warning(nullptr, "Ошибка", "Файл polygon.txt содержит некорректный полигон");
        return;
    }

    for (int i = 0; i < n; ++i)
    {
        finPol >> x1 >> y1;
        polygon.push_back(QPointF(x1, y1));
    }
    polygon.push_back(polygon[0]);

    finSeg >> n;
    for (int i = 0; i < n; ++i)
    {
        finSeg >> x1 >> y1 >> x2 >> y2;
        data.push_back({QPointF(x1, y1), QPointF(x2, y2)});
    }

    area->SetClippingWindow(polygon);

    for (int i = 0; i < data.size(); i++)
    {
         area->AddLineSegment(LineSegmentData{QPointF(data[i].first.x(), data[i].first.y()), QPointF(data[i].second.x(), data[i].second.y()), Qt::red});
    }

    convex(polygon, data);

    finPol.close();
    finSeg.close();
}

void MainWindow::convex(const std::vector<QPointF> &polygon, const std::vector<std::pair<QPointF, QPointF>> &data)
{
    for (int j = data.size() - 1 ; j >= 0; j--)
    {
        double a = data[j].second.x() - data[j].first.x();
        double b = data[j].second.y() - data[j].first.y();

        double tmin = 0., tmax = 1.;
        bool find = true;

        for (int i = 0; i < polygon.size() - 1; i++)
        {
            QPointF c1c2 = polygon[i + 1] - polygon[i];
            QPointF c1a = data[j].first - polygon[i];
            double v1 = c1c2.x() * b - c1c2.y() * a;
            double v2 = c1c2.x() * c1a.y() - c1c2.y() * c1a.x();

            if (v1 > 0)
            {
                double temp = -1.*(double)v2/v1;
                tmin = std::max(tmin, temp);
            }
            else if (v1 < 0)
            {
                double temp = -1.*(double)v2/v1;
                tmax = std::min(tmax, temp);
            }
            else if (v1 == 0 && v2 < 0)
            {
                find = false;
                break;
            }

            if (tmin > tmax)
            {
                find = false;
                break;
            }
        }

        if (find)
        {
            QPointF p1(data[j].first.x() + a * tmin, data[j].first.y() + b * tmin);
            QPointF p2(data[j].first.x() + a * tmax, data[j].first.y() + b * tmax);
            area->AddLineSegment(LineSegmentData{p1, p2, Qt::blue});
        }
    }
}

void MainWindow::on_segments_clicked()
{
    area->Clear();
    area->ChangeMode(PlotMode::Segments);
    ProcessSegments();
    area->repaint();
}

void MainWindow::on_poly_clicked()
{
    area->Clear();
    area->ChangeMode(PlotMode::Polygon);
    ProcessPoly();
    area->repaint();
}

void MainWindow::on_spinBox_valueChanged(int arg1)
{
    area->SetUnit(arg1);
    area->repaint();
}

