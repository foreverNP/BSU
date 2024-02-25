import tkinter as tk
from tkinter import ttk, messagebox, scrolledtext

rules = [
    {
        "name": "P1",
        "premises": {"класс": "голосеменные", "структура листа": "чешуеобразная"},
        "conclusion": ("семейство", "кипарисовые"),
    },
    {
        "name": "P2",
        "premises": {"класс": "голосеменные", "структура листа": "иглоподобная"},
        "conclusion": ("семейство", "сосновые"),
    },
    {
        "name": "P3",
        "premises": {"семейство": "сосновые"},
        "conclusion": ("тип леса", "хвойный"),
    },
    {
        "name": "P4",
        "premises": {"класс": "покрытосеменные", "структура листа": "широкая"},
        "conclusion": ("семейство", "дубовые"),
    },
    {
        "name": "P5",
        "premises": {"семейство": "дубовые"},
        "conclusion": ("тип леса", "лиственный"),
    },
    {
        "name": "P6",
        "premises": {"класс": "покрытосеменные", "структура листа": "сложная"},
        "conclusion": ("семейство", "березовые"),
    },
    {
        "name": "P7",
        "premises": {"семейство": "березовые"},
        "conclusion": ("тип леса", "лиственный"),
    },
]

feature_values = {
    "класс": ["голосеменные", "покрытосеменные"],
    "структура листа": ["чешуеобразная", "иглоподобная", "широкая", "сложная"],
    "семейство": ["кипарисовые", "сосновые", "дубовые", "березовые"],
    "тип леса": ["хвойный", "лиственный", "смешанный"],
}


class ProductionSystemGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Система продукционного вывода")
        self.root.geometry("800x600")

        # Зададим множества признаков
        self.features_pos = set()  # посылочные признаки
        self.features_concl = set()  # заключительные признаки

        for rule in rules:
            self.features_pos.update(rule["premises"].keys())
            self.features_concl.add(rule["conclusion"][0])

        # Хранение известных фактов
        self.known_facts = {}

        # Хранение информации о текущем выводе
        self.current_query = None
        self.current_feature = None

        self.create_widgets()

    def create_widgets(self):
        # Верхняя панель для выбора признака для вывода
        top_frame = ttk.Frame(self.root, padding=10)
        top_frame.pack(fill=tk.X, padx=10, pady=5)

        ttk.Label(top_frame, text="Выберите признак для вывода:").pack(
            side=tk.LEFT, padx=5
        )

        self.target_var = tk.StringVar()
        self.target_combo = ttk.Combobox(
            top_frame,
            textvariable=self.target_var,
            values=sorted(list(self.features_concl)),
            state="readonly",
        )
        self.target_combo.pack(side=tk.LEFT, padx=5)

        ttk.Button(top_frame, text="Начать вывод", command=self.start_inference).pack(
            side=tk.LEFT, padx=5
        )
        ttk.Button(top_frame, text="Сбросить", command=self.reset_system).pack(
            side=tk.LEFT, padx=5
        )

        # Панель для отображения правил и логов
        middle_frame = ttk.Frame(self.root, padding=10)
        middle_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=5)

        # Разделение экрана на две части
        paned = ttk.PanedWindow(middle_frame, orient=tk.HORIZONTAL)
        paned.pack(fill=tk.BOTH, expand=True)

        # Левая часть - правила
        rules_frame = ttk.LabelFrame(paned, text="База правил", padding=10)

        rules_text = scrolledtext.ScrolledText(
            rules_frame, wrap=tk.WORD, width=40, height=15
        )
        rules_text.pack(fill=tk.BOTH, expand=True)
        rules_text.insert(tk.END, self.format_rules_text())
        rules_text.config(state=tk.DISABLED)

        paned.add(rules_frame, weight=1)

        # Правая часть - логи
        log_frame = ttk.LabelFrame(paned, text="Лог вывода", padding=10)

        self.log_text = scrolledtext.ScrolledText(
            log_frame, wrap=tk.WORD, width=40, height=15
        )
        self.log_text.pack(fill=tk.BOTH, expand=True)
        self.log_text.config(state=tk.DISABLED)

        paned.add(log_frame, weight=1)

        # Нижняя панель для выбора значения пользователем
        self.input_frame = ttk.LabelFrame(self.root, text="Выбор значения", padding=10)
        self.input_frame.pack(fill=tk.X, padx=10, pady=5)

        self.feature_label = ttk.Label(self.input_frame, text="")
        self.feature_label.pack(side=tk.LEFT, padx=5)

        self.input_var = tk.StringVar()
        self.input_combo = ttk.Combobox(
            self.input_frame, textvariable=self.input_var, state="readonly"
        )
        self.input_combo.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=5)

        self.submit_button = ttk.Button(
            self.input_frame, text="Подтвердить", command=self.process_user_input
        )
        self.submit_button.pack(side=tk.LEFT, padx=5)

        # Изначально скрываем панель ввода
        self.input_frame.pack_forget()

        # Панель для отображения известных фактов
        self.facts_frame = ttk.LabelFrame(self.root, text="Известные факты", padding=10)
        self.facts_frame.pack(fill=tk.X, padx=10, pady=5)

        self.facts_text = scrolledtext.ScrolledText(
            self.facts_frame, wrap=tk.WORD, height=5
        )
        self.facts_text.pack(fill=tk.BOTH, expand=True)
        self.facts_text.config(state=tk.DISABLED)

    def format_rules_text(self):
        """Форматирует правила для отображения"""
        text = ""
        for rule in rules:
            prem_str = " ∧ ".join(
                ["({} = {})".format(k, v) for k, v in rule["premises"].items()]
            )
            concl = rule["conclusion"]
            text += "Правило {}: {} ⇒ ({} = {})\n\n".format(
                rule["name"], prem_str, concl[0], concl[1]
            )
        return text

    def log(self, message):
        """Добавляет сообщение в лог"""
        self.log_text.config(state=tk.NORMAL)
        self.log_text.insert(tk.END, message + "\n")
        self.log_text.see(tk.END)
        self.log_text.config(state=tk.DISABLED)
        self.root.update()

    def update_facts_display(self):
        """Обновляет отображение известных фактов"""
        self.facts_text.config(state=tk.NORMAL)
        self.facts_text.delete(1.0, tk.END)

        if not self.known_facts:
            self.facts_text.insert(tk.END, "Нет известных фактов")
        else:
            facts_str = "\n".join(
                ["{} = {}".format(k, v) for k, v in sorted(self.known_facts.items())]
            )
            self.facts_text.insert(tk.END, facts_str)

        self.facts_text.config(state=tk.DISABLED)

    def reset_system(self):
        """Сбрасывает систему в исходное состояние"""
        self.known_facts = {}
        self.current_query = None
        self.current_feature = None

        # Очищаем лог
        self.log_text.config(state=tk.NORMAL)
        self.log_text.delete(1.0, tk.END)
        self.log_text.config(state=tk.DISABLED)

        # Скрываем панель ввода
        self.input_frame.pack_forget()

        # Обновляем отображение фактов
        self.update_facts_display()

        self.log("Система сброшена в исходное состояние.")

    def start_inference(self):
        """Начинает процесс вывода"""
        target = self.target_var.get()

        if not target:
            messagebox.showwarning(
                "Предупреждение", "Пожалуйста, выберите признак для вывода"
            )
            return

        self.log("\n=== Начало нового вывода для признака '{}' ===\n".format(target))

        self.current_query = target
        self.infer(target)

    def get_table_goals(self, goal):
        """Возвращает таблицу целей и выводит ее в лог"""
        self.log("\n--- Таблица целей для признака '{}' ---".format(goal))
        t_goals = [rule for rule in rules if rule["conclusion"][0] == goal]

        if not t_goals:
            self.log("Нет правил для данного признака.")
        else:
            for rule in t_goals:
                prem_str = " ∧ ".join(
                    ["({} = {})".format(k, v) for k, v in rule["premises"].items()]
                )
                concl = rule["conclusion"]
                self.log(
                    "Правило {}: {} ⇒ ({} = {})".format(
                        rule["name"], prem_str, concl[0], concl[1]
                    )
                )

        self.log("------------------------------------------\n")
        return t_goals

    def ask_user(self, feature):
        """Запрашивает у пользователя значение для указанного признака из предопределенных вариантов"""
        # Сохраняем текущий признак
        self.current_feature = feature

        # Получаем возможные значения для этого признака
        if feature in feature_values:
            values = feature_values[feature]
        else:
            self.log(
                "Ошибка: нет предопределенных значений для признака '{}'".format(
                    feature
                )
            )
            return None

        # Показываем панель выбора
        self.feature_label.config(
            text="Выберите значение для признака '{}':".format(feature)
        )
        self.input_combo.config(values=values)
        self.input_var.set("")
        self.input_frame.pack(fill=tk.X, padx=10, pady=5)
        self.input_combo.focus_set()

        # Ждем выбора пользователя (обработка в process_user_input)
        return None

    def process_user_input(self):
        """Обрабатывает выбор пользователя и продолжает вывод"""
        user_val = self.input_var.get().strip()

        if not user_val:
            messagebox.showwarning("Предупреждение", "Пожалуйста, выберите значение")
            return

        feature = self.current_feature

        # Скрываем панель выбора
        self.input_frame.pack_forget()

        # Сохраняем значение
        self.known_facts[feature] = user_val
        self.update_facts_display()

        self.log("Получено значение: {} = {}".format(feature, user_val))

        # Продолжаем вывод
        self.continue_inference(feature, user_val)

    def continue_inference(self, feature, value):
        """Продолжает процесс вывода после получения значения от пользователя"""
        # Находим незавершенное правило и продолжаем проверку
        t_goals = [
            rule for rule in rules if rule["conclusion"][0] == self.current_query
        ]

        # Выбираем правило с минимальным числом посылок
        t_goals.sort(key=lambda r: len(r["premises"]))

        for rule in t_goals:
            premises = rule["premises"]

            # Если это не то правило, которое мы проверяли - пропускаем
            if feature not in premises:
                continue

            all_satisfied = True

            # Проверяем все посылки заново
            for prem_feature, prem_value in premises.items():
                if prem_feature in self.known_facts:
                    if self.known_facts[prem_feature] != prem_value:
                        self.log(
                            "Невыполнение правила {}: для признака '{}' установлено значение '{}', а требуется '{}'.".format(
                                rule["name"],
                                prem_feature,
                                self.known_facts[prem_feature],
                                prem_value,
                            )
                        )
                        all_satisfied = False
                        break
                    else:
                        self.log(
                            "Посылка ({0} = {1}) удовлетворена.".format(
                                prem_feature, prem_value
                            )
                        )
                else:
                    # Если еще не все посылки проверены - запускаем проверку для следующей
                    if prem_feature not in self.features_concl:
                        # Запрос значения у пользователя
                        self.log(
                            "Запрашиваем значение для признака '{}'...".format(
                                prem_feature
                            )
                        )
                        self.ask_user(prem_feature)
                        return  # Прерываем выполнение, ждем выбора пользователя
                    else:
                        # Запускаем вывод для этой посылки
                        self.log(
                            "Для посылки ({0} = {1}) запускаем вывод по признаку '{0}'.".format(
                                prem_feature, prem_value
                            )
                        )
                        self.current_query = prem_feature
                        result = self.infer(prem_feature)

                        if result is None:
                            # Вывод приостановлен, ждем выбора пользователя
                            return

                        if result != prem_value:
                            self.log(
                                "Невыполнение правила {}: для признака '{}' получено значение '{}', а требуется '{}'.".format(
                                    rule["name"], prem_feature, result, prem_value
                                )
                            )
                            all_satisfied = False
                            break

            if all_satisfied:
                # Если правило удовлетворено, устанавливаем заключение
                concl_feature, concl_value = rule["conclusion"]
                self.known_facts[concl_feature] = concl_value
                self.update_facts_display()

                self.log(
                    "\n>>> Вывод: Признак '{}' имеет значение '{}' (по правилу {}).\n".format(
                        concl_feature, concl_value, rule["name"]
                    )
                )

                # Если это ответ на исходный запрос - показываем сообщение
                if concl_feature == self.current_query:
                    messagebox.showinfo(
                        "Результат вывода",
                        "Получен результат: {} = {}".format(concl_feature, concl_value),
                    )
                return concl_value
            else:
                self.log(
                    "Правило {} не сработало. Переходим к следующему правилу (если есть).\n".format(
                        rule["name"]
                    )
                )

        self.log(
            "Не удалось вывести значение для признака '{}'.".format(self.current_query)
        )
        messagebox.showinfo(
            "Результат вывода",
            "Не удалось вывести значение для признака '{}'.".format(self.current_query),
        )
        return None

    def infer(self, goal):
        """
        Функция вывода для заданного признака (цели).
        Возвращает значение, если вывод возможен, иначе None.
        """
        # Если факт уже известен, возвращаем его
        if goal in self.known_facts:
            val = self.known_facts[goal]
            self.log("Признак '{}' уже имеет значение '{}'.".format(goal, val))
            return val

        # Выводим таблицу целей для текущего признака
        t_goals = self.get_table_goals(goal)

        if not t_goals:
            self.log(
                "Вывод невозможен: отсутствуют правила для признака '{}'.".format(goal)
            )
            messagebox.showinfo(
                "Результат вывода",
                "Вывод невозможен: отсутствуют правила для признака '{}'.".format(goal),
            )
            return None

        # Выбираем правило с минимальным числом посылок
        t_goals.sort(key=lambda r: len(r["premises"]))

        for rule in t_goals:
            self.log("Проверяем правило {}...".format(rule["name"]))
            premises = rule["premises"]
            all_satisfied = True

            # Для каждой посылки проверяем, установлено ли значение
            for prem_feature, prem_value in premises.items():
                if prem_feature in self.known_facts:
                    # Если факт уже известен – проверяем, совпадает ли он с требуемым
                    if self.known_facts[prem_feature] != prem_value:
                        self.log(
                            "Невыполнение правила {}: для признака '{}' установлено значение '{}', а требуется '{}'.".format(
                                rule["name"],
                                prem_feature,
                                self.known_facts[prem_feature],
                                prem_value,
                            )
                        )
                        all_satisfied = False
                        break
                    else:
                        self.log(
                            "Посылка ({0} = {1}) удовлетворена.".format(
                                prem_feature, prem_value
                            )
                        )
                else:
                    # Если факт не известен
                    if prem_feature not in self.features_concl:
                        # Запрос значения у пользователя
                        self.log(
                            "Запрашиваем значение для признака '{}'...".format(
                                prem_feature
                            )
                        )
                        self.ask_user(prem_feature)
                        return None  # Прерываем выполнение, ждем выбора пользователя
                    else:
                        # Запускаем вывод для этой посылки
                        self.log(
                            "Для посылки ({0} = {1}) запускаем вывод по признаку '{0}'.".format(
                                prem_feature, prem_value
                            )
                        )

                        # Сохраняем текущую цель
                        old_query = self.current_query
                        self.current_query = prem_feature

                        # Запускаем вывод
                        inferred_val = self.infer(prem_feature)

                        # Восстанавливаем цель
                        self.current_query = old_query

                        if inferred_val is None:
                            # Вывод приостановлен, ждем выбора пользователя
                            return None

                        if inferred_val != prem_value:
                            self.log(
                                "Невыполнение правила {}: для признака '{}' получено значение '{}', а требуется '{}'.".format(
                                    rule["name"], prem_feature, inferred_val, prem_value
                                )
                            )
                            all_satisfied = False
                            break

            if all_satisfied:
                # Если правило удовлетворено, устанавливаем заключение
                concl_feature, concl_value = rule["conclusion"]
                self.known_facts[concl_feature] = concl_value
                self.update_facts_display()

                self.log(
                    "\n>>> Вывод: Признак '{}' имеет значение '{}' (по правилу {}).\n".format(
                        concl_feature, concl_value, rule["name"]
                    )
                )

                # Если это ответ на исходный запрос - показываем сообщение
                if concl_feature == goal == self.current_query:
                    messagebox.showinfo(
                        "Результат вывода",
                        "Получен результат: {} = {}".format(concl_feature, concl_value),
                    )
                return concl_value
            else:
                self.log(
                    "Правило {} не сработало. Переходим к следующему правилу (если есть).\n".format(
                        rule["name"]
                    )
                )

        self.log("Не удалось вывести значение для признака '{}'.".format(goal))

        if goal == self.current_query:
            messagebox.showinfo(
                "Результат вывода",
                "Не удалось вывести значение для признака '{}'.".format(goal),
            )
        return None


def main():
    root = tk.Tk()
    app = ProductionSystemGUI(root)
    root.mainloop()


if __name__ == "__main__":
    main()
