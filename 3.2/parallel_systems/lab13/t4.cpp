#include <iostream>
#include <vector>
#include <algorithm>
#include <numeric>
#include <random>
#include <chrono>
#include <omp.h>

using Clock = std::chrono::high_resolution_clock;
using Seconds = std::chrono::duration<double>;

int main()
{
    std::vector<size_t> sizes = {1'000'000, 10'000'000, 100'000'000};
    std::mt19937_64 rng(12345);

    for (auto N : sizes)
    {
        std::vector<int> data(N);
        std::iota(data.begin(), data.end(), 1);
        std::shuffle(data.begin(), data.end(), rng);

        int globalMax = std::numeric_limits<int>::min();
        auto t0 = Clock::now();
#pragma omp parallel for reduction(max : globalMax)
        for (size_t i = 0; i < data.size(); ++i)
        {
            globalMax = std::max(globalMax, data[i]);
        }
        auto t1 = Clock::now();

        Seconds elapsed = t1 - t0;
        std::cout << "Size = " << N
                  << "  | Max = " << globalMax
                  << "  | Time = " << elapsed.count() << " s\n";
    }

    return 0;
}
