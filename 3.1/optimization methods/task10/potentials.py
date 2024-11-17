import networkx as nx
import numpy as np
import matplotlib.pyplot as plt
import time

# Шаг 0: Инициализация исходного графа
G = nx.DiGraph()


# Требования узлов (ai)
G.add_node(1, demand=-70)  # Источник
G.add_node(2, demand=-51)  # Источник
G.add_node(3, demand=40)  # Сток
G.add_node(4, demand=70)  # Сток
G.add_node(5, demand=40)  # Сток
G.add_node(6, demand=-29)  # Источник
G.add_node(7, demand=0)  # Промежуточный узел
G.add_node(8, demand=0)  # Промежуточный узел

# Дуги с емкостями (dij) и стоимостью (cij)
edges = [
    (1, 2, {"capacity": 41, "weight": 7}),
    (1, 5, {"capacity": 45, "weight": 9}),
    (2, 3, {"capacity": 40, "weight": 10}),
    (2, 7, {"capacity": 40, "weight": 12}),
    (3, 4, {"capacity": 40, "weight": 14}),
    (4, 6, {"capacity": 20, "weight": 12}),
    (5, 6, {"capacity": 20, "weight": 10}),
    (5, 8, {"capacity": 20, "weight": 6}),
    (6, 8, {"capacity": 50, "weight": 8}),
    (7, 1, {"capacity": 20, "weight": 3}),
    (7, 3, {"capacity": 40, "weight": 15}),
    (7, 5, {"capacity": 25, "weight": 7}),
    (8, 3, {"capacity": 25, "weight": 14}),
    (8, 4, {"capacity": 50, "weight": 9}),
    (8, 7, {"capacity": 15, "weight": 20}),
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


# Функция для поиска цикла, образованного добавлением входящей дуги
def find_cycle(U_B, entering_arc):
    # Создаем подграф с базисными дугами
    B_graph = nx.DiGraph()
    B_graph.add_edges_from(U_B)

    # Добавляем входящую дугу
    B_graph.add_edge(*entering_arc)

    try:
        # Находим простой цикл, содержащий входящую дугу
        cycles = list(nx.simple_cycles(B_graph))
        for cycle in cycles:
            if entering_arc[0] in cycle and entering_arc[1] in cycle:
                # Реконструируем цикл как список дуг
                cycle_edges = []
                for i in range(len(cycle)):
                    u = cycle[i]
                    v = cycle[(i + 1) % len(cycle)]
                    cycle_edges.append((u, v))
                return cycle_edges
    except nx.NetworkXNoCycle:
        print("Цикл не найден.")
    return []


# Функция для вычисления θ^0 и идентификации выходящей дуги
def compute_theta(G, capacities, flows, cycle, entering_arc):
    theta_values = []
    arcs_with_theta = []
    # Определяем направление дуг
    direction = {}
    for arc in cycle:
        if arc == entering_arc:
            direction[arc] = 1  # Положительное направление
        else:
            direction[arc] = -1  # Отрицательное направление

    # Вычисляем θ для каждой дуги в цикле
    for arc in cycle:
        if direction[arc] > 0:
            residual_capacity = capacities[arc] - flows.get(arc, 0)
            theta_values.append(residual_capacity)
            arcs_with_theta.append((arc, residual_capacity))
        else:
            flow_on_arc = flows.get(arc, 0)
            theta_values.append(flow_on_arc)
            arcs_with_theta.append((arc, flow_on_arc))

    theta_0 = min(theta_values)

    # Идентифицируем выходящую дугу
    leaving_arc = None
    for arc, theta in arcs_with_theta:
        if theta == theta_0:
            leaving_arc = arc
            break

    return theta_0, leaving_arc


# Функция для корректировки потоков вдоль цикла
def adjust_flows(flows, cycle, theta_0, direction):
    for arc in cycle:
        if direction[arc] > 0:
            flows[arc] += theta_0
        else:
            flows[arc] -= theta_0


# Основная функция метода потенциалов
def potential_method(G, capacities, costs, flows, demands, U_B, U_N):
    visualize_graph(
        G, flows, baseline_edges=U_B, name=f"start_potentials_{int(time.time())}.png"
    )

    iteration = 0
    while True:
        iteration += 1
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
        optimal = True
        entering_arc = None
        for u, v in U_N:
            if u in potentials and v in potentials:
                if flows.get((u, v), 0) == 0 and reduced_costs.get((u, v), 0) > 0:
                    optimal = False
                    entering_arc = (u, v)
                    break
                elif (
                    flows.get((u, v), 0) == capacities.get((u, v), 0)
                    and reduced_costs.get((u, v), 0) < 0
                ):
                    optimal = False
                    entering_arc = (u, v)
                    break

        if optimal:
            print("Оптимальное решение найдено.")
            break

        if entering_arc is None:
            print("Не удалось найти подходящую входящую дугу. Прерывание алгоритма.")
            break

        print(
            f"Входящая дуга: {entering_arc} с оценкой Δ = {reduced_costs[entering_arc]}"
        )

        print("Шаг 4: Добавление входящей дуги в базис и поиск цикла")
        U_B.append(entering_arc)
        U_N.remove(entering_arc)

        cycle = find_cycle(U_B, entering_arc)
        if not cycle:
            print("Не удалось найти цикл. Прерывание алгоритма.")
            break
        print(f"Найденный цикл: {cycle}")

        print("Шаг 5: Вычисление θ^0 и идентификация выходящей дуги")
        theta_0, leaving_arc = compute_theta(G, capacities, flows, cycle, entering_arc)
        print(f"θ^0 = {theta_0}, выходящая дуга: {leaving_arc}")

        # Определение направлений дуг в цикле
        direction = {}
        for arc in cycle:
            if arc == entering_arc:
                direction[arc] = 1  # Положительное направление
            else:
                direction[arc] = -1  # Отрицательное направление

        print("Шаг 6: Корректировка потоков вдоль цикла")
        adjust_flows(flows, cycle, theta_0, direction)
        print(f"Потоки после корректировки: {flows}")

        print("Шаг 7: Обновление базиса")
        if leaving_arc != entering_arc:
            U_B.remove(leaving_arc)
            U_N.append(leaving_arc)
            print(f"Выходящая дуга {leaving_arc} удалена из базиса.")
        else:
            print("Выходящая дуга совпадает с входящей.")

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
        # Удаление искусственного узла и дуг
        G_phase_one.remove_node(artificial_node)
        for arc in artificial_edges:
            del capacities_phase_one[arc]
            del costs_phase_one[arc]
            del flows_updated[arc]
        # Фаза 2: Решение основной задачи методом потенциалов
        print("Фаза 2: Решение основной задачи")
        # Восстанавливаем исходные стоимости дуг для основной задачи
        flows_final, U_B_final, U_N_final = potential_method(
            G_phase_one,
            capacities,
            costs,
            flows_updated,
            demands,
            U_B_updated,
            U_N_updated,
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
    plt.savefig("img/" + name)


run_potential_method()
