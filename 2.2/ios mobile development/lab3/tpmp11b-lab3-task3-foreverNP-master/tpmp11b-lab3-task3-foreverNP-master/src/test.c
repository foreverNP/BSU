#include "note.h"
#include <stdio.h>

int main()
{
    struct NOTE2 BLOCK2[7];

    BLOCK2[0].DATE.year = 1999;
    BLOCK2[0].DATE.month = 12;
    BLOCK2[0].DATE.day = 31;
    strcpy(BLOCK2[0].Name, "Ivan Ivanov");
    strcpy(BLOCK2[0].TELE, "801444444");

    BLOCK2[1].DATE.year = 2000;
    BLOCK2[1].DATE.month = 5;
    BLOCK2[1].DATE.day = 5;
    strcpy(BLOCK2[1].Name, "Petr Petrov");
    strcpy(BLOCK2[1].TELE, "802555555");

    BLOCK2[2].DATE.year = 1998;
    BLOCK2[2].DATE.month = 6;
    BLOCK2[2].DATE.day = 21;
    strcpy(BLOCK2[2].Name, "Sidor Sidorov");
    strcpy(BLOCK2[2].TELE, "803666666");

    BLOCK2[3].DATE.year = 2001;
    BLOCK2[3].DATE.month = 7;
    BLOCK2[3].DATE.day = 8;
    strcpy(BLOCK2[3].Name, "Nikolay Nikolaev");
    strcpy(BLOCK2[3].TELE, "804777777");

    BLOCK2[4].DATE.year = 1997;
    BLOCK2[4].DATE.month = 3;
    BLOCK2[4].DATE.day = 15;
    strcpy(BLOCK2[4].Name, "Alexey Alexeev");
    strcpy(BLOCK2[4].TELE, "805888888");

    BLOCK2[5].DATE.year = 2002;
    BLOCK2[5].DATE.month = 9;
    BLOCK2[5].DATE.day = 13;
    strcpy(BLOCK2[5].Name, "Vasily Vasilyev");
    strcpy(BLOCK2[5].TELE, "806999999");

    BLOCK2[6].DATE.year = 1996;
    BLOCK2[6].DATE.month = 11;
    BLOCK2[6].DATE.day = 18;
    strcpy(BLOCK2[6].Name, "Dmitry Dmitriev");
    strcpy(BLOCK2[6].TELE, "807000000");

    char *results[14];
    // case in which the person is found
    results[0] = personName(BLOCK2, 7, "Ivan Ivanov");
    results[1] = personName(BLOCK2, 7, "Petr Petrov");
    results[2] = personName(BLOCK2, 7, "Sidor Sidorov");
    results[3] = personName(BLOCK2, 7, "Nikolay Nikolaev");
    results[4] = personName(BLOCK2, 7, "Alexey Alexeev");
    results[5] = personName(BLOCK2, 7, "Vasily Vasilyev");
    results[6] = personName(BLOCK2, 7, "Dmitry Dmitriev");
    // case in which the person is not found
    results[7] = personName(BLOCK2, 7, "Mike");
    results[8] = personName(BLOCK2, 7, "John");
    results[9] = personName(BLOCK2, 7, "Bob");
    results[10] = personName(BLOCK2, 7, "Alice");
    results[11] = personName(BLOCK2, 7, "Eve");
    results[12] = personName(BLOCK2, 7, "Mallory");
    results[13] = personName(BLOCK2, 7, "Trent");

    char *expected[14];
    expected[0] = "Person found:\nName: Ivan Ivanov\nTELE: 801444444\nDATE: 1999-12-31\n";
    expected[1] = "Person found:\nName: Petr Petrov\nTELE: 802555555\nDATE: 2000-5-5\n";
    expected[2] = "Person found:\nName: Sidor Sidorov\nTELE: 803666666\nDATE: 1998-6-21\n";
    expected[3] = "Person found:\nName: Nikolay Nikolaev\nTELE: 804777777\nDATE: 2001-7-8\n";
    expected[4] = "Person found:\nName: Alexey Alexeev\nTELE: 805888888\nDATE: 1997-3-15\n";
    expected[5] = "Person found:\nName: Vasily Vasilyev\nTELE: 806999999\nDATE: 2002-9-13\n";
    expected[6] = "Person found:\nName: Dmitry Dmitriev\nTELE: 807000000\nDATE: 1996-11-18\n";

    expected[7] = "Person with the specified surname not found.\n";
    expected[8] = "Person with the specified surname not found.\n";
    expected[9] = "Person with the specified surname not found.\n";
    expected[10] = "Person with the specified surname not found.\n";
    expected[11] = "Person with the specified surname not found.\n";
    expected[12] = "Person with the specified surname not found.\n";
    expected[13] = "Person with the specified surname not found.\n";

    for (int i = 0; i < 14; ++i)
    {
        printf("Test %d: ", i + 1);
        if (strcmp(results[i], expected[i]) != 0)
        {
            printf("Failed!\n\nExepected: %sGot: %s\n", expected[i], results[i]);

            return 1;
        }
        printf("Passed!\n");
    }

    printf("Tests passed!\n");
    return 0;
}
