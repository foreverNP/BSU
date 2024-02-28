#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <conio.h>
#include <sstream>
#include <Windows.h>

using namespace std;

bool isDigit(char c)
{
    return c >= '0' && c <= '9';
}

int main(int argc, char *argv[])
{
    for (int i = 0; i < argc; i++)
    {
        cout << argv[i] << " ";
    }

    if (argc <= 1)
    {
        std::cout << "No arguments provided." << std::endl;
        return 0;
    }

    cout << "\nNumber of elements: " << argc - 1 << endl;

    int size = argc - 1;
    char *values = new char[size];

    cout << "Only digits:\n";
    for (int i = 0; i < size; i++)
    {
        if (isDigit(*argv[i + 1]))
        {
            cout << *argv[i + 1] << " ";
        }
    }
    cout << "\n";

    _getch();

    delete[] values;

    return 0;
}