import QtQuick 2.15
import QtQuick.Controls 2.15

Item {
    id: authScreen
    anchors.fill: parent

    Rectangle {
        anchors.centerIn: parent
        width: Math.min(parent.width * 0.9, 350)
        height: 320
        radius: 18
        color: "white"
        border.color: "#e0e0e0"
        elevation: 2
        layer.enabled: true
        layer.effect: DropShadow {
            color: "#888"
            radius: 12
            samples: 16
            verticalOffset: 4
        }

        Column {
            anchors.centerIn: parent
            spacing: 18
            width: parent.width * 0.8

            Text {
                text: qsTr("Welcome to GuessMaster")
                font.pixelSize: 22
                font.bold: true
                color: "#333"
                anchors.horizontalCenter: parent.horizontalCenter
            }

            TextField {
                id: userInput
                placeholderText: qsTr("Username")
                width: parent.width
                font.pixelSize: 16
                text: gameManager.username
                onTextChanged: gameManager.setUsername(text)
            }
            TextField {
                id: passInput
                placeholderText: qsTr("Password")
                echoMode: TextInput.Password
                width: parent.width
                font.pixelSize: 16
                text: gameManager.password
                onTextChanged: gameManager.setPassword(text)
            }
            Button {
                text: qsTr("Sign In")
                width: parent.width
                onClicked: gameManager.login(userInput.text, passInput.text)
                highlighted: true
            }
            Text {
                id: errorText
                color: "#e53935"
                font.pixelSize: 14
                wrapMode: Text.Wrap
                horizontalAlignment: Text.AlignHCenter
                width: parent.width
            }
        }
    }

    Connections {
        target: gameManager
        onLoginFailed: errorText.text = message
    }
} 