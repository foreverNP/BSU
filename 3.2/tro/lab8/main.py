import tkinter as tk
from tkinter import ttk
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np


class DisasterMonitoringSystem:
    def __init__(self, root):
        self.root = root
        self.root.title("Система мониторинга опасного региона")
        self.root.geometry("1200x800")
        self.root.option_add("*Font", ("Arial", 17))

        # Define data structures with objects
        self.objects = {
            "cities": {
                1: {"name": "Лазаревское", "pos": (150, 150), "value": 0, "status": "норма"},
                2: {"name": "Дагомыс", "pos": (200, 300), "value": 0, "status": "норма"},
                3: {"name": "Сочи", "pos": (220, 350), "value": 0, "status": "норма"},
                4: {"name": "Мацеста", "pos": (230, 400), "value": 0, "status": "норма"},
                5: {"name": "Кудепста", "pos": (250, 450), "value": 0, "status": "норма"},
                6: {"name": "Адлер", "pos": (270, 500), "value": 0, "status": "норма"},
            },
            "reserves": {
                1: {"name": "Заповедник 1", "pos": (350, 250), "value": 25, "status": "норма"},
                2: {"name": "Заповедник 2", "pos": (400, 450), "value": 25, "status": "норма"},
            },
            "sensors": {
                1: {"name": "Сейсмодатчик 1", "pos": (250, 100), "value": 0, "status": "норма"},
                2: {"name": "Сейсмодатчик 2", "pos": (320, 180), "value": 0, "status": "норма"},
                3: {"name": "Сейсмодатчик 3", "pos": (450, 320), "value": 0, "status": "норма"},
            },
        }

        # Thresholds and disaster attributes
        self.thresholds = {
            "cities": {"норма": 50, "опасность": 80},  # cm
            "reserves": {"норма": 40, "опасность": 60},  # C
            "sensors": {"норма": 3, "опасность": 5},  # Richter
        }

        self.disaster_attrs = {
            "cities": {"name": "Наводнение", "unit": "см", "field": "value", "color": "blue"},
            "reserves": {"name": "Пожар", "unit": "°C", "field": "value", "color": "red"},
            "sensors": {"name": "Землетрясение", "unit": "Р", "field": "value", "color": "purple"},
        }

        self.disaster_shapes = {
            "cities": {"shape": "o", "name": "Датчики уровня воды: •"},
            "reserves": {"shape": "s", "name": "Термодатчики: ■"},
            "sensors": {"shape": "^", "name": "Сейсмодатчики: ▲"},
        }

        self.create_ui()
        self.update_all()

    def create_ui(self):
        # Create main frames
        self.left_frame = tk.Frame(self.root, width=800, height=800)
        self.left_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

        self.right_frame = tk.Frame(self.root, width=400, height=800, bg="lightgray")
        self.right_frame.pack(side=tk.RIGHT, fill=tk.BOTH)

        # Create map
        self.fig, self.ax = plt.subplots(figsize=(8, 7))
        self.canvas = FigureCanvasTkAgg(self.fig, master=self.left_frame)
        self.canvas_widget = self.canvas.get_tk_widget()
        self.canvas_widget.pack(fill=tk.BOTH, expand=True)

        # Create control panel
        tk.Label(self.right_frame, text="Панель управления", font=("Arial", 16, "bold"), bg="lightgray").pack(pady=10)

        # Create disaster simulation controls
        self.disaster_frame = tk.LabelFrame(self.right_frame, text="Симуляция бедствий", padx=10, pady=10, bg="lightgray")
        self.disaster_frame.pack(fill=tk.X, padx=10, pady=5)

        # Create simulation controls for each disaster type
        self.disaster_widgets = {}
        row = 0

        for disaster_type, objects in self.objects.items():
            # Create combobox for object selection
            tk.Label(self.disaster_frame, text=f"{self.disaster_attrs[disaster_type]['name']} ({disaster_type}):", bg="lightgray").grid(
                row=row, column=0, sticky=tk.W, pady=5
            )

            combo = ttk.Combobox(self.disaster_frame, values=[obj["name"] for obj in objects.values()])
            combo.grid(row=row, column=1, pady=5)
            combo.current(0)
            row += 1

            # Create strength slider
            tk.Label(self.disaster_frame, text="Сила:", bg="lightgray").grid(row=row, column=0, sticky=tk.W, pady=5)

            max_val = 100 if disaster_type != "sensors" else 9
            slider = ttk.Scale(self.disaster_frame, from_=0, to=max_val, orient=tk.HORIZONTAL)
            slider.grid(row=row, column=1, pady=5)
            slider.set(0)
            row += 1

            # Create simulation button
            btn = tk.Button(
                self.disaster_frame,
                text=f"Симулировать {self.disaster_attrs[disaster_type]['name'].lower()}",
                command=lambda t=disaster_type: self.simulate_disaster(t),
            )
            btn.grid(row=row, column=0, columnspan=2, pady=10)
            row += 1

            # Store widgets for later access
            self.disaster_widgets[disaster_type] = {"combo": combo, "slider": slider}

        # Create status panel
        self.status_frame = tk.LabelFrame(self.right_frame, text="Статус объектов", padx=10, pady=10, bg="lightgray")
        self.status_frame.pack(fill=tk.X, padx=10, pady=5)

        self.status_text = tk.Text(self.status_frame, height=10, width=45)
        self.status_text.pack(fill=tk.X)

        # Create command center panel
        self.command_frame = tk.LabelFrame(self.right_frame, text="Центр управления", padx=10, pady=10, bg="lightgray")
        self.command_frame.pack(fill=tk.X, padx=10, pady=5)

        self.command_text = tk.Text(self.command_frame, height=10, width=45)
        self.command_text.pack(fill=tk.X)

        # Reset button
        tk.Button(self.right_frame, text="Сбросить все датчики", command=self.reset_all).pack(pady=10)

    def update_map(self):
        self.ax.clear()

        # Draw background
        self.ax.set_xlim(0, 600)
        self.ax.set_ylim(0, 600)
        self.ax.set_facecolor("#d4ffd4")

        # Draw sea
        sea_rect = plt.Rectangle((0, 0), 100, 600, facecolor="lightblue")
        self.ax.add_patch(sea_rect)

        # Draw rivers
        for y in range(100, 550, 50):
            x_points = np.linspace(100, 550, 20)
            y_points = y + np.sin(x_points / 30) * 20
            self.ax.plot(x_points, y_points, color="blue", alpha=0.5)

        # Draw all objects by type
        for obj_type, objects in self.objects.items():
            shape = self.disaster_shapes[obj_type]["shape"]

            for obj_id, obj in objects.items():
                # Determine color based on status
                color = "green"
                if obj["status"] == "опасность":
                    color = "orange"
                elif obj["status"] == "катастрофа":
                    color = "red"

                # Plot object
                self.ax.scatter(obj["pos"][0], obj["pos"][1], marker=shape, color=color, s=100, zorder=5)
                self.ax.text(obj["pos"][0] + 5, obj["pos"][1], obj["name"], fontsize=10)

                # Draw value indicator if active
                if obj["value"] > 0:
                    unit = self.disaster_attrs[obj_type]["unit"]
                    color = self.disaster_attrs[obj_type]["color"]
                    self.ax.text(obj["pos"][0], obj["pos"][1] - 15, f"{obj['value']}{unit}", color=color, fontsize=10)

        # Draw legend
        y_pos = 50
        for obj_type, shape_info in self.disaster_shapes.items():
            self.ax.text(10, y_pos, shape_info["name"], color="black")
            y_pos -= 20

        self.ax.set_title("Мониторинг потенциально опасного региона")
        self.canvas.draw()

    def update_status_text(self):
        self.status_text.delete(1.0, tk.END)

        for obj_type, objects in self.objects.items():
            title = obj_type.upper()
            if obj_type == "cities":
                title += " (датчики уровня воды)"
            elif obj_type == "reserves":
                title += " (термодатчики)"

            self.status_text.insert(tk.END, f"{title}:\n")

            for obj_id, obj in objects.items():
                status_color = "black"
                if obj["status"] == "опасность":
                    status_color = "orange"
                elif obj["status"] == "катастрофа":
                    status_color = "red"

                unit = self.disaster_attrs[obj_type]["unit"]
                self.status_text.insert(tk.END, f"{obj['name']}: {obj['value']}{unit} - ")
                self.status_text.insert(tk.END, f"{obj['status']}\n", status_color)

            self.status_text.insert(tk.END, "\n")

    def update_command_text(self):
        self.command_text.delete(1.0, tk.END)
        has_disaster = False

        for obj_type, objects in self.objects.items():
            disaster_name = self.disaster_attrs[obj_type]["name"]

            for obj_id, obj in objects.items():
                if obj["status"] != "норма":
                    has_disaster = True
                    self.command_text.insert(tk.END, f"{disaster_name} - {obj['name']}:\n")

                    # Commands for emergency services
                    self.command_text.insert(tk.END, "МЧС: ")
                    if obj["status"] == "опасность":
                        if obj_type == "sensors":
                            self.command_text.insert(tk.END, "Привести службы в повышенную готовность\n")
                        else:
                            self.command_text.insert(tk.END, "Отправить БПЛА для мониторинга\n")
                    else:  # Catastrophe
                        if obj_type == "cities":
                            self.command_text.insert(tk.END, "Отправить спасательные бригады\n")
                        elif obj_type == "reserves":
                            self.command_text.insert(tk.END, "Отправить пожарных и авиацию\n")
                        else:  # sensors
                            self.command_text.insert(tk.END, "Развернуть спасательные бригады\n")

                    # Commands for population
                    self.command_text.insert(tk.END, "Население: ")
                    if obj["status"] == "опасность":
                        if obj_type == "cities":
                            self.command_text.insert(tk.END, "Подготовиться к возможной эвакуации\n")
                        elif obj_type == "reserves":
                            self.command_text.insert(tk.END, "Наблюдать, не приближаться к заповеднику\n")
                        else:  # sensors
                            self.command_text.insert(tk.END, "Подготовиться, отойти от высотных зданий\n")
                    else:  # Catastrophe
                        if obj_type == "sensors":
                            self.command_text.insert(tk.END, "Эвакуация, избегать зданий и сооружений\n")
                        else:
                            self.command_text.insert(tk.END, "Срочная эвакуация\n")

                    self.command_text.insert(tk.END, "\n")

        if not has_disaster:
            self.command_text.insert(tk.END, "Все системы в норме. Особых указаний нет.\n")
            self.command_text.insert(tk.END, "МЧС: Штатный режим наблюдения\n")
            self.command_text.insert(tk.END, "Население: Без ограничений\n")

    def simulate_disaster(self, disaster_type):
        combo = self.disaster_widgets[disaster_type]["combo"]
        slider = self.disaster_widgets[disaster_type]["slider"]

        obj_name = combo.get()
        strength = slider.get()

        # Find object ID
        obj_id = None
        for oid, obj in self.objects[disaster_type].items():
            if obj["name"] == obj_name:
                obj_id = oid
                break

        if obj_id is not None:
            # Update value based on strength
            if disaster_type == "cities":
                value = int(strength)
            elif disaster_type == "reserves":
                value = 25 + int(strength * 0.75)
            else:  # sensors
                value = round(strength, 1)

            self.objects[disaster_type][obj_id]["value"] = value

            # Update status based on thresholds
            thresholds = self.thresholds[disaster_type]

            if value >= thresholds["опасность"]:
                self.objects[disaster_type][obj_id]["status"] = "катастрофа"
            elif value >= thresholds["норма"]:
                self.objects[disaster_type][obj_id]["status"] = "опасность"
            else:
                self.objects[disaster_type][obj_id]["status"] = "норма"

            self.update_all()

    def reset_all(self):
        for obj_type, objects in self.objects.items():
            default_value = 0 if obj_type != "reserves" else 25

            for obj_id in objects:
                self.objects[obj_type][obj_id]["value"] = default_value
                self.objects[obj_type][obj_id]["status"] = "норма"

        self.update_all()

    def update_all(self):
        self.update_map()
        self.update_status_text()
        self.update_command_text()


if __name__ == "__main__":
    root = tk.Tk()
    app = DisasterMonitoringSystem(root)
    root.mainloop()
