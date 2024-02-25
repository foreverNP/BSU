#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

long long binpow(long long a, long long n) {
	long long res = 1;

	while (n) {
		if (n & 1) {
			res = (res * a) % 1000000007;
		}
		a = (a * a) % 1000000007;
		n >>= 1;
	}
	
	return res;
}

int main()
{
	int n, k;
	cin >> n >> k;
	unsigned long long c = 1;

	for (int i = 1; i <= k; i++)
	{
		c = ((c * (n - i + 1)) % 1000000007 * binpow(i, 1000000005)) % 1000000007;
	}

	cout << c;
}