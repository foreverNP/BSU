import tkinter as tk
from tkinter import messagebox, ttk
from inference_engine import KnowledgeBase, InferenceEngine, Logger

kb = KnowledgeBase("rules.json")
logger = Logger()
engine = InferenceEngine(kb, logger)

COLORS = {
    "primary": "#3498db",
    "secondary": "#2980b9",
    "bg_light": "#f5f5f5",
    "bg_dark": "#e0e0e0",
    "text": "#2c3e50",
    "success": "#27ae60",
    "warning": "#f39c12",
    "error": "#e74c3c",
}


def run_engine():
    logger.logs.clear()
    selected = goal_var.get()
    if not selected:
        messagebox.showwarning("Ошибка", "Выберите цель")
        return

    goal_parts = selected.split(" = ")
    if len(goal_parts) != 2:
        messagebox.showerror("Ошибка", "Неверный формат цели")
        return

    goal = (goal_parts[0], eval(goal_parts[1]))
    success, context = engine.backward_chain(goal)

    result_text.config(state=tk.NORMAL)
    result_text.delete(1.0, tk.END)

    if success:
        result_text.insert(tk.END, "✅ Цель достигнута!\n\n", "success")
        result_text.insert(tk.END, "Условия:\n", "heading")
        for fact in context:
            result_text.insert(tk.END, f"• {fact[0]} = {fact[1]}\n", "fact")
    else:
        result_text.insert(tk.END, "❌ Цель не может быть достигнута.\n", "error")

    result_text.config(state=tk.DISABLED)


def show_log():
    log_window = tk.Toplevel(root)
    log_window.title("Протокол вывода")
    log_window.geometry("800x600")
    log_window.configure(bg=COLORS["bg_light"])

    title_frame = tk.Frame(log_window, bg=COLORS["primary"], padx=10, pady=10)
    title_frame.pack(fill=tk.X)

    title_label = tk.Label(title_frame, text="Подробный протокол работы системы", font=("Helvetica", 14, "bold"), bg=COLORS["primary"], fg="white")
    title_label.pack()

    content_frame = tk.Frame(log_window, bg=COLORS["bg_light"], padx=20, pady=20)
    content_frame.pack(fill=tk.BOTH, expand=True)

    log_text = tk.Text(
        content_frame,
        wrap=tk.WORD,
        width=80,
        height=30,
        bg="white",
        fg=COLORS["text"],
        font=("Consolas", 10),
        padx=10,
        pady=10,
        borderwidth=1,
        relief=tk.SOLID,
    )
    scrollbar = tk.Scrollbar(content_frame, command=log_text.yview)
    log_text.configure(yscrollcommand=scrollbar.set)

    log_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

    log_text.tag_configure("goal", foreground=COLORS["primary"], font=("Consolas", 10, "bold"))
    log_text.tag_configure("rule", foreground=COLORS["secondary"])
    log_text.tag_configure("success", foreground=COLORS["success"])
    log_text.tag_configure("error", foreground=COLORS["error"])

    for log in logger.logs:
        if "Цель достигнута" in log:
            log_text.insert(tk.END, log + "\n", "success")
        elif "Цель:" in log or "цель:" in log:
            log_text.insert(tk.END, log + "\n", "goal")
        elif "правило:" in log or "Правило:" in log:
            log_text.insert(tk.END, log + "\n", "rule")
        elif "не может быть достигнута" in log:
            log_text.insert(tk.END, log + "\n", "error")
        else:
            log_text.insert(tk.END, log + "\n")

    button_frame = tk.Frame(log_window, bg=COLORS["bg_light"], pady=10)
    button_frame.pack(fill=tk.X)

    close_button = tk.Button(
        button_frame, text="Закрыть", bg=COLORS["primary"], fg="white", padx=20, pady=5, font=("Helvetica", 10), command=log_window.destroy
    )
    close_button.pack()


