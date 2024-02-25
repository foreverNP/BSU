import networkx as nx
import numpy as np
import matplotlib.pyplot as plt
import time
import os

# Шаг 0: Инициализация исходного графа
G = nx.DiGraph()


# # Требования узлов (ai)
# G.add_node(1, demand=-70)  # Источник
# G.add_node(2, demand=-51)  # Источник
# G.add_node(3, demand=40)  # Сток
# G.add_node(4, demand=70)  # Сток
# G.add_node(5, demand=40)  # Сток
# G.add_node(6, demand=-29)  # Источник
# G.add_node(7, demand=0)  # Промежуточный узел
# G.add_node(8, demand=0)  # Промежуточный узел

# # Дуги с емкостями (dij) и стоимостью (cij)
# edges = [
#     (1, 2, {"capacity": 41, "weight": 7}),
#     (1, 5, {"capacity": 45, "weight": 9}),
#     (2, 3, {"capacity": 40, "weight": 10}),
#     (2, 7, {"capacity": 40, "weight": 12}),
#     (3, 4, {"capacity": 40, "weight": 14}),
#     (4, 6, {"capacity": 20, "weight": 12}),
#     (5, 6, {"capacity": 20, "weight": 10}),
#     (5, 8, {"capacity": 20, "weight": 6}),
#     (6, 8, {"capacity": 50, "weight": 8}),
#     (7, 1, {"capacity": 20, "weight": 3}),
#     (7, 3, {"capacity": 40, "weight": 15}),
#     (7, 5, {"capacity": 25, "weight": 7}),
#     (8, 3, {"capacity": 25, "weight": 14}),
#     (8, 4, {"capacity": 50, "weight": 9}),
#     (8, 7, {"capacity": 15, "weight": 20}),
# ]


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


demands = {node: data["demand"] for node, data in G.nodes(data=True)}
capacities = {(u, v): data["capacity"] for u, v, data in G.edges(data=True)}
costs = {(u, v): data["weight"] for u, v, data in G.edges(data=True)}
flows = {(u, v): 0 for u, v in G.edges()}


# Функция для вычисления потенциалов узлов
def compute_potentials(G, costs, U_B):
    potentials = {}
    nodes = list(G.nodes())
    potentials[nodes[0]] = 0  # Фиксируем потенциал первого узла

    equations = []
    for u, v in U_B:
        equations.append((u, v, -costs[(u, v)]))
        print(f"u_{u} - u_{v} = {-costs[(u, v)]}")

    while len(potentials) < len(nodes):
        progress = False
        for u, v, value in equations.copy():
            if u in potentials and v not in potentials:
                potentials[v] = potentials[u] - value
                equations.remove((u, v, value))
                progress = True
            elif v in potentials and u not in potentials:
                potentials[u] = potentials[v] + value
                equations.remove((u, v, value))
                progress = True
        if not progress:
            # Если не произошло прогресса, есть несвязные компоненты или ошибка
            print("Ошибка: не удалось вычислить потенциалы.")
            break
    return potentials


def find_cycle(U_B, entering_arc, flowDict, capacities):
    B_graph = nx.Graph()
    B_graph.add_edges_from(U_B)

    try:
        # Находим все циклы в графе
        cycles = list(nx.cycle_basis(B_graph))

        # Идентифицируем цикл, содержащий входящую дугу
        cycle = None
        for c in cycles:
            if entering_arc[0] in c and entering_arc[1] in c:
                cycle = c
                break

        if not cycle:
            print("Цикл не найден.")
            return []

        print(f"Найден цикл: {cycle}")

        # Определяем направление обхода на основе потока
        x = flowDict.get(entering_arc, 0)
        d = capacities.get(entering_arc, 0)
        entry = (entering_arc[0], entering_arc[1])
        if x == 0:
            print(
                f"x{entering_arc[0]}{entering_arc[1]} = 0, обход в направлении {entering_arc[0]}->{entering_arc[1]}"
            )
        elif x == d:
            print(
                f"x{entering_arc[0]}{entering_arc[1]} = d{entering_arc[0]}{entering_arc[1]}, обход в направлении {entering_arc[1]}->{entering_arc[0]}"
            )
            entry = (entering_arc[1], entering_arc[0])

        # Реконструируем цикл как список дуг
        try:
            start_index = cycle.index(entry[0])
            rotated_cycle = cycle[start_index:] + cycle[:start_index]
            cycle_edges = []
            for i in range(len(rotated_cycle)):
                from_node = rotated_cycle[i]
                to_node = rotated_cycle[(i + 1) % len(rotated_cycle)]
                if (from_node, to_node) == entry or (to_node, from_node) == entry:
                    cycle_edges.append(entry)
                else:
                    cycle_edges.append((from_node, to_node))

            # Проверяем наличие дуг в U_B
            edge_mapping = {}
            for edge in cycle_edges:
                if edge in U_B:
                    edge_mapping[edge] = 1
                elif (edge[1], edge[0]) in U_B:
                    edge_mapping[(edge[1], edge[0])] = -1

            return edge_mapping
        except ValueError:
            print(f"Ошибка: {entry} не найден в цикле.")
            return []

    except nx.NetworkXNoCycle:
        print("Цикл не найден.")
        return []


