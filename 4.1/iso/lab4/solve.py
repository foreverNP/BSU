import numpy as np
import pandas as pd


def unbounded_knapsack(weights, values, W):
    n = len(weights)

    f = np.zeros((n + 1, W + 1), dtype=int)
    p = np.zeros((n + 1, W + 1), dtype=int)

    # алгоритм KNAPSACK
    for i in range(1, n + 1):
        for w in range(1, W + 1):
            # не берем i
            f[i][w] = f[i - 1][w]
            p[i][w] = 0

            # проверяем, можем ли взять i
            if weights[i - 1] <= w:
                # берем i
                value_with_item = values[i - 1] + f[i][w - weights[i - 1]]
                if value_with_item > f[i][w]:
                    f[i][w] = value_with_item
                    p[i][w] = 1

    optimal_value = f[n][W]

    # обратный ход
    solution = [0] * n
    w = W

    print("\n" + "=" * 60)
    print("ОБРАТНЫЙ ход:")
    print("=" * 60)
    print(f"Стартуем с x = {solution}, остаток объема = {w}")
    print()

    for i in range(n, 0, -1):
        while w >= weights[i - 1] and p[i][w] == 1:
            print(f"p_{i}({w}) = {p[i][w]} ⇒ x_{i} = x_{i} + 1")
            solution[i - 1] += 1
            w -= weights[i - 1]
            print(f"Объем изменился, осталось {w} единиц.")
            print(f"Текущее решение: x = {solution}")
            print()

        if p[i][w] == 0 and i > 1:
            print(f"p_{i}({w}) = {p[i][w]} ⇒ x_{i} не изменяется")
            print(f"Объем не изменился, осталось {w} единиц.")
            print()

    print("Стоп. Обратный ход завершен.")
    print("=" * 60)

    return f, p, optimal_value, solution


def print_table(f, p, weights, n, W):
    print("\n" + "=" * 60)
    print("ТАБЛИЦА РЕШЕНИЯ:")
    print("=" * 60)

    columns = ["w"]
    for i in range(1, n + 1):
        columns.extend([f"f_{i}", f"p_{i}"])

    data = []
    for w in range(W + 1):
        row = [w]
        for i in range(1, n + 1):
            row.extend([f[i][w], p[i][w]])
        data.append(row)

    df = pd.DataFrame(data, columns=columns)
    print(df.to_string(index=False))
    print("=" * 60)


# вариант 21
weights = [3, 4, 5]
values = [5, 8, 15]
W = 10

print("=" * 60)
print("ЗАДАЧА О НЕОГРАНИЧЕННОМ РЮКЗАКЕ")
print("=" * 60)
print(f"\nЦелевая функция: max {values[0]}*x1 + {values[1]}*x2 + {values[2]}*x3")
print(f"Ограничение: {weights[0]}*x1 + {weights[1]}*x2 + {weights[2]}*x3 <= {W}")
print("x1, x2, x3 >= 0 — целые")

f, p, optimal_value, solution = unbounded_knapsack(weights, values, W)

print_table(f, p, weights, len(weights), W)

print(f"\nОптимальное значение целевой функции: OPT = f_{len(weights)}({W}) = {optimal_value}")
print(f"Оптимальное решение: ", end="")
for i, x in enumerate(solution, 1):
    print(f"x_{i} = {x}", end="")
    if i < len(solution):
        print(", ", end="")
print()

total_weight = sum(weights[i] * solution[i] for i in range(len(weights)))
total_value = sum(values[i] * solution[i] for i in range(len(weights)))

print(f"\nПроверка:")
print(f"Общий вес: {total_weight} <= {W} ✓")
print(f"Общая стоимость: {total_value}")
