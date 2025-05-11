import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from scipy.spatial.distance import cdist
import tkinter as tk
from tkinter import filedialog, scrolledtext, ttk
import os


def load_dataset(file_path):
    """
    Загрузка данных из файла CSV.
    Последний столбец считается меткой класса.
    """
    data = pd.read_csv(file_path)
    features = data.iloc[:, :-1].values
    labels = data.iloc[:, -1].values
    return features, labels


def divide_dataset(features, labels, test_size=0.2):
    """
    Разбиение данных на обучающую и контрольную выборки.
    """
    unique_classes = np.unique(labels)
    train_features, test_features, train_labels, test_labels = [], [], [], []

    for class_label in unique_classes:
        # Отбираем данные для текущего класса
        mask = labels == class_label
        class_features = features[mask]
        class_labels = labels[mask]

        # Разбиваем данные этого класса
        features_train, features_test, labels_train, labels_test = train_test_split(
            class_features, class_labels, test_size=test_size, random_state=42
        )

        train_features.append(features_train)
        test_features.append(features_test)
        train_labels.append(labels_train)
        test_labels.append(labels_test)

    return (
        np.vstack(train_features),
        np.hstack(train_labels),  # Обучающая выборка
        np.vstack(test_features),
        np.hstack(test_labels),  # Контрольная выборка
    )


def calculate_distance(point1, point2):
    """
    Функция сравнения объектов.
    """
    return cdist(point1, point2, metric="euclidean")


def min_distance_to_class(sample, class_samples):
    """
    Минимальное расстояние до объектов класса.
    """
    return np.min(cdist(sample, class_samples), axis=1)


def classify_with_details(samples, train_features, train_labels):
    unique_classes = np.unique(train_labels)
    all_distances = []

    for class_label in unique_classes:
        class_samples = train_features[train_labels == class_label]
        all_distances.append(min_distance_to_class(samples, class_samples))

    all_distances = np.vstack(all_distances).T
    predicted_labels = unique_classes[np.argmin(all_distances, axis=1)]

    return all_distances, predicted_labels


def evaluate_classifier(train_features, train_labels, test_features, test_labels):
    """
    Тестирование алгоритма с подробным выводом расчетов.
    """

    # Тестирование на обучающей выборке (T0)
    distances_train, pred_labels_train = classify_with_details(train_features, train_features, train_labels)
    accuracy_train = (pred_labels_train == train_labels).mean()

    # Тестирование на контрольной выборке (T1)
    distances_test, pred_labels_test = classify_with_details(test_features, train_features, train_labels)
    accuracy_test = (pred_labels_test == test_labels).mean()

    return (accuracy_train, accuracy_test, pred_labels_train, pred_labels_test, distances_train, distances_test)


