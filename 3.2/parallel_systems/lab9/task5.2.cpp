#include <iostream>
#include <iomanip>
#include <cmath>
#include <omp.h>

double pi_serial(int N)
{
    double sum = 0.0;
    for (int n = 0; n < N; ++n)
        sum += ((n % 2 == 0 ? 1.0 : -1.0) / (2 * n + 1));
    return 4.0 * sum;
}

double pi_reduction(int N)
{
    double sum = 0.0;
#pragma omp parallel for reduction(+ : sum)
    for (int n = 0; n < N; ++n)
        sum += ((n % 2 == 0 ? 1.0 : -1.0) / (2 * n + 1));
    return 4.0 * sum;
}

double pi_atomic(int N)
{
    double sum = 0.0;
#pragma omp parallel for
    for (int n = 0; n < N; ++n)
    {
        double term = (n % 2 == 0 ? 1.0 : -1.0) / (2 * n + 1);
#pragma omp atomic
        sum += term;
    }
    return 4.0 * sum;
}

double pi_critical(int N)
{
    double sum = 0.0;
#pragma omp parallel for
    for (int n = 0; n < N; ++n)
    {
        double term = (n % 2 == 0 ? 1.0 : -1.0) / (2 * n + 1);
#pragma omp critical
        sum += term;
    }
    return 4.0 * sum;
}

int main()
{
    const int Ns[] = {1000000, 5000000, 10000000};
    const int threads[] = {2, 4, 8};
    const double PI_REF = std::acos(-1.0);

    std::cout << std::fixed << std::setprecision(6);
    std::cout << "Method     |      N | Threads |   Time(s) |    Error\n";
    std::cout << "-------------------------------------------------------\n";

    for (int N : Ns)
    {
        // serial
        double t0 = omp_get_wtime();
        double ps = pi_serial(N);
        double ts = omp_get_wtime() - t0;
        double es = std::fabs(ps - PI_REF);
        std::cout
            << std::setw(10) << "serial"
            << " | " << std::setw(7) << N
            << " | " << std::setw(7) << 1
            << " | " << std::setw(8) << ts
            << " | " << std::setw(8) << es
            << "\n";

        for (int t : threads)
        {
            omp_set_num_threads(t);

            // reduction
            t0 = omp_get_wtime();
            double pr = pi_reduction(N);
            double tr = omp_get_wtime() - t0;
            double er = std::fabs(pr - PI_REF);
            std::cout
                << std::setw(10) << "reduction"
                << " | " << std::setw(7) << N
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << tr
                << " | " << std::setw(8) << er
                << "\n";

            // atomic
            t0 = omp_get_wtime();
            double pa = pi_atomic(N);
            double ta = omp_get_wtime() - t0;
            double ea = std::fabs(pa - PI_REF);
            std::cout
                << std::setw(10) << "atomic"
                << " | " << std::setw(7) << N
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << ta
                << " | " << std::setw(8) << ea
                << "\n";

            // critical
            t0 = omp_get_wtime();
            double pc = pi_critical(N);
            double tc = omp_get_wtime() - t0;
            double ec = std::fabs(pc - PI_REF);
            std::cout
                << std::setw(10) << "critical"
                << " | " << std::setw(7) << N
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << tc
                << " | " << std::setw(8) << ec
                << "\n";
        }
        std::cout << "-------------------------------------------------------\n";
    }

    return 0;
}
