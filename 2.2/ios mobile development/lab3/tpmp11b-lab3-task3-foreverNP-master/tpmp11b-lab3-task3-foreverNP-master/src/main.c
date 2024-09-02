// main.c
#include "note.h"
#include <stdio.h>

int main()
{
    struct NOTE2 BLOCK2[7];

    // Ввод данных
    inputNotes(BLOCK2, 7);

    // Вывод информации о человеке по фамилии
    while (1)
    {
        char searchSurname[50];
        printf("Enter surname to search: ");
        scanf("%s", searchSurname);

        printf(personName(BLOCK2, 7, searchSurname));
    }

    return 0;
}
