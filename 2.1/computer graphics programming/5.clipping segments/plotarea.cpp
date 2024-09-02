#include "plotarea.h"
#include <QPainter>
#include <QPainterPath>
#include <QMessageBox>

LineSegmentData::LineSegmentData(const QPointF &p1, const QPointF &p2, const QColor &color)
{
    _p1 = p1;
    _p2 = p2;
    _color = color;
}

qreal LineSegmentData::x1() const
{
    return _p1.x();
}

qreal LineSegmentData::x2() const
{
    return _p2.x();
}

qreal LineSegmentData::y1() const
{
    return _p1.y();
}

qreal LineSegmentData::y2() const
{
    return _p2.y();
}
QPointF LineSegmentData::p1() const
{

    return _p1;
}
QPointF LineSegmentData::p2() const
{
    return _p2;
}
QColor LineSegmentData::color() const
{
    return _color;
}

////////////////////////////////////////////////////////////////////////////////////////////

PlotArea::PlotArea(QWidget *parent, PlotMode _mode) : QWidget(parent)
{
    u = std::min(width(), height()) / 25;
    mode = _mode;
}
QPointF PlotArea::Adjust(const QPointF &p)
{
    return QPointF(zx + p.x() * u, zy - p.y() * u);
}
void PlotArea::drawBox(QPainter &p)
{
    int h = height() - 2 * boxOffset;
    int w = width() - 2 * boxOffset;
    QPen boxPen(boxColor);
    boxPen.setWidth(box_width);
    p.setPen(boxPen);
    p.drawRect(boxOffset, boxOffset, w, h);
}
void PlotArea::drawGrid(QPainter &p)
{
    QPen gridPen(gridColor);
    gridPen.setWidth(gridLineWidth);
    p.setPen(gridPen);
    int i = 0;
    while (zx + i * u <= width() - boxOffset)
    {
        i++;
        p.drawLine(zx + i * u, boxOffset, zx + i * u, height() - boxOffset);
        p.drawLine(zx - i * u, boxOffset, zx - i * u, height() - boxOffset);
    }
    i = 0;
    while (zy + i * u < height())
    {
        i++;
        p.drawLine(boxOffset, zy + i * u, width() - boxOffset, zy + i * u);
        p.drawLine(boxOffset, zy - i * u, width() - boxOffset, zy - i * u);
    }
}
void PlotArea::drawAxis(QPainter &p)
{
    QPen axisPen(axisColor);
    axisPen.setWidth(axisWidth);
    p.setPen(axisPen);
    p.drawLine(boxOffset, zy, width() - boxOffset, zy);
    p.drawLine(zx, boxOffset, zx, height() - boxOffset);
}
void PlotArea::drawTicks(QPainter &p)
{
    QPen ticksPen(axisColor);
    ticksPen.setWidth(axisWidth);
    p.setPen(ticksPen);
    QFont font = p.font();
    font.setPixelSize(12);
    p.setFont(font);
    // ticks x
    int i = 0;
    int alignFlags = Qt::AlignRight | Qt::AlignTop;
    p.drawText(QRect{zx - u + pixelWidth, zy + pixelWidth, u - pixelWidth, u - pixelWidth}, alignFlags, QString::number(0));
    while (zx + (i + 2) * u < width())
    {
        i++;
        p.drawLine(zx + i * u, zy + tickLength, zx + i * u, zy - tickLength);
        p.drawLine(zx - i * u, zy + tickLength, zx - i * u, zy - tickLength);
        if (zx + (i + 1) * u < width())
            p.drawText(QRect{zx + (i - 1) * u + pixelWidth, zy + pixelWidth, u - pixelWidth, u - pixelWidth}, alignFlags, QString::number(i));
        p.drawText(QRect{zx - (i + 1) * u + pixelWidth, zy + pixelWidth, u - pixelWidth, u - pixelWidth}, alignFlags, QString::number(-i));
    }
    // ticks y
    i = 0;

    while (zy + (i + 2) * u < height())
    {
        i++;
        p.drawLine(zx - tickLength, zy + i * u, zx + tickLength, zy + i * u);
        p.drawLine(zx - tickLength, zy - i * u, zx + tickLength, zy - i * u);
        if (zy - (i + 1) * u > 0)
            p.drawText(QRect{zx - u + pixelWidth, zy - (i)*u + pixelWidth, u - pixelWidth, u - pixelWidth}, alignFlags, QString::number(i));
        p.drawText(QRect{zx - u + pixelWidth, zy + (i)*u + pixelWidth, u - pixelWidth, u - pixelWidth}, alignFlags, QString::number(-i));
    }
}

