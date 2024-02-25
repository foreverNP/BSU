#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main()
{
	fstream fin("input.txt");
	ofstream fout("output.txt");

	short n;
	fin >> n;

	vector<short> val(n + 1);
	vector<vector<int>> dp(n, vector<int>(n, 0));

	for (int i = 0; i < n; i++) {
		fin >> val[i] >> val[i + 1];
	}

	for (int l = 1; l < n; l++) {
		for (int i = 0; i < n - l; i++) {
			int j = i + l;
			dp[i][j] = 1000000000;

			for (int k = i; k < j; k++) {
				dp[i][j] = min(dp[i][j], dp[i][k] + dp[k + 1][j] + val[i] * val[k + 1] * val[j + 1]);
			}
		}
	}

	fout << dp[0][n - 1];
}