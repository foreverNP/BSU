import random
import numpy as np
from scipy.spatial.distance import euclidean

# Список диагнозов и соответствующих методов лечения
diagnoses = [
    "Пневмония",
    "Грипп",
    "Сахарный диабет",
    "Артериальная гипертензия",
    "Астма",
    "Бронхит",
    "Аллергия",
    "Мигрень",
    "Желудочно-кишечные расстройства",
    "Коронавирусная инфекция",
    "Анемия",
    "Рак легких",
    "Остеопороз",
    "Аритмия",
]

treatments = [
    "Антибиотики",
    "Противовирусные препараты",
    "Инсулинотерапия",
    "Антигипертензивные препараты",
    "Ингаляторы",
    "Отхаркивающие препараты",
    "Антигистаминные средства",
    "Анальгетики",
    "Диетотерапия",
    "Антиковидные препараты",
    "Железосодержащие препараты",
    "Химиотерапия",
    "Кальцийсодержащие препараты",
    "Противоаритмические препараты",
]


# Генерация базы данных диагнозов
def generate_database(n, m):
    database = []
    for i in range(n):
        index = i % len(diagnoses)
        diagnosis = diagnoses[index]
        treatment = treatments[index]
        dna_vector = [random.uniform(0, 1) for _ in range(m)]
        database.append((diagnosis, dna_vector, treatment))
    return database


# Функция для поиска 3х наиболее похожих векторов
def find_closest_vectors(database, input_vector, top_n=3):
    distances = []
    for record in database:
        diagnosis, dna_vector, treatment = record
        distance = euclidean(dna_vector, input_vector)  # Евклидово расстояние
        distances.append((distance, diagnosis, treatment))

    distances.sort(key=lambda x: x[0])  # Сортировка по расстоянию
    return distances[:top_n]


n = 300  # Количество записей
m = 100  # Длина ДНК-вектора
database = generate_database(n, m)

# Ввод данных больного
patient_name = "Иванов Иван Иванович"
input_vector = [random.uniform(0, 1) for _ in range(m)]

closest_matches = find_closest_vectors(database, input_vector)

print(f"Пациент: {patient_name}")
print("Введённый ДНК-вектор:", input_vector)
print("\n3 наиболее близких диагноза из базы данных:")
for i, (distance, diagnosis, treatment) in enumerate(closest_matches):
    print(
        f"{i + 1}. Диагноз: {diagnosis}, Лечение: {treatment}, Расстояние: {distance:.4f}"
    )
