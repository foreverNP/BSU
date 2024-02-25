#include <windows.h>
#include <iostream>
#include <string>
#include <vector>
#include <ctime>

using namespace std;

CRITICAL_SECTION cs_work;
CRITICAL_SECTION cs_countElement;
HANDLE hWorkAndCountEvent;

vector<double> originalArray;
vector<double> resultArray;
int X, interval;

DWORD WINAPI work(LPVOID lpParam)
{
    EnterCriticalSection(&cs_work);
    cout << "Вход в критическую секцию потоком work\n";
  
    WaitForSingleObject(hWorkAndCountEvent, INFINITE);

    resultArray.clear();
    for (double num : originalArray) {
        if (num == X) {
            resultArray.insert(resultArray.begin(), num);
        }
        else {
            resultArray.push_back(num);
            Sleep(interval);
        }
    }

    LeaveCriticalSection(&cs_work);
    cout << "Выход из критической секции потоком work\n";

    return 0;
}

DWORD WINAPI countElement(LPVOID lpParam)
{
    EnterCriticalSection(&cs_countElement);
    cout << "Вход в критическую секцию потоком countElement\n";

    WaitForSingleObject(hWorkAndCountEvent, INFINITE);

    int* count = (int*)lpParam;
    for (double num : originalArray) {
        if (num != X) {
            (*count)++;
        }   
    }

    LeaveCriticalSection(&cs_countElement);
    cout << "Выход из критической секции потоком countElement\n";

    return 0;
}

int main()
{
    setlocale(LC_ALL, "");

    int size, count = 0;

    InitializeCriticalSection(&cs_work);
    InitializeCriticalSection(&cs_countElement);

    hWorkAndCountEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
    if (hWorkAndCountEvent == NULL) {
        return GetLastError();
    }

    cout << "Введите размер массива:\n";
    cin >> size;

    cout << "Введите элементы массива:\n";
    for (int i = 0; i < size; ++i) {
        double num;
        cin >> num;
        originalArray.push_back(num);
    }

    cout << "Размер массива: " << size << "\n";
    cout << "Элементы массива: ";
    for (double num : originalArray) {
        cout << num << " ";
    }
    cout << "\n";

    cout << "Введите временной интервал (в миллисекундах) для отдыха после подготовки каждого элемента в массиве:\n";
    cin >> interval;

    HANDLE hWork = CreateThread(NULL, 0, work, NULL, 0, NULL);
    if (hWork == NULL) {
        return GetLastError();
    }

    HANDLE hCountElement = CreateThread(NULL, 0, countElement, &count, 0, NULL);
    if (hCountElement == NULL) {
        return GetLastError();
    }

    Sleep(1);
    cout << "Введите значение X:\n";
    cin >> X;

    SetEvent(hWorkAndCountEvent);

    EnterCriticalSection(&cs_work);
    cout << "\nВход в критическую секцию потоком main\n";
    cout << "Результат работы потока work:\n";
    for (double num : resultArray) {
        cout << num << " ";
    }
    cout << "\n";
    LeaveCriticalSection(&cs_work);
    cout << "Выход из критической секции потоком main\n";

    EnterCriticalSection(&cs_countElement);
    cout << "\nВход в критическую секцию потоком main\n";
    cout << "Результат работы потока countElement:\n";
    cout << "Количество элементов, не равных " << X << ": " << count << "\n";
    LeaveCriticalSection(&cs_countElement);
    cout << "Выход из критической секции потоком main\n";

    CloseHandle(hWorkAndCountEvent);
    CloseHandle(hWork);
    CloseHandle(hCountElement);

    DeleteCriticalSection(&cs_work);
    DeleteCriticalSection(&cs_countElement);

    return 0;
}