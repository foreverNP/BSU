import random
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd

NUM_COMMERCIAL_BANKS = 5
CLIENT_COUNTS = [4567, 5010, 5400, 7900, 6750]
BANK_NAMES = [f"Коммерческий Банк {i + 1}" for i in range(NUM_COMMERCIAL_BANKS)]

MIN_SCORE = 30
MAX_SCORE = 100


def simulate_client_scores(num_clients, mean=None, std_dev=None):
    """Симулирует скоринговые баллы для заданного числа клиентов."""
    if mean is None:
        mean = random.uniform(65, 85)
    if std_dev is None:
        std_dev = random.uniform(10, 18)
    scores = np.random.normal(loc=mean, scale=std_dev, size=num_clients)
    scores = np.clip(scores, MIN_SCORE, MAX_SCORE)
    return scores.astype(int)


def evaluate_commercial_bank(client_scores):
    """Оценивает коммерческий банк на основе среднего балла его клиентов."""
    if not client_scores.any():
        return 0
    return np.mean(client_scores)


def evaluate_central_bank(bank_scores):
    """Оценивает центральный банк на основе среднего балла коммерческих банков."""
    if not bank_scores:
        return 0
    return np.mean(bank_scores)


# --- Функции Градации ---


def grade_client(score):
    """Присваивает градацию клиенту по его баллу."""
    if score >= 90:
        return "A (Отлично)"
    elif score >= 75:
        return "B (Хорошо)"
    elif score >= 60:
        return "C (Удовлетворительно)"
    elif score >= 45:
        return "D (Плохо)"
    else:
        return "E (Критично)"


def grade_commercial_bank(avg_score):
    """Присваивает градацию коммерческому банку."""
    if avg_score >= 80:
        return "Стабильный"
    elif avg_score >= 65:
        return "Приемлемый"
    elif avg_score >= 50:
        return "Требует внимания"
    else:
        return "Высокий риск"


def grade_central_bank(avg_score):
    """Присваивает градацию состоянию системы (оценка ЦБ)."""
    if avg_score >= 75:
        return "Здоровая система"
    elif avg_score >= 60:
        return "Требуется мониторинг"
    else:
        return "Требуется вмешательство"


# --- Функции Управляющих Решений ---


def suggest_client_decision(grade):
    """Предлагает решение по клиенту."""
    if grade == "A (Отлично)":
        return "Одобрять кредиты, возможно предложить лучшие условия."
    elif grade == "B (Хорошо)":
        return "Стандартное одобрение кредита."
    elif grade == "C (Удовлетворительно)":
        return "Стандартное одобрение, возможен запрос доп. информации/залога."
    elif grade == "D (Плохо)":
        return "Отказ в кредите или требование высокого залога/поручителя, повышенная ставка."
    else:
        return "Отказ в кредите, рассмотреть взыскание существующих долгов."


def suggest_cb_decision(grade):
    """Предлагает решение для коммерческого банка."""
    if grade == "Стабильный":
        return "Стандартный надзор."
    elif grade == "Приемлемый":
        return "Стандартный надзор, рекомендовать диверсификацию портфеля."
    elif grade == "Требует внимания":
        return "Усиленный мониторинг, запрос планов по управлению рисками."
    else:
        return "Интенсивный надзор, требование плана рекапитализации, возможны ограничения операций."


def suggest_central_bank_action(grade):
    """Предлагает действия для центрального банка."""
    if grade == "Здоровая система":
        return "Поддержание текущей монетарной политики."
    elif grade == "Требуется мониторинг":
        return "Пересмотр нормативов, проведение стресс-тестов банков."
    else:
        return "Корректировка ключевой ставки, норм резервирования, предоставление ликвидности, ужесточение регулирования."


def get_distribution_params():
    """Получает параметры распределения клиентских баллов на основе предустановленных сценариев."""
    print("\n--- Настройка распределения клиентских баллов ---")
    print("1. Стандартное распределение (умеренная стабильность)")
    print("2. Очень стабильное распределение (высокие баллы)")
    print("3. Нестабильное распределение (низкие баллы)")
    print("4. Задать индивидуальные сценарии для каждого банка")

    scenarios = {
        "1": (70, 12),  # Стандартное распределение
        "2": (85, 8),  # Очень стабильное (высокое среднее, малый разброс)
        "3": (55, 15),  # Нестабильное (низкое среднее, большой разброс)
    }

    choice = input("\nВыберите опцию (1-4): ").strip()

    if choice in scenarios:
        return [scenarios[choice]] * NUM_COMMERCIAL_BANKS

    elif choice == "4":
        params = []
        for i in range(NUM_COMMERCIAL_BANKS):
            print(f"\nВыберите сценарий для {BANK_NAMES[i]}")
            print("1. Стандартное распределение")
            print("2. Очень стабильное распределение")
            print("3. Нестабильное распределение")

            bank_choice = input(f"Сценарий для банка {i + 1} (1-3): ").strip()
            if bank_choice in scenarios:
                params.append(scenarios[bank_choice])
            else:
                print(f"Неверный выбор. Для {BANK_NAMES[i]} будет использовано стандартное распределение.")
                params.append(scenarios["1"])
        return params

    else:
        print("Неверный выбор. Будет использовано стандартное распределение.")
        return [scenarios["1"]] * NUM_COMMERCIAL_BANKS


distribution_params = get_distribution_params()

# 1. Симуляция и оценка
all_banks_data = []
commercial_bank_scores = []
all_clients_data = []

