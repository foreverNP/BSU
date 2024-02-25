// note.h
#ifndef NOTE_H
#define NOTE_H

struct NOTE2
{
    char Name[50];
    char TELE[20];
    struct
    {
        int year;
        int month;
        int day;
    } DATE;
};

void inputNotes(struct NOTE2 *BLOCK2, int size);
char *personName(struct NOTE2 *BLOCK2, int size, const char *surname);

#endif // NOTE_H
