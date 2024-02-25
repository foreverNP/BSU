#include <iostream>
#include <windows.h>
#include <vector>

using namespace std;

int main()
{
    int sizeOfArray;
    cout << "Input size of array: ";
    cin >> sizeOfArray;

    vector<char> array(sizeOfArray);
    cout << "Input elements of array: ";
    for (int i = 0; i < sizeOfArray; ++i)
    {
        cin >> array[i];
    }

    HANDLE hWritePipe1, hReadPipe1;
    HANDLE hWritePipe2, hReadPipe2;

    SECURITY_ATTRIBUTES sa;
    sa.nLength = sizeof(SECURITY_ATTRIBUTES);
    sa.lpSecurityDescriptor = NULL;
    sa.bInheritHandle = TRUE;

    CreatePipe(&hReadPipe1, &hWritePipe1, &sa, 0);
    CreatePipe(&hReadPipe2, &hWritePipe2, &sa, 0);

    STARTUPINFO stp;
    PROCESS_INFORMATION pi;
    ZeroMemory(&stp, sizeof(STARTUPINFO));
    stp.cb = sizeof(STARTUPINFO);
    wchar_t commandLine[80];
    wsprintf(commandLine, L"Alfavit.exe %d %d", (int)hWritePipe2, (int)hReadPipe1);
    CreateProcess(NULL, commandLine, NULL, NULL, TRUE, CREATE_NEW_CONSOLE, NULL, NULL, &stp, &pi);

    DWORD dwBytesWritten;
    WriteFile(hWritePipe1, &sizeOfArray, sizeof(int), &dwBytesWritten, NULL);
    for (int i = 0; i < sizeOfArray; i++)
    {
        WriteFile(hWritePipe1, &array[i], sizeof(char), &dwBytesWritten, NULL);
    }

    CloseHandle(hWritePipe1);
    WaitForSingleObject(pi.hProcess, INFINITE);

    DWORD dwBytesRead;
    int sizeOfNewArray;
    ReadFile(hReadPipe2, &sizeOfNewArray, sizeof(int), &dwBytesRead, NULL);
    vector<char> resultArray(sizeOfNewArray);

    cout << "Received Latin Letters from Alfavit Process: ";
    for (int i = 0; i < sizeOfNewArray; ++i)
    {
        ReadFile(hReadPipe2, &resultArray[i], sizeof(char), &dwBytesRead, NULL);
        cout << resultArray[i] << " ";
    }
    cout << endl;

    CloseHandle(hReadPipe2);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    CloseHandle(hWritePipe2);
    CloseHandle(hReadPipe1);

    return 0;
}
