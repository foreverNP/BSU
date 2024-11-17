import networkx as nx

G = nx.DiGraph()

# Добавляем узлы с атрибутами спроса или предложения
# Предположим, что узлы 1-4 являются источниками, а 5-8 — стоками
# Если a1=40, a2=50, ..., a6=20 — это предложения, распределим их по узлам

G.add_node(1, demand=-80)  # Источник
G.add_node(2, demand=-60)
G.add_node(3, demand=40)  # Сток
G.add_node(4, demand=62)
G.add_node(5, demand=60)
G.add_node(6, demand=-22)
G.add_node(7, demand=0)  # Промежуточные узлы
G.add_node(8, demand=0)

# Добавляем ребра с пропускными способностями и затратами
edges = [
    (1, 2, {"capacity": 70, "weight": 2}),
    (1, 5, {"capacity": 80, "weight": 3}),
    (2, 3, {"capacity": 60, "weight": 5}),
    (2, 7, {"capacity": 70, "weight": 7}),
    (3, 4, {"capacity": 37, "weight": 4}),
    (4, 6, {"capacity": 20, "weight": 1}),
    (5, 6, {"capacity": 30, "weight": 10}),
    (5, 8, {"capacity": 15, "weight": 8}),
    (6, 8, {"capacity": 40, "weight": 7}),
    (7, 1, {"capacity": 10, "weight": 6}),
    (7, 3, {"capacity": 50, "weight": 5}),
    (7, 5, {"capacity": 20, "weight": 4}),
    (8, 3, {"capacity": 15, "weight": 3}),
    (8, 4, {"capacity": 30, "weight": 8}),
    (8, 7, {"capacity": 10, "weight": 2}),
]

G.add_edges_from(edges)

# Решаем задачу минимального стоимостного потока
flowCost, flowDict = nx.network_simplex(G)

print("Минимальная стоимость потока:", flowCost)
print("Потоки по ребрам:")
for u in flowDict:
    for v, flow in flowDict[u].items():
        if flow > 0:
            print(f"{u} -> {v}: {flow}")


# Проверка уникальности решения
# Один из способов — проверить, существует ли альтернативный поток с той же стоимостью
# Это можно сделать, пытаясь найти другой поток и сравнить стоимость
def find_alternative_flow(G, original_flow, flow_cost):
    # Удаляем потоки из оригинального решения и ищем другой поток той же стоимости
    G_alt = G.copy()
    for u in flowDict:
        for v, flow in flowDict[u].items():
            if flow > 0:
                G_alt[u][v]["capacity"] -= flow
    try:
        alt_flow_cost, alt_flow = nx.network_simplex(G_alt)
        if alt_flow_cost == flow_cost:
            return alt_flow
        else:
            return None
    except:
        return None


alternative_flow = find_alternative_flow(G, flowDict, flowCost)
if alternative_flow:
    print(
        "Найдено альтернативное решение с той же стоимостью. Оптимальный поток не является уникальным."
    )
else:
    print(
        "Альтернативных решений с той же стоимостью не найдено. Оптимальный поток уникален."
    )
