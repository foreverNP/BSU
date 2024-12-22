#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

// Решение системы Ax = f
int main()
{
    srand(time(NULL));

    int size = 3;                  // Размерность системы
    int n = 1000;                  // Длина одной цепи маркова
    vector<vector<float>> a(size); // Исходная матрица
    vector<float> f(size);         // Правая часть системы
    vector<float> h(size);
    vector<float> pi(size);        // Вектор нач. вероятностей цепи Маркова
    vector<vector<float>> p(size); // Матрица переходных состояний цепи Маркова
    vector<int> i;                 // Цепь Маркова
    vector<float> Q;               // Веса состояний цепи Маркова
    vector<float> ksi;             // СВ
    int m;                         // Количество реализаций цепи Маркова
    float alpha;                   // БСВ

    for (int k = 0; k < size; k++)
        a[k] = vector<float>(size);

    for (int k = 0; k < size; k++)
        p[k] = vector<float>(size);

    i = vector<int>(n + 1);   // Элементы цепи маркова
    Q = vector<float>(n + 1); // Матрица весов

    m = 10000; // Число моделируемых цепей маркова

    ksi = vector<float>(m);

    a = {{0, -0.2 / 1.2, -1.0 / 1.2}, {-0.1, 0.5, 0.1}, {-0.035 / 0.045, -0.005 / 0.045, 0}};

    for (int j = 0; j < size; j++) // Приводим систему к виду x = Ax + f
        for (int k = 0; k < size; k++)
            if (j == k)
                a[j][k] = 1 - a[j][k];
            else
                a[j][k] = -a[j][k];

    f[0] = 3.0 / 1.2;
    f[1] = 3.0;
    f[2] = 0.35 / 0.045;

    h[0] = 0;
    h[1] = 0;
    h[2] = 0;

    pi[0] = 1.0 / 3.0;
    pi[1] = 1.0 / 3.0;
    pi[2] = 1.0 / 3.0;

    p[0][0] = 0.0;
    p[0][1] = 0.5;
    p[0][2] = 0.5;
    p[1][0] = 1.0 / 3.0;
    p[1][1] = 1.0 / 3.0;
    p[1][2] = 1.0 / 3.0;
    p[2][0] = 0.5;
    p[2][1] = 0.5;
    p[2][2] = 0.0;

    for (int posInH = 0; posInH < 3; posInH++)
    {
        h[posInH] = 1; // смещаем 1 в координатах h

        for (int j = 0; j < m; j++) // Чистим
            ksi[j] = 0;
        i = vector<int>(n + 1);   // Чистим
        Q = vector<float>(n + 1); // Чистим

        // Моделируем m цепей Маркова длины n
        for (int j = 0; j < m; j++)
        {
            alpha = rand() / float(RAND_MAX);
            if (alpha < pi[0])
                i[0] = 0; // реализуется 1-е состояние
            else if (alpha < pi[0] + pi[1])
                i[0] = 1; // реализуется 2-е состояние
            else
                i[0] = 2; // реализуется 3-е состояние
            for (int k = 1; k <= n; k++)
            {
                alpha = rand() / float(RAND_MAX);
                if (alpha < pi[0])
                    i[k] = 0; // реализуется 1-е состояние
                else if (alpha < pi[1] + pi[0])
                    i[k] = 1; // реализуется 2-е состояние
                else
                    i[k] = 2; // реализуется 3-е состояние
            }

            // Вычисляем веса цепи Маркова
            if (pi[i[0]] > 0)
                Q[0] = h[i[0]] / pi[i[0]];
            else
                Q[0] = 0;
            for (int k = 1; k <= n; k++)
            {
                if (p[i[k - 1]][i[k]] > 0)
                    Q[k] = Q[k - 1] * a[i[k - 1]][i[k]] / p[i[k - 1]][i[k]];
                else
                    Q[k] = 0;
            }
            for (int k = 0; k <= n; k++)
                ksi[j] += Q[k] * f[i[k]];
        }

        float x = 0;
        for (int k = 0; k < m; k++)
            x += ksi[k];
        x = x / m;
        cout << x << '\n';

        h[posInH] = 0;
    }
}