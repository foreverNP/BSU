#include <stdio.h>
#include <string.h>
#include "military.h"

// Функция записи данных о военнослужащих в файл
void writeMilitaryData(FILE *file)
{
    // Массив структур военнослужащих
    struct Military soldiers[] = {
        {"Иванов", "Петр", "Сергеевич", {123456, "Россия", "Московская", "Ленинская", "Москва", "Ленина", 10, 5}, "Русский", {1990, 5, 15}, "Капитан", "Лейтенант"},
        {"Петров", "Иван", "Александрович", {654321, "Россия", "Московская", "Ленинская", "Москва", "Пушкина", 20, 10}, "Русский", {1988, 9, 20}, "Майор", "Майор"},
        {"Сидоров", "Вадим", "Артемович", {342213, "Украина", "Киевская", "Ленинская", "Киев", "Минская", 32, 4}, "Украинец", {2000, 5, 3}, "Прапорщик", "Офицер"},
        {"Васильев", "Алексей", "Викторович", {789123, "Беларусь", "Минская", "Октябрьская", "Минск", "Советская", 15, 7}, "Белорус", {1985, 7, 25}, "Подполковник", "Лейтенант"},
        {"Кузнецов", "Дмитрий", "Андреевич", {321654, "Казахстан", "Алматинская", "Советская", "Алматы", "Ленина", 25, 12}, "Казах", {1995, 12, 30}, "Старший лейтенант", "Старший лейтенант"},
        {"Смирнов", "Александр", "Игоревич", {456789, "Россия", "Московская", "Ленинская", "Москва", "Ленина", 10, 5}, "Русский", {1990, 5, 15}, "Капитан", "Майор"},
        {"Козлов", "Игорь", "Александрович", {987654, "Россия", "Московская", "Ленинская", "Москва", "Пушкина", 20, 10}, "Русский", {1988, 9, 20}, "Майор", "Майор"},
        {"Лебедев", "Вадим", "Артемович", {123789, "Украина", "Киевская", "Ленинская", "Киев", "Минская", 32, 4}, "Украинец", {2000, 5, 3}, "Прапорщик", "Офицер"},
        {"Морозов", "Алексей", "Викторович", {456123, "Беларусь", "Минская", "Октябрьская", "Минск", "Советская", 15, 7}, "Белорус", {1985, 7, 25}, "Подполковник", "Лейтенант"},
        {"Новиков", "Дмитрий", "Андреевич", {789321, "Казахстан", "Алматинская", "Советская", "Алматы", "Ленина", 25, 12}, "Казах", {1995, 12, 30}, "Старший лейтенант", "Лейтенант"}};

    int numSoldiers = sizeof(soldiers) / sizeof(struct Military);

    // Запись данных в файл
    for (int i = 0; i < numSoldiers; i++)
    {
        fprintf(file, "%s ", soldiers[i].lastName);
        fprintf(file, "%s ", soldiers[i].firstName);
        fprintf(file, "%s ", soldiers[i].patronymic);
        fprintf(file, "%d ", soldiers[i].address.postalCode);
        fprintf(file, "%s ", soldiers[i].address.country);
        fprintf(file, "%s ", soldiers[i].address.region);
        fprintf(file, "%s ", soldiers[i].address.district);
        fprintf(file, "%s ", soldiers[i].address.city);
        fprintf(file, "%s ", soldiers[i].address.street);
        fprintf(file, "%d ", soldiers[i].address.houseNumber);
        fprintf(file, "%d ", soldiers[i].address.apartmentNumber);
        fprintf(file, "%d-%d-%d ", soldiers[i].dateOfBirth.year, soldiers[i].dateOfBirth.month, soldiers[i].dateOfBirth.day);
        fprintf(file, "%s ", soldiers[i].position);
        fprintf(file, "%s\n", soldiers[i].rank);
    }
}

// Функция чтения данных о военнослужащих из файла
void readMilitaryData(FILE *file, struct Military soldiers[], int *numSoldiers)
{
    // Чтение данных из файла
    while (!feof(file))
    {
        fscanf(file, "%s ", soldiers[*numSoldiers].lastName);
        fscanf(file, "%s ", soldiers[*numSoldiers].firstName);
        fscanf(file, "%s ", soldiers[*numSoldiers].patronymic);
        fscanf(file, "%d ", &soldiers[*numSoldiers].address.postalCode);
        fscanf(file, "%s ", soldiers[*numSoldiers].address.country);
        fscanf(file, "%s ", soldiers[*numSoldiers].address.region);
        fscanf(file, "%s ", soldiers[*numSoldiers].address.district);
        fscanf(file, "%s ", soldiers[*numSoldiers].address.city);
        fscanf(file, "%s ", soldiers[*numSoldiers].address.street);
        fscanf(file, "%d ", &soldiers[*numSoldiers].address.houseNumber);
        fscanf(file, "%d ", &soldiers[*numSoldiers].address.apartmentNumber);
        fscanf(file, "%d-%d-%d ", &soldiers[*numSoldiers].dateOfBirth.year, &soldiers[*numSoldiers].dateOfBirth.month, &soldiers[*numSoldiers].dateOfBirth.day);
        fscanf(file, "%s ", soldiers[*numSoldiers].position);
        fscanf(file, "%s\n", soldiers[*numSoldiers].rank);

        (*numSoldiers)++;
    }
}

// Функция вывода данных о военнослужащих с званием лейтенант в файл
void printLieutenantsToFile(struct Military soldiers[], int numSoldiers, FILE *file)
{
    fprintf(file, "Информация о лейтенантах:\n");
    for (int i = 0; i < numSoldiers; i++)
    {
        if (strcmp(soldiers[i].rank, "Лейтенант") == 0)
        {
            fprintf(file, "Фамилия: %s\n", soldiers[i].lastName);
            fprintf(file, "Имя: %s\n", soldiers[i].firstName);
            fprintf(file, "Отчество: %s\n", soldiers[i].patronymic);
            fprintf(file, "Домашний адрес: %d, %s, %s, %s, %s, %s, %d, %d\n",
                    soldiers[i].address.postalCode, soldiers[i].address.country,
                    soldiers[i].address.region, soldiers[i].address.district,
                    soldiers[i].address.city, soldiers[i].address.street,
                    soldiers[i].address.houseNumber, soldiers[i].address.apartmentNumber);
            fprintf(file, "Национальность: %s\n", soldiers[i].nationality);
            fprintf(file, "Дата рождения: %d-%d-%d\n", soldiers[i].dateOfBirth.year,
                    soldiers[i].dateOfBirth.month, soldiers[i].dateOfBirth.day);
            fprintf(file, "Должность: %s\n", soldiers[i].position);
            fprintf(file, "Звание: %s\n", soldiers[i].rank);
            fprintf(file, "\n");
        }
    }
}
