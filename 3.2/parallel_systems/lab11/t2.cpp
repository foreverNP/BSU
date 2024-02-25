#include <iostream>
#include <vector>
#include <thread>
#include <functional>
#include <chrono>
#include <cmath>

double eq_squared(double val) { return val * val; }
double eq_sine(double val) { return std::sin(val); }
double eq_gaussian(double val) { return std::exp(-val * val); }

struct calc_segment
{
    void compute(std::function<double(double)> expression,
                 double start_point, double step_size, size_t first_idx, size_t last_idx,
                 double &segment_value)
    {
        double accumulator = 0.0;
        for (size_t idx = first_idx; idx < last_idx; ++idx)
        {
            double position = start_point + idx * step_size;
            accumulator += expression(position);
        }
        segment_value = accumulator * step_size;
    }
};

double calculate_numerical_approximation(std::function<double(double)> expression,
                                         double lower_limit, double upper_limit,
                                         size_t sample_count,
                                         size_t worker_count)
{
    double step_size = (upper_limit - lower_limit) / sample_count;

    std::vector<double> partial_results(worker_count);
    std::vector<std::thread> worker_threads(worker_count - 1);

    size_t samples_per_worker = sample_count / worker_count;

    // Create worker threads
    for (size_t worker_id = 0; worker_id < worker_count - 1; ++worker_id)
    {
        size_t start_sample = worker_id * samples_per_worker;
        size_t end_sample = start_sample + samples_per_worker;

        worker_threads[worker_id] = std::thread(
            [&](size_t start, size_t end, size_t id)
            {
                calc_segment().compute(expression, lower_limit, step_size,
                                       start, end, partial_results[id]);
            },
            start_sample, end_sample, worker_id);
    }

    // Process final segment in current thread
    size_t final_start = (worker_count - 1) * samples_per_worker;
    size_t final_end = sample_count;
    calc_segment().compute(expression, lower_limit, step_size,
                           final_start, final_end, partial_results[worker_count - 1]);

    for (auto &thread : worker_threads)
        thread.join();

    double final_result = 0.0;
    for (double partial : partial_results)
        final_result += partial;

    return final_result;
}

int main()
{
    std::vector<std::string> formula_labels = {"x^2", "sin(x)", "exp(-x^2)"};
    std::vector<std::function<double(double)>> formulas = {eq_squared, eq_sine, eq_gaussian};
    std::vector<size_t> sample_counts = {10000000, 100000000, 1000000000};
    std::vector<size_t> worker_counts = {1, 2, 4};

    double lower_bound = 0.0, upper_bound = 1.0;

    for (size_t formula_idx = 0; formula_idx < formulas.size(); ++formula_idx)
    {
        std::cout << "\nFormula: " << formula_labels[formula_idx] << "\n";
        for (size_t samples : sample_counts)
        {
            for (size_t workers : worker_counts)
            {
                auto time_begin = std::chrono::high_resolution_clock::now();

                double answer = calculate_numerical_approximation(formulas[formula_idx],
                                                                  lower_bound, upper_bound,
                                                                  samples, workers);

                auto time_end = std::chrono::high_resolution_clock::now();
                std::chrono::duration<double> elapsed = time_end - time_begin;

                std::cout << "  Samples=" << samples << ", Workers=" << workers
                          << ", Value=" << answer
                          << ", Duration=" << elapsed.count() << "s\n";
            }
        }
    }

    return 0;
}
