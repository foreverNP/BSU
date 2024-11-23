import numpy as np


# Подынтегральная функция
def f2(y, x):
    return np.sqrt(y + np.sin(x) ** 2) * np.exp(x * y)


# Функция для сэмплирования из усеченного экспоненциального распределения
def sample_truncated_exponential(a, b, lambda_):
    cdf_a = 1 - np.exp(-lambda_ * a)
    cdf_b = 1 - np.exp(-lambda_ * b)
    u = np.random.uniform(0, 1)
    y = -np.log(1 - u * (cdf_b - cdf_a) - cdf_a) / lambda_
    return y


# Основная функция для интегрирования
def monte_carlo_integration(N):
    weights = []
    for _ in range(N):
        # Сэмплирование x
        u = np.random.uniform(0, 1)
        # С вероятностью 0.5 выбираем левую или правую часть
        if u < 0.5:
            # Левая часть: x в [-2, 0)
            lambda_x = 1.5  # Настраиваемый параметр
            x = -sample_truncated_exponential(0, 2, lambda_x)
            # Плотность q(x)
            cdf_a = 1 - np.exp(-lambda_x * 0)
            cdf_b = 1 - np.exp(-lambda_x * 2)
            q_x = lambda_x * np.exp(-lambda_x * (-x)) / (cdf_b - cdf_a) * 0.5
        else:
            # Правая часть: x в [0, 2]
            lambda_x = 1.0  # Настраиваемый параметр
            x = sample_truncated_exponential(0, 2, lambda_x)
            # Плотность q(x)
            cdf_a = 1 - np.exp(-lambda_x * 0)
            cdf_b = 1 - np.exp(-lambda_x * 2)
            q_x = lambda_x * np.exp(-lambda_x * x) / (cdf_b - cdf_a) * 0.5

        # Определяем границы для y
        y_min = x**2
        y_max = 4

        if y_min >= y_max:
            continue  # Пропускаем, если область для y пуста

        # Сэмплирование y
        lambda_y = 1.5  # Настраиваемый параметр
        y = sample_truncated_exponential(y_min, y_max, lambda_y)
        # Плотность q(y | x)
        cdf_a_y = 1 - np.exp(-lambda_y * y_min)
        cdf_b_y = 1 - np.exp(-lambda_y * y_max)
        q_y_given_x = lambda_y * np.exp(-lambda_y * y) / (cdf_b_y - cdf_a_y)

        # Общая плотность q(x, y)
        q_xy = q_x * q_y_given_x

        # Вычисляем подынтегральную функцию
        f_value = np.sqrt(y + np.sin(x) ** 2)

        weights.append(f_value)

    # Оценка интеграла
    integral_estimate = np.mean(weights)
    return integral_estimate


# Количество выборок
N = 10000

# Вычисление интеграла
integral = monte_carlo_integration(N)
print(f"Оценка интеграла методом Монте-Карло: {integral}")
