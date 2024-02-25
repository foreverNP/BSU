#include "statustipfilter.h"

StatusTipFilter::StatusTipFilter()
{

}

bool StatusTipFilter::eventFilter(QObject *obj, QEvent *ev)
{
    if (ev->type() == QEvent::StatusTip)
    {
        return true;
    }
    return false;
}