# Функция для вычисления θ^0 и идентификации выходящей дуги
def compute_theta(capacities, flows, cycle):
    theta_values = []

    for edge, direction in cycle.items():
        if direction == 1:
            theta = capacities.get(edge, 0) - flows.get(edge, 0)
            print(
                f"{edge} = U+, θ = d_ij - x_ij = {capacities.get(edge, 0)} - {flows.get(edge, 0)} = {theta}"
            )
        elif direction == -1:
            theta = flows.get(edge, 0)
            print(f"{edge} = U-, θ = x_ij = {flows.get(edge, 0)}")
        else:
            print(f"Ошибка: Направление дуги {edge} не определено.")
            return
        theta_values.append((theta, edge))

    if not theta_values:
        print("Нет дуг в цикле для вычисления θ^0.")
        return None, None

    min_theta = min(theta_values, key=lambda x: x[0])[0]
    min_edges = [edge for theta, edge in theta_values if theta == min_theta]
    min_edge = min_edges[0]

    for edge in min_edges:
        if edge[0] == 9 or edge[1] == 9:
            min_edge = edge
            break

    print(f"θ^0 = {min_theta}, дуга: {min_edge}")

    return min_theta, min_edge


# Основная функция метода потенциалов
def potential_method(G, capacities, costs, flows, demands, U_B, U_N):
    prefix = f"img/{int(time.time())}"
    if not os.path.exists(prefix):
        os.makedirs(prefix)

    iteration = 0
    max_iterations = 10
    while True and iteration < max_iterations:
        iteration += 1

        visualize_graph(
            G,
            flows,
            baseline_edges=U_B,
            name=f"{prefix}/iteration_{iteration}.png",
        )

        print(f"\nИтерация {iteration}")

        print("Шаг 1: Вычисление потенциалов узлов")
        potentials = compute_potentials(G, costs, U_B)
        print("Потенциалы узлов:")
        for potential in potentials.items():
            print(f"u_{potential[0]}: {potential[1]}")

        print("Шаг 2: Вычисление оценок для небазисных дуг")
        reduced_costs = {}
        for u, v in U_N:
            if u in potentials and v in potentials:
                delta = -costs[(u, v)] - (potentials[u] - potentials[v])
                print(
                    f"Δ({u}, {v}) = -c_{u}{v} - (u_{u} - u_{v}) = {-costs[(u, v)]} - ({potentials[u]} - {potentials[v]}) = {delta}"
                )
                reduced_costs[(u, v)] = delta
            else:
                # Если потенциал одного из узлов не определен, пропускаем дугу
                continue

        print("Шаг 3: Проверка условий оптимальности")
        io_jo = None
        violating_arcs = []
        for u, v in U_N:
            if u in potentials and v in potentials:
                delta = reduced_costs.get((u, v), 0)
                if flows.get((u, v), 0) == 0 and delta > 0:
                    violating_arcs.append(((u, v), abs(delta)))
                    print(f"Δ = {delta}, x_{u}{v} = {flows.get((u, v), 0)} -")
                elif flows.get((u, v), 0) == capacities.get((u, v), 0) and delta < 0:
                    violating_arcs.append(((u, v), abs(delta)))
                    print(
                        f"Δ = {delta}, x_{u}{v} = {flows.get((u, v), 0)} != d_{u}{v} = {capacities.get((u, v), 0)} -"
                    )
                else:
                    print(f"Δ = {delta}, x_{u}{v} = {flows.get((u, v), 0)} +")

        if violating_arcs:
            # Выбираем дугу с максимальным значением |Δij|
            io_jo, max_delta = max(violating_arcs, key=lambda x: x[1])

        else:
            print("Оптимальное решение найдено.")
            break

        print("Шаг 4: Добавление входящей дуги в базис и поиск цикла")
        print(
            f"дуга: {io_jo} с оценкой Δ = {reduced_costs[io_jo]} выбрана как (i_0, j_0)"
        )
        U_B.append(io_jo)
        U_N.remove(io_jo)
        print(f"U_B = U_B ∪ {io_jo}")

        cycle = find_cycle(U_B, io_jo, flows, capacities)
        if not cycle:
            print("Не удалось найти цикл. Прерывание алгоритма.")
            break
        print(f"Найденный цикл: {cycle}")

        print("Шаг 5: Вычисление θ^0 и идентификация выходящей дуги")
        theta_0, i_star_j_star = compute_theta(capacities, flows, cycle)

        print("Шаг 6: Обновление потоков и базиса")
        # Обновление потоков
        for edge, direction in cycle.items():
            if direction == 1:
                flows[edge] += theta_0
                print(
                    f"x_{edge[0]}{edge[1]} = x_{edge[0]}{edge[1]} + θ^0 = {flows[edge]}"
                )
            elif direction == -1:
                flows[edge] -= theta_0
                print(
                    f"x_{edge[0]}{edge[1]} = x_{edge[0]}{edge[1]} - θ^0 = {flows[edge]}"
                )
            else:
                print(f"Ошибка: Направление дуги {edge} не определено.")

        if io_jo == i_star_j_star:
            print(f"дуга i0j0 = {io_jo} равна дуге i*j* = {i_star_j_star}")
            print(f"U_B = U_B ∖ {io_jo}")
            U_B.remove(io_jo)
            U_N.append(io_jo)
        else:
            print(f"дуга i0j0 = {io_jo} не равна дуге i*j* = {i_star_j_star}")
            print(f"U_B = U_B \ {i_star_j_star}")
            U_B.remove(i_star_j_star)

            # если один из узлов дуги принадлежит исскуственному узлу 9 то удаляем дугу из графа
            if i_star_j_star[0] == 9 or i_star_j_star[1] == 9:
                G.remove_edge(i_star_j_star[0], i_star_j_star[1])
                capacities.pop(i_star_j_star)
                costs.pop(i_star_j_star)
                flows.pop(i_star_j_star)
            else:
                U_N.append(i_star_j_star)

    return flows, U_B, U_N


