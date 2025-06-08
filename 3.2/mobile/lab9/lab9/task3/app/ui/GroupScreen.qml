import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 1.15

Item {
    ColumnLayout {
        anchors.fill: parent
        spacing: 16
        ToolButton {
            icon.name: "arrow_back"
            onClicked: mainStack.pop()
        }
        Label {
            text: qsTr("Группировка записей")
            font.pixelSize: 22
            font.bold: true
            horizontalAlignment: Text.AlignHCenter
            Layout.fillWidth: true
        }
        RowLayout {
            spacing: 12
            ComboBox {
                id: columnComboBox
                model: [qsTr("format"), qsTr("paperType")]
                Layout.preferredWidth: 120
                onCurrentTextChanged: if (groupedModel.count > 0) groupedModel.clear()
            }
            Button {
                text: qsTr("Группировать")
                onClicked: {
                    var result = mainController.groupRecords("PrintTypes", columnComboBox.currentText);
                    groupedModel.clear();
                    for (var i = 0; i < result.length; i++) {
                        groupedModel.append({ groupValue: result[i][columnComboBox.currentText], count: result[i].count });
                    }
                    noResultsText.visible = (groupedModel.count === 0);
                }
            }
        }
        Label {
            id: noResultsText
            text: qsTr("Нет записей")
            color: "red"
            visible: false
        }
        ScrollView {
            Layout.fillWidth: true
            Layout.fillHeight: true
            ListView {
                width: parent.width
                model: groupedModel
                spacing: 10
                delegate: Rectangle {
                    width: parent.width * 0.95
                    height: 60
                    radius: 8
                    color: index % 2 === 0 ? "#e3eafc" : "#f7f7fa"
                    border.color: "#b0b0b0"
                    border.width: 1
                    anchors.horizontalCenter: parent.horizontalCenter
                    RowLayout {
                        anchors.fill: parent
                        anchors.margins: 10
                        spacing: 15
                        Label {
                            text: groupValue
                            font.bold: true
                            font.pixelSize: 14
                            Layout.preferredWidth: parent.width * 0.7
                            elide: Text.ElideRight
                        }
                        Rectangle {
                            width: 1
                            height: parent.height
                            color: "#e0e0e0"
                        }
                        Label {
                            text: count + (count === 1 ? qsTr(" запись") : qsTr(" записей"))
                            font.pixelSize: 14
                            color: "#2a5caa"
                        }
                    }
                }
            }
        }
        ListModel { id: groupedModel }
    }
} 