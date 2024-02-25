import sys

# Given data
c = 45
c_i = [20, 24, 5, 20, 9]
p_i = [4, 14, 2, 7, 3]
n = len(c_i)

# # Total possible value is the sum of all values
# total_value = sum(c_i)

# # Initialize dp array where dp[v] is the minimal total weight to achieve value v
# dp = [float("inf")] * (total_value + 1)
# dp[0] = 0

# # For backtracking to find the selected items
# prev = [-1] * (total_value + 1)

# for i in range(n):
#     value = c_i[i]
#     weight = p_i[i]
#     for v in range(total_value, value - 1, -1):
#         if dp[v - value] + weight < dp[v]:
#             dp[v] = dp[v - value] + weight
#             prev[v] = i

# # Find the minimal total weight for total value >= c
# min_weight = float("inf")
# min_value = -1
# for v in range(c, total_value + 1):
#     if dp[v] < min_weight:
#         min_weight = dp[v]
#         min_value = v

# if min_weight == float("inf"):
#     print("It's not possible to achieve the required total value.")
# else:
#     print(f"Minimum total weight to achieve value at least {c}: {min_weight}")

#     # Reconstruct the items selected
#     selected_items = []
#     v = min_value
#     while v > 0 and prev[v] != -1:
#         i = prev[v]
#         selected_items.append(i + 1)  # Item indices are 1-based
#         v -= c_i[i]

#     print("Selected item indices:", selected_items[::-1])
#     print("Selected items (value, weight):")
#     for idx in selected_items[::-1]:
#         print(f"Item {idx}: Value = {c_i[idx - 1]}, Weight = {p_i[idx - 1]}")


# def min_weight_knapsack(n, p, c, target_c):
#     """
#     Решает задачу о рюкзаке с минимальным весом.

#     Parameters:
#     n (int): Количество предметов.
#     p (list): Веса предметов.
#     c (list): Ценности предметов.
#     target_c (int): Минимальная общая ценность груза.

#     Returns:
#     min_weight (int): Минимальный общий вес груза.
#     selected_items (list): Индексы выбранных предметов.
#     """
#     # Создаем таблицу для динамического программирования
#     dp = [[float("inf")] * (target_c + 1) for _ in range(n + 1)]
#     dp[0][0] = 0

#     # Инициализируем список для хранения выбранных предметов
#     selected_items = []

#     # Итерируем по предметам
#     for i in range(1, n + 1):
#         # Итерируем по возможным ценностям груза
#         for j in range(target_c + 1):
#             # Если текущая ценность меньше ценности предмета, пропускаем
#             if j < c[i - 1]:
#                 dp[i][j] = dp[i - 1][j]
#             else:
#                 # Выбираем минимальный вес между двумя вариантами:
#                 # 1. Не включаем текущий предмет
#                 # 2. Включаем текущий предмет
#                 dp[i][j] = min(dp[i - 1][j], dp[i - 1][j - c[i - 1]] + p[i - 1])

#     # Находим минимальный вес и соответствующие выбранные предметы
#     min_weight = dp[n][target_c]
#     i, j = n, target_c
#     while i > 0 and j > 0:
#         if dp[i][j] != dp[i - 1][j]:
#             selected_items.append(i - 1)
#             j -= c[i - 1]
#         i -= 1

#     return min_weight, selected_items[::-1]


# # Пример использования

# target_c = 45
# c = [20, 24, 5, 20, 9]
# p = [4, 14, 2, 7, 3]
# n = len(c_i)

# min_weight, selected_items = min_weight_knapsack(n, p, c, target_c)
# print("Минимальный вес:", min_weight)
# print("Выбранные предметы:", selected_items)
import math


class Node:
    def __init__(self, level, weight, value, items):
        self.level = level
        self.weight = weight
        self.value = value
        self.items = items


def bound(node, n, p, c, target_c):
    """
    Оценка нижней границы веса для текущего узла.
    """
    if node.value >= target_c:
        return node.weight
    else:
        remaining_value = target_c - node.value
        remaining_weight = 0
        for i in range(node.level, n):
            if c[i] <= remaining_value:
                remaining_weight += p[i]
                remaining_value -= c[i]
            else:
                remaining_weight += (remaining_value / c[i]) * p[i]
                break
        return node.weight + remaining_weight


def branch_and_bound(n, p, c, target_c):
    """
    Решает задачу о рюкзаке с минимальным весом методом ветвей и границ.
    """
    print("Шаг 1: Создание корневого узла")
    root = Node(0, 0, 0, [])
    print(
        f"Узел {root.level}: вес = {root.weight}, ценность = {root.value}, предметы = {root.items}"
    )

    # Инициализируем минимальный вес и список выбранных предметов
    min_weight = float("inf")
    selected_items = []

    # Создаем стек для хранения узлов
    stack = [root]

    step = 2
    while stack:
        # Извлекаем верхний узел из стека
        node = stack.pop()

        # Если текущий узел соответствует условию, обновляем минимальный вес и список выбранных предметов
        if node.value >= target_c and node.weight < min_weight:
            min_weight = node.weight
            selected_items = node.items
            print(
                f"Шаг {step}: Найдено решение с минимальным весом {min_weight} и предметами {selected_items}"
            )
            step += 1

        # Если текущий узел имеет уровень меньше n, создаем два новых узла: с включением и без включения текущего предмета
        if node.level < n:
            # Создаем узел с включением текущего предмета
            new_node = Node(
                node.level + 1,
                node.weight + p[node.level],
                node.value + c[node.level],
                node.items + [node.level],
            )
            print(
                f"Шаг {step}: Создание узла с включением предмета {node.level} - вес = {new_node.weight}, ценность = {new_node.value}, предметы = {new_node.items}"
            )
            if bound(new_node, n, p, c, target_c) < min_weight:
                stack.append(new_node)
                step += 1
            else:
                print(
                    f"Шаг {step}: Узел с включением предмета {node.level} не является перспективным"
                )
                step += 1

            # Создаем узел без включения текущего предмета
            new_node = Node(node.level + 1, node.weight, node.value, node.items)
            print(
                f"Шаг {step}: Создание узла без включения предмета {node.level} - вес = {new_node.weight}, ценность = {new_node.value}, предметы = {new_node.items}"
            )
            if bound(new_node, n, p, c, target_c) < min_weight:
                stack.append(new_node)
                step += 1
            else:
                print(
                    f"Шаг {step}: Узел без включения предмета {node.level} не является перспективным"
                )
                step += 1

    print(f"Решение: минимальный вес = {min_weight}, предметы = {selected_items}")


# Пример использования
target_c = 45
c = [20, 24, 5, 20, 9]
p = [4, 14, 2, 7, 3]
n = len(c)

branch_and_bound(n, p, c, target_c)
