from rich.console import Console
from rich.table import Table

rules = [
    {
        "id": 1,
        "conditions": ["компьютер не включается", "индикатор питания не светится"],
        "conclusion": ("проблема", "отсутствие питания"),
    },
    {
        "id": 2,
        "conditions": ["компьютер не включается", "индикатор питания светится"],
        "conclusion": ("проблема", "неисправность блока питания"),
    },
    {
        "id": 3,
        "conditions": ["компьютер включается", "не воспроизводит звук"],
        "conclusion": ("проблема", "неисправность звуковой карты"),
    },
    {
        "id": 4,
        "conditions": ["компьютер включается", "монитор не отображает изображение"],
        "conclusion": ("проблема", "неисправность видеокарты"),
    },
    {
        "id": 5,
        "conditions": ["компьютер включается", "операционная система не загружается"],
        "conclusion": ("обнаружено повреждение системных файлов", True),
    },
    {
        "id": 6,
        "conditions": ["обнаружено повреждение системных файлов"],
        "conclusion": ("действие", "провести восстановление системы"),
    },
    {
        "id": 7,
        "conditions": ["экран гаснет через некоторое время работы"],
        "conclusion": ("перебои в работе оперативной памяти", True),
    },
    {
        "id": 8,
        "conditions": ["перебои в работе оперативной памяти"],
        "conclusion": ("действие", "провести тестирование модулей RAM"),
    },
    {
        "id": 9,
        "conditions": ["тестирование модулей RAM показало сбои"],
        "conclusion": ("проблема", "неисправность модулей RAM"),
    },
    {
        "id": 10,
        "conditions": [
            "потребление оперативной памяти аномально высокое",
            "минимальное количество запущенных приложений",
        ],
        "conclusion": ("проблема", "вирусное заражение системы"),
    },
]

facts = {}  # Известные факты: {признак: значение}
goal_stack = []  # Стек целей (признаков, которые требуется установить)
context_stack = []  # Контекстный стек – накопленные факты с информацией, каким правилом они получены
log_table = []  # Таблица с протоколом шагов
step_counter = 1  # Порядковый номер шага


def log_step(rule_analyzed, answer, accepted_rule, rejected_rule):
    """
    Формирует запись о шаге и добавляет её в log_table.
    Параметры:
      rule_analyzed      – информация об анализируемом правиле (например, "Правило 1" или "Прямой ввод")
      answer             – ответ на вопрос или комментарий (строка)
      accepted_rule      – номер правила, которое принято (если правило сработало)
      rejected_rule      – номер правила, которое отверглось (если не выполнено условие)
    """
    global step_counter
    row = {
        "Шаг": step_counter,
        "Анализируемое правило": rule_analyzed,
        "Ответ на вопрос": answer,
        "Стек целей": goal_stack.copy(),
        "Контекстный стек": context_stack.copy(),
        "№ принимаемого правила": accepted_rule,
        "№ отбрасываемого правила": rejected_rule,
    }
    log_table.append(row)
    step_counter += 1


def ask_question(condition):
    """
    Запрашивает у пользователя ввод для указанного условия.
    Возвращает булево значение (True для 'да', False для 'нет')
    и записывает шаг в лог.
    """
    answer = input(f"Установлено ли условие '{condition}'? (да/нет): ").strip().lower()
    while answer not in ["да", "нет"]:
        answer = input("Введите 'да' или 'нет': ").strip().lower()
    bool_answer = answer == "да"
    log_step(
        rule_analyzed="",
        answer=f"{condition}: {bool_answer}",
        accepted_rule="",
        rejected_rule="",
    )
    return bool_answer


def backward_chain(goal):
    """
    Реализует обратный вывод для доказательства цели.
    Если для цели существуют правила, пытается доказать её, запрашивая недостающие условия.
    При срабатывании правила значение цели сохраняется в facts.
    Если ни одно правило не доказало цель – запрашивается её значение напрямую у пользователя.
    """
    if goal in facts:
        return facts[goal]
    goal_stack.append(goal)
    applicable_rules = [rule for rule in rules if rule["conclusion"][0] == goal]
    for rule in applicable_rules:
        log_step(
            rule_analyzed=f"Правило {rule['id']}",
            answer="",
            accepted_rule="",
            rejected_rule="",
        )
        all_conditions_true = True
        for cond in rule["conditions"]:
            if cond not in facts:
                answer = ask_question(cond)
                facts[cond] = answer
                context_stack.append((cond, answer))
            if not facts[cond]:
                all_conditions_true = False
                log_step(
                    rule_analyzed=f"Правило {rule['id']}",
                    answer=f"Условие '{cond}' не выполнено",
                    accepted_rule="",
                    rejected_rule=rule["id"],
                )
                break
        if all_conditions_true:
            facts[goal] = rule["conclusion"][1]
            context_stack.append((goal, facts[goal], rule["id"]))
            log_step(
                rule_analyzed=f"Правило {rule['id']}",
                answer="Все условия выполнены",
                accepted_rule=rule["id"],
                rejected_rule="",
            )
            goal_stack.pop()
            return facts[goal]
    # Если ни одно правило не доказало цель – запрашиваем её значение напрямую.
    user_answer = ask_question(goal)
    facts[goal] = user_answer
    context_stack.append((goal, user_answer, "direct"))
    log_step(
        rule_analyzed="Прямой ввод",
        answer=f"{goal}: {user_answer}",
        accepted_rule="direct",
        rejected_rule="",
    )
    goal_stack.pop()
    return user_answer


def main():
    """
    Главная функция:
      1. Запускает механизм обратного вывода для установки конечной цели (диагностика проблемы).
      2. Выводит результаты диагностики.
      3. Формирует протокол работы в виде таблицы с использованием Rich и сохраняет его в файл.
    """
    print("=== Система диагностики неисправностей ПК ===")
    print("Ответьте на вопросы, вводя 'да' или 'нет'.\n")

    result = backward_chain("проблема")

    print("\n--- Результаты диагностики ---")
    if result:
        print(f"Обнаруженная проблема: {facts['проблема']}")
        if "действие" in facts:
            print(f"Рекомендуемое действие: {facts['действие']}")
    else:
        print("Не удалось установить проблему.")

    # Вывод протокола работы с использованием Rich
    console = Console()
    table = Table(show_header=True, header_style="bold magenta")
    table.add_column("Шаг", justify="center")
    table.add_column("Анализируемое правило", justify="center")
    table.add_column("Ответ на вопрос", justify="center")
    table.add_column("Стек целей", justify="center")
    table.add_column("Контекстный стек", justify="center")
    table.add_column("№ принимаемого правила", justify="center")
    table.add_column("№ отбрасываемого правила", justify="center")

    for row in log_table:
        table.add_row(
            str(row["Шаг"]),
            str(row["Анализируемое правило"]),
            str(row["Ответ на вопрос"]),
            str(row["Стек целей"]),
            str(row["Контекстный стек"]),
            str(row["№ принимаемого правила"]),
            str(row["№ отбрасываемого правила"]),
        )

    console.print("\n--- Протокол работы (формат Rich) ---")
    console.print(table)

    # Сохранение протокола в log.txt
    with open("log.txt", "w", encoding="utf-8") as f:
        file_console = Console(file=f, width=120)
        file_console.print(table)
    print("\nЛог работы сохранён в файле log.txt")


if __name__ == "__main__":
    main()
