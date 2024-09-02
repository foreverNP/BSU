// note.c
#include "note.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void inputNotes(struct NOTE2 *BLOCK2, int size)
{
    printf("Enter data for %d people:\n", size);

    for (int i = 0; i < size; ++i)
    {
        printf("Person %d:\n", i + 1);

        printf("Enter Name: ");
        scanf("%s", BLOCK2[i].Name);

        printf("Enter TELE (phone number): ");
        scanf("%s", BLOCK2[i].TELE);

        printf("Enter DATE (year month day): ");
        scanf("%d %d %d", &BLOCK2[i].DATE.year, &BLOCK2[i].DATE.month, &BLOCK2[i].DATE.day);
    }
}

char *personName(struct NOTE2 *BLOCK2, int size, const char *surname)
{
    char *result = malloc(250 * sizeof(char));
    int found = 0;

    for (int i = 0; i < size; ++i)
    {
        if (strcmp(BLOCK2[i].Name, surname) == 0)
        {
            sprintf(result, "Person found:\nName: %s\nTELE: %s\nDATE: %d-%d-%d\n", BLOCK2[i].Name, BLOCK2[i].TELE, BLOCK2[i].DATE.year, BLOCK2[i].DATE.month, BLOCK2[i].DATE.day);
            found = 1;
            break;
        }
    }

    if (!found)
    {
        sprintf(result, "Person with the specified surname not found.\n");
    }

    return result;
}