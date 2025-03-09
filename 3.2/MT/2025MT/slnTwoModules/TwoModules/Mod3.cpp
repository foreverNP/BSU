#include "pch.h"

extern int product(int n, float v[], float w[], float result[]);

int product(int n, float v[], float w[], float result[])
{
    if (n != 3) return 1;
    result[0] = v[1] * w[2] - v[2] * w[1];
    result[1] = v[2] * w[0] - v[0] * w[2];
    result[2] = v[0] * w[1] - v[1] * w[0];
    return 0;
}