#include "core/appmanager.h"

AppManager::AppManager(QObject *parent)
    : QObject(parent)
{
}

bool AppManager::init()
{
    if (recordsRepository.connect())
    {
        recordsRepository.prefillDatabase();
        return true;
    }
    return false;
}

QVariantList AppManager::getAllRecords(const QString &table)
{
    return recordsRepository.getAllRecords(table);
}

QVariant AppManager::getAggregateFunction(const QString &table, const QString &column, const QString &function)
{
    return recordsRepository.getAggregateFunction(table, column, function);
}

QVariantList AppManager::filterRecords(const QString &table, const QString &column, int value)
{
    return recordsRepository.filterRecords(table, column, value);
}

QVariantList AppManager::groupRecords(const QString &table, const QString &column)
{
    return recordsRepository.groupRecords(table, column);
}

QVariantList AppManager::sortRecords(const QString &table, const QString &column, Qt::SortOrder order)
{
    return recordsRepository.sortRecords(table, column, order);
}
