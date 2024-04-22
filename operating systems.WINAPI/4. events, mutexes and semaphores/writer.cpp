#include <iostream>
#include <string>
#include <windows.h>
#include <sstream>

const int EVENTS_COUNT = 2;

int main(int argc, char* argv[])
{
    std::cout << "Writer\n";

    HANDLE hMutex = OpenMutex(MUTEX_ALL_ACCESS, FALSE, L"WriterMutex");
    HANDLE readerEvents[EVENTS_COUNT];
    HANDLE writerCloseEvent;

    for (int i = 0; i < EVENTS_COUNT; ++i) {
        std::wstringstream readerEventName;
        readerEventName << L"ReaderEvent" << i;

        readerEvents[i] = OpenEvent(EVENT_ALL_ACCESS, FALSE, readerEventName.str().c_str());
        if (readerEvents[i] == NULL) {
            return GetLastError();
        }
    }

    writerCloseEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, L"WriterEndEvent");

    if (writerCloseEvent == NULL || hMutex == NULL) {
        return GetLastError();
    }

    int messageNum = atoi(argv[1]);

    WaitForSingleObject(hMutex, INFINITE);

    for (int i = 0; i < messageNum; ++i) {
        char message;
        std::cout << "Enter message(A, B): ";
        std::cin >> message;

        switch (message) {
        case 'A':
            SetEvent(readerEvents[0]);
            break;
        case 'B':
            SetEvent(readerEvents[1]);
            break;
        default:
            i--;
            break;
        }
    }

    ReleaseMutex(hMutex);

    std::string s;
    getline(std::cin, s);
    getline(std::cin, s);

    SetEvent(writerCloseEvent);
    CloseHandle(hMutex);

    for (int i = 0; i < EVENTS_COUNT; ++i) {
        CloseHandle(readerEvents);
    }

    return 0;
}