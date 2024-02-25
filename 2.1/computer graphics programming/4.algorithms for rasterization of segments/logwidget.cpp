#include "logwidget.h"
#include <QScrollBar>

LogWidget::LogWidget(QWidget *parent):QPlainTextEdit(parent)
{
    setReadOnly(true);
    setFrameStyle(QFrame::StyledPanel);
    setStyleSheet("color: black;");
}

void LogWidget::AppendMessage(const QString& message, int indent)
{
    QString indentstr("");
    for (int i = 0; i < indent; ++i)
    {
        indentstr += "      ";
    }
    appendPlainText(indentstr + message);
}
void LogWidget::AppendSeparator()
{
    AppendMessage("-----------------------------------------------------------------------");
    //verticalScrollBar()->setValue(verticalScrollBar()->maximum());
}
