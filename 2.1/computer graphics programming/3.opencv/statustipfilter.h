#ifndef STATUSTIPFILTER_H
#define STATUSTIPFILTER_H

#include <QObject>
#include <QEvent>

class StatusTipFilter : public QObject
{
    Q_OBJECT
public:
    StatusTipFilter();
    bool eventFilter(QObject *obj, QEvent *ev);
};

#endif // STATUSTIPFILTER_H
