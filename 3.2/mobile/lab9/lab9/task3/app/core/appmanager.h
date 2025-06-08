#ifndef APPMANAGER_H
#define APPMANAGER_H

#include <QObject>
#include "logic/recordsrepository.h"

class AppManager : public QObject
{
    Q_OBJECT
public:
    explicit AppManager(QObject *parent = nullptr);
    bool init();

    Q_INVOKABLE QVariantList getAllRecords(const QString &table);
    Q_INVOKABLE QVariant getAggregateFunction(const QString &table, const QString &column, const QString &function);
    Q_INVOKABLE QVariantList filterRecords(const QString &table, const QString &column, int value);
    Q_INVOKABLE QVariantList groupRecords(const QString &table, const QString &column);
    Q_INVOKABLE QVariantList sortRecords(const QString &table, const QString &column, Qt::SortOrder order);

private:
    RecordsRepository recordsRepository;
};

#endif // APPMANAGER_H
