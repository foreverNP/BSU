import pulp

capacities = [
    [15, 13, 17, 20, 15],
    [17, 25, 14, 16, 19],
    [15, 16, 16, 15, 20],
    [18, 15, 17, 14, 18],
]

# 13, 25
# costs = [[10, 3, 8, 11, 2], [8, 7, 6, 10, 5], [11, 10, 12, 9, 10], [12, 14, 10, 14, 8]]

# supply = [20, 10, 14, 10]  # Ресурсы районов A1, A2, A3, A4
# demand = [6, 8, 20, 5, 15]  # Мощности элеваторов B1, B2, B3, B4, B5

# 19, 1
costs = [[10, 8, 5, 9, 16], [4, 3, 4, 11, 12], [5, 10, 29, 7, 6], [9, 2, 4, 1, 3]]

supply = [9, 8, 8, 12]  # Ресурсы районов A1, A2, A3, A4
demand = [6, 7, 9, 8, 7]  # Мощности элеваторов B1, B2, B3, B4, B5

# 1, 13
# costs = [[10, 10, 1, 2, 9], [10, 6, 1, 5, 3], [10, 8, 14, 5, 10], [9, 10, 2, 6, 10]]

# supply = [17, 8, 10, 9]  # Ресурсы районов A1, A2, A3, A4
# demand = [6, 15, 7, 8, 8]  # Мощности элеваторов B1, B2, B3, B4, B5


def build_first_phase_problem(supply, demand, capacities):
    m = len(supply)
    n = len(demand)
    total_supply = sum(supply)
    total_demand = sum(demand)

    # Проверка баланса поставок и спроса
    if total_supply != total_demand:
        print("Общий объем поставок и спроса не равен. Балансировка задачи.")

    # Скопировать capacities и добавить новую строку и столбец
    extended_capacities = [row.copy() for row in capacities]
    for row in extended_capacities:
        row.append(0)  # Добавить столбец для искусственных
    extended_capacities.append([0] * (n + 1))  # Добавить строку для искусственных

    # Инициализируем расширенную матрицу затрат
    extended_costs = []
    for i in range(m + 1):  # m+1 строк
        row = []
        for j in range(n + 1):  # n+1 столбцов
            if i < m and j < n:
                # Исходные клетки: c_{ij} = 0
                row.append(0)
            elif i == m and j == n:
                # Клетка (m+1, n+1): c_{m+1,n+1} = 0
                row.append(0)
            else:
                # Искусственные клетки: c_{ij} = 1
                row.append(1)
        extended_costs.append(row)

    # Начальные базисные переменные и их значения
    basic_variables = []
    flows = [
        [0 for _ in range(n + 1)] for _ in range(m + 1)
    ]  # Инициализируем матрицу распределения нулями

    # Переменные искусственного столбца x_{i,n+1}
    for i in range(m):
        basic_variables.append((i, n))
        flows[i][n] = supply[i]
        extended_capacities[i][n] = supply[i]
    # Переменные искусственной строки x_{m+1,j}
    for j in range(n):
        basic_variables.append((m, j))
        flows[m][j] = demand[j]
        extended_capacities[m][j] = demand[j]

    basic_variables.append((m, n))
    flows[m][n] = 0
    extended_capacities[m][n] = total_supply

    return extended_costs, basic_variables, flows, extended_capacities


def calculate_potentials(basis, costs):
    m = len(costs)
    n = len(costs[0])
    u = [None] * m
    v = [None] * n

    # Count basis cells in each row and column
    row_counts = [0] * m
    col_counts = [0] * n
    for i, j in basis:
        row_counts[i] += 1
        col_counts[j] += 1

    # Find the row or column with the highest count
    max_row = max(range(m), key=lambda x: row_counts[x], default=None)
    max_col = max(range(n), key=lambda x: col_counts[x], default=None)
    max_row_count = row_counts[max_row] if max_row is not None else -1
    max_col_count = col_counts[max_col] if max_col is not None else -1

    if max_row_count > max_col_count:
        u[max_row] = 0
        print(f"u_{max_row} = 0")
    elif max_col_count > max_row_count:
        v[max_col] = 0
        print(f"v_{max_col} = 0")
    else:
        # If counts are equal, prioritize the first row
        u[max_row] = 0
        print(f"u_{max_row} = 0")

    updated = True
    while updated:
        updated = False
        for i, j in basis:
            cij = costs[i][j]
            if u[i] is not None and v[j] is None:
                v[j] = -cij - u[i]
                print(f"v_{j} = -c_{i}{j} - u_{i} = {v[j]}")
                updated = True
            elif u[i] is None and v[j] is not None:
                u[i] = -cij - v[j]
                print(f"u_{i} = -c_{i}{j} - v_{j} = {u[i]}")
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
                print(f"Δ{i}{j} = -c_{i}{j} - u_{i} - v_{j} = {delta[i][j]}")
    return delta


