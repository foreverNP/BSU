#include <omp.h>
#include <stdio.h>
#include <cstdlib>

#define PAD 8

void calculate_pi(long num_steps, int num_threads, int schedule_type)
{
    double step;
    double pi = 0.0;
    double start_time, end_time;

    double (*sum)[PAD] = (double (*)[PAD])malloc(num_threads * sizeof(double[PAD]));
    for (int i = 0; i < num_threads; i++)
    {
        sum[i][0] = 0.0;
    }

    step = 1.0 / (double)num_steps;

    omp_set_num_threads(num_threads);

    start_time = omp_get_wtime();

    if (schedule_type == 0)
    {
// static scheduling
#pragma omp parallel for reduction(+ : pi) schedule(static)
        for (int i = 0; i < num_steps; i++)
        {
            double x = (i + 0.5) * step;
            pi += 4.0 / (1.0 + x * x);
        }
    }
    else if (schedule_type == 1)
    {
// dynamic scheduling
#pragma omp parallel for reduction(+ : pi) schedule(dynamic, 1000)
        for (int i = 0; i < num_steps; i++)
        {
            double x = (i + 0.5) * step;
            pi += 4.0 / (1.0 + x * x);
        }
    }
    else if (schedule_type == 2)
    {
// guided scheduling
#pragma omp parallel for reduction(+ : pi) schedule(guided, 1000)
        for (int i = 0; i < num_steps; i++)
        {
            double x = (i + 0.5) * step;
            pi += 4.0 / (1.0 + x * x);
        }
    }

    pi *= step;

    end_time = omp_get_wtime();

    const char *schedule_name;
    switch (schedule_type)
    {
    case 0:
        schedule_name = "static";
        break;
    case 1:
        schedule_name = "dynamic";
        break;
    case 2:
        schedule_name = "guided";
        break;
    }

    printf("Threads: %d | Steps: %ld | Schedule: %s | Pi: %.10f | Time: %.6f seconds\n",
           num_threads, num_steps, schedule_name, pi, end_time - start_time);

    free(sum);
}

int main()
{
    long steps_array[] = {10000000, 100000000, 1000000000};
    int threads_array[] = {1, 2, 4, 8};
    int schedule_types[] = {0, 1, 2}; // 0-static, 1-dynamic, 2-guided

    int num_steps_count = sizeof(steps_array) / sizeof(steps_array[0]);
    int num_threads_count = sizeof(threads_array) / sizeof(threads_array[0]);
    int num_schedules = sizeof(schedule_types) / sizeof(schedule_types[0]);

    for (int i = 0; i < num_steps_count; i++)
    {
        for (int j = 0; j < num_threads_count; j++)
        {
            for (int k = 0; k < num_schedules; k++)
            {
                calculate_pi(steps_array[i], threads_array[j], schedule_types[k]);
            }
            printf("--------------------------------------------------\n");
        }
    }

    return 0;
}