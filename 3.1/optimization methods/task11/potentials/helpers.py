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
