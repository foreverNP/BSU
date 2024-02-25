#include <iostream>
#include <cmath>
#include <iomanip>
#include <omp.h>

int main()
{
    constexpr double PI = 3.141592653589;

    const int sizes[] = {1024, 4096, 4 * 4096, 8 * 4096};
    const int threads[] = {1, 2, 4, 8, 16};

    std::cout << " size | threads |   time (s)   \n";
    std::cout << "------+---------+--------------\n";

    for (int sz : sizes)
    {
        for (int nt : threads)
        {
            omp_set_num_threads(nt);
            double *sinTable = new double[sz];

            double t0 = omp_get_wtime();
#pragma omp parallel for
            for (int n = 0; n < sz; ++n)
            {
                sinTable[n] = std::sin(2 * PI * n / sz);
            }
            double t1 = omp_get_wtime();

            std::cout
                << std::setw(5) << sz << " | "
                << std::setw(7) << nt << " | "
                << std::fixed
                << std::setprecision(6)
                << (t1 - t0) << "\n";

            delete[] sinTable;
        }
    }

    return 0;
}
