import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 1.15

Item {
    ColumnLayout {
        anchors.fill: parent
        spacing: 12

        ToolButton {
            icon.name: "arrow_back"
            onClicked: mainStack.pop()
        }
        Label {
            text: qsTr("Все записи")
            font.pixelSize: 22
            font.bold: true
            horizontalAlignment: Text.AlignHCenter
            Layout.fillWidth: true
        }
        ScrollView {
            Layout.fillWidth: true
            Layout.fillHeight: true
            ListView {
                width: parent.width
                model: mainController.getAllRecords("PrintTypes")
                spacing: 16
                delegate: Rectangle {
                    width: parent.width * 0.95
                    height: 110
                    radius: 12
                    color: index % 2 === 0 ? "#e3eafc" : "#f7f7fa"
                    border.color: "#b0b0b0"
                    border.width: 1
                    anchors.horizontalCenter: parent.horizontalCenter
                    ColumnLayout {
                        anchors.fill: parent
                        anchors.margins: 12
                        spacing: 4
                        Label { text: qsTr("ID: ") + modelData.id; font.bold: true }
                        Label { text: qsTr("Название: ") + modelData.name }
                        Label { text: qsTr("Формат: ") + modelData.format }
                        Label { text: qsTr("Тип бумаги: ") + modelData.paperType }
                        Label { text: qsTr("Стоимость: ") + modelData.cost.toFixed(2) }
                    }
                }
            }
        }
    }
} 