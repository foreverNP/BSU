#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <conio.h>
#include <Windows.h>
#include <stdio.h>
#include <tchar.h>
#include <string>

using namespace std;

int main()
{
    SetConsoleTitle(L"Parent Window");

    int arraySize;
    wstring titleStr;
    wstring temp = L"Child.exe";

    wchar_t commandLine[100];
    wchar_t childTitle[100];

    cout << "Input array size:\n";
    cin >> arraySize;

    wchar_t *array = new wchar_t[arraySize];

    cout << "Input array elements:\n";
    for (int i = 0; i < arraySize; ++i)
    {
        wcin >> array[i];
    }

    cout << "Input Child title:\n";
    wcin >> titleStr;

    for (int i = 0; i < arraySize; ++i)
    {
        temp.append(L" ");
        temp.append(1, array[i]);
    }

    wcscpy(commandLine, temp.c_str());
    wcscpy(childTitle, titleStr.c_str());

    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory(&si, sizeof(STARTUPINFO));
    si.cb = sizeof(STARTUPINFO);
    si.lpTitle = childTitle;

    CreateProcess(NULL, commandLine, NULL, NULL, FALSE, CREATE_NEW_CONSOLE, NULL, NULL, &si, &pi);

    WaitForSingleObject(pi.hProcess, INFINITE);

    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
}