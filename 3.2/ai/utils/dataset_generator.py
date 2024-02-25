import numpy as np
import pandas as pd
import argparse
from sklearn.datasets import make_classification, make_blobs


def generate_dataset(num_samples=100, num_features=2, num_classes=3, method="blobs", noise=0.1, filename="dataset.csv"):
    """
    Генерирует синтетический датасет для задачи классификации.

    Параметры:
    ----------
    num_samples : int
        Количество объектов в датасете
    num_features : int
        Количество признаков каждого объекта
    num_classes : int
        Количество классов
    method : str
        Метод генерации: 'blobs' для хорошо разделимых кластеров или
        'classification' для более сложных данных
    noise : float
        Уровень шума (от 0 до 1)
    filename : str
        Имя выходного CSV файла
    """
    print(f"Генерация датасета с параметрами:")
    print(f"  - Количество объектов: {num_samples}")
    print(f"  - Количество признаков: {num_features}")
    print(f"  - Количество классов: {num_classes}")
    print(f"  - Метод генерации: {method}")
    print(f"  - Уровень шума: {noise}")

    # Генерация данных
    if method == "blobs":
        # Генерация хорошо разделимых кластеров
        features, labels = make_blobs(
            n_samples=num_samples,
            n_features=num_features,
            centers=num_classes,
            cluster_std=noise * 2,  # Стандартное отклонение определяет разброс точек
            random_state=42,
        )
    else:
        # Генерация данных с помощью make_classification (более сложные данные)
        features, labels = make_classification(
            n_samples=num_samples,
            n_features=num_features,
            n_informative=max(1, num_features - 1),  # Информативные признаки
            n_redundant=0,  # Избыточные признаки
            n_classes=num_classes,
            n_clusters_per_class=1,
            class_sep=1.0 / noise if noise > 0 else 10.0,  # Разделимость классов
            random_state=42,
        )

    # Создание DataFrame
    columns = [f"feature_{i + 1}" for i in range(num_features)]
    columns.append("class")

    # Объединение признаков и меток
    data = np.column_stack((features, labels))

    # Создание и сохранение датафрейма
    df = pd.DataFrame(data, columns=columns)

    # Округление значений для лучшей читабельности (опционально)
    for col in columns[:-1]:
        df[col] = df[col].round(4)

    # Сохранение в CSV
    df.to_csv(filename, index=False)
    print(f"Датасет успешно сохранен в файл: {filename}")

    # Выводим распределение классов
    class_counts = df["class"].value_counts().sort_index()
    print("\nРаспределение классов:")
    for class_label, count in class_counts.items():
        print(f"  - Класс {int(class_label)}: {count} объектов")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Генератор датасета для задачи классификации")
    parser.add_argument("-n", "--samples", type=int, default=100, help="Количество объектов")
    parser.add_argument("-f", "--features", type=int, default=2, help="Количество признаков")
    parser.add_argument("-c", "--classes", type=int, default=3, help="Количество классов")
    parser.add_argument(
        "-m",
        "--method",
        choices=["blobs", "classification"],
        default="blobs",
        help="Метод генерации: blobs - хорошо разделимые кластеры, classification - более сложные данные",
    )
    parser.add_argument("--noise", type=float, default=0.1, help="Уровень шума (от 0 до 1)")
    parser.add_argument("-o", "--output", default="dataset.csv", help="Имя выходного файла")

    args = parser.parse_args()

    generate_dataset(
        num_samples=args.samples, num_features=args.features, num_classes=args.classes, method=args.method, noise=args.noise, filename=args.output
    )
