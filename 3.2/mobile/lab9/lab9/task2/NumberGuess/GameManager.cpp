#include "GameManager.h"
#include <QUuid>

GameManager::GameManager(QObject *parent) : QObject(parent), m_settings("GuessMaster", "AuroraApp")
{
    m_settings.beginGroup("stats");
    m_uniqueId = m_settings.value("uniqueId", QUuid::createUuid().toString()).toString();
    m_settings.setValue("uniqueId", m_uniqueId);
    m_launchCount = m_settings.value("launchCount", 0).toInt() + 1;
    m_settings.setValue("launchCount", m_launchCount);
    m_bestScore = m_settings.value("bestScore", 0).toInt();
    m_attempts = m_settings.value("attempts", 0).toInt();
    m_settings.endGroup();
    m_settings.beginGroup("user");
    m_username = m_settings.value("username", "").toString();
    m_password = m_settings.value("password", "").toString();
    m_settings.endGroup();
    generateNumber();
}

QString GameManager::status() const { return m_status; }
int GameManager::attempts() const { return m_attempts; }
int GameManager::bestScore() const { return m_bestScore; }
QString GameManager::uniqueId() const { return m_uniqueId; }
int GameManager::launchCount() const { return m_launchCount; }
QString GameManager::username() const { return m_username; }
void GameManager::setUsername(const QString &username)
{
    if (m_username != username)
    {
        m_username = username;
        m_settings.beginGroup("user");
        m_settings.setValue("username", m_username);
        m_settings.endGroup();
        emit usernameChanged();
    }
}
QString GameManager::password() const { return m_password; }
void GameManager::setPassword(const QString &password)
{
    if (m_password != password)
    {
        m_password = password;
        m_settings.beginGroup("user");
        m_settings.setValue("password", m_password);
        m_settings.endGroup();
        emit passwordChanged();
    }
}

void GameManager::generateNumber()
{
    m_secretNumber = QRandomGenerator::global()->bounded(1, 101);
}

void GameManager::checkGuess(int guess)
{
    if (guess < 1 || guess > 100)
        return;
    m_attempts++;
    emit attemptsChanged();
    if (guess == m_secretNumber)
    {
        if (m_attempts < m_bestScore || m_bestScore == 0)
        {
            m_bestScore = m_attempts;
            emit bestScoreChanged();
        }
        m_status = tr("You guessed it in %1 tries!").arg(m_attempts);
        emit statusChanged();
        m_attempts = 0;
        emit attemptsChanged();
        generateNumber();
    }
    else if (guess < m_secretNumber)
    {
        m_status = tr("Try higher! Your guess was %1").arg(guess);
        emit statusChanged();
    }
    else
    {
        m_status = tr("Try lower! Your guess was %1").arg(guess);
        emit statusChanged();
    }
    m_settings.beginGroup("stats");
    m_settings.setValue("bestScore", m_bestScore);
    m_settings.setValue("attempts", m_attempts);
    m_settings.endGroup();
}

void GameManager::login(const QString &username, const QString &password)
{
    if (username == "user" && password == "1234")
    {
        setUsername(username);
        setPassword(password);
        emit loginSuccess();
    }
    else
    {
        emit loginFailed(tr("Wrong username or password"));
    }
}

void GameManager::logout()
{
    emit logout();
}

void GameManager::clearCredentials()
{
    setUsername("");
    setPassword("");
}