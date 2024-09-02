#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main()
{
    ifstream fin("input.txt");
    ofstream fout("output.txt");

    string str;
    fin >> str;

    vector<vector<short>> dp(str.size(), vector<short>(str.size(), 0));
    vector<vector<pair<short, short>>> pr(str.size(), vector<pair<short, short>>(str.size(), {-1, -1}));

    for (int i = 0; i < str.size(); i++)
    {
        dp[i][i] = 1;
    }

    for (int i = 1; i < str.size(); i++)
    {
        for (int j = 0; j < str.size() - i; j++)
        {
            if (str[j + i] == str[j])
            {
                dp[j + i][j] = dp[j + i - 1][j + 1] + 2;
                pr[j + i][j] = {j + i - 1, j + 1};
            }
            else if (dp[j + i - 1][j] >= dp[j + i][j + 1])
            {
                dp[j + i][j] = dp[j + i - 1][j];
                pr[j + i][j] = {j + i - 1, j};
            }
            else
            {
                dp[j + i][j] = dp[j + i][j + 1];
                pr[j + i][j] = {j + i, j + 1};
            }
        }
    }

    string ans(dp[str.size() - 1][0], ' ');
    int i = str.size() - 1, j = 0, k = 0;

    while (i != j && pr[i][j].first != -1)
    {
        if (pr[i][j].first == i - 1 && pr[i][j].second == j + 1)
        {
            ans[k] = ans[ans.size() - 1 - k] = str[i];
            k++;
        }
        int iTemp = i, jTemp = j;
        i = pr[iTemp][jTemp].first;
        j = pr[iTemp][jTemp].second;
    }

    if (i == j)
    {
        ans[k] = str[i];
    }

    fout << ans.size() << "\n" << ans;
}