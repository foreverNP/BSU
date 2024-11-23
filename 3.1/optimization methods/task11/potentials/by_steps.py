import numpy as np

costs = [[10, 8, 5, 9, 16], [4, 3, 4, 11, 12], [5, 10, 29, 7, 6], [9, 2, 4, 1, 3]]

supply = [9, 8, 8, 12]  # Ресурсы районов A1, A2, A3, A4
demand = [6, 7, 9, 8, 7]  # Мощности элеваторов B1, B2, B3, B4, B5


def northwest_corner_method(supply, demand):
    print("Метод северо-западного угла")
    supply = supply.copy()  # Копируем списки, чтобы не изменить исходные данные
    demand = demand.copy()
    m = len(supply)
    n = len(demand)

    basis = []

    allocation = [
        [0 for _ in range(n)] for _ in range(m)
    ]  # Инициализируем матрицу распределения нулями

    i = 0  # Индекс текущего поставщика
    j = 0  # Индекс текущего потребителя

    while i < m and j < n:
        amount = min(
            supply[i], demand[j]
        )  # Определяем максимально возможное количество для поставки
        allocation[i][j] = amount  # Заполняем клетку
        basis.append((i, j))
        supply[i] -= amount  # Обновляем остаток у поставщика
        demand[j] -= amount  # Обновляем потребность у потребителя

        # Если требуется, можно раскомментировать следующую строку для вывода таблицы
        # print_table(allocation, supply, demand)

        if supply[i] == 0:
            i += 1  # Переходим к следующему поставщику
        elif demand[j] == 0:
            j += 1  # Переходим к следующему потребителю

    return allocation, basis


def calculate_potentials(basis, costs):
    m = len(costs)
    n = len(costs[0])
    u = [None] * m
    v = [None] * n

    # Устанавливаем один из потенциалов в 0, например, u[0]
    u[0] = 0

    updated = True
    while updated:
        updated = False
        for i, j in basis:
            cij = costs[i][j]
            if u[i] is not None and v[j] is None:
                v[j] = -cij - u[i]
                updated = True
            elif u[i] is None and v[j] is not None:
                u[i] = -cij - v[j]
                updated = True
    return u, v


def calculate_reduced_costs(basis, costs, u, v):
    m = len(costs)
    n = len(costs[0])
    delta = [[0 for _ in range(n)] for _ in range(m)]
    for i in range(m):
        for j in range(n):
            if (i, j) not in basis:
                delta[i][j] = -costs[i][j] - (u[i] + v[j])
    return delta


initial_allocation, basis = northwest_corner_method(supply, demand)

print("Начальный базисный план:")
for row in initial_allocation:
    print(row)
print("Базис:", basis)

# Шаг 1: Расчет потенциалов
u, v = calculate_potentials(basis, costs)
print("Потенциалы u:", u)
print("Потенциалы v:", v)

# Шаг 2: Подсчет оценок
delta = calculate_reduced_costs(basis, costs, u, v)
print("Оценки delta:")
for row in delta:
    print(row)
