#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

struct Node {
    int value;
    long long min;
    long long max;
};

bool isInInterv(Node v) {
    return v.min <= v.value && v.value < v.max;
}


int main() {
    ifstream fin("bst.in");
    ofstream fout("bst.out");

    int n;
    fin >> n;

    vector<Node> nodes(n);

    fin >> nodes[0].value;
    nodes[0].min = INT64_MIN;
    nodes[0].max = INT64_MAX;

    int p;
    char c;
    for (int i = 1; i < n; i++) {
        fin >> nodes[i].value >> p >> c;

        if (c == 'R') {
            nodes[i].min = nodes[p - 1].value;
            nodes[i].max = nodes[p - 1].max;
        }
        else {
            nodes[i].min = nodes[p - 1].min;
            nodes[i].max = nodes[p - 1].value;
        }

        if (!isInInterv(nodes[i])) {
            fout << "NO";
            return 0;
        }
    }

    fout << "YES";
}