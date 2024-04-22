#include <iostream>
#include <string>
#include <windows.h>
#include <sstream>

const int EVENTS_COUNT = 2;

int main(int argc, char* argv[])
{
    std::cout << "Reader\n";

    HANDLE hSemaphore = OpenSemaphore(SEMAPHORE_ALL_ACCESS, FALSE, L"ReaderSemaphore");
    HANDLE readerEvents[EVENTS_COUNT];
    HANDLE readerCloseEvent;

    for (int i = 0; i < EVENTS_COUNT; ++i) {
        std::wstringstream readerEventName;
        readerEventName << L"ReaderEvent" << i;

        readerEvents[i] = OpenEvent(EVENT_ALL_ACCESS, FALSE, readerEventName.str().c_str());
        if (readerEvents[i] == NULL) {
            return GetLastError();
        }
    }

    readerCloseEvent = OpenEvent(EVENT_ALL_ACCESS, FALSE, L"ReaderEndEvent");
    if (readerCloseEvent == NULL || hSemaphore == NULL) {
        return GetLastError();
    }

    int messageNum = atoi(argv[1]);

    WaitForSingleObject(hSemaphore, INFINITE);

    while (messageNum) {
        DWORD message = WaitForMultipleObjects(EVENTS_COUNT, readerEvents, FALSE, INFINITE);

        switch (message) {
        case 0:
            std::cout << "Recieved message: A\n";
            break;
        case 1:
            std::cout << "Recieved message: B\n";
            break;
        }

        --messageNum;
    }

    ReleaseSemaphore(hSemaphore, 1, NULL);

    std::string s;
    getline(std::cin, s);

    SetEvent(readerCloseEvent);
    CloseHandle(hSemaphore);

    for (int i = 0; i < EVENTS_COUNT + 1; ++i) {
        CloseHandle(readerEvents[i]);
    }

    return 0;
}