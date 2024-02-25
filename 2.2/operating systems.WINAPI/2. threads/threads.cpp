#include <windows.h>
#include <iostream>
#include <process.h>

using namespace std;

struct Data
{
    char *arr;
    int size;
};

DWORD WINAPI Worker(LPVOID p)
{
    cout << "Worker started\n";

    int size = ((Data *)p)->size;
    char *arr = ((Data *)p)->arr;

    for (int i = 0; i < size; i++)
    {
        if (arr[i] >= '0' && arr[i] <= '9')
        {
            cout << arr[i] << "\n";
        }
        Sleep(10);
    }

    cout << "Worker finished\n";

    return 0;
}

DWORD WINAPI Count(LPVOID p)
{
    cout << "Count started\n";

    long long a = 1, b = 1;
    cout << b << endl;

    for (; b < INT32_MAX;)
    {
        cout << b << endl;
        b = b + a;
        a = b - a;

        Sleep(500);
    }

    cout << "Count finished\n";

    return 0;
}

int main()
{
    int n;
    int startTime;
    int suspendTime;

    char *arr;

    cout << "Size of array:\n";
    cin >> n;
    arr = new char[n];

    cout << "Array elements:\n";
    for (int i = 0; i < n; i++)
    {
        cin >> arr[i];
    }

    cout << "Time(ms) to suspend thread(<= " << (n - 1) * 10 << "):\n";
    cin >> suspendTime;

    while (suspendTime > (n - 1) * 10)
    {
        cout << "Suspend time must be <= than " << (n - 1) * 10 << ". Try again:\n";
        cin >> suspendTime;
    }

    cout << "Time(ms) to start thread:\n";
    cin >> startTime;

    HANDLE hThread, hThread1;
    DWORD IDThread, IDThread1;

    hThread = CreateThread(NULL, 0, Worker, new Data{arr, n}, 0, &IDThread);
   // hThread = (HANDLE)_beginthreadex(NULL, 0, (_beginthreadex_proc_type)Worker, new Data{arr, n}, 0, (UINT*)&IDThread);
    hThread1 = CreateThread(NULL, 0, Count, NULL, CREATE_SUSPENDED, &IDThread1);

    if (hThread == NULL || hThread1 == NULL)
    {
        return GetLastError();
    }

    Sleep(suspendTime);
    SuspendThread(hThread);
    Sleep(startTime);
    ResumeThread(hThread);

    WaitForSingleObject(hThread, INFINITE);

    CloseHandle(hThread);

    //////////////////////////////////////////////////////////////

    ResumeThread(hThread1);

    WaitForSingleObject(hThread1, 30000);

    CloseHandle(hThread1);

    return 0;
}