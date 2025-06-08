#ifndef RECORDSREPOSITORY_H
#define RECORDSREPOSITORY_H

#include <QObject>
#include <QSqlDatabase>
#include <QSqlQuery>
#include <QSqlError>
#include <QVariant>
#include "data/database.h"

class RecordsRepository : public QObject
{
    Q_OBJECT
public:
    explicit RecordsRepository(QObject *parent = nullptr);
    bool connect();
    bool prefillDatabase();

    QVariantList getAllRecords(const QString &table);
    QVariant getAggregateFunction(const QString &table, const QString &column, const QString &function);
    QVariantList filterRecords(const QString &table, const QString &column, int value);
    QVariantList groupRecords(const QString &table, const QString &column);
    QVariantList sortRecords(const QString &table, const QString &column, Qt::SortOrder order);

private:
    Database db;
};

#endif // RECORDSREPOSITORY_H
