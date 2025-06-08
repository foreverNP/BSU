#include "logic/recordsrepository.h"

RecordsRepository::RecordsRepository(QObject *parent)
    : QObject(parent)
{
}

bool RecordsRepository::connect()
{
    return db.connect();
}

QVariantList RecordsRepository::getAllRecords(const QString &table)
{
    return db.getAllRecords(table);
}

QVariant RecordsRepository::getAggregateFunction(const QString &table, const QString &column, const QString &function)
{
    return db.getAggregateFunction(table, column, function);
}

QVariantList RecordsRepository::filterRecords(const QString &table, const QString &column, int value)
{
    return db.filterRecords(table, column, value);
}

QVariantList RecordsRepository::groupRecords(const QString &table, const QString &column)
{
    return db.groupRecords(table, column);
}

QVariantList RecordsRepository::sortRecords(const QString &table, const QString &column, Qt::SortOrder order)
{
    return db.sortRecords(table, column, order);
}

bool RecordsRepository::prefillDatabase()
{
    return db.prefillDatabase();
}
