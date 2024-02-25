import random
import matplotlib.pyplot as plt


class Patient:
    def __init__(self, id, health_score):
        self.id = id
        self.health_score = health_score
        # Определяем состояние пациента по показателю здоровья:
        # критический: здоровье < 30, серьёзный: 30 <= здоровье < 60, стабильный: здоровье >= 60
        if health_score < 30:
            self.condition = "критический"
        elif health_score < 60:
            self.condition = "серьёзный"
        else:
            self.condition = "стабильный"


class MedicalStaff:
    def __init__(self, name, role):
        self.name = name
        self.role = role
        self.calls = 0


def simulate_hospital():
    patients = [Patient(i + 1, random.randint(1, 100)) for i in range(56)]

    doctors = [MedicalStaff(f"Доктор {i + 1}", "Доктор") for i in range(2)]
    senior_nurses = [
        MedicalStaff(f"Старшая медсестра {i + 1}", "Старшая медсестра")
        for i in range(3)
    ]
    junior_nurses = [
        MedicalStaff(f"Младшая медсестра {i + 1}", "Младшая медсестра")
        for i in range(4)
    ]

    # Индексы для циклического распределения вызовов внутри групп
    doctor_index = 0
    senior_index = 0
    junior_index = 0

    assignment_commands = []

    # Проходим по всем пациентам и назначаем вызов в зависимости от их состояния
    for patient in patients:
        if patient.condition == "критический":
            assigned = doctors[doctor_index % len(doctors)]
            doctor_index += 1
        elif patient.condition == "серьёзный":
            assigned = senior_nurses[senior_index % len(senior_nurses)]
            senior_index += 1
        else:  # стабильный
            assigned = junior_nurses[junior_index % len(junior_nurses)]
            junior_index += 1

        assigned.calls += 1
        command = (
            f"Вызов {assigned.role} {assigned.name}: "
            f"Пациент {patient.id} ({patient.condition}, здоровье: {patient.health_score})"
        )
        assignment_commands.append(command)

    # Вывод текстовых команд
    print("Команды для медперсонала:")
    for command in assignment_commands:
        print(command)

    print("Загруженность каждого сотрудника:")
    for doc in doctors:
        print(f"{doc.name} ({doc.role}): {doc.calls} вызов(ов)")
    for nurse in senior_nurses:
        print(f"{nurse.name} ({nurse.role}): {nurse.calls} вызов(ов)")
    for nurse in junior_nurses:
        print(f"{nurse.name} ({nurse.role}): {nurse.calls} вызов(ов)")

    role_calls = {
        "Доктора": sum(staff.calls for staff in doctors),
        "Старшие медсестры": sum(staff.calls for staff in senior_nurses),
        "Младшие медсестры": sum(staff.calls for staff in junior_nurses),
    }

    # Визуализация результатов – диаграмма загруженности медперсонала за 1 день
    roles = list(role_calls.keys())
    calls = list(role_calls.values())

    all_staff = doctors + senior_nurses + junior_nurses
    names = [s.name for s in all_staff]
    calls_for_each = [s.calls for s in all_staff]

    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(10, 10))

    ax1.bar(roles, calls, color=["blue", "green", "orange"])
    ax1.set_xlabel("Медперсонал")
    ax1.set_ylabel("Количество вызовов")
    ax1.set_title("Загруженность медперсонала за 1 день")

    ax2.bar(names, calls_for_each)
    ax2.set_xticks(range(len(names)))
    ax2.set_xticklabels(names, rotation=45)
    ax2.set_xlabel("Сотрудник")
    ax2.set_ylabel("Количество вызовов")
    ax2.set_title("Загруженность по каждому сотруднику")

    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    simulate_hospital()
