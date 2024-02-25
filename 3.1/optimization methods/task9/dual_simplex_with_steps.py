import numpy as np
from scipy.optimize import linprog


def dual_simplex_method(A, b, c, bounds, j_b):
    iteration = 0
    max_iterations = 100
    num_vars = len(c)  # Количество переменных
    num_constraints = len(b)  # Количество ограничений

    # Преобразуем входные данные в формат numpy для удобства вычислений
    A = np.array(A, dtype=float)
    b = np.array(b, dtype=float)
    c = np.array(c, dtype=float)
    x = np.zeros(num_vars)
    bounds = np.array(bounds, dtype=float)

    # Преобразуем индексы базисных переменных из 1-based в 0-based
    j_b = [j - 1 for j in j_b]

    while iteration < max_iterations:
        print("\n============= ИТЕРАЦИЯ №{} =============".format(iteration + 1))

        print("\n# Шаг 1: Вычисление потенциалов u")
        j_h = [j for j in range(num_vars) if j not in j_b]
        A_B = A[:, j_b]
        c_B = c[j_b]
        A_B_T = A_B.T

        print(f"Решаем уравнение:\n {A_B_T} * u = {c_B}")

        # Решаем систему для нахождения потенциалов u
        try:
            u = np.linalg.solve(A_B_T, c_B)
        except np.linalg.LinAlgError:
            print("Матрица A_B^T вырожденная, невозможно решить для потенциалов u.")
            return None

        print("Потенциалы u:", u)

        print("\n# Шаг 2: Подсчитываем небазисные компоненты коплана δ_j")
        delta = {}
        for j in j_h:
            a_j = A[:, j]
            delta_j = c[j] - np.dot(a_j, u)
            delta[j] = delta_j
            print(f"δ_{j + 1} = c_{j + 1} - a_{j + 1}^T * u = {c[j]} - {a_j.T} * {u} = {delta_j}")

        print("\n# Шаг 3: Определение псевдоплана x_e")
        x_e = x.copy()
        print("Псевдоплан xe (небазисные переменные):")
        for j in j_h:
            d_j, d_star_j = bounds[j]  # Нижняя и верхняя границы для x_j
            # Устанавливаем x_e[j] в зависимости от знака δ_j
            if delta[j] < 0:
                print(f"δ_{j + 1} < 0, выбираем xe[{j + 1}] = {d_j}")
                x_e[j] = d_j
            elif delta[j] > 0:
                print(f"δ_{j + 1} > 0, выбираем xe[{j + 1}] = {d_star_j}")
                x_e[j] = d_star_j
            else:
                # Если δ_j = 0, выбираем границу, максимизирующую c_j * x_j
                if c[j] >= 0:
                    x_e[j] = d_star_j
                else:
                    x_e[j] = d_j
                print(f"δ_{j + 1} = 0, выбираем xe[{j + 1}] = {x_e[j]}")

        # Вычисление базисных переменных из A_B * x_e_B = b - A_H * x_e_H
        A_H = A[:, j_h]
        x_H = x_e[j_h]
        b_adjusted = b - A_H.dot(x_H)
        print(f"\nРешаем уравнение:\n {A_B} * xe_B = b({b}) - \nA_H({A_H}) * xe_H{x_H} = {b_adjusted}")
        try:
            x_e_B = np.linalg.solve(A_B, b_adjusted)
        except np.linalg.LinAlgError:
            print("Матрица A_B вырожденная, невозможно решить для базисных переменных x_B.")
            return None
        x_e[j_b] = x_e_B
        print("\nПсевдоплан x_e (базисные переменные):")
        for idx, j in enumerate(j_b):
            print(f"x_e[{j + 1}] = {x_e[j]}")

        # Шаг 4: Проверка условий оптимальности
        print("\n# Шаг 4: Проверка условий оптимальности")
        optimal = True
        for j in j_b:
            d_j, d_star_j = bounds[j]
            print(f"Проверка границ для x[{j + 1}]: {d_j} <= {x_e[j]} <= {d_star_j}")
            if not (d_j <= x_e[j] <= d_star_j):
                print("План не оптимален.")
                optimal = False
                break
        if optimal:
            # Если план оптимален, выводим результат и завершаем
            print("\nНайдено оптимальное решение:")
            for i in range(num_vars):
                print(f"x[{i + 1}] = {x_e[i]}")
            print(f"\nЗначение целевой функции: {np.dot(c, x_e)}")
            print(f"Базисные переменные: {[j + 1 for j in j_b]}")
            return x_e, np.dot(c, x_e)

        # Шаг 5: Определение нарушающих базисных переменных j_*
        print("\n# Шаг 5.1: Определение нарушающих базисных переменных j_*")
        violating_variables = []
        for j in j_b:
            d_j, d_star_j = bounds[j]
            # Определяем переменные, нарушающие границы
            if x_e[j] < d_j:
                rho_j = x_e[j] - d_j
                violating_variables.append((rho_j, j))
            elif x_e[j] > d_star_j:
                rho_j = x_e[j] - d_star_j
                violating_variables.append((rho_j, j))
        if not violating_variables:
            print("Нет нарушающих базисных переменных, но оптимальность не достигнута.")
            return None

        # Выбираем j_* с минимальным значением rho_j
        rho_j_star, j_star = min(violating_variables, key=lambda item: item[0])
        print(f"Нарушающая переменная j_* = {j_star + 1}, rho_j = {rho_j_star}")

        print("\n# Шаг 5.2: Решение системы для p_u")
        e_j_star = np.zeros(len(j_b))
        idx_j_star_in_basis = j_b.index(j_star)
        d_j_star, d_star_j_star = bounds[j_star]
        # Определяем знак вектора e_j_star
        if x_e[j_star] < d_j_star:
            sign = 1  # Знак +1, если x_e[j_star] < d_j
            x_bar_j_star = d_j_star
        else:
            sign = -1  # Знак -1, если x_e[j_star] > d*_j
            x_bar_j_star = d_star_j_star
        e_j_star[idx_j_star_in_basis] = sign

        # Выводим уравнение, которое решается
        print(f"Решаем уравнение:\n {A_B_T} * p_u = {e_j_star}\n")

        # Решаем систему для нахождения направления p_u
        try:
            p_u = np.linalg.solve(A_B_T, e_j_star)
        except np.linalg.LinAlgError:
            print("Невозможно решить систему для p_u.")
            return None
        print("Направление p_u:", p_u)

        print("\n# Шаг 6: Вычисление компонентов вектора p_δ")
        l_b = {}
        for j in j_h:
            a_j = A[:, j]
            l_H_j = -np.dot(a_j, p_u)
            l_b[j] = l_H_j
            print(f"p_δ[{j + 1}] = -a_{j + 1}^T * p_u = {-a_j.T} * {p_u} = {l_H_j}")

        print("\n# Шаг 7.1: Вычисление значений σ_j")
        sigma = {}
        for j in j_h:
            if l_b[j] == 0 or l_b[j] * delta[j] >= 0:
                print(f"p_δ[{j + 1}] * δ_{j + 1} = {l_b[j]} * {delta[j]} >= 0")
                sigma_j = np.inf
            else:
                print(f"p_δ[{j + 1}] * δ_{j + 1} = {l_b[j]} * {delta[j]} < 0")
                sigma_j = -delta[j] / l_b[j]
            sigma[j] = sigma_j
            print(f"σ[{j + 1}] = {sigma[j]}")

        print("\n# Шаг 7.2: Нахождение минимального σ_j и выбор переменной j_0")
        sigma_values = [(sigma[j], j) for j in j_h if sigma[j] != np.inf]
        if not sigma_values:
            print("Задача не имеет решения (все значения σ_j = ∞).")
            return None
        sigma_1, j_1 = min(sigma_values, key=lambda item: item[0])
        print(f"Минимальное σ^1 = {sigma_1} для переменной j_0 = {j_1 + 1}")

        print("\n# Шаг 8: Обновление базиса")
        print(f"Обновление базиса: замена j_* = {j_star + 1} на j_0 = {j_1 + 1}")
        j_b.remove(j_star)
        j_b.append(j_1)
        j_b.sort()
        x = x_e.copy()
        iteration += 1

    print("Достигнуто максимальное количество итераций без нахождения оптимального решения.")
    return None


