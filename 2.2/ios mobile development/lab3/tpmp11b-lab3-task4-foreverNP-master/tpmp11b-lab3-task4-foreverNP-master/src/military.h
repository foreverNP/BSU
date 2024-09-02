#ifndef MILITARY_H
#define MILITARY_H

#define MAX_ADDRESS_LENGTH 100
#define MAX_NAME_LENGTH 50

#include <stdio.h>

struct DateOfBirth
{
    int year;
    int month;
    int day;
};

struct Address
{
    int postalCode;
    char country[MAX_ADDRESS_LENGTH];
    char region[MAX_ADDRESS_LENGTH];
    char district[MAX_ADDRESS_LENGTH];
    char city[MAX_ADDRESS_LENGTH];
    char street[MAX_ADDRESS_LENGTH];
    int houseNumber;
    int apartmentNumber;
};

struct Military
{
    char lastName[MAX_NAME_LENGTH];
    char firstName[MAX_NAME_LENGTH];
    char patronymic[MAX_NAME_LENGTH];
    struct Address address;
    char nationality[MAX_NAME_LENGTH];
    struct DateOfBirth dateOfBirth;
    char position[MAX_NAME_LENGTH];
    char rank[MAX_NAME_LENGTH];
};

void writeMilitaryData(FILE *file);
void readMilitaryData(FILE *file, struct Military soldiers[], int *numSoldiers);
void printLieutenantsToFile(struct Military soldiers[], int numSoldiers, FILE *file);

#endif /* MILITARY_H */
