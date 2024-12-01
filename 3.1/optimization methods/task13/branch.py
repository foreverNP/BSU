from rich import print
from rich.tree import Tree


def lp_relaxation(items, c, fixed_vars, tree: Tree, branch_label: str):
    subtree = tree.add(branch_label + " LP-расслабление")
    total_weight = 0.0
    total_value = 0.0
    solution = {}
    remaining_items = []

    # Обработка фиксированных переменных
    processing = subtree.add("Обработка фиксированных переменных:")
    for item in items:
        i = item["index"]
        if i in fixed_vars:
            x_i = fixed_vars[i]
            solution[i] = x_i
            total_weight += x_i * item["weight"]
            total_value += x_i * item["value"]
            processing.add(f"Предмет {i+1}: x_{i+1} = {x_i} (фиксировано)")
        else:
            solution[i] = None  # Еще не назначено
            remaining_items.append(item)

    subtree.add(f"Текущий общий вес: {total_weight}")
    subtree.add(f"Текущая общая ценность: {total_value}")

    if total_value >= c:
        subtree.add(f"Текущая ценность >= {c}. Установка оставшихся переменных в 0.")
        for item in remaining_items:
            i = item["index"]
            solution[i] = 0.0
            subtree.add(f"Предмет {i+1}: x_{i+1} = 0.0")
        return total_weight, solution, True

    remaining_value = c - total_value
    max_possible_value = sum(item["value"] for item in remaining_items)
    subtree.add(f"Оставшаяся необходимая ценность: {remaining_value}")
    subtree.add(
        f"Максимально возможная ценность из оставшихся предметов: {max_possible_value}"
    )

    if remaining_value > max_possible_value:
        subtree.add(f"Не выполнимо: {remaining_value} > {max_possible_value}")
        return None, None, False

    # Сортировка
    remaining_items.sort(key=lambda item: item["ratio"])
    sorting = subtree.add(
        "Сортировка оставшихся предметов по возрастанию отношения веса к ценности:"
    )
    for item in remaining_items:
        sorting.add(f"Предмет {item['index']+1}: отношение = {item['ratio']:.4f}")

    for item in remaining_items:
        i = item["index"]
        if remaining_value <= 0:
            solution[i] = 0.0
            subtree.add(f"Предмет {i+1}: x_{i+1} = 0.0 (не нужен)")
            continue
        if item["value"] <= remaining_value:
            # Взять весь элемент
            x_i = 1.0
            solution[i] = x_i
            total_weight += x_i * item["weight"]
            total_value += x_i * item["value"]
            remaining_value -= item["value"]
            subtree.add(f"Предмет {i+1}: x_{i+1} = 1.0 (взято полностью)")
            subtree.add(f"Обновленный общий вес: {total_weight}")
            subtree.add(f"Обновленная общая ценность: {total_value}")
            subtree.add(f"Оставшаяся ценность: {remaining_value}")
        else:
            # Взять дробь
            x_i = remaining_value / item["value"]
            solution[i] = x_i
            total_weight += x_i * item["weight"]
            total_value += x_i * item["value"]
            subtree.add(f"Предмет {i+1}: x_{i+1} = {x_i:.4f} (дробная часть)")
            subtree.add(f"Обновленный общий вес: {total_weight}")
            subtree.add(f"Обновленная общая ценность: {total_value}")
            remaining_value = 0.0
            break  # Достигли c

    if total_value >= c:
        subtree.add(f"LP-расслабление выполнимо: {total_value} >= {c}")
        for item in remaining_items:
            i = item["index"]
            if solution[i] is None:
                solution[i] = 0.0
                subtree.add(f"Предмет {i+1}: x_{i+1} = 0.0 (не нужен)")
        return total_weight, solution, True
    else:
        subtree.add(f"LP-расслабление не выполнимо: {total_value} < {c}")
        return None, None, False


def branch_and_bound(
    items, c, fixed_vars, best_weight, best_solution, tree: Tree, branch_label: str
):
    subtree = tree.add(branch_label + f" Ветвление. Уровень: {len(fixed_vars)}")
    subtree.add(f"Текущие фиксированные переменные: {fixed_vars}")

    total_weight, solution, feasible = lp_relaxation(
        items, c, fixed_vars, subtree, branch_label
    )

    if not feasible:
        subtree.add("Отсечение ветки: решение не выполнимо.")
        return best_weight, best_solution  # Отсечь

    current_value = sum(item["value"] * solution[item["index"]] for item in items)
    subtree.add(
        f"Получено решение LP-расслабления: общий вес = {total_weight}, ценность = {current_value}"
    )

    if total_weight >= best_weight:
        subtree.add(
            f"Отсечение ветки: общий вес {total_weight} >= текущего лучшего {best_weight}."
        )
        return best_weight, best_solution  # Отсечь

    # Проверка целостности
    is_integral = all(x_i == 0.0 or x_i == 1.0 for x_i in solution.values())
    if is_integral:
        subtree.add("Найдено целочисленное решение.")
        if total_weight < best_weight:
            subtree.add(
                f"Обновление лучшего решения: общий вес {total_weight} < {best_weight}"
            )
            best_weight = total_weight
            best_solution = solution.copy()
        else:
            subtree.add("Текущее решение не лучше лучшего.")
        return best_weight, best_solution
    else:
        subtree.add("Решение не целочисленное. Необходимо ветвиться.")
        # Выбор дробной переменной
        fractional_vars = [i for i in solution if 0.0 < solution[i] < 1.0]
        if not fractional_vars:
            subtree.add("Нет дробных переменных для ветвления.")
            return best_weight, best_solution
        i = min(fractional_vars)
        subtree.add(
            f"Выбрана дробная переменная: x_{i+1} = {solution[i]} для ветвления."
        )

        # Ветвление на x_i = 0
        branch0 = subtree.add(f"Ветка 1: x_{i+1} = 0")
        new_fixed_vars_0 = fixed_vars.copy()
        new_fixed_vars_0[i] = 0.0
        best_weight, best_solution = branch_and_bound(
            items, c, new_fixed_vars_0, best_weight, best_solution, tree, "Ветка 1"
        )

        # Ветвление на x_i = 1
        branch1 = subtree.add(f"Ветка 2: x_{i+1} = 1")
        new_fixed_vars_1 = fixed_vars.copy()
        new_fixed_vars_1[i] = 1.0
        best_weight, best_solution = branch_and_bound(
            items, c, new_fixed_vars_1, best_weight, best_solution, tree, "Ветка 2"
        )

        return best_weight, best_solution


# Вариант 1
pi_ci = [
    (4, 20),  # Предмет 1
    (14, 24),  # Предмет 2
    (2, 5),  # Предмет 3
    (7, 20),  # Предмет 4
    (3, 9),  # Предмет 5
]
c = 45

items = []
for idx, (weight, value) in enumerate(pi_ci):
    items.append(
        {"index": idx, "weight": weight, "value": value, "ratio": weight / value}
    )

best_weight = float("inf")
best_solution = {}

fixed_vars = {}

tree = Tree("Корень")

best_weight, best_solution = branch_and_bound(
    items, c, fixed_vars, best_weight, best_solution, tree, "Ветка 1"
)

print("\nАлгоритм завершен.")
print(tree)

if best_solution:
    print("\n[bold green]Оптимальный общий вес:[/bold green]", best_weight)
    print("[bold green]Выбор предметов:[/bold green]")
    for item in items:
        i = item["index"]
        print(f"Предмет {i+1}: x_{i+1} = {best_solution[i]}")
else:
    print("Не найдено выполнимое решение.")
