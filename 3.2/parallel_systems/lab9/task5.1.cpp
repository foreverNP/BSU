#include <iostream>
#include <omp.h>

// Факториал с reduction
long long factorial_reduction(int number)
{
    long long fac = 1;
#pragma omp parallel for reduction(* : fac)
    for (int n = 2; n <= number; ++n)
    {
        fac *= n;
    }
    return fac;
}

// Факториал с atomic
long long factorial_atomic(int number)
{
    long long fac = 1;
#pragma omp parallel for
    for (int n = 2; n <= number; ++n)
    {
#pragma omp atomic
        fac *= n;
    }
    return fac;
}

int main()
{
    const int Ns[] = {1000000, 10000000, 100000000};
    const int threads[] = {1, 2, 4, 8};

    std::cout << "Threads | reduction time (s) | atomic time (s)\n";
    for (int t : threads)
    {
        omp_set_num_threads(t);

        for (int N : Ns)
        {
            double t0 = omp_get_wtime();
            volatile long long r1 = factorial_reduction(N);
            double tr = omp_get_wtime() - t0;

            double t1 = omp_get_wtime();
            volatile long long r2 = factorial_atomic(N);
            double ta = omp_get_wtime() - t1;

            std::cout
                << t << "\t| "
                << tr << "\t\t| "
                << ta << "\n";
        }
        std::cout << "--------------------------------------------\n";
    }
    return 0;
}
