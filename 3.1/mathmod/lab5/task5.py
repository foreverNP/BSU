import numpy as np
import matplotlib.pyplot as plt

# Инициализация переменных
n = 3  # Количество переменных в системе

A = np.array([[1.0, -0.4, -0.1], [0.4, 0.7, -0.1], [0.3, 0.2, 1.0]])
f = np.array([-1, 5, -4])

B = np.array(
    [
        [0, 0.4, 0.1],
        [-0.4 / 0.7, 0, 0.1 / 0.7],
        [-0.3, -0.2, 0],
    ]
)
g = np.array([-1, 5 / 0.7, -4])

# Начальные вероятности для цепи Маркова (должны суммироваться в 1)
pi = np.array([1 / 3, 1 / 3, 1 / 3])

# Матрица переходов для цепи Маркова
p = np.array([[0, 0.5, 0.5], [0.5, 0, 0.5], [0.5, 0.5, 0]])

x_direct = np.linalg.solve(A, f)


def solve_monte_carlo(N, m, l):
    # Инициализация вектора h для текущей переменной
    h = np.zeros(n)
    h[l] = 1
    ksi = np.zeros(m)  # Массив для хранения результатов ksi для текущей переменной

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
            pr = p[i_chain[k - 1]][i_chain[k]]
            if pr > 0:
                Q[k] = Q[k - 1] * B[i_chain[k - 1]][i_chain[k]] / pr
            else:
                Q[k] = 0

        # Вычисление ksi для данной цепи
        for k in range(N + 1):
            ksi[j] += Q[k] * g[i_chain[k]]

    # Вычисление среднего значения x[l]
    return np.sum(ksi) / m


m_fixed = 1000
Ns = [100, 500, 1000, 5000]  # Различные длины цепей Маркова
errors_N = []

for N in Ns:
    x = np.zeros(n)
    for l in range(n):
        x[l] = solve_monte_carlo(N, m_fixed, l)
    error = np.linalg.norm(x - x_direct)
    errors_N.append(error)

plt.figure()
plt.plot(Ns, errors_N, marker="o")
plt.xlabel("Длина цепи Маркова (N)")
plt.ylabel("Ошибка")
plt.title("Зависимость ошибки от длины цепи Маркова при m = {}".format(m_fixed))
plt.grid(True)
plt.savefig("img/error_vs_N.png")
plt.close()

N_fixed = 1000
ms = [100, 500, 1000, 2000, 5000]  # Различные количества реализаций
errors_m = []

for m in ms:
    x = np.zeros(n)
    for l in range(n):
        x[l] = solve_monte_carlo(N_fixed, m, l)
    error = np.linalg.norm(x - x_direct)
    errors_m.append(error)

plt.figure()
plt.plot(ms, errors_m, marker="o")
plt.xlabel("Количество реализаций (m)")
plt.ylabel("Ошибка")
plt.title("Зависимость ошибки от количества реализаций при N = {}".format(N_fixed))
plt.grid(True)
plt.savefig("img/error_vs_m.png")
plt.close()
