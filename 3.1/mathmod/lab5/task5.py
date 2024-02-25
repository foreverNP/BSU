import numpy as np
import matplotlib.pyplot as plt

n = 3

A = np.array(
    [
        [0.7, -0.3, -0.2],
        [-0.2, 0.2, -0.7],
        [-0.5, 0.1, 2],
    ]
)
f = np.array([-1, -3, 8])

B = np.array(
    [
        [0, 0.3 / 0.7, 0.2 / 0.7],
        [0.2, 0, -0.2],
        [0.5 / 2, -0.1 / 2, 0],
    ]
)
g = np.array([-1 / 0.7, -3.45454545, 8 / 2])

# Начальные вероятности для цепи Маркова (должны суммироваться в 1)
pi = np.array([1 / 3, 1 / 3, 1 / 3])

# Матрица переходов для цепи Маркова
p = np.array([[0, 0.5, 0.5], [0.5, 0, 0.5], [0.5, 0.5, 0]])

x_direct = np.linalg.solve(A, f)


def solve_monte_carlo_all_coords(N, m):
    # Результирующий вектор
    x_sum = np.zeros(n)

    # Чтобы удобнее было инициализировать Q, определим "h" как единичную матрицу:
    # h[l, i] = 1, если l == i, иначе 0.
    # Тогда Q[l,k] будет соответствовать случаю, когда "h" берётся для компоненты l.
    h = np.eye(n)

    for _ in range(m):
        # 1. Инициализация цепи Маркова
        alpha = np.random.rand()
        if alpha < pi[0]:
            i_chain = [0]
        elif alpha < pi[0] + pi[1]:
            i_chain = [1]
        else:
            i_chain = [2]

        # 2. Генерация цепи Маркова длины N
        for k in range(1, N + 1):
            alpha = np.random.rand()
            cumulative_prob = 0.0
            for next_state in range(n):
                cumulative_prob += p[i_chain[-1]][next_state]
                if alpha < cumulative_prob:
                    i_chain.append(next_state)
                    break

        # 3. Вычисление весов Q для всех l = 0..n-1
        #    Q будет размером (n, N+1), т.к. для каждого l своя строка
        Q = np.zeros((n, N + 1))

        # Для каждого l: Q[l, 0] = h[l, i_chain[0]] / pi[i_chain[0]] (если pi[i_chain[0]]>0)
        # h[l, i_chain[0]] = 1, если l == i_chain[0], иначе 0
        if pi[i_chain[0]] > 0:
            for l in range(n):
                Q[l, 0] = h[l, i_chain[0]] / pi[i_chain[0]]

        for k in range(1, N + 1):
            pr = p[i_chain[k - 1]][i_chain[k]]
            if pr > 0:
                for l in range(n):
                    Q[l, k] = Q[l, k - 1] * B[i_chain[k - 1]][i_chain[k]] / pr

        # 4. Накопим вклад в x для каждой компоненты l
        #    ksi[l] = sum_{k=0..N} Q[l, k] * g[i_chain[k]]
        ksi = np.zeros(n)
        for l in range(n):
            for k in range(N + 1):
                ksi[l] += Q[l, k] * g[i_chain[k]]

        x_sum += ksi

    x_est = x_sum / m
    return x_est


print("Решение методом прямого вычисления:")
print(x_direct)

print("Решение методом Монте-Карло:")
print(solve_monte_carlo_all_coords(N=1000, m=1000))

# # Построение графиков зависимости ошибки от длины цепи Маркова и количества реализаций
# Ns = [100, 500, 1000, 5000]
# m_fixed = 1000
# errors_N = []
# for N in Ns:
#     x_est = solve_monte_carlo_all_coords(N, m_fixed)
#     error = np.linalg.norm(x_est - x_direct)
#     errors_N.append(error)

# plt.figure()
# plt.plot(Ns, errors_N, marker="o")
# plt.xlabel("Длина цепи Маркова (N)")
# plt.ylabel("Ошибка")
# plt.title("Зависимость ошибки от длины цепи (m = {})".format(m_fixed))
# plt.grid(True)
# plt.savefig("img/error_vs_N.png")
# plt.close()


# ms = [100, 500, 1000, 2000, 5000]
# N_fixed = 1000
# errors_m = []
# for m in ms:
#     x_est = solve_monte_carlo_all_coords(N_fixed, m)
#     error = np.linalg.norm(x_est - x_direct)
#     errors_m.append(error)

# plt.figure()
# plt.plot(ms, errors_m, marker="o")
# plt.xlabel("Количество реализаций (m)")
# plt.ylabel("Ошибка")
# plt.title("Зависимость ошибки от числа реализаций (N = {})".format(N_fixed))
# plt.grid(True)
# plt.savefig("img/error_vs_m.png")
# plt.close()


# Ns = [100, 500, 1000, 5000]  # Длина цепи Маркова
# ms = [100, 500, 1000, 2000, 5000]  # Количество реализаций

# error_grid = np.zeros((len(ms), len(Ns)))

# for i, N_ in enumerate(Ns):
#     for j, m_ in enumerate(ms):
#         x_est = solve_monte_carlo_all_coords(N_, m_)
#         error_grid[j, i] = np.linalg.norm(x_est - x_direct)

# # Подготовим координатные сетки (они должны совпадать с размерностями error_grid)
# N_grid, M_grid = np.meshgrid(Ns, ms, indexing="xy")  # shape -> (len(ms), len(Ns))

# fig, ax = plt.subplots(subplot_kw={"projection": "3d"}, figsize=(8, 6))

# surf = ax.plot_surface(N_grid, M_grid, error_grid, cmap="viridis", edgecolor="none")

# ax.set_title("Зависимость ошибки от (N, m)")
# ax.set_xlabel("N")
# ax.set_ylabel("m")
# ax.set_zlabel("Error")

# fig.colorbar(surf, ax=ax, shrink=0.5, aspect=10)

# plt.savefig("img/error_3d_surface.png")
# plt.show()
# plt.close()