void PlotArea::drawArrows(QPainter &p)
{
    QPen arrowsPen(axisColor);
    arrowsPen.setWidth((axisWidth));
    p.setBrush(QBrush(axisColor));
    p.setRenderHint(QPainter::RenderHint::Antialiasing);
    // arrow x
    QPainterPath px;
    px.moveTo(width() - u - 1, zy + 2 * tickLength);
    px.lineTo(width() - u - 1, zy - 2 * tickLength);
    px.lineTo(width() - 1, zy);
    px.lineTo(width() - u - 1, zy + 2 * tickLength);
    p.drawPath(px);
    p.drawText(QRect{width() - u / 2 - 1, zy + u / 2, u, u}, "X");
    // arrow y
    QPainterPath py;
    py.moveTo(zx + 2 * tickLength, u + 1);
    py.lineTo(zx - 2 * tickLength, u + 1);
    py.lineTo(zx, 1);
    py.lineTo(zx + 2 * tickLength, u + 1);
    p.drawPath(py);
    p.drawText(QRect{zx + u / 2, u / 2, u, u}, "Y");
}

void PlotArea::drawClippingWindow(QPainter &p)
{
    p.setPen(QPen(clippingWindowColor, lineWidth));
    p.setBrush(Qt::NoBrush);

    switch (mode)
    {
    case PlotMode::Segments:
        p.drawRect(QRectF{Adjust(clippingWindowp1), Adjust(clippingWindowp2)});
        break;
    case PlotMode::Polygon:
        QPainterPath path;
        path.moveTo(Adjust(clippingWindowPoints[0]));
        for (size_t i = 1; i < clippingWindowPoints.size(); ++i)
        {
            path.lineTo(Adjust(clippingWindowPoints[i]));
        }
        path.lineTo(Adjust(clippingWindowPoints[0]));
        p.drawPath(path);
        break;
    }
}

void PlotArea::drawLineSegments(QPainter &p)
{
    if (segments.empty())
    {
        QMessageBox::warning(nullptr, "Ошибка", "Нет ни одного отрезка");
        return;
    }
    for (const auto &segmentData : segments)
    {
        p.setPen(QPen(segmentData.color(), lineWidth));
        p.drawLine(Adjust(segmentData.p1()), Adjust(segmentData.p2()));
    }
}

void PlotArea::AddLineSegment(const LineSegmentData &data)
{
    segments.push_back(data);
}

void PlotArea::SetPolygonBorderColor(const QColor &color)
{
    polygonBorderColor = color;
}

void PlotArea::SetClippingWindow(const QPoint &p1, const QPoint &p2)
{
    clippingWindowp1 = QPoint(std::min(p1.x(), p2.x()), std::max(p1.y(), p2.y()));
    clippingWindowp2 = QPoint(std::max(p1.x(), p2.x()), std::min(p1.y(), p2.y()));
}

void PlotArea::SetClippingWindow(const std::vector<QPointF> &points)
{
    clippingWindowPoints = points;
}

QPoint PlotArea::getClippingWindowP1() const
{
    return clippingWindowp1;
}

QPoint PlotArea::getClippingWindowP2() const
{
    return clippingWindowp2;
}

void PlotArea::ChangeMode(PlotMode newMode)
{
    mode = newMode;
}

void PlotArea::Clear()
{
    segments.clear();
    clippingWindowPoints.clear();
}

void PlotArea::paintEvent(QPaintEvent *)
{
    zx = width() / 2;
    zy = height() / 2;
    QPainter pt(this);

    drawBox(pt);
    drawAxis(pt);
    drawTicks(pt);
    drawArrows(pt);
    drawGrid(pt);

    switch (mode)
    {
    case PlotMode::Segments:
        drawClippingWindow(pt);
        drawLineSegments(pt);
        break;
    case PlotMode::Polygon:
        drawClippingWindow(pt);
        drawLineSegments(pt);
        break;
    case PlotMode::None:
        break;
    }
}

int PlotArea::getUnit() const
{
    return u;
}
void PlotArea::SetUnit(int nu)
{
    u = nu;
}