class ClassifierApp(tk.Tk):
    def __init__(self):
        super().__init__()

        self.title("Классификатор")
        self.geometry("800x700")
        self.configure(bg="#f0f0f0")

        self.style = ttk.Style()
        self.style.theme_use("clam")
        self.style.configure("TButton", font=("Arial", 10), padding=10)
        self.style.configure("TLabel", font=("Arial", 10), background="#f0f0f0")
        self.style.configure("Header.TLabel", font=("Arial", 11, "bold"), background="#f0f0f0")

        # Создание фрейма для содержимого
        main_frame = ttk.Frame(self, padding=15)
        main_frame.pack(fill=tk.BOTH, expand=True)

        header_label = ttk.Label(main_frame, text="Классификатор с методом ближайшего соседа", style="Header.TLabel")
        header_label.pack(pady=(0, 15))

        self.load_button = ttk.Button(main_frame, text="Загрузить данные", command=self.load_data, style="TButton")
        self.load_button.pack(fill=tk.X, pady=5)

        self.file_label = ttk.Label(main_frame, text="Файл не выбран", wraplength=700, style="TLabel")
        self.file_label.pack(pady=5)

        self.run_button = ttk.Button(main_frame, text="Запустить классификацию", command=self.run_classification, state=tk.DISABLED, style="TButton")
        self.run_button.pack(fill=tk.X, pady=5)

        result_frame = ttk.LabelFrame(main_frame, text="Результаты", padding=10)
        result_frame.pack(fill=tk.BOTH, expand=True, pady=10)

        self.result_text = scrolledtext.ScrolledText(result_frame, wrap=tk.WORD, font=("Courier New", 10), bg="#ffffff", height=25)
        self.result_text.pack(fill=tk.BOTH, expand=True)

        self.status_bar = ttk.Label(self, text="Готов к работе", relief=tk.SUNKEN, anchor=tk.W, padding=(5, 2))
        self.status_bar.pack(side=tk.BOTTOM, fill=tk.X)

        self.file_path = None
        self.train_features, self.train_labels = None, None
        self.test_features, self.test_labels = None, None

    def load_data(self):
        file_path = filedialog.askopenfilename(title="Выбрать файл данных", filetypes=[("CSV файлы", "*.csv"), ("Все файлы", "*.*")])

        if file_path:
            self.file_path = file_path
            filename = os.path.basename(file_path)
            self.file_label.config(text=f"Выбран файл: {filename}")
            self.status_bar.config(text=f"Загрузка файла: {filename}")

            try:
                features, labels = load_dataset(file_path)
                self.train_features, self.train_labels, self.test_features, self.test_labels = divide_dataset(features, labels, test_size=0.8)

                self.run_button.config(state=tk.NORMAL)
                self.result_text.delete(1.0, tk.END)
                self.result_text.insert(tk.END, "✅ Данные успешно загружены и разделены.\n")
                self.result_text.insert(tk.END, f"   Обучающая выборка: {len(self.train_features)} объектов\n")
                self.result_text.insert(tk.END, f"   Контрольная выборка: {len(self.test_features)} объектов\n")
                self.result_text.insert(tk.END, f"   Количество признаков: {self.train_features.shape[1]}\n")
                self.result_text.insert(tk.END, f"   Уникальные классы: {np.unique(self.train_labels)}\n\n")
                self.result_text.insert(tk.END, "Нажмите 'Запустить классификацию' для продолжения.")

                self.status_bar.config(text="Данные загружены успешно")
            except Exception as e:
                self.result_text.delete(1.0, tk.END)
                self.result_text.insert(tk.END, f"❌ Ошибка при загрузке данных: {e}")
                self.status_bar.config(text="Ошибка загрузки данных")

    def run_classification(self):
        """Запуск процесса классификации и вывод результатов"""
        if self.train_features is not None and self.test_features is not None:
            self.status_bar.config(text="Выполняется классификация...")
            self.result_text.delete(1.0, tk.END)
            self.result_text.insert(tk.END, "⏳ Выполняется классификация...\n\n")
            self.update_idletasks()

            try:
                (accuracy_train, accuracy_test, pred_labels_train, pred_labels_test, distances_train, distances_test) = evaluate_classifier(
                    self.train_features, self.train_labels, self.test_features, self.test_labels
                )

                # Вывод результатов
                result_text = ""
                result_text += f"Точность на обучающей выборке (T0): {accuracy_train:.2%}\n"
                result_text += f"Точность на контрольной выборке (T1): {accuracy_test:.2%}\n\n"

                result_text += "Расстояния до классов для объектов обучающей выборки (T0):\n\n"
                unique_classes = np.unique(self.train_labels)
                result_text += "Объект | " + " | ".join([f"Класс {c}" for c in unique_classes]) + " | Реальный | Предсказан\n"
                result_text += "-" * 80 + "\n"

                for i in range(min(len(self.train_features), 20)):  # Ограничиваем вывод до 20 объектов
                    dist_str = " | ".join([f"{d:.2f}" for d in distances_train[i]])
                    result_text += f"{i + 1:6d} | {dist_str} | {self.train_labels[i]:8} | {pred_labels_train[i]:9}\n"

                if len(self.train_features) > 20:
                    result_text += "... и еще объектов: " + str(len(self.train_features) - 20) + "\n"

                result_text += "\nРасстояния до классов для объектов контрольной выборки (T1):\n\n"
                result_text += "Объект | " + " | ".join([f"Класс {c}" for c in unique_classes]) + " | Реальный | Предсказан\n"
                result_text += "-" * 80 + "\n"

                for i in range(min(len(self.test_features), 20)):  # Ограничиваем вывод до 20 объектов
                    dist_str = " | ".join([f"{d:.2f}" for d in distances_test[i]])
                    result_text += f"{i + 1:6d} | {dist_str} | {self.test_labels[i]:8} | {pred_labels_test[i]:9}\n"

                if len(self.test_features) > 20:
                    result_text += "... и еще объектов: " + str(len(self.test_features) - 20) + "\n"

                self.result_text.delete(1.0, tk.END)
                self.result_text.insert(tk.END, result_text)
                self.status_bar.config(text="Классификация завершена успешно")

            except Exception as e:
                self.result_text.delete(1.0, tk.END)
                self.result_text.insert(tk.END, f"❌ Ошибка при выполнении классификации: {e}")
                self.status_bar.config(text="Ошибка выполнения классификации")
        else:
            self.result_text.delete(1.0, tk.END)
            self.result_text.insert(tk.END, "⚠️ Ошибка: данные не загружены.")
            self.status_bar.config(text="Ошибка: данные не загружены")


def main():
    app = ClassifierApp()
    app.mainloop()


if __name__ == "__main__":
    main()
