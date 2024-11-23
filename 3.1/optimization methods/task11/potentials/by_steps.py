import numpy as np
from helpers import print_table


costs = [[10, 8, 5, 9, 16], [4, 3, 4, 11, 12], [5, 10, 29, 7, 6], [9, 2, 4, 1, 3]]

supply = [9, 8, 8, 12]  # Ресурсы районов A1, A2, A3, A4
demand = [6, 7, 9, 8, 7]  # Мощности элеваторов B1, B2, B3, B4, B5


def northwest_corner_method(supply, demand):
    print("Метод северо-западного угла")
    supply = supply.copy()  # Копируем списки, чтобы не изменить исходные данные
    demand = demand.copy()
    m = len(supply)
    n = len(demand)

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
        supply[i] -= amount  # Обновляем остаток у поставщика
        demand[j] -= amount  # Обновляем потребность у потребителя

        # Вывод таблицы на текущей итерации
        print_table(allocation, supply, demand)

        if supply[i] == 0:
            i += 1  # Переходим к следующему поставщику
        elif demand[j] == 0:
            j += 1  # Переходим к следующему потребителю

    return allocation


# def least_cost_method(supply, demand, costs):
#     allocation = np.zeros((len(supply), len(demand)))

#     # Создаем копии, чтобы не изменять исходные списки
#     supply_copy = supply.copy()
#     demand_copy = demand.copy()

#     # Создаем маски для отмеченных строк и столбцов
#     supply_mask = np.zeros(len(supply_copy), dtype=bool)
#     demand_mask = np.zeros(len(demand_copy), dtype=bool)

#     while np.any(supply_mask == False) and np.any(demand_mask == False):
#         # Находим минимальную стоимость среди доступных клеток
#         min_cost = np.inf
#         min_cost_cell = (0, 0)
#         for i in range(len(supply_copy)):
#             if supply_mask[i]:
#                 continue
#             for j in range(len(demand_copy)):
#                 if demand_mask[j]:
#                     continue
#                 if costs[i][j] < min_cost:
#                     min_cost = costs[i][j]
#                     min_cost_cell = (i, j)

#         i, j = min_cost_cell
#         alloc = min(supply_copy[i], demand_copy[j])
#         allocation[i, j] = alloc

#         supply_copy[i] -= alloc
#         demand_copy[j] -= alloc
#         print_table(allocation, supply_copy, demand_copy)

#         if supply_copy[i] == 0:
#             supply_mask[i] = True
#         if demand_copy[j] == 0:
#             demand_mask[j] = True

#     return allocation


initial_allocation = northwest_corner_method(supply, demand)

print("Начальный базисный план:")
for row in initial_allocation:
    print(row)
