def nb_dig(n, d):
    count = 0
    for k in range(n + 1):
        # Возводим число k в квадрат
        square = k**2
        # Преобразуем квадрат в строку и подсчитываем, сколько раз цифра d встречается в строке
        count += str(square).count(str(d))
    return count


print(nb_dig(25, 1))
