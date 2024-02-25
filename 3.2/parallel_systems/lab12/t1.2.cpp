#include <chrono>
#include <iostream>
#include <stop_token>
#include <thread>

void workerRoutine(std::stop_token stopToken)
{
    while (!stopToken.stop_requested())
    {
        std::cout << "Working...\n";
        std::this_thread::sleep_for(std::chrono::milliseconds(500));
    }
    std::cout << "Background task stopping\n";
}

int main()
{
    std::jthread backgroundWorker{workerRoutine};

    std::this_thread::sleep_for(std::chrono::seconds(3));
    std::cout << "Stop requested\n";

    backgroundWorker.request_stop();
    backgroundWorker.join();
    std::cout << "Worker thread has ended\n";
    return 0;
}
