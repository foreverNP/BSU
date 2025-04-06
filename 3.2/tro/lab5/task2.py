import random
import matplotlib.pyplot as plt

# Генерация данных для аптек, поликлиник, больниц и социальных сетей
days = 30  # 30 дней наблюдений
pharmacy_sales = [random.randint(5, 20) for _ in range(days)]
clinic_visits = [random.randint(2, 10) for _ in range(days)]
hospital_visits = [random.randint(1, 5) for _ in range(days)]
social_media_mentions = [random.randint(50, 200) for _ in range(days)]

# Симуляция резкого увеличения активности на 20-й день (начало эпидемии)
for i in range(20, days):
    pharmacy_sales[i] += random.randint(10, 30)
    clinic_visits[i] += random.randint(5, 15)
    hospital_visits[i] += random.randint(3, 10)
    social_media_mentions[i] += random.randint(100, 300)

# Пороговые значения для фиксации эпидемии
min_pharmacy_sales = 15
min_clinic_visits = 10
min_hospital_visits = 4
min_social_media_mentions = 150


# Функция для фиксации момента эпидемии с учётом порогов
def detect_epidemic(
    pharmacy_sales, clinic_visits, hospital_visits, social_media_mentions
):
    for i in range(1, len(pharmacy_sales)):
        if (
            pharmacy_sales[i] >= pharmacy_sales[i - 1] * 1.5
            and pharmacy_sales[i] >= min_pharmacy_sales
            and clinic_visits[i] >= clinic_visits[i - 1] * 1.5
            and clinic_visits[i] >= min_clinic_visits
            and hospital_visits[i] >= hospital_visits[i - 1] * 1.5
            and hospital_visits[i] >= min_hospital_visits
            and social_media_mentions[i] >= social_media_mentions[i - 1] * 1.5
            and social_media_mentions[i] >= min_social_media_mentions
        ):
            return i  # День начала эпидемии
    return 31


# Фиксация момента начала эпидемии
epidemic_day = detect_epidemic(
    pharmacy_sales, clinic_visits, hospital_visits, social_media_mentions
)

if epidemic_day:
    print(f"Epidemic detected on day {epidemic_day}")
else:
    print("No epidemic detected")

# Визуализация данных
plt.figure(figsize=(12, 8))

# Визиты в поликлиники
plt.subplot(2, 2, 1)
plt.plot(clinic_visits, label="Clinic Visits", color="green")
plt.axvline(x=epidemic_day, color="red", linestyle="--", label="Epidemic Start")
plt.title("Clinic Visits")
plt.xlabel("Days")
plt.ylabel("Visits")
plt.legend()

# Визиты в больницы
plt.subplot(2, 2, 2)
plt.plot(hospital_visits, label="Hospital Visits", color="purple")
plt.axvline(x=epidemic_day, color="red", linestyle="--", label="Epidemic Start")
plt.title("Hospital Visits")
plt.xlabel("Days")
plt.ylabel("Visits")
plt.legend()

# Продажи в аптеках
plt.subplot(2, 2, 3)
plt.plot(pharmacy_sales, label="Pharmacy Sales", color="blue")
plt.axvline(x=epidemic_day, color="red", linestyle="--", label="Epidemic Start")
plt.title("Pharmacy Sales")
plt.xlabel("Days")
plt.ylabel("Sales")
plt.legend()

# Упоминания в соцсетях
plt.subplot(2, 2, 4)
plt.plot(social_media_mentions, label="Social Media Mentions", color="orange")
plt.axvline(x=epidemic_day, color="red", linestyle="--", label="Epidemic Start")
plt.title("Social Media Mentions")
plt.xlabel("Days")
plt.ylabel("Mentions")
plt.legend()

plt.tight_layout()
plt.show()
