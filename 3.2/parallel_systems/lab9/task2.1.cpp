#include <iostream>
#include <omp.h>

int main()
{
    const int N = 16;

    omp_set_num_threads(4);

    int count[32] = {0};

#pragma omp parallel for
    for (int i = 0; i < N; ++i)
    {
        int tid = omp_get_thread_num();
#pragma omp atomic
        ++count[tid];

#pragma omp critical
        {
            std::cout << "i=" << i
                      << " выполняет поток " << tid
                      << std::endl;
        }
    }

    std::cout << "\nИтераций по потокам:\n";
    for (int t = 0; t < omp_get_max_threads(); ++t)
    {
        std::cout << "Поток " << t << ": "
                  << count[t] << " итераций\n";
    }

    return 0;
}