def check_optimality_and_select_entering_cell(capacities, delta, allocation, basis):
    is_optimal = True
    max_delta = float("-inf")
    entering_cell = None
    for i in range(len(delta)):
        for j in range(len(delta[0])):
            if (i, j) not in basis:
                x_ij = allocation[i][j]
                delta_ij = delta[i][j]
                if (x_ij == 0 and delta_ij > 0) or (
                    x_ij == capacities[i][j] and delta_ij < 0
                ):
                    print(f"x{i}{j} = {x_ij}, Δ{i}{j} = {delta_ij} -")
                    is_optimal = False  # Нарушается условие оптимальности
                    if abs(delta_ij) > max_delta:
                        max_delta = abs(delta_ij)
                        entering_cell = (i, j)
                else:
                    print(f"x{i}{j} = {x_ij}, Δ{i}{j} = {delta_ij} +")
    return is_optimal, entering_cell


def find_cycle_and_calculate_theta(allocation, basis, entering_cell, capacities):
    m = len(allocation)
    n = len(allocation[0])

    # Временно добавляем входящую клетку в базис
    temp_basis = basis.copy()
    temp_basis.append(entering_cell)

    # Строим структуру для быстрого доступа к базисным клеткам по строкам и столбцам
    rows = [[] for _ in range(m)]
    cols = [[] for _ in range(n)]
    for i, j in temp_basis:
        rows[i].append(j)
        cols[j].append(i)

    # Функция для поиска цикла
    def find_cycle():
        # Используем DFS для поиска цикла
        stack = [(entering_cell, [entering_cell], set([entering_cell]), True)]
        while stack:
            (current_cell, path, visited, is_row) = stack.pop()
            i, j = current_cell
            if is_row:
                # Переходим по столбцам в той же строке
                for jj in rows[i]:
                    next_cell = (i, jj)
                    if next_cell == entering_cell and len(path) >= 4:
                        return path
                    if next_cell not in visited:
                        stack.append(
                            (
                                next_cell,
                                path + [next_cell],
                                visited | {next_cell},
                                not is_row,
                            )
                        )
            else:
                # Переходим по строкам в том же столбце
                for ii in cols[j]:
                    next_cell = (ii, j)
                    if next_cell == entering_cell and len(path) >= 4:
                        return path
                    if next_cell not in visited:
                        stack.append(
                            (
                                next_cell,
                                path + [next_cell],
                                visited | {next_cell},
                                not is_row,
                            )
                        )
        return None

    cycle = find_cycle()
    if not cycle:
        print("Цикл не найден.")
        return None, None, None, None, None

    # Назначаем знаки
    signs = []
    first, second = "-", "+"
    if allocation[entering_cell[0]][entering_cell[1]] == 0:
        first, second = "+", "-"
    for idx in range(len(cycle)):
        sign = first if idx % 2 == 0 else second
        signs.append(sign)

    # Вычисляем θ_ij для каждой клетки в цикле
    theta_values = []
    for idx, (i, j) in enumerate(cycle):
        sign = signs[idx]
        if sign == "+":
            theta_ij = capacities[i][j] - allocation[i][j]
        else:
            theta_ij = allocation[i][j]
        theta_values.append(theta_ij)

    # Находим минимальное θ^0
    theta_0 = min(theta_values)
    min_idx = theta_values.index(theta_0)
    istar_jstar = cycle[min_idx]

    return cycle, signs, theta_values, theta_0, istar_jstar


def update_allocation(allocation, cycle, signs, theta_0):
    for idx, (i, j) in enumerate(cycle):
        sign = signs[idx]
        if sign == "+":
            allocation[i][j] += theta_0
            print(f"x{i}{j} += {theta_0} = {allocation[i][j]}")
        else:
            allocation[i][j] -= theta_0
            print(f"x{i}{j} -= {theta_0} = {allocation[i][j]}")
    return allocation


def update_basis(basis, i0_j0, istar_jstar):
    print(f"U_B = {basis} - (i*, j*) = {istar_jstar} + (i0, j0) = {i0_j0}")
    if istar_jstar == i0_j0:
        return basis
    basis.remove(istar_jstar)
    basis.append(i0_j0)
    return basis


