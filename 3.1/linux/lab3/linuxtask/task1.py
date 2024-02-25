def calc_som(name):
    som = 0
    name = name.upper()
    for ch in name:
        if "A" <= ch <= "Z":
            som += ord(ch) - ord("A") + 1
    return som + len(name)


def prize_draw(st, we, n):
    if not st:
        return "Нет участников"

    names = st.split(",")

    if n > len(names):
        return "Недостаточно участников"

    participants = []

    for i, name in enumerate(names):
        som = calc_som(name)
        win_number = som * we[i]
        participants.append((name, win_number))

    participants.sort(key=lambda x: (-x[1], x[0]))

    return participants[n - 1][0]


names = "COLIN,AMANDBA,AMANDAB,CAROL,PauL,JOSEPH"
weights = [1, 4, 4, 5, 2, 1]
rank = 4


result = prize_draw(names, weights, rank)
print(f"Результат: {result}")
