import sys
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from scipy.spatial.distance import cdist
from PySide6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QWidget, QPushButton, QFileDialog, QTextEdit, QLabel
from PySide6.QtGui import QFont, QColor, QPalette


def load_data(file_path):
    """
    Загрузка данных из файла CSV.
    Последний столбец считается меткой класса.
    """
    data = pd.read_csv(file_path)
    X = data.iloc[:, :-1].values
    y = data.iloc[:, -1].values
    return X, y


def split_data(X, y, test_size=0.2):
    """
    Разбиение данных на обучающую и контрольную выборки.
    """
    unique_classes = np.unique(y)
    X_train, X_test, y_train, y_test = [], [], [], []
    for cls in unique_classes:
        # Отбираем данные для текущего класса
        mask = y == cls
        X_cls = X[mask]
        y_cls = y[mask]
        # Разбиваем данные этого класса
        X_tr, X_te, y_tr, y_te = train_test_split(X_cls, y_cls, test_size=test_size, random_state=42)
        X_train.append(X_tr)
        X_test.append(X_te)
        y_train.append(y_tr)
        y_test.append(y_te)
    return (
        np.vstack(X_train),
        np.hstack(y_train),  # Обучающая выборка
        np.vstack(X_test),
        np.hstack(y_test),  # Контрольная выборка
    )


def s(x1, x2):
    """
    Функция сравнения объектов (евклидова метрика).
    """
    return cdist(x1, x2, metric="euclidean")


def f_min_dist(x, x_train_class):
    """
    Минимальное расстояние до объектов класса.
    """
    return np.min(cdist(x, x_train_class), axis=1)


def classify_verbose(x, x_train, y_train):
    """
    Классификация объекта x с подробными расчетами.
    """
    unique_classes = np.unique(y_train)
    distances = []
    for cls in unique_classes:
        x_train_cls = x_train[y_train == cls]
        distances.append(f_min_dist(x, x_train_cls))
    distances = np.vstack(distances).T
    predictions = unique_classes[np.argmin(distances, axis=1)]
    return distances, predictions


def test_algorithm_verbose(X_train, y_train, X_test, y_test):
    """
    Тестирование алгоритма с подробным выводом расчетов.
    """
    # Тестирование на обучающей выборке (T0)
    distances_T0, y_pred_T0 = classify_verbose(X_train, X_train, y_train)
    accuracy_T0 = (y_pred_T0 == y_train).mean()

    # Тестирование на контрольной выборке (T1)
    distances_T1, y_pred_T1 = classify_verbose(X_test, X_train, y_train)
    accuracy_T1 = (y_pred_T1 == y_test).mean()

    return accuracy_T0, accuracy_T1, y_pred_T0, y_pred_T1, distances_T0, distances_T1


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("🔍 Классификатор: Подробные расчеты")
        self.setGeometry(200, 200, 700, 700)

        self.widget = QWidget()
        self.layout = QVBoxLayout(self.widget)
        self.layout.setSpacing(15)

        # Кнопка загрузки
        self.load_button = QPushButton("📂 Загрузить данные")
        self.load_button.clicked.connect(self.load_data)
        self.load_button.setStyleSheet("padding: 10px; background-color: #2e86de; color: white; border-radius: 8px;")
        self.layout.addWidget(self.load_button)

        # Метка файла
        self.file_label = QLabel("Файл не выбран")
        self.file_label.setFont(QFont("Arial", 10, QFont.Bold))
        self.layout.addWidget(self.file_label)

        # Кнопка запуска
        self.run_button = QPushButton("🚀 Запустить классификацию")
        self.run_button.clicked.connect(self.run_classification)
        self.run_button.setEnabled(False)
        self.run_button.setStyleSheet("padding: 10px; background-color: #27ae60; color: white; border-radius: 8px;")
        self.layout.addWidget(self.run_button)

        # Область вывода
        self.result_text = QTextEdit()
        self.result_text.setReadOnly(True)
        self.result_text.setFont(QFont("Courier New", 10))
        self.result_text.setStyleSheet("background-color: #f4f6f7; border: 1px solid #ccc; padding: 10px;")
        self.layout.addWidget(self.result_text)

        self.setCentralWidget(self.widget)

        # Инициализация переменных
        self.file_path = None
        self.X_train, self.y_train = None, None
        self.X_test, self.y_test = None, None

    def load_data(self):
        file_path, _ = QFileDialog.getOpenFileName(self, "Выбрать файл данных", "", "CSV Files (*.csv)")
        if file_path:
            self.file_path = file_path
            self.file_label.setText(f"📁 Выбран файл: {file_path}")

            try:
                X, y = load_data(file_path)
                self.X_train, self.y_train, self.X_test, self.y_test = split_data(X, y, test_size=0.8)
                self.run_button.setEnabled(True)
                self.result_text.setText("✅ Данные успешно загружены и разделены.")
            except Exception as e:
                self.result_text.setText(f"❌ Ошибка при загрузке данных: {e}")

    def run_classification(self):
        if self.X_train is not None and self.X_test is not None:
            accuracy_T0, accuracy_T1, y_pred_T0, y_pred_T1, distances_T0, distances_T1 = test_algorithm_verbose(
                self.X_train, self.y_train, self.X_test, self.y_test
            )

            lines = [
                f"📊 Точность на обучающей выборке (T0): {accuracy_T0:.2%}",
                f"📊 Точность на контрольной выборке (T1): {accuracy_T1:.2%}",
                "\n🔎 Расстояния до классов для объектов обучающей выборки (T0):\n",
            ]

            for i in range(len(self.X_train)):
                dist_str = ", ".join([f"{d:.2f}" for d in distances_T0[i]])
                lines.append(f"Объект {i + 1}: {dist_str} (Реальный: {self.y_train[i]}, Предсказанный: {y_pred_T0[i]})")

            lines.append("\n\n🔍 Расстояния до классов для объектов контрольной выборки (T1):\n")

            for i in range(len(self.X_test)):
                dist_str = ", ".join([f"{d:.2f}" for d in distances_T1[i]])
                lines.append(f"Объект {i + 1}: {dist_str} (Реальный: {self.y_test[i]}, Предсказанный: {y_pred_T1[i]})")

            self.result_text.setText("\n".join(lines))
        else:
            self.result_text.setText("⚠️ Ошибка: данные не загружены.")


def main():
    app = QApplication(sys.argv)
    app.setStyle("Fusion")

    # Тема приложения (опционально)
    palette = QPalette()
    palette.setColor(QPalette.Window, QColor(245, 245, 245))
    palette.setColor(QPalette.WindowText, QColor(33, 33, 33))
    app.setPalette(palette)

    window = MainWindow()
    window.show()
    sys.exit(app.exec())


if __name__ == "__main__":
    main()
