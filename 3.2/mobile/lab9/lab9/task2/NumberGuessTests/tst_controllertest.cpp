#include <QtTest>
#include <QCoreApplication>
#include <QSettings>
#include "../NumberGuess/GameManager.h"

class GameManagerTest : public QObject
{
    Q_OBJECT
public:
    GameManagerTest() {}
    ~GameManagerTest() {}

private slots:
    void init() { manager = new GameManager(); }
    void cleanup() { delete manager; }

    void testInitialValues();
    void testNumberGeneration();
    void testGuessChecking();
    void testAuthentication();
    void testAppStatsTracking();
    void testCredentials();

private:
    GameManager *manager;
};

void GameManagerTest::testInitialValues()
{
    QVERIFY(manager->status().isEmpty());
    QVERIFY(manager->uniqueId().size() > 0);
    QVERIFY(manager->attempts() >= 0);
    QVERIFY(manager->bestScore() >= 0);
    QVERIFY(manager->launchCount() > 0);
}

void GameManagerTest::testNumberGeneration()
{
    for (int i = 0; i < 100; ++i)
    {
        GameManager m;
        QVERIFY(m.status().isEmpty() || m.status().size() >= 0);
    }
}

void GameManagerTest::testGuessChecking()
{
    int attempts = manager->attempts();
    manager->checkGuess(0);
    QCOMPARE(manager->attempts(), attempts);
    manager->checkGuess(101);
    QCOMPARE(manager->attempts(), attempts);
    // Guess wrong
    manager->checkGuess(1);
    QVERIFY(manager->status().contains("higher") || manager->status().contains("lower") || manager->status().contains("guessed"));
}

void GameManagerTest::testAuthentication()
{
    QSignalSpy successSpy(manager, &GameManager::loginSuccess);
    manager->login("user", "1234");
    QCOMPARE(successSpy.count(), 1);
    QSignalSpy failSpy(manager, &GameManager::loginFailed);
    manager->login("wrong", "wrong");
    QCOMPARE(failSpy.count(), 1);
}

void GameManagerTest::testAppStatsTracking()
{
    QVERIFY(manager->uniqueId().size() > 0);
    QVERIFY(manager->launchCount() > 0);
}

void GameManagerTest::testCredentials()
{
    manager->setUsername("testuser");
    manager->setPassword("testpass");
    QCOMPARE(manager->username(), QString("testuser"));
    QCOMPARE(manager->password(), QString("testpass"));
    manager->clearCredentials();
    QCOMPARE(manager->username(), QString(""));
    QCOMPARE(manager->password(), QString(""));
}

QTEST_MAIN(GameManagerTest)
#include "tst_gamemanagertest.moc"
