import random
import matplotlib.pyplot as plt

# Список сотрудников с их здоровьем и дополнительными метриками
employees = [
    {
        "name": "Анна Смирнова",
        "age": 50,
        "pressure": 135,
        "pulse": 85,
        "sugar": 7.2,
        "cholesterol": 210,
        "BMI": 28,
        "stress": 6,
        "status": "Сахарный диабет",
    },
    {
        "name": "Игорь Козлов",
        "age": 45,
        "pressure": 145,
        "pulse": 82,
        "sugar": 6.3,
        "cholesterol": 190,
        "BMI": 31,
        "stress": 8,
        "status": "Проблемы с давлением",
    },
    {
        "name": "Мария Иванова",
        "age": 34,
        "pressure": 125,
        "pulse": 75,
        "sugar": 5.3,
        "cholesterol": 180,
        "BMI": 24,
        "stress": 4,
        "status": "Здоров",
    },
    {
        "name": "Дмитрий Орлов",
        "age": 29,
        "pressure": 128,
        "pulse": 78,
        "sugar": 5.7,
        "cholesterol": 195,
        "BMI": 26,
        "stress": 5,
        "status": "Здоров",
    },
    {
        "name": "Елена Федорова",
        "age": 40,
        "pressure": 132,
        "pulse": 80,
        "sugar": 6.8,
        "cholesterol": 205,
        "BMI": 29,
        "stress": 7,
        "status": "Преддиабет",
    },
    {
        "name": "Алексей Новиков",
        "age": 38,
        "pressure": 118,
        "pulse": 72,
        "sugar": 5.0,
        "cholesterol": 170,
        "BMI": 23,
        "stress": 3,
        "status": "Здоров",
    },
]

# Врачи и медперсонал с расширением штата
medical_staff = [
    {"name": "Медсестра Васильева", "specialty": "Общий уход", "patients": 0},
    {"name": "Доктор Смирнов", "specialty": "Терапевт", "patients": 0},
    {"name": "Доктор Иванов", "specialty": "Кардиолог", "patients": 0},
    {"name": "Доктор Петрова", "specialty": "Диетолог", "patients": 0},
    {"name": "Доктор Соколова", "specialty": "Психолог", "patients": 0},
]


def health_check(employees, medical_staff):
    for employee in employees:
        assigned = False

        if employee["pressure"] > 130:
            print(
                f"У {employee['name']} повышенное давление: {employee['pressure']}. Назначен кардиолог."
            )
            assign_doctor(employee, "Кардиолог", medical_staff)
            assigned = True

        if employee["sugar"] > 6.5:
            print(
                f"У {employee['name']} высокий уровень сахара: {employee['sugar']}. Назначен терапевт."
            )
            assign_doctor(employee, "Терапевт", medical_staff)
            assigned = True

        if employee["cholesterol"] > 200:
            print(
                f"У {employee['name']} повышенный уровень холестерина: {employee['cholesterol']}. Назначен кардиолог."
            )
            assign_doctor(employee, "Кардиолог", medical_staff)
            assigned = True

        if employee["BMI"] > 30:
            print(
                f"У {employee['name']} повышенный индекс массы тела (BMI): {employee['BMI']}. Назначен диетолог."
            )
            assign_doctor(employee, "Диетолог", medical_staff)
            assigned = True

        if employee["stress"] > 7:
            print(
                f"У {employee['name']} высокий уровень стресса: {employee['stress']}. Назначен психолог."
            )
            assign_doctor(employee, "Психолог", medical_staff)
            assigned = True

        if not assigned:
            print(f"{employee['name']} в норме. Регулярный осмотр назначен.")
            assign_doctor(employee, "Общий уход", medical_staff)


# Назначение врача или медперсонала
def assign_doctor(employee, specialty, medical_staff):
    for doctor in medical_staff:
        if doctor["specialty"] == specialty:
            doctor["patients"] += 1
            print(f"{doctor['name']} назначен для {employee['name']}")
            break


# Генерация отчёта по загрузке медперсонала
def generate_report(medical_staff):
    names = [doc["name"] for doc in medical_staff]
    patients = [doc["patients"] for doc in medical_staff]

    plt.bar(names, patients, color="blue")
    plt.title("Загрузка медперсонала")
    plt.xlabel("Медперсонал")
    plt.ylabel("Количество пациентов")
    plt.show()


def simulate_day(employees, medical_staff):
    print("Проверка состояния здоровья сотрудников:")
    health_check(employees, medical_staff)
    generate_report(medical_staff)


simulate_day(employees, medical_staff)
