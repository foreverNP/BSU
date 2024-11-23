def print_table(allocation, supply, demand):
    m = len(allocation)
    n = len(allocation[0])

    # Вывод строк таблицы
    for i in range(m):
        for j in range(n):
            print(f"{allocation[i][j]:<6}", end="")
        print(f"| {supply[i]:<6}")

    # Разделительная линия
    print("-" * (5 * (n + 2)))

    # Вывод остатка потребностей
    for d in demand:
        print(f"{d:<6}", end="")
    print("\n")


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
