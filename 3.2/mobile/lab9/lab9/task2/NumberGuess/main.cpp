#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QTranslator>
#include <QLocale>
#include "GameManager.h"

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);

    QTranslator translator;
    QString locale = QLocale::system().name();
    if (translator.load(QString(":/i18n/NumberGuess_%1.qm").arg(locale)))
        app.installTranslator(&translator);

    GameManager gameManager;

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("gameManager", &gameManager);
    engine.loadFromModule("GuessMaster", "AppRoot");

    if (engine.rootObjects().isEmpty())
        return -1;
    return app.exec();
}
