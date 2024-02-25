import matplotlib.pyplot as plt
import numpy as np


# Функция для определения состояния датчика и соответствующего управляющего решения
def get_sensor_status(value):
    """
    Определяет состояние датчика по его значению.
    :param value: значение датчика
    :return: кортеж (состояние, рекомендация)
    """
    if 0.0 <= value <= 0.15:
        return "Норма", "Наблюдать"
    elif 0.16 <= value <= 0.47:
        return "Нарушение", "Снизить мощность"
    elif 0.48 <= value <= 1.00:
        return "Превышение", "Остановить"
    else:
        return "Неопределено", "Нет решения"


# Функция для определения общего решения по набору датчиков.
def aggregate_decision(sensor_results):
    """
    Определяет общее решение для объекта по приоритету:
    - Если хотя бы один датчик в состоянии 'Превышение' -> 'Остановить'
    - Если нет превышений, но есть 'Нарушение' -> 'Снизить мощность'
    - Иначе -> 'Наблюдать'
    """
    states = [result[0] for result in sensor_results.values()]
    if "Превышение" in states:
        return "Остановить"
    elif "Нарушение" in states:
        return "Снизить мощность"
    else:
        return "Наблюдать"


sensor_names = [
    "dt1 (Общий)",
    "dt2 (Вода)",
    "dt3 (Снег)",
    "dt4 (Лёд)",
    "dt5 (Почва)",
    "dt6 (Растения)",
]

scenarios = {
    "Все в норме": {
        "dt1": 0.14,
        "dt2": 0.14,
        "dt3": 0.14,
        "dt4": 0.14,
        "dt5": 0.14,
        "dt6": 0.14,
    },
    "Превышение в снегу": {
        "dt1": 0.14,
        "dt2": 0.14,
        "dt3": 0.55,
        "dt4": 0.14,
        "dt5": 0.14,
        "dt6": 0.14,
    },
    "Нарушение в почве": {
        "dt1": 0.14,
        "dt2": 0.14,
        "dt3": 0.14,
        "dt4": 0.14,
        "dt5": 0.30,
        "dt6": 0.14,
    },
    "Смешанный сценарий": {
        "dt1": 0.14,
        "dt2": 0.50,
        "dt3": 0.20,
        "dt4": 0.14,
        "dt5": 0.55,
        "dt6": 0.14,
    },
}

for scenario, readings in scenarios.items():
    print(f"\nСценарий: {scenario}")
    sensor_results = {}
    for sensor, value in readings.items():
        status, decision = get_sensor_status(value)
        sensor_results[sensor] = (status, decision)
        print(f"  {sensor}: {value:.2f} → {status} → Рекомендация: {decision}")

    overall_decision = aggregate_decision(sensor_results)
    print(f"Общее решение для МАЭС: {overall_decision}")

    values = [readings[f"dt{i}"] for i in range(1, 7)]
    statuses = [get_sensor_status(val)[0] for val in values]
    decisions = [get_sensor_status(val)[1] for val in values]

    color_map = {
        "Норма": "green",
        "Нарушение": "orange",
        "Превышение": "red",
        "Неопределено": "grey",
    }
    colors = [color_map.get(status, "grey") for status in statuses]

    x = np.arange(len(sensor_names))

    plt.figure(figsize=(10, 5))
    bars = plt.bar(x, values, color=colors, edgecolor="black")

    for i, bar in enumerate(bars):
        height = bar.get_height()
        plt.text(
            bar.get_x() + bar.get_width() / 2,
            height + 0.01,
            f"{statuses[i]} ({decisions[i]})",
            ha="center",
            va="bottom",
            fontsize=9,
        )

    plt.ylim(0, 1.1)
    plt.xticks(x, sensor_names, rotation=15)
    plt.ylabel("Показание датчика")
    plt.title(
        f"Диаграмма показаний датчиков – {scenario}\nДействие: {overall_decision}"
    )
    plt.grid(axis="y", linestyle="--", alpha=0.7)
    plt.tight_layout()
    plt.show()
