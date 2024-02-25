import networkx as nx
import matplotlib.pyplot as plt

G = nx.DiGraph()

G.add_node(1, demand=-80)  # Источник
G.add_node(2, demand=-60)  # Источник
G.add_node(3, demand=40)  # Сток
G.add_node(4, demand=62)  # Сток
G.add_node(5, demand=60)  # Сток
G.add_node(6, demand=-22)  # Источник
G.add_node(7, demand=0)  # Промежуточный узел
G.add_node(8, demand=0)  # Промежуточный узел

# Добавляем ребра с пропускными способностями и затратами
edges = [
    (1, 2, {"capacity": 50, "weight": 2}),
    (1, 5, {"capacity": 60, "weight": 3}),
    (2, 3, {"capacity": 55, "weight": 5}),
    (2, 7, {"capacity": 50, "weight": 7}),
    (3, 4, {"capacity": 45, "weight": 4}),
    (4, 6, {"capacity": 18, "weight": 1}),
    (5, 6, {"capacity": 25, "weight": 10}),
    (5, 8, {"capacity": 15, "weight": 8}),
    (6, 8, {"capacity": 42, "weight": 7}),
    (7, 1, {"capacity": 10, "weight": 6}),
    (7, 3, {"capacity": 40, "weight": 5}),
    (7, 5, {"capacity": 16, "weight": 4}),
    (8, 3, {"capacity": 15, "weight": 3}),
    (8, 4, {"capacity": 27, "weight": 8}),
    (8, 7, {"capacity": 15, "weight": 2}),
]

G.add_edges_from(edges)

# Решаем задачу минимального стоимостного потока
flowCost, flowDict = nx.network_simplex(G)

print("Минимальная стоимость потока:", flowCost)
print("\nПотоки по ребрам:")
for u in flowDict:
    for v, flow in flowDict[u].items():
        if flow > 0:
            print(f"{u} -> {v}: {flow}")


# Попытка найти альтернативный поток с той же стоимостью.
# Метод: уменьшаем пропускную способность ребер, использованных в оригинальном потоке,
# и пытаемся найти другой поток той же стоимости.
def find_alternative_flow(G, original_flow, flow_cost):
    G_alt = G.copy()
    for u in original_flow:
        for v, flow in original_flow[u].items():
            if flow > 0:
                # Уменьшаем пропускную способность на текущий поток
                G_alt[u][v]["capacity"] -= flow
                if G_alt[u][v]["capacity"] < 0:
                    G_alt[u][v]["capacity"] = (
                        0  # Пропускная способность не может быть отрицательной
                    )

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
        "\nНайдено альтернативное решение с той же стоимостью. Оптимальный поток не является уникальным."
    )
else:
    print(
        "\nАльтернативных решений с той же стоимостью не найдено. Оптимальный поток уникален."
    )


def visualize_graph(G, flowDict):
    pos = {
        1: (0, 1),
        2: (1, 2),
        3: (2, 2),
        4: (3, 1),
        5: (1, 0),
        6: (2, 0),
        7: (1, 1),
        8: (2, 1),
    }

    plt.figure(figsize=(14, 10))

    # Определяем цвета узлов в зависимости от их роли
    node_colors = []
    for node, data in G.nodes(data=True):
        if data["demand"] < 0:
            node_colors.append("lightblue")  # Источники
        elif data["demand"] > 0:
            node_colors.append("lightgreen")  # Стоки
        else:
            node_colors.append("lightgray")  # Промежуточные

    # Рисуем узлы
    nx.draw_networkx_nodes(
        G, pos, node_size=800, node_color=node_colors, edgecolors="black"
    )

    # Рисуем ребра
    nx.draw_networkx_edges(
        G, pos, arrowstyle="->", arrowsize=20, edge_color="black", width=2
    )

    # Подписываем узлы
    labels = {
        node: f"{node}\n(d={data['demand']})" for node, data in G.nodes(data=True)
    }
    nx.draw_networkx_labels(G, pos, labels, font_size=10, font_weight="bold")

    # Подготавливаем метки ребер с информацией о capacity, weight и flow
    edge_labels = {}
    for u, v, data in G.edges(data=True):
        flow = flowDict[u][v]
        edge_labels[(u, v)] = f"c={data['capacity']}\nw={data['weight']}\nf={flow}"

    # Рисуем метки ребер
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=8)

    plt.title("Сетевой граф с потоками", fontsize=16)
    plt.axis("off")
    plt.tight_layout()
    plt.savefig("img/network_graph.png")


visualize_graph(G, flowDict)
