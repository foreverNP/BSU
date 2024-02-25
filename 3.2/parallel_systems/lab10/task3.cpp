#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

// Структура для хранения результатов вычислений
typedef struct
{
    double sum;
    long start;
    long end;
    double step;
} PiCalcTask;

double calculate_interval(long start, long end, double step)
{
    double sum = 0.0;
    for (long i = start; i < end; i++)
    {
        double x = (i + 0.5) * step;
        sum += 4.0 / (1.0 + x * x);
    }
    return sum;
}

// Рекурсивная функция "разделяй и властвуй" для вычисления π
double pi_divide_conquer(long start, long end, double step, int depth, int cutoff_depth)
{
    if (depth >= cutoff_depth || (end - start) <= 10000)
    {
        return calculate_interval(start, end, step);
    }

    // делим интервал на две части
    long mid = start + (end - start) / 2;
    double left_sum, right_sum;

// Создаем задачи для левой и правой частей интервала
#pragma omp task shared(left_sum)
    left_sum = pi_divide_conquer(start, mid, step, depth + 1, cutoff_depth);

#pragma omp task shared(right_sum)
    right_sum = pi_divide_conquer(mid, end, step, depth + 1, cutoff_depth);

// Ждем завершения обеих задач
#pragma omp taskwait

    return left_sum + right_sum;
}

// Функция для запуска вычисления π
void calculate_pi_task(long num_steps, int num_threads, int cutoff_depth)
{
    double step = 1.0 / (double)num_steps;
    double pi = 0.0;
    double start_time, end_time;

    omp_set_num_threads(num_threads);

    start_time = omp_get_wtime();

#pragma omp parallel
    {
#pragma omp single
        {
            pi = pi_divide_conquer(0, num_steps, step, 0, cutoff_depth) * step;
        }
    }

    end_time = omp_get_wtime();

    printf("| %10ld | %8d | %8d | %16.10f | %12.6f |\n",
           num_steps, num_threads, cutoff_depth, pi, end_time - start_time);
}

int main()
{
    long steps_array[] = {10000000, 100000000, 1000000000};
    int threads_array[] = {1, 2, 4, 8};
    int cutoff_depths[] = {32, 512, 512 * 4};

    int num_steps_count = sizeof(steps_array) / sizeof(steps_array[0]);
    int num_threads_count = sizeof(threads_array) / sizeof(threads_array[0]);
    int num_depths = sizeof(cutoff_depths) / sizeof(cutoff_depths[0]);

    printf("+------------+----------+----------+------------------+--------------+\n");
    printf("| Шаги       | Потоки   | Глубина  | Значение π       | Время (сек)  |\n");
    printf("+------------+----------+----------+------------------+--------------+\n");

    for (int i = 0; i < num_steps_count; i++)
    {
        for (int j = 0; j < num_threads_count; j++)
        {
            for (int k = 0; k < num_depths; k++)
            {
                calculate_pi_task(steps_array[i], threads_array[j], cutoff_depths[k]);
            }
        }
        printf("+------------+----------+----------+------------------+--------------+\n");
    }

    return 0;
}