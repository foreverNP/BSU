#ifndef PLOTAREA_H
#define PLOTAREA_H

#include <QPainter>
#include <QWidget>
#include <vector>

class LineSegmentData
{
public:
    explicit LineSegmentData(const QPointF &p1, const QPointF &p2, const QColor &color);
    qreal x1() const;
    qreal x2() const;
    qreal y1() const;
    qreal y2() const;
    QPointF p1() const;
    QPointF p2() const;
    QColor color() const;

private:
    QPointF _p1, _p2;
    QColor _color;
};

enum class PlotMode
{
    None,
    Segments,
    Polygon,
};

class PlotArea : public QWidget
{
    Q_OBJECT

public:
    explicit PlotArea(QWidget *parent = nullptr, PlotMode mode = PlotMode::None);
    void AddLineSegment(const LineSegmentData &data);
    QPointF Adjust(const QPointF &p);
    void AddPolygon(const std::vector<QPointF> &points, const QColor &fillingColor);
    void SetPolygonBorderColor(const QColor &color);
    void SetClippingWindow(const QPoint &p1, const QPoint &p2);
    void SetClippingWindow(const std::vector<QPointF> &points);
    void ChangeMode(PlotMode newMode);
    void Clear();
    void SetUnit(int nu);
    int getUnit() const;
    QPoint getClippingWindowP1() const;
    QPoint getClippingWindowP2() const;

private:
    int u;
    int tickLength = 4;
    int gridLineWidth = 1;
    int axisWidth = 2;
    int boxOffset = 1;
    int box_width = 2;
    int pixelWidth = 1;
    int lineWidth = 3;
    int zx = 0;
    int zy = 0;

    PlotMode mode = PlotMode::None;
    std::vector<LineSegmentData> segments;
    std::vector<QPointF> clippingWindowPoints;

    QColor polygonBorderColor = Qt::black;
    QColor clippingWindowColor = Qt::green;
    QColor axisColor = Qt::black;
    QColor gridColor = Qt::gray;
    QColor boxColor = Qt::gray;
    QPoint clippingWindowp1;
    QPoint clippingWindowp2;

    void inline drawBox(QPainter(&p));
    void inline drawGrid(QPainter &p);
    void inline drawAxis(QPainter &p);
    void inline drawTicks(QPainter &p);
    void inline drawArrows(QPainter &p);
    void inline drawLineSegments(QPainter &p);
    void inline drawPolygons(QPainter &p);
    void inline drawClippingWindow(QPainter &p);
    void paintEvent(QPaintEvent *event) override;
};

#endif
