#include <iostream>
#include <vector>
#include <algorithm>
#include <future>
#include <numeric>
#include <random>
#include <chrono>

using Clock = std::chrono::high_resolution_clock;
using Duration = std::chrono::duration<double>;

template <typename It>
int findMaximum(It first, It last)
{
    auto count = std::distance(first, last);
    if (count < 2000)
    {
        return *std::max_element(first, last);
    }

    It mid = first + count / 2;

    auto rightFuture = std::async(std::launch::async, findMaximum<It>, mid, last);

    int leftMax = findMaximum(first, mid);
    int rightMax = rightFuture.get();

    return std::max(leftMax, rightMax);
}

int main()
{
    std::vector<size_t> testSizes = {1'000'000, 10'000'000, 100'000'000};

    std::mt19937_64 rng(12345);

    for (auto n : testSizes)
    {
        std::vector<int> data(n);
        std::iota(data.begin(), data.end(), 1);
        std::shuffle(data.begin(), data.end(), rng);

        auto t0 = Clock::now();
        int maximum = findMaximum(data.begin(), data.end());
        auto t1 = Clock::now();

        Duration elapsed = t1 - t0;
        std::cout << "N = " << n
                  << "  | Max = " << maximum
                  << "  | Time = " << elapsed.count() << " s\n";
    }

    return 0;
}
