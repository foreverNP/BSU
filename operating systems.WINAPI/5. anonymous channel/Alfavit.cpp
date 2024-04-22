#include <iostream>
#include <windows.h>
#include <vector>
#include <cctype>

using namespace std;

bool isLatin(char c)
{
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}

int main(int argc, char *argv[])
{
    HANDLE hWritePipe = (HANDLE)atoi(argv[1]), hReadPipe = (HANDLE)atoi(argv[2]);

    int size;
    DWORD dwBytesRead;
    ReadFile(hReadPipe, &size, sizeof(int), &dwBytesRead, NULL);

    cout << "Size of array: " << size << endl;
    vector<char> arr(size);
    cout << "Received array" << endl;
    for (int i = 0; i < size; i++)
    {
        ReadFile(hReadPipe, &arr[i], sizeof(char), &dwBytesRead, NULL);
        cout << arr[i] << " ";
    }
    cout << endl;

    cout << "Latin letters array" << endl;
    vector<char> latinLetters;
    for (int i = 0; i < size; ++i)
    {
        if (isLatin(arr[i]))
        {
            latinLetters.push_back(arr[i]);
            cout << arr[i] << " ";
        }
    }
    cout << endl;

    int latinLettersCount = latinLetters.size();
    WriteFile(hWritePipe, &latinLettersCount, sizeof(int), &dwBytesRead, NULL);

    for (int i = 0; i < latinLettersCount; ++i)
    {
        WriteFile(hWritePipe, &latinLetters[i], sizeof(char), &dwBytesRead, NULL);
    }

    CloseHandle(hWritePipe);
    CloseHandle(hReadPipe);

    int a;
    cin >> a;

    return 0;
}
