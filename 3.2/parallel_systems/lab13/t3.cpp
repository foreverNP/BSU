#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>
#include <execution>
#include <random>
#include <chrono>

using Clock = std::chrono::high_resolution_clock;
using Seconds = std::chrono::duration<double>;

enum class Policy
{
    Seq,
    Par,
    ParUnseq
};

const char *policyName(Policy p)
{
    switch (p)
    {
    case Policy::Seq:
        return "seq";
    case Policy::Par:
        return "par";
    case Policy::ParUnseq:
        return "par_unseq";
    }
    return "";
}

template <typename It>
int maxWithPolicy(Policy pol, It first, It last)
{
    switch (pol)
    {
    case Policy::Seq:
        return *std::max_element(std::execution::seq, first, last);
    case Policy::Par:
        return *std::max_element(std::execution::par, first, last);
    case Policy::ParUnseq:
        return *std::max_element(std::execution::par_unseq, first, last);
    }
    return *std::max_element(first, last);
}

int main()
{
    std::vector<size_t> sizes = {1'000'000, 10'000'000, 100'000'000};
    std::mt19937_64 rng(12345);

    for (auto N : sizes)
    {
        std::vector<int> data(N);
        std::iota(data.begin(), data.end(), 1);
        std::shuffle(data.begin(), data.end(), rng);

        std::cout << "---- N = " << N << " ----\n";
        for (Policy pol : {Policy::Seq, Policy::Par, Policy::ParUnseq})
        {
            auto t0 = Clock::now();
            int maximum = maxWithPolicy(pol, data.begin(), data.end());
            auto t1 = Clock::now();

            Seconds elapsed = t1 - t0;
            std::cout << policyName(pol)
                      << "  | Max = " << maximum
                      << "  | Time = " << elapsed.count() << " s\n";
        }
        std::cout << "\n";
    }

    return 0;
}
