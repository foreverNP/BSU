import QtQuick 2.15
import QtQuick.Controls 2.15

ApplicationWindow {
    id: appWindow
    width: 400
    height: 700
    visible: true
    title: qsTr("GuessMaster")
    color: "#f0f2f5"

    StackView {
        id: mainStack
        anchors.fill: parent
        initialItem: AuthScreen {}
    }

    Component {
        id: playScreenComponent
        PlayScreen {}
    }

    Connections {
        target: gameManager
        onLoginSuccess: mainStack.push(playScreenComponent)
        onLogout: mainStack.pop()
    }
} 