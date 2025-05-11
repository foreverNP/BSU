#include <iostream>
#include <vector>
#include <thread>
#include <numeric>
#include <chrono>
#include <functional>

template <typename Iter, typename Val>
class BlockSummer
{
public:
    void operator()(Iter begin, Iter finish, Val &output)
    {
        output = std::accumulate(begin, finish, output);
    }
};

template <typename Iter, typename Val>
Val multiThreadSum(Iter begin, Iter finish, Val initial, size_t workerCount)
{
    size_t dataLength = std::distance(begin, finish);

    if (dataLength == 0)
    {
        return initial;
    }

    // Calculate chunk size for each thread
    size_t chunkSize = dataLength / workerCount;

    // Store partial results and thread objects
    std::vector<Val> partialSums(workerCount);
    std::vector<std::thread> workers(workerCount - 1);

    // Distribute work among threads
    Iter chunkBegin = begin;
    for (size_t i = 0; i < (workerCount - 1); ++i)
    {
        Iter chunkEnd = chunkBegin;
        std::advance(chunkEnd, chunkSize);

        workers[i] = std::thread(
            BlockSummer<Iter, Val>(),
            chunkBegin,
            chunkEnd,
            std::ref(partialSums[i]));

        chunkBegin = chunkEnd;
    }

    BlockSummer<Iter, Val>()(chunkBegin, finish, partialSums[workerCount - 1]);

    for (auto &worker : workers)
    {
        worker.join();
    }

    // Combine partial results
    return std::accumulate(partialSums.begin(), partialSums.end(), initial);
}

int main()
{
    const std::vector<unsigned long> arraySizes = {10000000, 100000000, 1000000000};

    const std::vector<size_t> threadCounts = {1, 2, 4, 8};

    for (const auto &arraySize : arraySizes)
    {
        std::cout << "Array size: " << arraySize << std::endl;

        std::vector<int> dataset(arraySize);
        std::iota(dataset.begin(), dataset.end(), 1);

        for (const auto &threadCount : threadCounts)
        {
            auto startTime = std::chrono::high_resolution_clock::now();

            long long finalSum = multiThreadSum(
                dataset.begin(),
                dataset.end(),
                0LL,
                threadCount);

            auto endTime = std::chrono::high_resolution_clock::now();
            auto elapsedTime = std::chrono::duration<double>(endTime - startTime);

            std::cout << "  Workers: " << threadCount
                      << " | Duration: " << elapsedTime.count() << "s"
                      << " | Sum: " << finalSum << std::endl;
        }
        std::cout << std::endl;
    }

    return 0;
}