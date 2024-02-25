#include <omp.h>
#include <stdio.h>
#include <cstdlib>

#define PAD 8 // Для устранения ложного разделения

void calculate_pi(long num_steps, int num_threads)
{
    double step;
    int i, nthreads;
    double pi = 0.0;
    double start_time, end_time;

    double (*sum)[PAD] = (double (*)[PAD])malloc(num_threads * sizeof(double[PAD]));

    step = 1.0 / (double)num_steps;

    omp_set_num_threads(num_threads);

    start_time = omp_get_wtime();

#pragma omp parallel
    {
        int i, id, nthrds;
        double x;
        id = omp_get_thread_num();
        nthrds = omp_get_num_threads();
        if (id == 0)
            nthreads = nthrds;

        for (i = id, sum[id][0] = 0.0; i < num_steps; i = i + nthrds)
        {
            x = (i + 0.5) * step;
            sum[id][0] += 4.0 / (1.0 + x * x);
        }
    }

    for (i = 0, pi = 0.0; i < nthreads; i++)
    {
        pi += sum[i][0] * step;
    }

    end_time = omp_get_wtime();

    printf("Threads: %d | Steps: %ld | Pi: %.10f | Time: %.6f seconds\n",
           num_threads, num_steps, pi, end_time - start_time);

    free(sum);
}

int main()
{
    long steps_array[] = {10000000, 100000000, 1000000000};
    int threads_array[] = {1, 2, 4, 8};
    int num_steps_count = sizeof(steps_array) / sizeof(steps_array[0]);
    int num_threads_count = sizeof(threads_array) / sizeof(threads_array[0]);

    for (int i = 0; i < num_steps_count; i++)
    {
        for (int j = 0; j < num_threads_count; j++)
        {
            calculate_pi(steps_array[i], threads_array[j]);
        }
        printf("--------------------------------------------------\n");
    }

    return 0;
}