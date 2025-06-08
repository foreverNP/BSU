#ifndef DATABASE_H
#define DATABASE_H

#include <QObject>
#include <QSqlDatabase>
#include <QSqlQuery>
#include <QSqlError>
#include <QVariant>

class Database : public QObject
{
    Q_OBJECT

public:
    Database(QObject *parent = nullptr);
    bool connect();
    bool prefillDatabase();

    Q_INVOKABLE QVariantList getAllRecords(const QString &table);
    Q_INVOKABLE QVariant getAggregateFunction(const QString &table, const QString &column, const QString &function);
    Q_INVOKABLE QVariantList filterRecords(const QString &table, const QString &column, int value);
    Q_INVOKABLE QVariantList groupRecords(const QString &table, const QString &column);
    Q_INVOKABLE QVariantList sortRecords(const QString &table, const QString &column, Qt::SortOrder order);

private:
    QSqlDatabase db;
};

#endif // DATABASE_H
