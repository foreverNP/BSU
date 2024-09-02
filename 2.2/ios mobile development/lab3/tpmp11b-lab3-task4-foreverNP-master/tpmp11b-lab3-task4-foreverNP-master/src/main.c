#include <stdio.h>
#include "military.h"

int main() {
    FILE *file = fopen("../docs/military.txt", "w");
    if (file == NULL) {
        printf("Ошибка при открытии файла для записи.");
        return 1;
    }

    // Записываем данные о военнослужащих в файл
    writeMilitaryData(file);

    // Закрываем файл
    fclose(file);

    // Открываем файл для чтения
    file = fopen("../docs/military.txt", "r");
    if (file == NULL) {
        printf("Ошибка при открытии файла для чтения.");
        return 1;
    }

    struct Military soldiers[100]; // Предположим, что не более 100 военнослужащих
    int numSoldiers = 0;

    // Читаем данные из файла
    readMilitaryData(file, soldiers, &numSoldiers);

    // Закрываем файл
    fclose(file);

    // Создаем новый файл для вывода информации о лейтенантах
    file = fopen("../docs/lieutenants.txt", "w");
    if (file == NULL) {
        printf("Ошибка при открытии файла для записи.");
        return 1;
    }

    // Выводим информацию о лейтенантах и сохраняем результат в файл
    printLieutenantsToFile(soldiers, numSoldiers, file);

    // Закрываем файл
    fclose(file);

    return 0;
}
