import pulp

costs = [[10, 8, 5, 9, 16], [4, 3, 4, 11, 12], [5, 10, 29, 7, 6], [9, 2, 4, 1, 3]]

supply = [9, 8, 8, 12]  # Ресурсы районов A1, A2, A3, A4
demand = [6, 7, 9, 8, 7]  # Мощности элеваторов B1, B2, B3, B4, B5

# costs = [[10, 3, 8, 11, 2], [8, 7, 6, 10, 5], [11, 10, 12, 9, 10], [12, 14, 10, 14, 8]]

# supply = [20, 10, 14, 10]  # Ресурсы районов A1, A2, A3, A4
# demand = [6, 8, 20, 5, 15]  # Мощности элеваторов B1, B2, B3, B4, B5

capacities = [
    [15, 13, 17, 20, 15],
    [17, 25, 14, 16, 19],
    [15, 16, 16, 15, 20],
    [18, 15, 17, 14, 18],
]

# Индексы
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
print("Оптимальный план перевозки:")
for i in regions:
    for j in elevators:
        if x[i][j].varValue > 0:
            print(f"Перевозить {x[i][j].varValue} единиц из {i} в {j}")
print(f"Общая стоимость: {pulp.value(prob.objective)}")
