#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <windows.h>
#include <string>
#include <sstream>

using namespace std;

const int EVENTS_COUNT = 2;

int main()
{
    HANDLE hMutex = CreateMutex(NULL, FALSE, L"WriterMutex");
    HANDLE hSemaphore = CreateSemaphore(NULL, 1, 1, L"ReaderSemaphore");

    if (hMutex == NULL || hSemaphore == NULL) {
        return GetLastError();
    }

    HANDLE readerEvents[EVENTS_COUNT];
    HANDLE closeEvents[2];

    for (int i = 0; i < EVENTS_COUNT; ++i) {
        std::wstringstream readerEventName;
        readerEventName << L"ReaderEvent" << i;

        readerEvents[i] = CreateEvent(NULL, FALSE, FALSE, readerEventName.str().c_str());

        if (readerEvents[i] == NULL) {
            return GetLastError();
        }
    }

    closeEvents[0] = CreateEvent(NULL, FALSE, FALSE, L"WriterEndEvent");
    closeEvents[1] = CreateEvent(NULL, FALSE, FALSE, L"ReaderEndEvent");

    int processNumber;
    int messageNumber;

    cout << "Enter number of Writer and Reader processes: ";
    cin >> processNumber;
    cout << "Enter number of messages: ";
    cin >> messageNumber;

    STARTUPINFO si;
    PROCESS_INFORMATION* w_pi = new PROCESS_INFORMATION[processNumber];
    PROCESS_INFORMATION* read_pi = new PROCESS_INFORMATION[processNumber];

    wchar_t readerCommandline[100];
    wchar_t writerCommandline[100];
    std::wstring writerCommandLineStr = L"writer.exe " + std::to_wstring(messageNumber);
    std::wstring readerCommandLineStr = L"reader.exe " + std::to_wstring(messageNumber);
    wcscpy(writerCommandline, writerCommandLineStr.c_str());
    wcscpy(readerCommandline, readerCommandLineStr.c_str());

    for (int i = 0; i < processNumber; ++i) {
        ZeroMemory(&si, sizeof(si));
        si.cb = sizeof(si);
        if (!CreateProcess(NULL, writerCommandline, NULL, NULL, FALSE,
            CREATE_NEW_CONSOLE, NULL, NULL, &si, &w_pi[i])) {
            cout << "Writer process is not created\n";
            return GetLastError();
        }

        ZeroMemory(&si, sizeof(si));
        si.cb = sizeof(si);
        if (!CreateProcess(NULL, readerCommandline, NULL, NULL, FALSE,
            CREATE_NEW_CONSOLE, NULL, NULL, &si, &read_pi[i])) {
            cout << "Reader process is not created\n";
            return GetLastError();
        }
    }

    int writerNum = processNumber;
    int readerNum = processNumber;
    while (writerNum || readerNum) {
        DWORD message = WaitForMultipleObjects(EVENTS_COUNT, closeEvents, FALSE, INFINITE);

        switch (message) {
        case 0:
            cout << "Writer process closed\n";
            --writerNum;
            break;
        case 1:
            cout << "Reader process closed\n";
            --readerNum;
            break;
        }
    }

    CloseHandle(hMutex);
    CloseHandle(hSemaphore);

    for (int i = 0; i < EVENTS_COUNT; ++i) {
        CloseHandle(readerEvents[i]);
    }

    CloseHandle(closeEvents[0]);
    CloseHandle(closeEvents[1]);

    for (int i = 0; i < processNumber; ++i) {
        CloseHandle(w_pi[i].hThread);
        CloseHandle(w_pi[i].hProcess);
        CloseHandle(read_pi[i].hThread);
        CloseHandle(read_pi[i].hProcess);
    }

    return 0;
}