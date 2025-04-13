import json


class KnowledgeBase:
    def __init__(self, filepath):
        with open(filepath, "r", encoding="utf-8") as f:
            self.rules = json.load(f)

    def find_rules_with_conclusion(self, goal):
        return [rule for rule in self.rules if rule["then"] == list(goal)]

    def is_derivable(self, condition):
        return any(rule["then"] == list(condition) for rule in self.rules)

    def get_all_derivable_goals(self):
        # Получаем все THEN из правил как возможные цели
        return list({tuple(rule["then"]) for rule in self.rules})


class Logger:
    def __init__(self):
        self.logs = []

    def log(self, message):
        print(message)
        self.logs.append(message)

    def save_to_file(self, filepath):
        with open(filepath, "w", encoding="utf-8") as f:
            f.write("\n".join(self.logs))


class InferenceEngine:
    def __init__(self, kb, logger):
        self.kb = kb
        self.logger = logger
        self.context_stack = []
        self.solved_goals = set()

    def backward_chain(self, goal):
        self.context_stack.clear()
        self.solved_goals.clear()

        goal_stack = [goal]
        self.logger.log(f"Начальная цель: {goal[0]} = {goal[1]}")

        while goal_stack:
            current_goal = goal_stack[-1]
            self.logger.log(f"Текущая цель: {current_goal[0]} = {current_goal[1]}")

            rules = self.kb.find_rules_with_conclusion(current_goal)
            if not rules:
                if len(goal_stack) == 1:
                    self.logger.log("Подходящее правило не найдено. Цель не может быть достигнута.")
                    return False, []
                else:
                    goal_stack.pop()
                    continue

            rule = rules[0]
            self.logger.log(f"Найдено правило: IF {rule['if']} THEN {rule['then']}")

            all_conditions_met = True
            for condition in rule["if"]:
                condition = tuple(condition)

                if self.kb.is_derivable(condition):
                    if condition in self.solved_goals:
                        continue
                    if condition not in goal_stack:
                        goal_stack.append(condition)
                        self.logger.log(f"Добавлена новая цель: {condition[0]} = {condition[1]}")
                        all_conditions_met = False
                        break
                    else:
                        self.logger.log(f"Пропущена цель (уже в стеке): {condition[0]} = {condition[1]}")
                        all_conditions_met = False
                        break
                else:
                    if condition not in self.context_stack:
                        self.context_stack.append(condition)
                        self.logger.log(f"Добавлено в контекст: {condition[0]} = {condition[1]}")

            if all_conditions_met:
                self.solved_goals.add(current_goal)
                goal_stack.pop()
                self.logger.log(f"Цель достигнута: {current_goal[0]} = {current_goal[1]}")

        return True, self.context_stack
