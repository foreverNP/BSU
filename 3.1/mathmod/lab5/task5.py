import numpy as np

# Инициализация переменных
n = 3  # Количество переменных в системе
x = np.zeros(n)  # Вектор решений

a = np.array(
    [
        [0, 0.2 / 0.7, -0.3 / 0.7],
        [-0.4 / 1.3, 0, -0.1 / 1.3],
        [-0.2 / 1.1, -0.1 / 1.1, 0],
    ]
)
f = np.array([3 / 0.7, 1 / 1.3, 1 / 1.1])

# Начальные вероятности для цепи Маркова (должны суммироваться в 1)
pi = np.array([0.2, 0.3, 0.5])

# Матрица переходных вероятностей для цепи Маркова (3x3)
p = np.array([[0, 0.5, 0.5], [0.5, 0, 0.5], [0.5, 0.5, 0]])

N = 1000  # Длина цепи Маркова
m = 10000  # Количество реализаций цепи Маркова

# Цикл по переменным системы
for l in range(n):
    # Инициализация вектора h для текущей переменной
    h = np.zeros(n)
    h[l] = 1
    ksi = np.zeros(m)  # Массив для хранения результатов ksi для текущей переменной
    # Моделирование m цепей Маркова длины N
    for j in range(m):
        # Инициализация цепи Маркова
        alpha = np.random.rand()
        if alpha < pi[0]:
            i_chain = [0]  # Начальное состояние 0
        elif alpha < pi[0] + pi[1]:
            i_chain = [1]  # Начальное состояние 1
        else:
            i_chain = [2]  # Начальное состояние 2

        # Генерация цепи Маркова
        for k in range(1, N + 1):
            alpha = np.random.rand()
            cumulative_prob = 0.0
            for next_state in range(n):
                cumulative_prob += p[i_chain[-1]][next_state]
                if alpha < cumulative_prob:
                    i_chain.append(next_state)
                    break

        # Вычисление весов цепи Маркова
        Q = [0] * (N + 1)
        if pi[i_chain[0]] > 0:
            Q[0] = h[i_chain[0]] / pi[i_chain[0]]
        else:
            Q[0] = 0

        for k in range(1, N + 1):
            denom = p[i_chain[k - 1]][i_chain[k]]
            if denom > 0:
                Q[k] = Q[k - 1] * a[i_chain[k - 1]][i_chain[k]] / denom
            else:
                Q[k] = 0

        # Вычисление ksi для данной цепи
        for k in range(N + 1):
            ksi[j] += Q[k] * f[i_chain[k]]

    # Вычисление среднего значения x[l]
    x[l] = np.sum(ksi) / m

a_direct = np.array([[0.7, -0.2, 0.3], [0.4, 1.3, 0.1], [0.2, 0.1, 1.1]])
f_direct = np.array([3, 1, 1])
x_direct = np.linalg.solve(a_direct, f_direct)
print("Прямое решение с использованием numpy.linalg.solve:")
print(x_direct)

# Вывод вектора решений x
for idx, val in enumerate(x):
    print(f"x[{idx}] = {val}")
