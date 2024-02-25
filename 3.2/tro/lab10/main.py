import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression

# =====================
# Модуль 1: Исследование данных
# =====================


def explore_data(data):
    print("Исследование данных")
    for label, samples in data.items():
        arr = np.vstack(samples)
        print(f"\nМетка: {label}")
        print(f"  Число образцов: {arr.shape[0]}")
        print(f"  Среднее по признакам: {np.round(arr.mean(axis=0), 2)}")
        print(f"  Стандартное отклонение: {np.round(arr.std(axis=0), 2)}")


# =====================
# Модуль 2: Построение модели
# =====================


def build_model(data):
    # Подготовка данных
    X = np.vstack([s for samples in data.values() for s in samples])
    y = np.hstack([[label] * len(samples) for label, samples in data.items()])

    # Масштабирование
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    # Логистическая регрессия
    clf = LogisticRegression(multi_class="multinomial", solver="lbfgs")
    clf.fit(X_scaled, y)

    # Вычисление центроидов и максимальных расстояний
    centroids = {}
    max_dists = {}
    for label in data.keys():
        # векторы признаков данного класса
        class_samples = X_scaled[y == label]
        centroid = class_samples.mean(axis=0)
        centroids[label] = centroid
        # максимальное расстояние до центра среди учебных
        dists = np.linalg.norm(class_samples - centroid, axis=1)
        max_dists[label] = dists.max()

    print("\nМодель построена. Классы:", clf.classes_)
    return scaler, clf, centroids, max_dists


# =====================
# Модуль 3: Симуляция доступа
# =====================


def simulate_access(scaler, clf, centroids, max_dists, samples, prob_threshold=0.6, dist_factor=1.2):
    """
    Для каждого образца возвращает (метка, макс_вероятность, расстояние, разрешен_доступ)
    Условие отказа: либо вероятность < prob_threshold, либо расстояние до центра > max_dist*dist_factor
    """
    X = np.vstack(samples)
    X_scaled = scaler.transform(X)

    # Вероятности и предсказания
    probs = clf.predict_proba(X_scaled)
    preds = clf.classes_[np.argmax(probs, axis=1)]
    max_probs = np.max(probs, axis=1)

    results = []
    for vec, pred, prob in zip(X_scaled, preds, max_probs):
        # расстояние до центроида предсказанного класса
        dist = np.linalg.norm(vec - centroids[pred])
        # условие по расстоянию
        dist_limit = max_dists[pred] * dist_factor

        grant = (prob >= prob_threshold) and (dist <= dist_limit)
        assigned = pred if grant else "Unknown"
        results.append((assigned, float(np.round(prob, 2)), float(np.round(dist, 2)), grant))
    return results


def main():
    data = {
        "Директор": [
            [0.9, 0.7, 0.9, 0.5, 0.8, 0.5, 0.5, 0.6, 0.9, 0.8, 0.9, 0.9, 0.4, 0.2, 0.2],
            [0.8, 0.5, 0.8, 0.6, 0.6, 0.6, 0.5, 0.5, 0.8, 0.8, 0.6, 0.8, 0.3, 0.3, 0.3],
            [0.8, 0.9, 0.8, 0.7, 0.7, 0.5, 0.4, 0.4, 0.9, 0.9, 0.9, 0.9, 0.2, 0.4, 0.2],
        ],
        "Бухгалтер": [
            [0.6, 0.5, 0.4, 0.5, 0.6, 0.5, 0.4, 0.6, 0.1, 0.2, 0.3, 0.5, 0.4, 0.6, 0.2],
            [0.5, 0.6, 0.5, 0.6, 0.5, 0.4, 0.6, 0.5, 0.3, 0.3, 0.3, 0.2, 0.6, 0.3, 0.3],
            [0.4, 0.3, 0.6, 0.5, 0.6, 0.3, 0.5, 0.4, 0.6, 0.4, 0.1, 0.3, 0.3, 0.6, 0.1],
        ],
    }

    # Модуль 1: исследование
    explore_data(data)

    # Модуль 2: построение модели
    scaler, clf, centroids, max_dists = build_model(data)

    # Тестирование известных пользователей
    print("\nТестирование известных пользователей:")
    for label, samples in data.items():
        res = simulate_access(scaler, clf, centroids, max_dists, samples)
        print(f"\nРезультаты для {label}:")
        for i, (assigned, prob, dist, grant) in enumerate(res, 1):
            status = "РАЗРЕШЕН" if grant else "ОТКАЗАН"
            print(f" Образец {i}: назначено={assigned}, вероятность={prob}, расстояние={dist}, доступ={status}")

    # Пример отказа в доступе на неизвестном лице
    unknown = [[0.2, 0.3, 0.4, 0.3, 0.3, 0.4, 0.5, 0.6, 0.8, 0.7, 0.8, 0.3, 0.8, 0.6, 0.6]]
    print("\nПример отказа в доступе для неизвестного пользователя:")
    for i, (assigned, prob, dist, grant) in enumerate(simulate_access(scaler, clf, centroids, max_dists, unknown), 1):
        status = "РАЗРЕШЕН" if grant else "ОТКАЗАН"
        print(f" Образец {i}: назначено={assigned}, вероятность={prob}, расстояние={dist}, доступ={status}")


if __name__ == "__main__":
    main()
