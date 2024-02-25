#include <fstream>
#include <iostream>
#include <string>
#include <chrono>
#include <vector>

using namespace std;

void delFirstZero(string& s)
{
    uint16_t ind = s.find('1');
    if (ind == -1)
    {
        return;
    }

    s = s.substr(ind);
}

int main()
{
    ifstream fin("input.txt");
    ofstream fout("output.txt");

    string x, y;
    fin >> x >> x >> x >> y;

    delFirstZero(x);
    delFirstZero(y);

    vector <vector<short>> vec(x.size() + 1, vector<short>(y.size() + 1, 0));

    for (int i = 0; i < x.size(); i++)
    {
        for (int j = 0; j < y.size(); j++)
        {
            if (x[x.size() - 1 - i] == y[y.size() - 1 - j])
            {
                vec[i + 1][j + 1] = vec[i][j] + 1;
            }
            else
            {
                vec[i + 1][j + 1] = max(vec[i][j + 1], vec[i + 1][j]);
            }
        }
    }
   
    string ans(vec[x.size()][y.size()],'0');
    ans[0] = '1';

    int i = 1, j = 1;
    int prevI = 0, prevJ = 0;
    int prevLenth = ans.size();

    for  (; i < y.size(); i++)
    {
        if (y[i] == '1') {
            for (; j < x.size(); j++)
            {
                if (x[j] == '1') {
                    if (prevLenth - vec[x.size() - j][y.size() - i] > min(i - prevI,  j - prevJ)) {
                        if (prevLenth - vec[x.size() - j][y.size() - i] >  i - prevI) {
                            prevI++;
                            break;
                        }
                        else 
                        {
                            prevJ++;
                            continue;
                        }
                    }
                    else
                    {
                        ans[ans.size() - vec[x.size() - j][y.size() - i]] = '1';
                        prevI = i;
                        prevJ = j;
                        prevLenth = vec[x.size() - j][y.size() - i];
                        j++;
                        break;
                    }
                }

            }
        }
    }

    fout << ans.size() << "\n" << ans;
}