summary_lines = ["--- Сводка по Системе ---"]


for i in range(NUM_COMMERCIAL_BANKS):
    bank_name = BANK_NAMES[i]
    num_clients = CLIENT_COUNTS[i]

    if distribution_params[i]:
        mean, std_dev = distribution_params[i]
        client_scores = simulate_client_scores(num_clients, mean, std_dev)
        dist_info = f" (среднее={mean:.1f}, отклонение={std_dev:.1f})"
    else:
        client_scores = simulate_client_scores(num_clients)
        dist_info = " (случайное распределение)"

    bank_score = evaluate_commercial_bank(client_scores)
    commercial_bank_scores.append(bank_score)

    bank_grade = grade_commercial_bank(bank_score)
    decision = suggest_cb_decision(bank_grade)

    bank_data = {
        "bank_name": bank_name,
        "num_clients": num_clients,
        "avg_client_score": bank_score,
        "grade": bank_grade,
        "decision": decision,
        "client_scores": client_scores,
    }
    all_banks_data.append(bank_data)

    summary_lines.append(f"* {bank_name} ({num_clients} кл.):{dist_info}")
    summary_lines.append(f"  Средний балл = {bank_score:.2f}, Градация = '{bank_grade}'")
    summary_lines.append(f"  Решение: {decision}")
    summary_lines.append("")

    for score in client_scores:
        all_clients_data.append({"bank": bank_name, "score": score})

# Оценка центрального банка
central_bank_score = evaluate_central_bank(commercial_bank_scores)
cb_system_grade = grade_central_bank(central_bank_score)
cb_action = suggest_central_bank_action(cb_system_grade)

# Добавляем строку для ЦБ в сводку
summary_lines.append("* Центральный Банк (Состояние системы):")
summary_lines.append(f"  Средний балл КБ = {central_bank_score:.2f}, Градация = '{cb_system_grade}'")
summary_lines.append(f"  Действие: {cb_action}")
summary_lines.append("")

summary_text = "\n".join(summary_lines)

# 2. Создание DataFrame для визуализации
df_banks = pd.DataFrame(all_banks_data)
df_clients = pd.DataFrame(all_clients_data)


sns.set_theme(style="whitegrid")
try:
    plt.rcParams["font.family"] = "DejaVu Sans"
except Exception:
    plt.rcParams["font.family"] = "sans-serif"


# Создаем фигуру и сетку для графиков (2 строки, 2 столбца)
fig, axes = plt.subplots(2, 2, figsize=(18, 12))
fig.suptitle("Анализ Банковской Системы по Возврату Кредитов", fontsize=16)

# --- График 1: Средние баллы коммерческих банков (верхний левый) ---
ax1 = axes[0, 0]
bank_plot = sns.barplot(
    x="bank_name", y="avg_client_score", data=df_banks, palette="viridis", ax=ax1, hue="bank_name", dodge=False, legend=False
)  # Используем hue для палитры, но отключаем легенду здесь
ax1.set_title("Средний Балл Клиентов по Банкам")
ax1.set_xlabel("Коммерческий Банк")
ax1.set_ylabel("Средний Балл")
ax1.tick_params(axis="x", rotation=45)
# Добавление текстовых меток со значениями
for container in bank_plot.containers:
    ax1.bar_label(container, fmt="%.2f", fontsize=9)
ax1.set_ylim(bottom=min(MIN_SCORE, df_banks["avg_client_score"].min() - 5), top=MAX_SCORE + 2)  # Настраиваем оси Y

# --- График 2: Общее распределение клиентских баллов (верхний правый) ---
ax2 = axes[0, 1]
sns.histplot(data=df_clients, x="score", kde=True, bins=30, color="skyblue", ax=ax2)
ax2.set_title("Общее Распределение Баллов Клиентов")
ax2.set_xlabel("Скоринговый Балл")
ax2.set_ylabel("Количество Клиентов")

ax2.axvline(90, color="darkgreen", linestyle="--", linewidth=1.2, label="A (90+)")
ax2.axvline(75, color="blue", linestyle="--", linewidth=1.2, label="B (75-89)")
ax2.axvline(60, color="orange", linestyle="--", linewidth=1.2, label="C (60-74)")
ax2.axvline(45, color="red", linestyle="--", linewidth=1.2, label="D (45-59)")
ax2.axvline(MIN_SCORE, color="grey", linestyle=":", linewidth=1, label=f"Min Score ({MIN_SCORE})")
ax2.legend(title="Градации")

# # --- График 3: Распределение баллов по банкам (Box Plot) (нижний левый) ---
ax3 = axes[1, 0]
sns.boxplot(x="bank", y="score", data=df_clients, palette="pastel", ax=ax3, hue="bank", legend=False)
ax3.set_title("Распределение Баллов Клиентов в Разрезе Банков")
ax3.set_xlabel("Коммерческий Банк")
ax3.set_ylabel("Скоринговый Балл")
ax3.tick_params(axis="x", rotation=45)

# --- Блок 4: Текстовая Сводка (нижний правый) ---
ax4 = axes[1, 1]
ax4.axis("off")
ax4.text(
    0.01,
    0.99,
    summary_text,
    ha="left",
    va="top",
    fontsize=9,
    wrap=True,
    bbox=dict(boxstyle="round,pad=0.5", fc="aliceblue", alpha=0.9),
)
ax4.set_title("Сводная Информация и Решения", fontsize=12)


plt.tight_layout(rect=[0, 0.03, 1, 0.97])


plt.show()
