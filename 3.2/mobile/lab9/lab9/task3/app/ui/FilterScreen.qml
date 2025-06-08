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
            text: qsTr("Фильтр записей")
            font.pixelSize: 22
            font.bold: true
            horizontalAlignment: Text.AlignHCenter
            Layout.fillWidth: true
        }
        RowLayout {
            spacing: 12
            Label { text: qsTr("Колонка:") }
            ComboBox {
                id: columnComboBox
                model: [qsTr("cost"), qsTr("id")]
                Layout.preferredWidth: 120
                onCurrentTextChanged: valueTextField.text = ""
            }
            Label { text: qsTr("Минимум:") }
            TextField {
                id: valueTextField
                Layout.preferredWidth: 120
                placeholderText: qsTr("Введите минимум")
                validator: DoubleValidator { bottom: 0 }
                inputMethodHints: Qt.ImhFormattedNumbersOnly
            }
        }
        Button {
            text: qsTr("Фильтровать")
            enabled: valueTextField.text !== ""
            onClicked: {
                var filterValue = parseFloat(valueTextField.text);
                if (!isNaN(filterValue)) {
                    var result = mainController.filterRecords("PrintTypes", columnComboBox.currentText, filterValue);
                    filteredModel.clear();
                    for (var i = 0; i < result.length; i++) {
                        filteredModel.append(result[i]);
                    }
                    noResultsText.visible = (result.length === 0);
                }
            }
        }
        Label {
            id: noResultsText
            text: qsTr("Нет подходящих записей")
            color: "red"
            visible: false
        }
        ScrollView {
            Layout.fillWidth: true
            Layout.fillHeight: true
            ListView {
                width: parent.width
                model: filteredModel
                spacing: 12
                delegate: Rectangle {
                    width: parent.width * 0.95
                    height: 100
                    radius: 10
                    color: index % 2 === 0 ? "#e3eafc" : "#f7f7fa"
                    border.color: "#b0b0b0"
                    border.width: 1
                    anchors.horizontalCenter: parent.horizontalCenter
                    ColumnLayout {
                        anchors.fill: parent
                        anchors.margins: 10
                        spacing: 3
                        Label { text: qsTr("ID: ") + id; font.bold: true }
                        Label { text: qsTr("Название: ") + name }
                        Label { text: qsTr("Формат: ") + format }
                        Label { text: qsTr("Тип бумаги: ") + paperType }
                        Label { text: qsTr("Стоимость: ") + cost.toFixed(2) }
                    }
                }
            }
        }
        ListModel { id: filteredModel }
    }
} 