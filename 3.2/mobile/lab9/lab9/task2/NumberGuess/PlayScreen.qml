import QtQuick 2.15
import QtQuick.Controls 2.15

Item {
    id: playScreen
    anchors.fill: parent
    Rectangle {
        anchors.fill: parent
        color: "#f0f2f5"
    }
    Column {
        anchors.centerIn: parent
        spacing: 24
        width: Math.min(parent.width * 0.92, 400)

        Rectangle {
            width: parent.width
            height: 120
            radius: 16
            color: "white"
            border.color: "#e0e0e0"
            elevation: 2
            layer.enabled: true
            layer.effect: DropShadow {
                color: "#888"
                radius: 10
                samples: 16
                verticalOffset: 3
            }
            Column {
                anchors.centerIn: parent
                spacing: 8
                Text {
                    text: qsTr("Guess the number between 1 and 100")
                    font.pixelSize: 18
                    font.bold: true
                    color: "#333"
                }
                Text {
                    text: gameManager.status
                    font.pixelSize: 15
                    color: "#666"
                }
            }
        }
        Row {
            width: parent.width
            spacing: 12
            TextField {
                id: guessInput
                placeholderText: qsTr("Your guess")
                width: parent.width * 0.6
                font.pixelSize: 16
                validator: IntValidator { bottom: 1; top: 100 }
                inputMethodHints: Qt.ImhDigitsOnly
            }
            Button {
                text: qsTr("Try!")
                width: parent.width * 0.3
                onClicked: {
                    var guess = parseInt(guessInput.text)
                    if (!isNaN(guess)) {
                        gameManager.checkGuess(guess)
                        guessInput.clear()
                    }
                }
                highlighted: true
            }
        }
        Row {
            width: parent.width
            spacing: 10
            Button {
                text: qsTr("Stats")
                onClicked: statsDialog.open()
            }
            Button {
                text: qsTr("Logout")
                onClicked: {
                    gameManager.clearCredentials()
                    gameManager.logout()
                }
            }
        }
    }
    Dialog {
        id: statsDialog
        modal: true
        title: qsTr("Statistics")
        standardButtons: Dialog.Ok
        x: (parent.width - width) / 2
        y: (parent.height - height) / 2
        width: Math.min(parent.width * 0.8, 400)
        contentItem: Column {
            spacing: 10
            Text { text: qsTr("Launches: %1").arg(gameManager.launchCount) }
            Text { text: qsTr("Best score: %1").arg(gameManager.bestScore) }
            Text { text: qsTr("Attempts: %1").arg(gameManager.attempts) }
            Text { text: qsTr("Unique ID: %1").arg(gameManager.uniqueId) }
        }
    }
} 