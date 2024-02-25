import matplotlib.pyplot as plt


class Neuron:
    def __init__(self, weights, threshold):
        self.weights = weights
        self.threshold = threshold

    def activate(self, inputs):
        weighted_sum = sum(i * w for i, w in zip(inputs, self.weights))
        print(f"Взвешенная сумма: {weighted_sum:.2f}")

        if weighted_sum >= self.threshold:
            return 1  # Решение: увольнение
        else:
            return 0  # Решение: оставить сотрудников

    def decision(self, inputs):
        result = self.activate(inputs)
        if result == 1:
            return "Уволить сотрудников"
        else:
            return "Оставить сотрудников"


# Финансовые показатели – 40%, Операционные – 30%, Клиентские – 20%, HR – 10%
weights = [0.4, 0.3, 0.2, 0.1]

# Пороговое значение определено на основе модели: например, итоговый балл 60 является критерием увольнения
threshold = 60

neuron = Neuron(weights, threshold)

inputs_object = [58, 55, 50, 45]

# Примем решение на основе данных объекта
decision_result = neuron.decision(inputs_object)
print("Решение:", decision_result)


def plot_kpi_contribution(inputs, weights):
    contributions = [i * w for i, w in zip(inputs, weights)]
    kpi_labels = ["Финансы (40%)", "Операционные (30%)", "Клиентские (20%)", "HR (10%)"]

    plt.figure(figsize=(8, 4))
    bars = plt.bar(kpi_labels, contributions, color="skyblue")
    plt.axhline(y=threshold, color="r", linestyle="--", label=f"Порог {threshold}")
    plt.title("Вклад KPI в итоговый балл")
    plt.ylabel("Взвешенная оценка")
    plt.legend()

    for bar in bars:
        yval = bar.get_height()
        plt.text(
            bar.get_x() + bar.get_width() / 2,
            yval + 0.5,
            f"{yval:.1f}",
            ha="center",
            va="bottom",
        )

    plt.show()


plot_kpi_contribution(inputs_object, weights)