# Фаза 1: Построение задачи первой фазы
def phase_one(G, demands, capacities):
    # Создаем копию графа G, чтобы не изменять оригинальный граф
    G_phase_one = G.copy()

    # Добавление искусственного узла
    artificial_node = max(G_phase_one.nodes()) + 1
    G_phase_one.add_node(artificial_node, demand=0)

    # Добавление искусственных дуг от источников к искусственному узлу и от искусственного узла к стокам
    artificial_edges = []
    flows = {}
    capacities_phase_one = capacities.copy()
    costs_phase_one = {}

    # Проставим стоимость для всех дуг, кроме искусственных, равной 0
    for edge in G_phase_one.edges():
        costs_phase_one[edge] = 0
    for u, v, data in G_phase_one.edges(data=True):
        data["weight"] = 0

    # Потоки на всех дугах, кроме искусственных, равны 0
    for edge in G_phase_one.edges():
        flows[edge] = 0

    # Обновим стоимости искусственных дуг и добавим их в граф
    for node in G_phase_one.nodes():
        demand = demands.get(node, 0)
        if demand < 0:
            # Узел является источником
            # Добавляем дугу от источника к искусственному узлу
            G_phase_one.add_edge(node, artificial_node, capacity=-demand, weight=1)
            artificial_edge = (node, artificial_node)
            artificial_edges.append(artificial_edge)
            capacities_phase_one[artificial_edge] = -demand
            costs_phase_one[artificial_edge] = 1  # Стоимость искусственных дуг равна 1
            flows[artificial_edge] = -demand  # Поток равен модулю предложения
        elif demand > 0:
            # Узел является стоком
            # Добавляем дугу от искусственного узла к стоку
            G_phase_one.add_edge(artificial_node, node, capacity=demand, weight=1)
            artificial_edge = (artificial_node, node)
            artificial_edges.append(artificial_edge)
            capacities_phase_one[artificial_edge] = demand
            costs_phase_one[artificial_edge] = 1  # Стоимость искусственных дуг равна 1
            flows[artificial_edge] = demand  # Поток равен спросу узла

    # Включаем искусственные дуги в базис
    U_B = artificial_edges.copy()

    # Добавляем в базис дуги (7, 3) и (8, 3)
    additional_basis_edges = [(7, 3), (8, 3)]
    for edge in additional_basis_edges:
        if edge in G_phase_one.edges():
            U_B.append(edge)
            flows[edge] = 0  # Начальный поток на этих дугах равен 0
        else:
            print(f"Дуга {edge} отсутствует в графе.")

    # Все остальные дуги являются небазисными
    U_N = [edge for edge in G_phase_one.edges() if edge not in U_B]

    return (
        G_phase_one,
        flows,
        capacities_phase_one,
        costs_phase_one,
        U_B,
        U_N,
        artificial_node,
        artificial_edges,
    )


