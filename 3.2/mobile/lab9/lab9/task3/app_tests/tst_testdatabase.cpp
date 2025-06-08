#include <QObject>
#include <QtTest>
#include <QSqlQuery>
#include <QVariant>
#include "../app/data/database.h"
#include "../app/logic/recordsrepository.h"
#include "../app/core/appmanager.h"

class TestDatabase : public QObject
{
    Q_OBJECT

public:
    TestDatabase();
    ~TestDatabase();

private slots:
    void initTestCase();
    void cleanupTestCase();
    void init();
    void cleanup();

    void testConnect();
    void testPrefillDatabase();
    void testGetAllRecords();
    void testGetAggregateFunction();
    void testFilterRecords();
    void testGroupRecords();
    void testSortRecords();

private:
    Database *db;
    QString testDbName;
};

TestDatabase::TestDatabase()
{
    testDbName = "test_publishing.db";
}

TestDatabase::~TestDatabase()
{
}

void TestDatabase::initTestCase()
{
    QFile::remove(testDbName);

    db = new Database(this);
    QSqlDatabase::database().setDatabaseName(testDbName);
}

void TestDatabase::cleanupTestCase()
{
    delete db;
    QFile::remove(testDbName);
}

void TestDatabase::init()
{
    QFile::remove(testDbName);
    QVERIFY(db->connect());
    QVERIFY(db->prefillDatabase());
}

void TestDatabase::cleanup()
{
    QSqlDatabase::database().close();
}

void TestDatabase::testConnect()
{
    QVERIFY(db->connect());

    QSqlQuery query;
    QVERIFY(query.exec("SELECT name FROM sqlite_master WHERE type='table' AND name='PrintTypes'"));
    QVERIFY(query.next());

    QVERIFY(query.exec("SELECT name FROM sqlite_master WHERE type='table' AND name='Orders'"));
    QVERIFY(query.next());
}

void TestDatabase::testPrefillDatabase()
{
    QVariantList printTypes = db->getAllRecords("PrintTypes");
    QCOMPARE(printTypes.size(), 5);

    QVariantList orders = db->getAllRecords("Orders");
    QCOMPARE(orders.size(), 5);

    QVERIFY(db->prefillDatabase());
    printTypes = db->getAllRecords("PrintTypes");
    QCOMPARE(printTypes.size(), 5);
    orders = db->getAllRecords("Orders");
    QCOMPARE(orders.size(), 5);
}

void TestDatabase::testGetAllRecords()
{
    QVariantList printTypes = db->getAllRecords("PrintTypes");
    QCOMPARE(printTypes.size(), 5);

    QVariantMap firstPrintType = printTypes.first().toMap();
    QVERIFY(firstPrintType.contains("name"));
    QVERIFY(firstPrintType.contains("format"));
    QVERIFY(firstPrintType.contains("paperType"));
    QVERIFY(firstPrintType.contains("cost"));
    QCOMPARE(firstPrintType["name"].toString(), QString("Brochure"));

    QVariantList orders = db->getAllRecords("Orders");
    QCOMPARE(orders.size(), 5);

    QVariantMap firstOrder = orders.first().toMap();
    QVERIFY(firstOrder.contains("orderDate"));
    QVERIFY(firstOrder.contains("quantity"));
    QVERIFY(firstOrder.contains("pageCount"));
    QVERIFY(firstOrder.contains("cost"));
    QVERIFY(firstOrder.contains("printFormat"));
}

void TestDatabase::testGetAggregateFunction()
{
    QVariant count = db->getAggregateFunction("PrintTypes", "id", "COUNT");
    QCOMPARE(count.toInt(), 5);

    QVariant sum = db->getAggregateFunction("Orders", "quantity", "SUM");
    QCOMPARE(sum.toInt(), 100 + 500 + 50 + 200 + 75);

    QVariant avg = db->getAggregateFunction("Orders", "cost", "AVG");
    QVERIFY(avg.toDouble() > 0);

    QVariant max = db->getAggregateFunction("Orders", "cost", "MAX");
    QCOMPARE(max.toDouble(), 600.00);

    QVariant min = db->getAggregateFunction("Orders", "cost", "MIN");
    QCOMPARE(min.toDouble(), 250.00);
}

void TestDatabase::testFilterRecords()
{
    QVariantList filtered = db->filterRecords("Orders", "quantity", 100);
    QCOMPARE(filtered.size(), 2);

    for (const QVariant &record : filtered)
    {
        QVERIFY(record.toMap()["quantity"].toInt() > 100);
    }

    filtered = db->filterRecords("PrintTypes", "cost", 3.00);
    QCOMPARE(filtered.size(), 2);

    for (const QVariant &record : filtered)
    {
        QVERIFY(record.toMap()["cost"].toDouble() > 3.00);
    }
}

void TestDatabase::testGroupRecords()
{
    QVariantList grouped = db->groupRecords("Orders", "printFormat");
    QVERIFY(grouped.size() > 0);

    for (const QVariant &record : grouped)
    {
        QVariantMap map = record.toMap();
        QVERIFY(map.contains("printFormat"));
        QVERIFY(map.contains("count"));
        QVERIFY(map["count"].toInt() > 0);
    }

    grouped = db->groupRecords("PrintTypes", "paperType");
    QVERIFY(grouped.size() > 0);

    for (const QVariant &record : grouped)
    {
        QVariantMap map = record.toMap();
        QVERIFY(map.contains("paperType"));
        QVERIFY(map.contains("count"));
        QVERIFY(map["count"].toInt() > 0);
    }
}

void TestDatabase::testSortRecords()
{
    QVariantList sorted = db->sortRecords("PrintTypes", "cost", Qt::AscendingOrder);
    QCOMPARE(sorted.size(), 5);

    double prevCost = 0.0;
    for (const QVariant &record : sorted)
    {
        double currentCost = record.toMap()["cost"].toDouble();
        QVERIFY(currentCost >= prevCost);
        prevCost = currentCost;
    }

    sorted = db->sortRecords("Orders", "quantity", Qt::DescendingOrder);
    QCOMPARE(sorted.size(), 5);

    int prevQuantity = INT_MAX;
    for (const QVariant &record : sorted)
    {
        int currentQuantity = record.toMap()["quantity"].toInt();
        QVERIFY(currentQuantity <= prevQuantity);
        prevQuantity = currentQuantity;
    }
}

QTEST_MAIN(TestDatabase)

#include "tst_testdatabase.moc"
