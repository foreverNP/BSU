#include <atomic>
#include <chrono>
#include <iostream>
#include <thread>

static std::atomic<bool> terminationSignal{false};

void workerRoutine()
{
    while (!terminationSignal.load())
    {
        std::cout << "Working...\n";
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }
    std::cout << "Background task stopping\n";
}

int main()
{
    std::thread backgroundWorker{workerRoutine};

    std::this_thread::sleep_for(std::chrono::seconds(3));
    std::cout << "Stop requested\n";
    terminationSignal.store(true);

    backgroundWorker.join();
    std::cout << "Worker thread has ended\n";
    return 0;
}
