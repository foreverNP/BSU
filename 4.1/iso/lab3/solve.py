import heapq

with open("in.txt", "r") as f:
    data = list(map(int, f.read().split()))

it = iter(data)
N = next(it)
M = next(it)
a = [next(it) for _ in range(N)]
b = [next(it) for _ in range(M)]
c = [[next(it) for _ in range(M)] for __ in range(N)]

# Индексация вершин:
# 0  - источник s
# 1..N - вышки
# N+1 .. N+M - заводы
# N+M+1 - сток t
V = N + M + 2
s = 0
t = N + M + 1


class Edge:
    __slots__ = ("to", "rev", "cap", "cost")

    def __init__(self, to, rev, cap, cost):
        self.to = to
        self.rev = rev
        self.cap = cap
        self.cost = cost


g = [[] for _ in range(V)]


def add_edge(u, v, cap, cost):
    g[u].append(Edge(v, len(g[v]), cap, cost))
    g[v].append(Edge(u, len(g[u]) - 1, 0, -cost))


sum_a = sum(a)
INF = 10**18

# s -> вышки
for i in range(N):
    add_edge(s, 1 + i, a[i], 0)

# заводы -> t
for j in range(M):
    add_edge(1 + N + j, t, b[j], 0)

# вышки -> заводы
cap_between = sum_a
for i in range(N):
    ui = 1 + i
    for j in range(M):
        vj = 1 + N + j
        add_edge(ui, vj, cap_between, c[i][j])


def min_cost_flow(s, t, maxf):
    V = len(g)
    potential = [0] * V
    prevv = [0] * V
    preve = [0] * V
    flow = 0
    cost = 0
    while flow < maxf:
        dist = [INF] * V
        dist[s] = 0
        pq = [(0, s)]
        while pq:
            d, v = heapq.heappop(pq)
            if d != dist[v]:
                continue
            for i, e in enumerate(g[v]):
                if e.cap > 0:
                    nd = d + e.cost + potential[v] - potential[e.to]
                    if nd < dist[e.to]:
                        dist[e.to] = nd
                        prevv[e.to] = v
                        preve[e.to] = i
                        heapq.heappush(pq, (nd, e.to))
        if dist[t] == INF:
            break
        for v in range(V):
            if dist[v] < INF:
                potential[v] += dist[v]
        addf = maxf - flow
        v = t
        while v != s:
            e = g[prevv[v]][preve[v]]
            addf = min(addf, e.cap)
            v = prevv[v]
        flow += addf
        cost += addf * potential[t]
        v = t
        while v != s:
            e = g[prevv[v]][preve[v]]
            e.cap -= addf
            g[v][e.rev].cap += addf
            v = prevv[v]
    return cost


result = min_cost_flow(s, t, sum_a)

with open("out.txt", "w") as f:
    f.write(str(result) + "\n")
