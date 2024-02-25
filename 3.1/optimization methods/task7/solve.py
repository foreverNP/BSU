from scipy.optimize import linprog
import numpy as np

# Коэффициенты целевой функции
c = [-5, 0, 4, -15, -5]  # Минус для максимизации

# Матрица ограничений
A_eq = [
    [3, 0, -1, 0, 1],
    [-2, 0, 0, 3, 0],
    [0, 4, 0, 0, 1]
]

# Вектор ресурсов
b_eq = [30, -11, 2]

# Нижние и верхние ограничения на переменные
bounds = [(2, 10), (0, 4), (1, 5), (0, 4), (-1, 3)]

# Первая фаза: добавим искусственные переменные и решим с целью минимизации их суммы
# Построим задачу с учетом искусственных переменных
A_eq_phase1 = np.hstack((A_eq, np.identity(len(A_eq))))
c_phase1 = [0, 0, 0, 0, 0, 1, 1, 1]  # Коэффициенты для минимизации искусственных переменных

# Выполняем первую фазу симплекс-метода
result_phase1 = linprog(c_phase1, A_eq=A_eq_phase1, b_eq=b_eq, bounds=bounds + [(0, None)] * len(b_eq), method='highs')
result_phase1
print(result_phase1)


# Переходим ко второй фазе с исходной целью на максимум
# Удаляем искусственные переменные и используем исходную целевую функцию
result_phase2 = linprog(c, A_eq=A_eq, b_eq=b_eq, bounds=bounds, method='highs')
result_phase2
print(result_phase2)