def solution(costs, supply, demand, capacities):
    regions = [f"A{i+1}" for i in range(len(supply))]
    elevators = [f"B{j+1}" for j in range(len(demand))]

    # Формирование задачи
    prob = pulp.LpProblem("Grain_Transportation", pulp.LpMinimize)

    # Переменные решения
    x = pulp.LpVariable.dicts("x", (regions, elevators), lowBound=0, cat="Continuous")

    # Целевая функция
    prob += pulp.lpSum(
        costs[i][j] * x[regions[i]][elevators[j]]
        for i in range(len(regions))
        for j in range(len(elevators))
    )

    # Ограничения на ресурсы (поставки)
    for i in range(len(regions)):
        prob += (
            pulp.lpSum(x[regions[i]][elevators[j]] for j in range(len(elevators)))
            == supply[i],
            f"Supply_{regions[i]}",
        )

    # Ограничения на мощности (спрос)
    for j in range(len(elevators)):
        prob += (
            pulp.lpSum(x[regions[i]][elevators[j]] for i in range(len(regions)))
            == demand[j],
            f"Demand_{elevators[j]}",
        )

    # Ограничения на мощности элеваторов
    for i in range(len(regions)):
        for j in range(len(elevators)):
            prob += (
                x[regions[i]][elevators[j]] <= capacities[i][j],
                f"Capacity_{regions[i]}_{elevators[j]}",
            )

    prob.solve(pulp.PULP_CBC_CMD(msg=False))

    print("Статус:", pulp.LpStatus[prob.status])
    print("Точный оптимальный план перевозки:")
    for i in regions:
        for j in elevators:
            if x[i][j].varValue > 0:
                print(f"Перевозить {x[i][j].varValue} единиц из {i} в {j}")
    print(f"Общая стоимость: {pulp.value(prob.objective)}")


def optimize_transportation_plan(costs, capacities, basic_flows, basis_cells):
    iteration = 0
    while True:
        iteration += 1
        print(f"\nИтерация {iteration}")

        print("\nТекущий план распределения:")
        for row in basic_flows:
            print(row)
        print("Базис:", basis_cells)

        # Шаг 1: Расчет потенциалов
        print("Шаг 1: Расчет потенциалов")
        u, v = calculate_potentials(basis_cells, costs)
        print("Потенциалы u:", u)
        print("Потенциалы v:", v)

        # Шаг 2: Подсчет оценок
        print("Шаг 2: Подсчет оценок")
        delta = calculate_reduced_costs(basis_cells, costs, u, v)
        print("Оценки delta:")
        for row in delta:
            print(row)

        # Шаг 3 и 4: Проверка оптимальности и выбор клетки для ввода в базис
        print("Шаг 3 и 4: Проверка оптимальности и выбор клетки для ввода в базис")
        is_optimal, entering_cell = check_optimality_and_select_entering_cell(
            capacities, delta, basic_flows, basis_cells
        )
        if is_optimal:
            print("Текущий план оптимален.")
            return basic_flows, basis_cells
        else:
            print("Текущий план не оптимален.")
            print("Клетка для ввода в базис:", entering_cell)

        # Шаг 5: Поиск цикла и вычисление θ^0
        print("Шаг 5: Поиск цикла и вычисление θ^0")
        cycle, signs, theta_values, theta_0, istar_jstar = (
            find_cycle_and_calculate_theta(
                basic_flows, basis_cells, entering_cell, capacities
            )
        )

        if cycle:
            print("Найденный цикл:")
            for idx, cell in enumerate(cycle):
                i, j = cell
                print(
                    f"Клетка ({i}, {j}), знак: {signs[idx]}, θ_ij: {theta_values[idx]}"
                )
            print(f"Минимальное θ^0: {theta_0}")
        else:
            print("Цикл не найден.")
            break

        # Шаг 6: Обновление плана распределения
        print("Шаг 6: Обновление плана распределения")
        basic_flows = update_allocation(basic_flows, cycle, signs, theta_0)

        print("Обновленный план распределения:")
        for row in basic_flows:
            print(row)

        # Шаг 7: Обновление базисного множества клеток
        print("Шаг 7: Обновление базисного множества клеток")
        basis_cells = update_basis(basis_cells, entering_cell, istar_jstar)

        print("Обновленный базис:", basis_cells)


# Строим задачу первой фазы
extended_costs, basic_variables, flows, capacities_ext = build_first_phase_problem(
    supply, demand, capacities
)


print("Первая фаза: построение задачи")
print("Расширенная матрица затрат:")
for row in extended_costs:
    print(row)
print("Базисные потоки:")
for row in flows:
    print(row)
print("Расширенные мощности:")
for row in capacities_ext:
    print(row)

# Решаем задачу первой фазы
basic_flows, basis_cells = optimize_transportation_plan(
    extended_costs, capacities_ext, flows, basic_variables
)

# Выводим оптимальный план перевозок
print("Оптимальный план перевозок для задачи первой фазы:")
for row in basic_flows:
    print(row)


# TODO: Проверить решение задачи второй фазы

# Решаем задачу второй фазы
flows, cells = optimize_transportation_plan(costs, capacities, basic_flows, basis_cells)

# Выводим оптимальный план перевозок
print("Оптимальный план перевозок:")
for row in flows:
    print(row)
solution(costs, supply, demand, capacities)
