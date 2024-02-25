// TwoModules.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include "pch.h"
#include <iostream>
//

extern int scalar(int n, float v[], float w[], float* vw);
extern int product(int n, float v[], float w[], float result[]);

int main()
{
    std::cout << "Hello World!\n";
    float v[] = { 1,2,3 };
    float w[] = { 3,2,1 };
    float vw;
    int res = scalar(3, v, w, &vw);

    std::cout << "\nv-vector\n";
    for (int i = 0; i < 3; i++)
        std::cout << v[i] << ";";

    std::cout << "\n\nw-vector\n";
    for (int i = 0; i < 3; i++)
        std::cout << w[i] << ";";

    std::cout << "\n\nscalar product (v,w)\n";

    std::cout << vw << "\n";

    float result[3];
    int res2 = product(3, v, w, result);
    std::cout << "\nvector product (v, w): ("
        << result[0] << ", "
        << result[1] << ", "
        << result[2] << ")" << std::endl;
    return 0;
}


// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
