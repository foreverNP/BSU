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
            text: qsTr("Агрегация")
            font.pixelSize: 22
            font.bold: true
            horizontalAlignment: Text.AlignHCenter
            Layout.fillWidth: true
        }
        RowLayout {
            spacing: 12
            Label { text: qsTr("Функция:") }
            ComboBox {
                id: functionComboBox
                model: [qsTr("SUM"), qsTr("MIN"), qsTr("MAX"), qsTr("COUNT"), qsTr("AVG")]
                Layout.preferredWidth: 120
            }
            Label { text: qsTr("Колонка:") }
            ComboBox {
                id: columnComboBox
                model: [qsTr("cost")]
                Layout.preferredWidth: 120
            }
        }
        Button {
            text: qsTr("Вычислить")
            onClicked: {
                var result = mainController.getAggregateFunction("PrintTypes", columnComboBox.currentText, functionComboBox.currentText);
                if (result !== undefined && result !== null) {
                    if (typeof result === 'number') {
                        resultText.text = functionComboBox.currentText + ": " + result.toFixed(2);
                    } else {
                        resultText.text = functionComboBox.currentText + ": " + result;
                    }
                } else {
                    resultText.text = qsTr("Нет результата или ошибка");
                }
            }
        }
        Rectangle {
            width: 240; height: 60; color: "#f5f5f5"; radius: 5; border.color: "#ddd"
            Label {
                id: resultText
                anchors.centerIn: parent
                font.pointSize: 16
                text: qsTr("Результат появится здесь")
            }
        }
    }
} 