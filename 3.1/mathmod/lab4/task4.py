import numpy as np
import matplotlib.pyplot as plt
from scipy.integrate import quad, dblquad
from tabulate import tabulate


def f1(x):
    return x**x * (1 + np.log(x)) * np.tan(x)


a_L1 = 3
b_L1 = 3.5

exact_value_L1, _ = quad(f1, a_L1, b_L1)
print("Точное значение L1 (приблизительно):", exact_value_L1)

N_values_L1 = np.logspace(2, 7, num=15, dtype=int)
errors_L1 = []
results_L1 = []
for N in N_values_L1:
    x_random = np.random.uniform(a_L1, b_L1, N)
    f_values = f1(x_random)
    # 1/N * sum(f(x_i)) / (1 / (b - a))
    integral_estimate = (b_L1 - a_L1) * np.mean(f_values)
    error = np.abs(integral_estimate - exact_value_L1)
    errors_L1.append(error)
    results_L1.append([N, integral_estimate, error])

print(
    tabulate(
        results_L1,
        headers=["N", "Оценка интеграла", "Ошибка"],
        tablefmt="grid",
        floatfmt=".6f",
    )
)

plt.figure(figsize=(10, 6))
plt.loglog(N_values_L1, errors_L1, marker="o")
plt.xlabel("Число итераций N")
plt.ylabel("Абсолютная ошибка")
plt.title("Ошибка vs Число итераций для интеграла L1")


def f2(y, x):
    return np.sqrt(y + np.sin(x) ** 2) * np.exp(x * y)


# Параметры интегрирования
x_min = -2
x_max = 2
y_max = 4

# Точное значение интеграла
exact_value_L2, _ = dblquad(f2, x_min, x_max, lambda x: x**2, y_max)
print("Точное значение L2 (приблизительно):", exact_value_L2)

# Количество выборок
N_values_L2 = np.logspace(3, 7, num=15, dtype=int)
errors_L2 = []
results_L2 = []


# Функция для усеченного экспоненциального распределения
def sample_truncated_exponential(min, max, h):
    u = np.random.uniform(0, 1)
    cdf_min = 1 - np.exp(-h * min)
    cdf_max = 1 - np.exp(-h * max)
    y = -np.log(1 - u * (cdf_max - cdf_min) - cdf_min) / h
    return y


# Основная функция для вычисления интеграла
def monte_carlo_integration(N):
    weights = []

    for _ in range(N):
        x = np.random.uniform(x_min, x_max)
        y_min = x**2
        y = np.random.uniform(y_min, y_max)

        f_value = f2(y, x) * (y_max - y_min) * (x_max - x_min)

        weights.append(f_value)

    # Оценка интеграла
    integral_estimate = np.mean(weights)
    return integral_estimate


for N in N_values_L2:
    integral_estimate = monte_carlo_integration(N)
    error = np.abs(integral_estimate - exact_value_L2)
    errors_L2.append(error)
    results_L2.append([N, integral_estimate, error])

# Вывод результатов
print(
    tabulate(
        results_L2,
        headers=["N", "Оценка интеграла", "Ошибка"],
        tablefmt="grid",
        floatfmt=".6f",
    )
)

# Построение графика ошибки
plt.figure(figsize=(10, 6))
plt.loglog(N_values_L2, errors_L2, marker="o")
plt.xlabel("Число итераций N")
plt.ylabel("Абсолютная ошибка")
plt.title("Ошибка vs Число итераций для интеграла L2")
plt.show()
