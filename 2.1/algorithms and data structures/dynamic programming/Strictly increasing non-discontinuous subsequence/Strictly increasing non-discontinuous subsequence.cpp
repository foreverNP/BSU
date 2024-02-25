#include <fstream>
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

int LowerBound(const vector<int>& vec, int x) {
    int l = 0;
    int r = vec.size();
    int k;

    while (l < r) {
        k = (l + r) / 2;

        if (x <= vec[k]) {
            r = k;
        }
        else {
            l = k + 1;
        }
    }

    return l;
}

int main()
{
    ifstream fin("input.txt");
    ofstream fout("output.txt");

    int n;
    fin >> n;
    vector<int> dp(n + 2, 1000000001);
    dp[0] = -1000000001;

    int val;
    for (int i = 0; i < n; i++)
    {
        fin >> val;
        int j = LowerBound(dp, val);
        dp[j] = val;
    }

    int size = 0;
    for (int i = 1; i < dp.size(); i++)
    {
        if (dp[i] != 1000000001) {
            size++;
        }
    }

    fout << size;
}