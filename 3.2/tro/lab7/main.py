import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import time
from datetime import datetime
import random
import matplotlib.animation as animation


class ThermosensorData:
    def __init__(self, sensor_id, x, y):
        self.sensor_id = sensor_id
        self.x = x
        self.y = y
        self.status = "Норма"
        self.recommendation = "Ждать"

    def update_status(self, status):
        self.status = status
        if status == "Норма":
            self.recommendation = "Ждать"
        elif status == "Внимание":
            self.recommendation = "Отправить локальный ППЭА"
        elif status == "Пожар":
            self.recommendation = "Отправить пожарную команду"


class Drone:
    def __init__(self, drone_id, x, y):
        self.drone_id = drone_id
        self.x = x
        self.y = y
        self.status = "Ожидание"
        self.target = None
        self.water = 100

    def move_towards(self, target_x, target_y):
        dx = target_x - self.x
        dy = target_y - self.y
        distance = np.sqrt(dx**2 + dy**2)

        if distance < 0.5:
            self.x = target_x
            self.y = target_y
            return True
        else:
            step = 0.5
            self.x += (dx / distance) * step
            self.y += (dy / distance) * step
            return False

    def extinguish_fire(self):
        if self.water > 0:
            self.water -= 5
            return True
        return False

    def refill_water(self):
        self.water = 100


