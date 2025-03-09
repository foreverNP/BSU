#include "pch.h"


	extern int scalar(int n, float v[], float w[], float* vw);

	int scalar(int n, float v[], float w[], float* vw)
	{
		if (n != 3) return 1;
		*vw = 0;
		for (int i = 0; i < n; i++)
			*vw = *vw + v[i] * w[i];
		return 0;

	}
