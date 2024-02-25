import QtQuick 2.15
import QtQuick.Controls 2.15
import QtQuick.Layouts 1.15

Item {
    ColumnLayout {
        anchors.centerIn: parent
        spacing: 24
        width: parent.width * 0.8

        Button {
            text: qsTr("Все записи")
            icon.name: "list"
            Layout.fillWidth: true
            onClicked: mainStack.push(recordsScreenComponent)
        }
        Button {
            text: qsTr("Агрегация")
            icon.name: "functions"
            Layout.fillWidth: true
            onClicked: mainStack.push(aggregateScreenComponent)
        }
        Button {
            text: qsTr("Фильтр")
            icon.name: "filter"
            Layout.fillWidth: true
            onClicked: mainStack.push(filterScreenComponent)
        }
        Button {
            text: qsTr("Группировка")
            icon.name: "group"
            Layout.fillWidth: true
            onClicked: mainStack.push(groupScreenComponent)
        }
        Button {
            text: qsTr("Сортировка")
            icon.name: "sort"
            Layout.fillWidth: true
            onClicked: mainStack.push(sortScreenComponent)
        }
    }

    Component { id: recordsScreenComponent; RecordsScreen {} }
    Component { id: aggregateScreenComponent; AggregateScreen {} }
    Component { id: filterScreenComponent; FilterScreen {} }
    Component { id: groupScreenComponent; GroupScreen {} }
    Component { id: sortScreenComponent; SortScreen {} }
} 