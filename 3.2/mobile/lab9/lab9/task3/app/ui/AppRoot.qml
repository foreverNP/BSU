import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 1.15

ApplicationWindow {
    id: appWindow
    width: 400
    height: 700
    visible: true
    title: qsTr("Print Manager")
    color: "#f0f2f5"

    Drawer {
        id: navDrawer
        width: parent.width * 0.7
        height: parent.height
        edge: Qt.LeftEdge
        modal: true
        ListView {
            anchors.fill: parent
            model: [qsTr("Главная"), qsTr("Все записи"), qsTr("Агрегация"), qsTr("Фильтр"), qsTr("Группировка"), qsTr("Сортировка")]
            delegate: ItemDelegate {
                text: modelData
                onClicked: {
                    navDrawer.close()
                    mainStack.currentIndex = index
                }
            }
        }
    }

    ToolBar {
        RowLayout {
            anchors.fill: parent
            ToolButton {
                icon.name: "menu"
                onClicked: navDrawer.open()
            }
            Label {
                text: qsTr("Print Manager")
                font.bold: true
                Layout.alignment: Qt.AlignHCenter
            }
        }
    }

    StackView {
        id: mainStack
        anchors.fill: parent
        initialItem: MainScreen {}
    }
} 