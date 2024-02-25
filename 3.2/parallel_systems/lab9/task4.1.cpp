#include <iostream>
#include <omp.h>

int main()
{
    const int value = 2;
    const int n = 1000000;
    int counter = 0;

#pragma omp parallel for
    for (int i = 0; i < n; ++i)
    {
// закомментировать pragma, чтобы увидеть погрешность
#pragma omp atomic
        counter += value;
    }

    std::cout << "counter = " << counter << std::endl;
    return 0;
}
