import tkinter as tk
from tkinter import messagebox, ttk
from rich.console import Console
from rich.table import Table

log_table = []
step_counter = 1


def log_step(rule, answer, accepted_rule=""):
    global step_counter
    log_table.append(
        {
            "Шаг": step_counter,
            "Анализируемое правило": rule,
            "Ответ на вопрос": answer,
            "№ принимаемого правила": accepted_rule,
        }
    )
    step_counter += 1


def ask_question_tk(question):
    """
    Отображает модальное окно с вопросом и кнопками 'Да' и 'Нет'.
    Возвращает True, если выбран ответ 'Да', иначе False.
    """
    return messagebox.askyesno("Вопрос", question)


def main():
    # Инициализация основного окна Tkinter (оно будет скрыто)
    root = tk.Tk()
    root.withdraw()  # Скрываем главное окно

    # Вывод вступительного сообщения
    messagebox.showinfo(
        "Система диагностики неисправностей ПК",
        "Ответьте на вопросы, нажимая 'Да' или 'Нет'.",
    )

    problem = None
    action = None

    # Начинаем опрос пользователя
    computer_on = ask_question_tk("Включается ли компьютер?")
    log_step("Начало", f"Включается ли компьютер: {computer_on}")

    if not computer_on:
        power_indicator = ask_question_tk("Светится ли индикатор питания?")
        log_step("Правило 1/2", f"Светится ли индикатор питания: {power_indicator}")
        if not power_indicator:
            problem = "отсутствие питания"  # Правило 1
            log_step("Правило 1", "Все условия выполнены", "Правило 1 принято")
        else:
            problem = "неисправность блока питания"  # Правило 2
            log_step("Правило 2", "Все условия выполнены", "Правило 2 принято")
    else:
        sound = ask_question_tk("Воспроизводится ли звук?")
        log_step("Правило 3", f"Воспроизводится ли звук: {sound}")
        if not sound:
            problem = "неисправность звуковой карты"  # Правило 3
            log_step("Правило 3", "Все условия выполнены", "Правило 3 принято")
        else:
            display = ask_question_tk("Отображает ли монитор изображение?")
            log_step("Правило 4", f"Отображает ли монитор изображение: {display}")
            if not display:
                problem = "неисправность видеокарты"  # Правило 4
                log_step("Правило 4", "Все условия выполнены", "Правило 4 принято")
            else:
                os_load = ask_question_tk("Загружается ли операционная система?")
                log_step("Правило 5", f"Загружается ли операционная система: {os_load}")
                if not os_load:
                    problem = "повреждение системных файлов"  # Правило 5
                    log_step("Правило 5", "Все условия выполнены", "Правило 5 принято")
                    action = "провести восстановление системы"  # Правило 6
                    log_step("Правило 6", "Все условия выполнены", "Правило 6 принято")
                else:
                    screen_off = ask_question_tk(
                        "Гаснет ли экран через некоторое время работы?"
                    )
                    log_step(
                        "Правило 7",
                        f"Гаснет ли экран через некоторое время работы: {screen_off}",
                    )
                    if screen_off:
                        log_step(
                            "Правило 7", "Все условия выполнены", "Правило 7 принято"
                        )
                        action = "провести тестирование модулей RAM"  # Правило 8
                        log_step(
                            "Правило 8", "Все условия выполнены", "Правило 8 принято"
                        )
                        ram_test = ask_question_tk(
                            "Показало ли тестирование модулей RAM сбои?"
                        )
                        log_step(
                            "Правило 9",
                            f"Показало ли тестирование модулей RAM сбои: {ram_test}",
                        )
                        if ram_test:
                            problem = "неисправность модулей RAM"  # Правило 9
                            log_step(
                                "Правило 9",
                                "Все условия выполнены",
                                "Правило 9 принято",
                            )
                    virus = ask_question_tk(
                        "Аномально высоко ли потребление оперативной памяти при минимальном количестве запущенных приложений?"
                    )
                    log_step(
                        "Правило 10",
                        f"Аномально высоко ли потребление оперативной памяти: {virus}",
                    )
                    if virus:
                        problem = "вирусное заражение системы"  # Правило 10
                        log_step(
                            "Правило 10", "Все условия выполнены", "Правило 10 принято"
                        )

    result_message = ""
    if problem:
        result_message += f"Обнаруженная проблема: {problem}\n"
    else:
        result_message += "Проблема не выявлена.\n"
    if action:
        result_message += f"Рекомендуемое действие: {action}\n"

    messagebox.showinfo("Результаты диагностики", result_message)

    # Отображение протокола работы в новом окне с использованием Treeview
    log_window = tk.Toplevel(root)
    log_window.title("Протокол работы")

    columns = (
        "Шаг",
        "Анализируемое правило",
        "Ответ на вопрос",
        "№ принимаемого правила",
    )
    tree = ttk.Treeview(log_window, columns=columns, show="headings", height=10)
    for col in columns:
        tree.heading(col, text=col)
        tree.column(col, width=180, anchor="center")
    for row in log_table:
        tree.insert(
            "",
            tk.END,
            values=(
                row["Шаг"],
                row["Анализируемое правило"],
                row["Ответ на вопрос"],
                row["№ принимаемого правила"],
            ),
        )
    tree.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

    close_button = ttk.Button(log_window, text="Закрыть", command=log_window.destroy)
    close_button.pack(pady=5)

    with open("logs.txt", "w", encoding="utf-8") as f:
        console = Console(file=f, width=120)
        table = Table(show_header=True, header_style="bold magenta")
        table.add_column("Шаг", justify="center")
        table.add_column("Анализируемое правило", justify="center")
        table.add_column("Ответ на вопрос", justify="center")
        table.add_column("№ принимаемого правила", justify="center")
        for row in log_table:
            table.add_row(
                str(row["Шаг"]),
                str(row["Анализируемое правило"]),
                str(row["Ответ на вопрос"]),
                str(row["№ принимаемого правила"]),
            )
        console.print(table)
    messagebox.showinfo("Завершено", "Лог работы сохранён в файле logs.txt")

    root.mainloop()


if __name__ == "__main__":
    main()