if __name__ == "__main__":
    # вариант 5
    A = [[3, 0, -1, 0, 1], [-2, 0, 0, 3, 0], [0, 4, 0, 0, 1]]
    b = [30, -11, 2]
    c = [5, 0, -4, 15, 5]

    # Нижние и верхние границы для переменных
    bounds = [(2, 10), (0, 4), (1, 5), (0, 4), (-1, 3)]

    # Начальный план
    j_b = [1, 2, 3]

    # A = [[0, 0, 0, 1, 2], [3, 0, 2, 0, -4], [0, -1, 3, 0, 0]]
    # b = [-4, 25, 6]
    # c = [6, 3, -2, -1, -14]
    # bounds = [(1, 4), (-1, 3), (-2, 2), (1, 4), (-3, 1)]
    # x = [10, 0.25, 1, 3, 1]
    # j_b = [2, 3, 5]

    # # Вариант 21
    # A = [[2, 1, 0, 0, 0], [0, 3, 1, 0, 0], [1, 0, 0, 2, 4]]
    # b = [12, 14, 30]
    # c = [8, 8, 1, 4, 7]
    # bounds = [(1, 5), (2, 4), (1, 3), (3, 6), (4, 7)]
    # j_b = [1, 2, 3]

    x, f = dual_simplex_method(A, b, c, bounds, j_b)

    # Используем библиотечный метод для проверки результатов
    c_min = [-i for i in c]
    true_max = linprog(c_min, A_eq=A, b_eq=b, bounds=bounds, method="highs")

    print(f"\n\nНайденное решение:\n{x}\nf: {f}")
    print(f"Решение через scipy:\n{true_max.x}\nf: {-true_max.fun}")

    if f == -true_max.fun:
        print("\033[92mРешения совпадают.\033[0m")
    else:
        print("\033[91mРешения не совпадают. Рекомендуется проверить решение.\033[0m")