def run_potential_method():
    # Фаза 1: Построение начального базисного потока
    (
        G_phase_one,
        flows,
        capacities_phase_one,
        costs_phase_one,
        U_B,
        U_N,
        artificial_node,
        artificial_edges,
    ) = phase_one(G, demands, capacities)

    print("Фаза 1: Решение вспомогательной задачи")
    flows_updated, U_B_updated, U_N_updated = potential_method(
        G_phase_one, capacities_phase_one, costs_phase_one, flows, demands, U_B, U_N
    )

    # Проверка, все ли искусственные дуги имеют нулевой поток
    artificial_flows = [flows_updated.get(arc, 0) for arc in artificial_edges]
    if all(flow == 0 for flow in artificial_flows):
        print("Все искусственные потоки равны нулю. Переход к фазе 2.")
        print("Фаза 2: Решение основной задачи")
        # Удаляем искусственные дуги из U_B и U_N
        U_B_final = [edge for edge in U_B_updated if edge not in artificial_edges]
        U_N_final = [edge for edge in U_N_updated if edge not in artificial_edges]

        # Восстанавливаем исходные стоимости дуг для основной задачи
        flows_final, U_B_final, U_N_final = potential_method(
            G,
            capacities,
            costs,
            flows_updated,
            demands,
            U_B_final,
            U_N_final,
        )
    else:
        print("Искусственные потоки не равны нулю. Решение исходной задачи невозможно.")
        return

    # Вывод окончательных потоков
    print("\nОкончательные потоки:")
    total_cost = 0
    for edge, flow in flows_final.items():
        if flow > 0 and edge not in artificial_edges:
            print(f"Дуга {edge}: Поток = {flow}")
            total_cost += flow * costs[edge]
    print(f"\nОбщая стоимость: {total_cost}")


def visualize_graph(G, flowDict, baseline_edges=None, name="network_graph.png"):
    pos = {
        1: (0, 1),
        2: (1, 2),
        3: (2, 2),
        4: (3, 1),
        5: (1, 0),
        6: (2, 0),
        7: (1, 1),
        8: (2, 1),
        9: (4, 1.75),
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
    edge_colors = [
        "red" if baseline_edges and (u, v) in baseline_edges else "black"
        for u, v in G.edges()
    ]
    nx.draw_networkx_edges(
        G, pos, arrowstyle="->", arrowsize=20, edge_color=edge_colors, width=2
    )

    # Подписываем узлы
    labels = {
        node: f"{node}\n(d={data['demand']})" for node, data in G.nodes(data=True)
    }
    nx.draw_networkx_labels(G, pos, labels, font_size=10, font_weight="bold")

    # Подготавливаем метки ребер с информацией о capacity, weight и flow
    edge_labels = {}
    for u, v, data in G.edges(data=True):
        flow = flowDict.get((u, v), 0)
        edge_labels[(u, v)] = f"c={data['capacity']}\nw={data['weight']}\nf={flow}"

    # Рисуем метки ребер
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=8)

    plt.axis("off")
    plt.tight_layout()
    plt.savefig(name)


run_potential_method()
