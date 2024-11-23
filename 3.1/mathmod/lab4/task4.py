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
plt.savefig("error_L1.png")


# def f2(y, x):
#     return np.sqrt(y + np.sin(x) ** 2) * np.exp(x * y)


# x_min = -2
# x_max = 2
# y_min = 0
# y_max = 4

# exact_value_L2, _ = dblquad(f2, x_min, x_max, lambda x: x**2, lambda x: 4)
# print("Точное значение L2 (приблизительно):", exact_value_L2)

# # Площадь прямоугольника (область отбора)
# area_rect = (x_max - x_min) * (y_max - y_min)
# print("Площадь прямоугольника:", area_rect)

# # Importance Sampling
# N_values_L2 = np.logspace(2, 7, num=15, dtype=int)
# errors_L2 = []
# results_L2 = []
# for N in N_values_L2:
#     x_random = np.random.uniform(x_min, x_max, N)
#     y_random = np.random.uniform(y_min, y_max, N)

#     # Определение точек, попадающих в область D
#     inside_mask = (y_random >= x_random**2) & (y_random <= 4)
#     x_inside = x_random[inside_mask]
#     y_inside = y_random[inside_mask]

#     f_values_inside = f2(y_inside, x_inside)
#     # Обработка возможных 'inf' или 'nan' значений
#     f_values_inside = np.where(np.isfinite(f_values_inside), f_values_inside, 0)

#     integral_estimate = area_rect * np.sum(f_values_inside) / N
#     error = np.abs(integral_estimate - exact_value_L2)
#     errors_L2.append(error)
#     results_L2.append([N, integral_estimate, error])

# print(
#     tabulate(
#         results_L2,
#         headers=["N", "Оценка интеграла", "Ошибка"],
#         tablefmt="grid",
#         floatfmt=".6f",
#     )
# )

# plt.figure(figsize=(10, 6))
# plt.loglog(N_values_L2, errors_L2, marker="o")
# plt.xlabel("Число итераций N")
# plt.ylabel("Абсолютная ошибка")
# plt.title("Ошибка vs Число итераций для интеграла L2")
# plt.grid(True, which="both", ls="--")
# plt.savefig("error_L2.png")


def f2(y, x):
    return np.sqrt(y + np.sin(x) ** 2) * np.exp(x * y)


# Параметры интегрирования
x_min = -2
x_max = 2
y_min = 0
y_max = 4

# Точное значение интеграла
exact_value_L2, _ = dblquad(f2, x_min, x_max, lambda x: x**2, lambda x: y_max)
print("Точное значение L2 (приблизительно):", exact_value_L2)

# Количество выборок
N_values_L2 = np.logspace(2, 6, num=15, dtype=int)
errors_L2 = []
results_L2 = []


# Функция для сэмплирования из усеченного экспоненциального распределения
def sample_truncated_exponential(y_min, y_max, lambda_y):
    # Сэмплирование из экспоненциального распределения с параметром lambda_y
    u = np.random.uniform(0, 1)
    cdf_min = 1 - np.exp(-lambda_y * y_min)
    cdf_max = 1 - np.exp(-lambda_y * y_max)
    y = -np.log(1 - u * (cdf_max - cdf_min) - cdf_min) / lambda_y
    return y


# Основная функция для вычисления интеграла
def monte_carlo_integration(N):
    samples = []
    weights = []

    # Задаем объем области интегрирования для масштабирования
    volume_x = 4  # Диапазон x от -2 до 2
    total_integral = 0.0

    for _ in range(N):
        # Сэмплируем x равномерно на (-2, 2)
        x = np.random.uniform(-2, 2)

        # Определяем границы для y
        y_min = x**2
        y_max = 4

        # Настраиваем параметр lambda_y в зависимости от x
        # Для x >= 0 используем положительный lambda_y
        # Для x < 0 используем равномерное распределение для y
        if x >= 0:
            lambda_y = 1.0  # Можно настроить это значение для оптимизации
            # Сэмплируем y из усеченного экспоненциального распределения
            y = sample_truncated_exponential(y_min, y_max, lambda_y)
            # Вычисляем плотность q(y | x)
            cdf_min = 1 - np.exp(-lambda_y * y_min)
            cdf_max = 1 - np.exp(-lambda_y * y_max)
            q_y_given_x = lambda_y * np.exp(-lambda_y * y) / (cdf_max - cdf_min)
        else:
            # Для x < 0 сэмплируем y равномерно
            y = np.random.uniform(y_min, y_max)
            # Плотность q(y | x) для равномерного распределения
            q_y_given_x = 1 / (y_max - y_min)

        # Вычисляем подынтегральную функцию
        f_value = f2(y, x)

        # Вычисляем вес
        if x >= 0:
            # Для важностного сэмплирования
            weight = f_value / q_y_given_x
        else:
            # Для равномерного сэмплирования
            weight = f_value

        weights.append(weight)

    # Оценка интеграла
    integral_estimate = np.mean(weights) * volume_x
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
plt.savefig("error_L2_exponential.png")
