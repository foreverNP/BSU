#include <iostream>
#include <vector>
#include <random>

int main()
{
    int n = 2;                                                     // Размерность системы
    double x = 0, y = 0;                                           // Решение системы
    std::vector<std::vector<double>> a(n, std::vector<double>(n)); // Исходная матрица
    std::vector<double> f(n);                                      // Правая часть системы
    std::vector<double> h(n);
    std::vector<double> pi(n);                                     // Вектор нач. вероятностей цепи Маркова
    std::vector<std::vector<double>> p(n, std::vector<double>(n)); // Матрица переходных состояний цепи Маркова
    int N = 1000;                                                  // Длина цепи Маркова

    std::vector<int> i(N + 1);    // Цепь Маркова
    std::vector<double> Q(N + 1); // Веса состояний цепи Маркова

    int m = 10000;                   // Количество реализаций цепи Маркова
    std::vector<double> ksi(m, 0.0); // СВ

    // Инициализация матриц и векторов
    a[0][0] = -0.1;
    a[0][1] = 0.8;
    a[1][0] = 0.4;
    a[1][1] = -0.1;
    f[0] = 0.1;
    f[1] = -0.2;
    h[0] = 0.0;
    h[1] = 1.0;
    pi[0] = 0.5;
    pi[1] = 0.5;
    p[0][0] = 0.5;
    p[0][1] = 0.5;
    p[1][0] = 0.5;
    p[1][1] = 0.5;

    // Настройка генератора случайных чисел
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(0.0, 1.0);

    // Моделируем m цепей Маркова длины N
    for (int j = 0; j < m; j++)
    {
        double alpha = dis(gen);
        i[0] = (alpha < pi[0]) ? 0 : 1; // Начальное состояние

        for (int k = 1; k <= N; k++)
        {
            alpha = dis(gen);
            i[k] = (alpha < p[i[k - 1]][0]) ? 0 : 1;
        }

        // Вычисляем веса цепи Маркова
        Q[0] = (pi[i[0]] > 0) ? h[i[0]] / pi[i[0]] : 0.0;

        for (int k = 1; k <= N; k++)
        {
            double denominator = p[i[k - 1]][i[k]];
            Q[k] = (denominator > 0) ? Q[k - 1] * a[i[k - 1]][i[k]] / denominator : 0.0;
        }

        for (int k = 0; k <= N; k++)
        {
            ksi[j] += Q[k] * f[i[k]];
        }

        x += ksi[j];
    }

    x /= m;
    std::cout << x << std::endl;

    return 0;
}