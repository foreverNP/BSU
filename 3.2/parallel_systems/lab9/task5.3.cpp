#include <iostream>
#include <iomanip>
#include <vector>
#include <random>
#include <omp.h>

double dot_serial(const std::vector<double> &A, const std::vector<double> &B)
{
    double sum = 0.0;
    size_t M = A.size();
    for (size_t i = 0; i < M; ++i)
        sum += A[i] * B[i];
    return sum;
}

double dot_reduction(const std::vector<double> &A, const std::vector<double> &B)
{
    double sum = 0.0;
    size_t M = A.size();
#pragma omp parallel for reduction(+ : sum)
    for (size_t i = 0; i < M; ++i)
        sum += A[i] * B[i];
    return sum;
}

double dot_atomic(const std::vector<double> &A, const std::vector<double> &B)
{
    double sum = 0.0;
    size_t M = A.size();
#pragma omp parallel for
    for (size_t i = 0; i < M; ++i)
    {
#pragma omp atomic
        sum += A[i] * B[i];
    }
    return sum;
}

double dot_manual(const std::vector<double> &A, const std::vector<double> &B)
{
    double global_sum = 0.0;
    size_t M = A.size();
#pragma omp parallel
    {
        double local_sum = 0.0;
        int tid = omp_get_thread_num();
        int nthreads = omp_get_num_threads();
        size_t chunk = M / nthreads;
        size_t start = tid * chunk;
        size_t end = (tid == nthreads - 1 ? M : start + chunk);
        for (size_t i = start; i < end; ++i)
            local_sum += A[i] * B[i];
#pragma omp critical
        global_sum += local_sum;
    }
    return global_sum;
}

int main()
{
    const std::vector<size_t> Ms = {10000000, 50000000, 100000000};
    const int threads[] = {2, 4, 8};

    std::mt19937_64 rng(12345);
    std::uniform_real_distribution<double> dist(0.0, 1.0);

    std::cout << std::fixed << std::setprecision(6);
    std::cout << "Method     |       M | Threads |   Time(s)\n";
    std::cout << "--------------------------------------------\n";

    for (size_t M : Ms)
    {
        std::vector<double> A(M), B(M);
        for (size_t i = 0; i < M; ++i)
        {
            A[i] = dist(rng);
            B[i] = dist(rng);
        }

        // serial
        double t0 = omp_get_wtime();
        double ds = dot_serial(A, B);
        double ts = omp_get_wtime() - t0;
        std::cout
            << std::setw(10) << "serial"
            << " | " << std::setw(8) << M
            << " | " << std::setw(7) << 1
            << " | " << std::setw(8) << ts
            << "\n";

        for (int t : threads)
        {
            omp_set_num_threads(t);

            // reduction
            t0 = omp_get_wtime();
            double dr = dot_reduction(A, B);
            double tr = omp_get_wtime() - t0;
            std::cout
                << std::setw(10) << "reduction"
                << " | " << std::setw(8) << M
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << tr
                << "\n";

            // atomic
            t0 = omp_get_wtime();
            double da = dot_atomic(A, B);
            double ta = omp_get_wtime() - t0;
            std::cout
                << std::setw(10) << "atomic"
                << " | " << std::setw(8) << M
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << ta
                << "\n";

            // manual
            t0 = omp_get_wtime();
            double dm = dot_manual(A, B);
            double tm = omp_get_wtime() - t0;
            std::cout
                << std::setw(10) << "manual"
                << " | " << std::setw(8) << M
                << " | " << std::setw(7) << t
                << " | " << std::setw(8) << tm
                << "\n";
        }
        std::cout << "--------------------------------------------\n";
    }

    return 0;
}
