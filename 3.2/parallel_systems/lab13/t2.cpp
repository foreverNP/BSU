#include <iostream>
#include <vector>
#include <algorithm>
#include <future>
#include <numeric>
#include <random>
#include <chrono>

using Clock = std::chrono::high_resolution_clock;
using Seconds = std::chrono::duration<double>;

int computeMaxDC(const std::vector<int> &arr, size_t left, size_t right, size_t cutOff = 2000)
{
    size_t span = right - left;
    if (span <= cutOff)
    {
        return *std::max_element(arr.begin() + left, arr.begin() + right);
    }

    size_t mid = left + span / 2;
    auto rightFut = std::async(std::launch::async, computeMaxDC,
                               std::cref(arr), mid, right, cutOff);
    int leftMax = computeMaxDC(arr, left, mid, cutOff);
    int rightMax = rightFut.get();
    return std::max(leftMax, rightMax);
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

        auto tStart = Clock::now();
        int maximum = computeMaxDC(data, 0, data.size());
        auto tEnd = Clock::now();

        Seconds elapsed = tEnd - tStart;
        std::cout << "Size = " << N
                  << "  | Max = " << maximum
                  << "  | Time = " << elapsed.count() << " s\n";
    }

    return 0;
}
