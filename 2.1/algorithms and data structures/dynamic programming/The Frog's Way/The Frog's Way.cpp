#include <iostream>
#include <vector>

using namespace std;

int main() {
  int n;
  cin >> n;

  if (n == 2) {
    cout << -1;
    return 0;
  }

  vector<int> mosquitos(n);
  vector<int> dp(n);
  for (int i = 0; i < n; i++) {
    cin >> mosquitos[i];

    if (i > 2) {
      dp[i] = mosquitos[i] + max(dp[i - 3], dp[i - 2]);
    } else if (i == 0) {
      dp[i] = mosquitos[i];
    } else if (i == 1) {
      dp[i] = -1;
    } else if (i == 2) {
      dp[i] = dp[0] + mosquitos[i];
    }
  }

  cout << dp[n - 1] << "\n";

  vector<int> moves;
  moves.push_back(n);
  int k = dp[n - 1] - mosquitos[n - 1];
  for (int i = n - 3; i >= 0; i--) {
    if (dp[i] == k) {
      moves.push_back(i + 1);
      k = k - mosquitos[i];
      i--;
    }
  }

  for (int i = moves.size() - 1; i >= 0; i--) {
    cout << moves[i] << " ";
  }
}