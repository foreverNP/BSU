#include <algorithm>
#include <chrono>
#include <execution>
#include <iostream>
#include <random>
#include <vector>

static bool isOdd(int value)
{
    return (value % 2) != 0;
}

int main()
{
    constexpr size_t ELEMENT_COUNT = 1000'000'000;
    std::vector<int> values(ELEMENT_COUNT);

    std::mt19937_64 rng{std::random_device{}()};
    std::uniform_int_distribution<int> dist(0, 1'000'000);
    std::generate(std::execution::par, values.begin(), values.end(),
                  [&]()
                  { return dist(rng); });

    // measure single‐threaded sort
    auto t_start = std::chrono::high_resolution_clock::now();
    std::sort(std::execution::seq, values.begin(), values.end());
    auto t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Single‐threaded sort took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n";

    // measure single‐threaded max_element
    t_start = std::chrono::high_resolution_clock::now();
    auto max_it_seq = std::max_element(std::execution::seq,
                                       values.begin(), values.end());
    t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Single‐threaded max_element took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n"
              << "Maximum (seq): " << *max_it_seq << "\n";

    // measure single‐threaded count_if
    t_start = std::chrono::high_resolution_clock::now();
    auto odd_count_seq = std::count_if(std::execution::seq,
                                       values.begin(), values.end(),
                                       isOdd);
    t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Single‐threaded count_if(isOdd) took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n"
              << "Odd count (seq): " << odd_count_seq << "\n\n";

    // parallel versions
    t_start = std::chrono::high_resolution_clock::now();
    std::sort(std::execution::par, values.begin(), values.end());
    t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Parallel sort took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n";

    t_start = std::chrono::high_resolution_clock::now();
    auto max_it_par = std::max_element(std::execution::par,
                                       values.begin(), values.end());
    t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Parallel max_element took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n"
              << "Maximum (par): " << *max_it_par << "\n";

    t_start = std::chrono::high_resolution_clock::now();
    auto odd_count_par = std::count_if(std::execution::par,
                                       values.begin(), values.end(),
                                       isOdd);
    t_end = std::chrono::high_resolution_clock::now();
    std::cout << "Parallel count_if(isOdd) took: "
              << std::chrono::duration<double>(t_end - t_start).count()
              << " seconds\n"
              << "Odd count (par): " << odd_count_par << "\n";

    return 0;
}
