#pragma once
#include <QObject>
#include <QSettings>
#include <QRandomGenerator>

class GameManager : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QString status READ status NOTIFY statusChanged)
    Q_PROPERTY(int attempts READ attempts NOTIFY attemptsChanged)
    Q_PROPERTY(int bestScore READ bestScore NOTIFY bestScoreChanged)
    Q_PROPERTY(QString uniqueId READ uniqueId NOTIFY uniqueIdChanged)
    Q_PROPERTY(int launchCount READ launchCount NOTIFY launchCountChanged)
    Q_PROPERTY(QString username READ username WRITE setUsername NOTIFY usernameChanged)
    Q_PROPERTY(QString password READ password WRITE setPassword NOTIFY passwordChanged)
public:
    explicit GameManager(QObject *parent = nullptr);

    QString status() const;
    int attempts() const;
    int bestScore() const;
    QString uniqueId() const;
    int launchCount() const;
    QString username() const;
    void setUsername(const QString &username);
    QString password() const;
    void setPassword(const QString &password);

    Q_INVOKABLE void checkGuess(int guess);
    Q_INVOKABLE void login(const QString &username, const QString &password);
    Q_INVOKABLE void logout();
    Q_INVOKABLE void clearCredentials();

signals:
    void statusChanged();
    void attemptsChanged();
    void bestScoreChanged();
    void uniqueIdChanged();
    void launchCountChanged();
    void loginSuccess();
    void loginFailed(const QString &message);
    void logout();
    void usernameChanged();
    void passwordChanged();

private:
    void generateNumber();
    void loadSettings();
    void saveSettings();

    int m_secretNumber = 0;
    QString m_status;
    int m_attempts = 0;
    int m_bestScore = 0;
    QString m_uniqueId;
    int m_launchCount = 0;
    QSettings m_settings;
    QString m_username;
    QString m_password;
};