import os
import json
import random
import math
import matplotlib.pyplot as plt

KEY_CITY = "city"
KEY_DISTRICTS = "districts"
KEY_NAME = "name"
KEY_NUM_OBJECTS = "num_objects"
KEY_AVAILABLE_RESOURCES = "available_resources"
KEY_STATUS_RANGES = "status_ranges"
KEY_DECISIONS = "decisions"
KEY_COSTS = "costs"
KEY_STATUS = "status"
KEY_SIGNAL = "signal"
KEY_DECISION = "decision"
KEY_COST = "cost"
KEY_STATUS_COUNTS = "status_counts"
KEY_TOTAL_COST = "total_cost"
KEY_ADDITIONAL_RESOURCES_NEEDED = "additional_resources_needed"
KEY_OBJECTS = "objects"
KEY_DISTRICT = "district"


def load_json_file(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        return json.load(f)


def save_to_json(data, filename):
    with open(filename, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=4)


def truncated_exponential(h):
    U = random.random()
    return round(-1 / h * math.log(1 - U * (1 - math.exp(-h))), 2)


def simulate_district(district, config, earthquake_factor):
    objects = []
    total_cost = 0
    status_counts = {"норма": 0, "поврежден": 0, "разрушен": 0}
    ranges = config[KEY_STATUS_RANGES]
    decisions = config[KEY_DECISIONS]
    costs = config[KEY_COSTS]
    for i in range(1, district[KEY_NUM_OBJECTS] + 1):
        signal = truncated_exponential(earthquake_factor)
        if ranges["норма"]["min"] <= signal <= ranges["норма"]["max"]:
            status = "норма"
        elif ranges["поврежден"]["min"] <= signal <= ranges["поврежден"]["max"]:
            status = "поврежден"
        else:
            status = "разрушен"
        decision = decisions[status]
        cost = costs[status]
        objects.append(
            {
                "id": i,
                KEY_SIGNAL: signal,
                KEY_STATUS: status,
                KEY_DECISION: decision,
                KEY_COST: cost,
            }
        )
        status_counts[status] += 1
        total_cost += cost
    additional_resources_needed = max(0, total_cost - district[KEY_AVAILABLE_RESOURCES])
    district_result = {
        KEY_DISTRICT: district[KEY_NAME],
        KEY_NUM_OBJECTS: district[KEY_NUM_OBJECTS],
        KEY_AVAILABLE_RESOURCES: district[KEY_AVAILABLE_RESOURCES],
        KEY_TOTAL_COST: total_cost,
        KEY_ADDITIONAL_RESOURCES_NEEDED: additional_resources_needed,
        KEY_STATUS_COUNTS: status_counts,
        KEY_OBJECTS: objects,
    }
    return district_result


def visualize_results(districts_data):
    district_names = [d[KEY_DISTRICT] for d in districts_data]
    total_costs = [d[KEY_TOTAL_COST] for d in districts_data]
    available_resources = [d[KEY_AVAILABLE_RESOURCES] for d in districts_data]

    statuses = ["норма", "поврежден", "разрушен"]
    status_colors = {"норма": "green", "поврежден": "orange", "разрушен": "red"}

    fig, axes = plt.subplots(1, 2, figsize=(14, 6))
    x = range(len(district_names))
    axes[0].bar(x, total_costs, width=0.4, label="Общая стоимость", align="center")
    axes[0].bar(
        x, available_resources, width=0.4, label="Доступные ресурсы", align="edge"
    )
    axes[0].set_xticks(x)
    axes[0].set_xticklabels(district_names, rotation=45)
    axes[0].set_ylabel("Единицы")
    axes[0].set_title("Сравнение затрат на ремонт/строительство и доступных ресурсов")
    axes[0].legend()

    bottom = [0] * len(districts_data)
    for status in statuses:
        counts = [d[KEY_STATUS_COUNTS][status] for d in districts_data]
        axes[1].bar(
            district_names,
            counts,
            bottom=bottom,
            color=status_colors[status],
            label=status,
        )
        bottom = [bottom[i] + counts[i] for i in range(len(counts))]

    axes[1].set_ylabel("Количество объектов")
    axes[1].set_title("Распределение статусов объектов по районам")
    axes[1].legend()

    plt.tight_layout()
    plt.show()


def main():
    city_structure = load_json_file(os.path.join("data", "city_structure.json"))
    simulation_config = load_json_file(os.path.join("data", "simulation_config.json"))
    print(
        "Введите оценку землетрясения по 10-бальной шкале (1 - минимальное, 10 - максимальное):"
    )
    try:
        scale = int(input().strip())
        if scale < 1 or scale > 10:
            scale = 5
    except:
        scale = 5
    h = 2.0 - (scale - 1) * (1.5 / 9)
    simulation_results = {KEY_CITY: city_structure[KEY_CITY], KEY_DISTRICTS: []}
    for district in city_structure[KEY_DISTRICTS]:
        result = simulate_district(district, simulation_config, h)
        simulation_results[KEY_DISTRICTS].append(result)
    results_path = os.path.join("data", "simulation_results.json")
    save_to_json(simulation_results, results_path)
    for d in simulation_results[KEY_DISTRICTS]:
        print(f"Район: {d[KEY_DISTRICT]}")
        print(f"  Количество объектов: {d[KEY_NUM_OBJECTS]}")
        print(f"  Распределение статусов: {d[KEY_STATUS_COUNTS]}")
        print(f"  Общая стоимость: {d[KEY_TOTAL_COST]} единиц")
        print(f"  Доступные ресурсы: {d[KEY_AVAILABLE_RESOURCES]} единиц")
        if d[KEY_ADDITIONAL_RESOURCES_NEEDED] > 0:
            print(f"  Не хватает ресурсов: {d[KEY_ADDITIONAL_RESOURCES_NEEDED]} единиц")
        else:
            print("  Ресурсов достаточно для восстановления")
        print("-" * 50)
    visualize_results(simulation_results[KEY_DISTRICTS])


if __name__ == "__main__":
    main()
