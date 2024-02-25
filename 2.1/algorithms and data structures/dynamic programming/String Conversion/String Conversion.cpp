#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main()
{
	fstream fin("in.txt");
	ofstream fout("out.txt");

	int x, y, z;
	string a, b;
	fin >> x >> y >> z >> a >> b;
    vector<vector<int>> dp(a.size() + 1, vector<int>(b.size() + 1, 0));

	for (size_t i = 1; i <= b.size(); i++)
	{
		dp[0][i] = i * y;
	}
	for (size_t i = 1; i <= a.size(); i++)
	{
		dp[i][0] = i * x;
	}

	for (size_t i = 1; i <= a.size(); i++)
	{
		for (size_t j = 1; j <= b.size(); j++)
		{
			dp[i][j] = min(min(dp[i - 1][j] + x, dp[i][j - 1] + y), dp[i - 1][j - 1] + z * (a[i - 1] == b[j - 1] ? 0 : 1));
		}
	}

	fout << dp[a.size()][b.size()];
}