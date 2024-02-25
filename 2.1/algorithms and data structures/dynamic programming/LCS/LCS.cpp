#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main()
{
    short n;
    cin >> n;

    vector <short> a(n);
    for (size_t i = 0; i < n; i++)
    {
        cin >> a[i];
    }

    vector <vector<short>> dp(n + 1, vector<short>(n + 1, 0));

    short b;
    for (size_t i = 0; i < n; i++)
    {
        cin >> b;

        for (size_t j = 0; j < n; j++)
        {
            if (b == a[j]) 
            {
                dp[i + 1][j + 1] = dp[i][j] + 1;
            }
            else
            {
                dp[i + 1][j + 1] = max(dp[i + 1][j], dp[i][j + 1]);
            }
        }
    }

    short k = dp[n][n];
    vector<short> aInd(k), bInd(k);
    short i = n, j = n;
    while (k)
    {
        while (dp[i][j - 1] == k) {
            j--;
        }
        while (dp[i - 1][j] == k) {
            i--;
        }

        bInd[k - 1] = --i;
        aInd[k - 1] = --j;
        k--;
    }

    cout << dp[n][n] << "\n";
    for (short i : aInd)
    {
        cout << i << " ";
    }
    cout << "\n";
    for (short i : bInd)
    {
        cout << i << " ";
    }
}