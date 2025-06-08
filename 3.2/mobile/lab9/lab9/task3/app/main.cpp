#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QTranslator>
#include <QLocale>
#include "core/appmanager.h"

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);

    QTranslator translator;
    QString locale = QLocale::system().name();

    if (translator.load(QString(":/i18n/DbApp_%1.qm").arg(locale)))
    {
        app.installTranslator(&translator);
        qDebug() << "Successfully loaded translation for:" << locale;
    }

    AppManager appManager;
    if (!appManager.init())
    {
        return -1;
    }

    QQmlApplicationEngine engine;
    engine.rootContext()->setContextProperty("appManager", &appManager);
    QObject::connect(
        &engine,
        &QQmlApplicationEngine::objectCreationFailed,
        &app,
        []()
        { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);
    engine.load(QUrl(QStringLiteral("qrc:/ui/AppRoot.qml")));

    return app.exec();
}