# Setup main window
root = tk.Tk()
root.title("Продукционная система - Обратный вывод")
root.geometry("1920x1080")
root.configure(bg=COLORS["bg_light"])

# Create custom styles
style = ttk.Style()
style.theme_use("clam")
style.configure("TCombobox", fieldbackground="white", background=COLORS["bg_light"], foreground=COLORS["text"])

# Header frame
header_frame = tk.Frame(root, bg=COLORS["primary"], padx=20, pady=15)
header_frame.pack(fill=tk.X)

header_label = tk.Label(header_frame, text="Система обратного логического вывода", font=("Helvetica", 16, "bold"), bg=COLORS["primary"], fg="white")
header_label.pack()

# Content frame
content_frame = tk.Frame(root, bg=COLORS["bg_light"], padx=20, pady=20)
content_frame.pack(fill=tk.BOTH, expand=True)

# Goal selection frame
goal_frame = tk.LabelFrame(
    content_frame, text="Выбор цели", bg=COLORS["bg_light"], fg=COLORS["text"], font=("Helvetica", 10, "bold"), padx=15, pady=15
)
goal_frame.pack(fill=tk.X, pady=10)

# Goal selection components
goal_label = tk.Label(goal_frame, text="Выберите цель:", bg=COLORS["bg_light"], fg=COLORS["text"], font=("Helvetica", 10))
goal_label.grid(row=0, column=0, padx=5, pady=10, sticky="w")

derivable_goals = kb.get_all_derivable_goals()
goal_options = [f"{k} = {repr(v)}" for k, v in derivable_goals]
goal_var = tk.StringVar()
goal_menu = ttk.Combobox(goal_frame, textvariable=goal_var, values=goal_options, width=40)
goal_menu.grid(row=0, column=1, padx=5, pady=10)

run_button = tk.Button(
    goal_frame,
    text="Запустить",
    command=run_engine,
    bg=COLORS["primary"],
    fg="white",
    padx=15,
    pady=5,
    font=("Helvetica", 10),
    relief=tk.RAISED,
    borderwidth=1,
)
run_button.grid(row=0, column=2, padx=10, pady=10)


# Add hover effect to buttons
def on_enter(e):
    e.widget["background"] = COLORS["secondary"]


def on_leave(e):
    e.widget["background"] = COLORS["primary"]


run_button.bind("<Enter>", on_enter)
run_button.bind("<Leave>", on_leave)

# Results frame
results_frame = tk.LabelFrame(
    content_frame, text="Результаты", bg=COLORS["bg_light"], fg=COLORS["text"], font=("Helvetica", 10, "bold"), padx=15, pady=15
)
results_frame.pack(fill=tk.BOTH, expand=True, pady=10)

result_text = tk.Text(
    results_frame, wrap=tk.WORD, height=12, bg="white", fg=COLORS["text"], padx=10, pady=10, font=("Helvetica", 10), borderwidth=1, relief=tk.SOLID
)
result_text.pack(fill=tk.BOTH, expand=True)

# Add tags for styling text
result_text.tag_configure("success", foreground=COLORS["success"], font=("Helvetica", 12, "bold"))
result_text.tag_configure("error", foreground=COLORS["error"], font=("Helvetica", 12, "bold"))
result_text.tag_configure("heading", font=("Helvetica", 10, "bold"))
result_text.tag_configure("fact", foreground=COLORS["text"])

# Bottom button area
button_frame = tk.Frame(content_frame, bg=COLORS["bg_light"], pady=5)
button_frame.pack(fill=tk.X)

log_button = tk.Button(
    button_frame,
    text="Показать протокол",
    command=show_log,
    bg=COLORS["primary"],
    fg="white",
    padx=15,
    pady=5,
    font=("Helvetica", 10),
    relief=tk.RAISED,
    borderwidth=1,
)
log_button.pack(side=tk.RIGHT)
log_button.bind("<Enter>", on_enter)
log_button.bind("<Leave>", on_leave)


root.mainloop()
