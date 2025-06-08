#include "data/database.h"
#include <QSqlRecord>
#include <QVariantList>

Database::Database(QObject *parent)
    : QObject(parent)
{
    db = QSqlDatabase::addDatabase("QSQLITE");
    db.setDatabaseName("publishing.db");
}

bool Database::connect()
{
    if (!db.open())
    {
        qWarning() << "Error: connection with database failed";
        return false;
    }
    else
    {
        qWarning() << "Database: connection ok";
    }

    QSqlQuery query;
    query.exec("CREATE TABLE IF NOT EXISTS PrintTypes ("
               "id INTEGER PRIMARY KEY AUTOINCREMENT, "
               "name TEXT, "
               "format TEXT, "
               "paperType TEXT, "
               "cost REAL)");

    query.exec("CREATE TABLE IF NOT EXISTS Orders ("
               "id INTEGER PRIMARY KEY AUTOINCREMENT, "
               "orderDate TEXT, "
               "quantity INTEGER, "
               "pageCount INTEGER, "
               "cost REAL, "
               "printFormat TEXT)");

    return true;
}

QVariantList Database::getAllRecords(const QString &table)
{
    QVariantList records;
    QSqlQuery query(QString("SELECT * FROM %1").arg(table));

    while (query.next())
    {
        QVariantMap record;
        QSqlRecord sqlRecord = query.record();
        for (int i = 0; i < sqlRecord.count(); ++i)
        {
            record.insert(sqlRecord.fieldName(i), query.value(i));
        }
        records.append(record);
    }

    return records;
}

QVariant Database::getAggregateFunction(const QString &table, const QString &column, const QString &function)
{
    QSqlQuery query(QString("SELECT %1(%2) FROM %3").arg(function, column, table));
    if (query.next())
    {
        return query.value(0);
    }
    return QVariant();
}

QVariantList Database::filterRecords(const QString &table, const QString &column, int value)
{
    QVariantList records;
    QSqlQuery query(QString("SELECT * FROM %1 WHERE %2 > %3").arg(table, column).arg(value));

    while (query.next())
    {
        QVariantMap record;
        QSqlRecord sqlRecord = query.record();
        for (int i = 0; i < sqlRecord.count(); ++i)
        {
            record.insert(sqlRecord.fieldName(i), query.value(i));
        }
        records.append(record);
    }

    return records;
}

QVariantList Database::groupRecords(const QString &table, const QString &column)
{
    QVariantList records;
    QSqlQuery query(QString("SELECT %1, COUNT(*) FROM %2 GROUP BY %1").arg(column, table));

    while (query.next())
    {
        QVariantMap record;
        record.insert(column, query.value(0));
        record.insert("count", query.value(1));
        records.append(record);
    }

    return records;
}

QVariantList Database::sortRecords(const QString &table, const QString &column, Qt::SortOrder order)
{
    QVariantList records;
    QString orderStr = order == Qt::AscendingOrder ? "ASC" : "DESC";
    QSqlQuery query(QString("SELECT * FROM %1 ORDER BY %2 %3").arg(table, column, orderStr));

    while (query.next())
    {
        QVariantMap record;
        QSqlRecord sqlRecord = query.record();
        for (int i = 0; i < sqlRecord.count(); ++i)
        {
            record.insert(sqlRecord.fieldName(i), query.value(i));
        }
        records.append(record);
    }

    return records;
}

bool Database::prefillDatabase()
{
    if (!db.isOpen())
    {
        if (!connect())
        {
            return false;
        }
    }

    QSqlQuery query;

    query.exec("SELECT COUNT(*) FROM PrintTypes");
    query.next();
    if (query.value(0).toInt() == 0)
    {
        query.exec("INSERT INTO PrintTypes (name, format, paperType, cost) VALUES "
                   "('Brochure', 'A4', 'Glossy', 2.50), "
                   "('Flyer', 'A5', 'Matte', 1.20), "
                   "('Poster', 'A3', 'Glossy', 5.75), "
                   "('Booklet', 'A5', 'Standard', 3.00), "
                   "('Catalog', 'A4', 'Glossy', 4.50)");
    }

    query.exec("SELECT COUNT(*) FROM Orders");
    query.next();
    if (query.value(0).toInt() == 0)
    {
        query.exec("INSERT INTO Orders (orderDate, quantity, pageCount, cost, printFormat) VALUES "
                   "('2023-01-15', 100, 20, 250.00, 'A4'), "
                   "('2023-02-03', 500, 2, 600.00, 'A5'), "
                   "('2023-02-10', 50, 40, 287.50, 'A3'), "
                   "('2023-03-01', 200, 16, 600.00, 'A5'), "
                   "('2023-03-15', 75, 32, 337.50, 'A4')");
    }

    return true;
}
