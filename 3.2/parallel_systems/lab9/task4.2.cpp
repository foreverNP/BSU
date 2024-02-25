#include <iostream>
#include <vector>
#include <random>
#include <limits>
#include <omp.h>
#include <iomanip>

int main()
{
    const int sizes[] = {1000000, 5000000, 10000000, 100000000};
    const int threads[] = {1, 2, 4, 8, 16};

    std::mt19937 gen(42);
    std::uniform_real_distribution<> dist(-1e6, 1e6);

    std::cout << " size   | threads |   time (s)\n";
    std::cout << "--------+---------+------------\n";

    for (int sz : sizes)
    {
        std::vector<double> a(sz);
        for (int i = 0; i < sz; ++i)
            a[i] = dist(gen);

        for (int nt : threads)
        {
            omp_set_num_threads(nt);

            double t0 = omp_get_wtime();

            double global_min = std::numeric_limits<double>::infinity();
#pragma omp parallel for
            for (int i = 0; i < sz; ++i)
            {
                if (a[i] < global_min)
                {
#pragma omp critical
                    {
                        if (a[i] < global_min)
                            global_min = a[i];
                    }
                }
            }

            double t1 = omp_get_wtime();
            double dt = t1 - t0;

            std::cout
                << std::setw(7) << sz << " | "
                << std::setw(7) << nt << " | "
                << std::fixed << std::setprecision(6)
                << dt << "\n";
        }
    }

    return 0;
}