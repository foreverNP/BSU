import random
import matplotlib.pyplot as plt

# Список сотрудников с их здоровьем
employees = [
    {
        "name": "Кузнецова Жанна",
        "age": 50,
        "pressure": 130,
        "pulse": 85,
        "sugar": 7.0,
        "status": "Сахарный диабет",
    },
    {
        "name": "Сидоров Павел",
        "age": 45,
        "pressure": 140,
        "pulse": 80,
        "sugar": 6.0,
        "status": "Проблемы с давлением",
    },
    {
        "name": "Иванов Иван",
        "age": 34,
        "pressure": 120,
        "pulse": 70,
        "sugar": 5.1,
        "status": "Здоров",
    },
    {
        "name": "Петрова Мария",
        "age": 29,
        "pressure": 130,
        "pulse": 75,
        "sugar": 5.5,
        "status": "Здоров",
    },
]

# Врачи и медперсонал
medical_staff = [
    {"name": "Медсестра Васильева", "specialty": "Общий уход", "patients": 0},
    {"name": "Доктор Смирнов", "specialty": "Терапевт", "patients": 0},
    {"name": "Доктор Иванов", "specialty": "Кардиолог", "patients": 0},
]


# Проверка здоровья сотрудников и назначение лечения
def health_check(employees, medical_staff):
    for employee in employees:
        if employee["pressure"] > 130:
            print(
                f"У {employee['name']} повышенное давление: {employee['pressure']}. Назначен кардиолог."
            )
            assign_doctor(employee, "Кардиолог", medical_staff)
        elif employee["sugar"] > 6.5:
            print(
                f"У {employee['name']} высокий уровень сахара: {employee['sugar']}. Назначен терапевт."
            )
            assign_doctor(employee, "Терапевт", medical_staff)
        else:
            print(f"{employee['name']} здоров. Регулярный осмотр назначен.")
            assign_doctor(employee, "Общий уход", medical_staff)


# Назначение врача или медперсонала
def assign_doctor(employee, specialty, medical_staff):
    for doctor in medical_staff:
        if doctor["specialty"] == specialty:
            doctor["patients"] += 1
            print(f"{doctor['name']} назначен для {employee['name']}")


# Генерация отчёта по загрузке медперсонала
def generate_report(medical_staff):
    names = [doc["name"] for doc in medical_staff]
    patients = [doc["patients"] for doc in medical_staff]

    plt.bar(names, patients, color="blue")
    plt.title("Загрузка медперсонала")
    plt.xlabel("Медперсонал")
    plt.ylabel("Количество пациентов")
    plt.show()


# Симуляция рабочего дня
def simulate_day(employees, medical_staff):
    print("Проверка состояния здоровья сотрудников:")
    health_check(employees, medical_staff)

    # Генерация отчёта по загруженности медперсонала
    generate_report(medical_staff)


# Запуск симуляции
simulate_day(employees, medical_staff)
