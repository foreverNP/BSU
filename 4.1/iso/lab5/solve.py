# Построю визуализацию сетевого графа и выведу таблицу с расчетами.
# Требования: networkx, pandas, matplotlib должны быть установлены в среде.
# Этот код рисует ориентированный граф (AON), подписывает вершины (ES/EF/LS/LF/TF)
# и помечает критические работы в подписи. Также показывает таблицу с расчетами.

import networkx as nx
import pandas as pd
import matplotlib.pyplot as plt
import math

# Входные данные
tasks = {
    "b1": {"dur": 5, "pred": []},
    "b2": {"dur": 8, "pred": []},
    "b3": {"dur": 3, "pred": []},
    "b4": {"dur": 6, "pred": ["b1"]},
    "b5": {"dur": 4, "pred": ["b1"]},
    "b6": {"dur": 1, "pred": ["b3"]},
    "b7": {"dur": 2, "pred": ["b2", "b5", "b6"]},
    "b8": {"dur": 6, "pred": ["b2", "b5", "b6"]},
    "b9": {"dur": 3, "pred": ["b4", "b7"]},
    "b10": {"dur": 9, "pred": ["b3"]},
    "b11": {"dur": 7, "pred": ["b2", "b5", "b6", "b10"]},
}

# Построим граф
G = nx.DiGraph()
for t, info in tasks.items():
    G.add_node(t, dur=info["dur"])
    for p in info["pred"]:
        G.add_edge(p, t)

# Топологическая сортировка
topo = list(nx.topological_sort(G))

# Прямой проход
ES = {n: 0 for n in G.nodes()}
EF = {}
for n in topo:
    preds = list(G.predecessors(n))
    if preds:
        ES[n] = max(EF[p] for p in preds)
    else:
        ES[n] = 0
    EF[n] = ES[n] + G.nodes[n]["dur"]

project_duration = max(EF.values())

# Обратный проход
LF = {}
LS = {}
for n in reversed(topo):
    succs = list(G.successors(n))
    if succs:
        LF[n] = min(LS[s] for s in succs)
    else:
        LF[n] = project_duration
    LS[n] = LF[n] - G.nodes[n]["dur"]

# Резервы
TF = {n: LS[n] - ES[n] for n in G.nodes()}  # полный резерв (total float)
FF = {}
for n in G.nodes():
    succs = list(G.successors(n))
    if succs:
        FF[n] = min(ES[s] for s in succs) - EF[n]
    else:
        FF[n] = TF[n]

IF = {}
for n in G.nodes():
    succs = list(G.successors(n))
    preds = list(G.predecessors(n))
    min_es_succ = min((ES[s] for s in succs), default=project_duration)
    max_lf_pred = max((LF[p] for p in preds), default=0)
    val = min_es_succ - max_lf_pred - G.nodes[n]["dur"]
    IF[n] = max(0, val)

max_TF = max(TF.values()) if TF else 0
K_A = {n: (G.nodes[n]["dur"] / (G.nodes[n]["dur"] + TF[n]) if TF[n] > 0 else 1.0) for n in G.nodes()}
K_B = {n: (1 - TF[n] / max_TF if max_TF > 0 else 1.0) for n in G.nodes()}

critical = [n for n, v in TF.items() if v == 0]

# Подготовим таблицу
rows = []
for n in topo:
    rows.append(
        {
            "task": n,
            "dur": G.nodes[n]["dur"],
            "ES": ES[n],
            "EF": EF[n],
            "LS": LS[n],
            "LF": LF[n],
            "TF": TF[n],
            "FF": FF[n],
            "IF": IF[n],
            "K_A": round(K_A[n], 3),
            "K_B": round(K_B[n], 3),
            "critical": (TF[n] == 0),
        }
    )
df = pd.DataFrame(rows)

# Построим визуализацию
plt.figure(figsize=(10, 7))

# Позиция вершин: чтобы получить читаемое расположение, используем spring_layout с фиксированным seed
pos = nx.spring_layout(G, seed=42)

# Размер вершин пропорционален длительности (с масштабом)
node_sizes = [300 + 200 * math.sqrt(G.nodes[n]["dur"]) for n in G.nodes()]

# Формируем подписи: имя, длительность, ES-EF, TF (и пометка критичности)
labels = {}
for n in G.nodes():
    crit_mark = " (crit)" if TF[n] == 0 else ""
    labels[n] = f"{n}{crit_mark}\n{G.nodes[n]['dur']} | ES={ES[n]} EF={EF[n]}\nLS={LS[n]} LF={LF[n]} TF={TF[n]}"

# Рисуем узлы и подписи
nx.draw_networkx_nodes(G, pos, node_size=node_sizes)
nx.draw_networkx_edges(G, pos, arrows=True, arrowstyle="-|>", arrowsize=12)
nx.draw_networkx_labels(G, pos, labels=labels, font_size=9)

plt.title(f"Сетевой граф (AON). Длительность проекта = {project_duration}")
plt.axis("off")
plt.tight_layout()

# Покажем граф
plt.show()

# Покажем таблицу с расчетами
try:
    from caas_jupyter_tools import display_dataframe_to_user

    display_dataframe_to_user("CPM calculations", df)
except Exception:
    # Если вспомогательная функция недоступна, просто выведем таблицу
    print("\nТаблица расчётов (ES/EF/LS/LF/TF/FF/IF):\n")
    print(df.to_string(index=False))