class ForestFireMonitoringSystem:
    def __init__(self, width=50, height=50):
        self.width = width
        self.height = height
        self.sensors = []
        self.drones = []
        self.fire_locations = []
        self.forest_area = np.zeros((height, width))
        self.initialize_system()

    def initialize_system(self):
        sensor_positions = [
            (5, 45),
            (7, 45),
            (9, 45),
            (10, 45),
            (12, 45),
            (15, 45),
            (17, 45),
            (19, 45),
            (22, 45),
            (24, 45),
        ]

        for i, (x, y) in enumerate(sensor_positions):
            sensor_id = f"d-{i + 1:02d}"
            self.sensors.append(ThermosensorData(sensor_id, x, y))

        base_x, base_y = 25, 5
        for i in range(30):
            drone_id = f"drone-{i + 1:02d}"
            x = base_x + random.uniform(-2, 2)
            y = base_y + random.uniform(-2, 2)
            self.drones.append(Drone(drone_id, x, y))

        # Иницализируем лесную зону (1 - лес, 0 - пустое пространство)
        for x in range(self.width):
            for y in range(self.height):
                if 4 <= x <= 45 and 30 <= y <= 48:  # Основная лесная зона
                    self.forest_area[y, x] = 1
                # Добавляем базу для дронов
                if (x - base_x) ** 2 + (
                    y - base_y
                ) ** 2 <= 9:  # Круглая база радиусом 3
                    self.forest_area[y, x] = 2  # 2 - база

    def simulate_scenario(self, scenario="no_threat"):
        for sensor in self.sensors:
            sensor.update_status("Норма")

        self.fire_locations = []

        if scenario == "medium_threat":
            danger_sensor = random.choice(self.sensors)
            danger_sensor.update_status("Пожар")
            self.fire_locations.append((danger_sensor.x, danger_sensor.y))

        elif scenario == "high_threat":
            num_danger = random.randint(2, 3)
            danger_sensors = random.sample(self.sensors, num_danger)
            for sensor in danger_sensors:
                sensor.update_status("Пожар")
                self.fire_locations.append((sensor.x, sensor.y))

        if scenario != "no_threat":
            for sensor in self.sensors:
                if sensor.status == "Норма" and random.random() < 0.2:
                    sensor.update_status("Внимание")

    def dispatch_drones(self):
        available_drones = [
            drone for drone in self.drones if drone.status == "Ожидание"
        ]

        for fire_x, fire_y in self.fire_locations:
            num_drones_needed = random.randint(3, 6)

            if len(available_drones) >= num_drones_needed:
                selected_drones = available_drones[:num_drones_needed]
                available_drones = available_drones[num_drones_needed:]

                for drone in selected_drones:
                    drone.status = "В пути"
                    drone.target = (fire_x, fire_y)

    def generate_report(self):
        print("\Мониторинг Леса\n")
        print(
            f"{'Датчик':<8} {'Координаты':<15} {'Состояние':<15} {'Рекомендации':<25}"
        )
        for sensor in self.sensors:
            print(
                f"{sensor.sensor_id:<8} {f'{sensor.x}–{sensor.y}':<15} {sensor.status:<15} {sensor.recommendation:<25}"
            )

        print(f"\nДата и время: {datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f')}")

        print("\nСостояние БПЛА:")
        active_drones = [drone for drone in self.drones if drone.status != "Ожидание"]
        print(f"Активных БПЛА: {len(active_drones)} из {len(self.drones)}")

    def update_drones(self):
        for drone in self.drones:
            if drone.status == "В пути" and drone.target:
                target_x, target_y = drone.target
                reached = drone.move_towards(target_x, target_y)

                if reached:
                    drone.status = "Тушение"

            elif drone.status == "Тушение":
                success = drone.extinguish_fire()
                if not success:
                    drone.status = "Возвращение на базу"
                    drone.target = (25, 5)  # Координаты базы

            elif drone.status == "Возвращение на базу":
                reached = drone.move_towards(25, 5)
                if reached:
                    drone.refill_water()
                    drone.status = "Ожидание"
                    drone.target = None

    def visualize_system(self, ax):
        ax.clear()

        # Рисуем лес и базу
        forest_viz = np.zeros((self.height, self.width, 4))  # RGBA
        for y in range(self.height):
            for x in range(self.width):
                if self.forest_area[y, x] == 1:  # Лес
                    forest_viz[y, x] = [0.0, 0.5, 0.0, 0.7]  # Зеленый, полупрозрачный
                elif self.forest_area[y, x] == 2:  # База
                    forest_viz[y, x] = [0.7, 0.7, 0.7, 1.0]  # Серый

        ax.imshow(forest_viz, origin="lower")

        # Рисуем сенсоры
        for sensor in self.sensors:
            if sensor.status == "Норма":
                color = "green"
            elif sensor.status == "Внимание":
                color = "yellow"
            else:  # Пожар
                color = "red"
            ax.plot(sensor.x, sensor.y, "o", color=color, markersize=8)
            ax.text(sensor.x + 0.5, sensor.y + 0.5, sensor.sensor_id, fontsize=8)

        # Рисуем пожары
        for fire_x, fire_y in self.fire_locations:
            # Рисуем красный круг с желтым ореолом
            fire_circle = patches.Circle(
                (fire_x, fire_y), radius=1.5, facecolor="orangered", alpha=0.7
            )
            ax.add_patch(fire_circle)
            fire_circle_outer = patches.Circle(
                (fire_x, fire_y), radius=2.5, facecolor="yellow", alpha=0.3
            )
            ax.add_patch(fire_circle_outer)

        for drone in self.drones:
            if drone.status == "Ожидание":
                color = "blue"
            elif drone.status == "В пути":
                color = "cyan"
            elif drone.status == "Тушение":
                color = "lime"
            else:
                color = "orange"

            ax.plot(drone.x, drone.y, "^", color=color, markersize=6)

            if drone.status != "Ожидание":
                ax.text(drone.x + 0.5, drone.y - 0.5, f"{drone.water}%", fontsize=6)

        ax.set_title("Система мониторинга и тушения пожаров в реликтовой роще")
        ax.set_xlim(0, self.width)
        ax.set_ylim(0, self.height)

        ax.plot([], [], "o", color="green", label="Датчик: Норма")
        ax.plot([], [], "o", color="yellow", label="Датчик: Внимание")
        ax.plot([], [], "o", color="red", label="Датчик: Пожар")
        ax.plot([], [], "^", color="blue", label="Дрон: Ожидание")
        ax.plot([], [], "^", color="cyan", label="Дрон: В пути")
        ax.plot([], [], "^", color="lime", label="Дрон: Тушение")
        ax.plot([], [], "^", color="orange", label="Дрон: Возвращение")

        ax.legend(loc="upper left", bbox_to_anchor=(1, 1))

        active_drones = len([d for d in self.drones if d.status != "Ожидание"])
        fires = len(self.fire_locations)
        ax.text(
            self.width + 1,
            self.height // 2,
            f"Активных БПЛА: {active_drones}/30\n"
            f"Обнаружено пожаров: {fires}\n"
            f"Время: {datetime.now().strftime('%H:%M:%S')}",
            fontsize=10,
        )


def animate_simulation(scenario):
    system = ForestFireMonitoringSystem()
    system.simulate_scenario(scenario)

    print(f"==== Анимация сценария: {scenario} ====")
    system.generate_report()

    if system.fire_locations:
        print("\nОтправляем дроны на тушение пожаров...\n")
        system.dispatch_drones()

    fig, ax = plt.subplots(figsize=(12, 8))

    def update(frame):
        system.update_drones()
        system.visualize_system(ax)
        return (ax,)

    ani = animation.FuncAnimation(fig, update, frames=100, interval=100, blit=False)
    plt.tight_layout()
    plt.show()

    return system


if __name__ == "__main__":
    print(
        "Демонстрация работы системы мониторинга и тушения пожаров в реликтовой роще\n"
    )

    animate_simulation("no_threat")
    animate_simulation("medium_threat")
    animate_simulation("high_threat")
