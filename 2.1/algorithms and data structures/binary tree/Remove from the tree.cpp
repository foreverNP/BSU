#include <fstream>
#include <vector>
#include <iostream>

using namespace std;

struct Node
{
	int value;
	int rNodes = 0;
	int lNodes = 0;

	Node* l = nullptr;
	Node* r = nullptr;
};

void addNode(Node* root, int val, int &n)
{
	if (root->value == val)
	{
		return;
	}

	if (root->value > val)
	{
		root->lNodes++;

		if (root->l == nullptr)
		{
			root->l = new Node;
			root->l->value = val;
			n++;
		}
		else
		{
			addNode(root->l, val, n);
		}
		
		return;
	}

	root->rNodes++;

	if (root->r == nullptr)
	{
		root->r = new Node;
		root->r->value = val;
		n++;
		return;
	}

	addNode(root->r, val, n);
}

void leftBypassPrint(ofstream& fout, Node* root)
{
	if (root == nullptr)
	{
		return;
	}

	fout << root->value << "\n";
	leftBypassPrint(fout, root->l);
	leftBypassPrint(fout, root->r);
}

void leftBypassCount(Node* root, vector <int> &values, int &n)
{
	if (root == nullptr)
	{
		return;
	}

	leftBypassCount(root->l, values, n);
	if (root->lNodes != root->rNodes)
	{
		values[n] = root->value;
		n++;
	}
	leftBypassCount(root->r, values, n);
}

void deleteValue(Node*& root, int value) 
{
	if (root == nullptr) 
	{
		return;
	}

	if (root->value == value) {
		if (root->r == nullptr) {
			root = root->l;
			return;
		}
		if (root->l == nullptr) {
			root = root->r;
			return;
		}

		Node* prev = root;
		Node* cur = root->r;

		while (cur->l != nullptr) {
			prev = cur;
			cur = cur->l;
		}

		root->value = cur->value;
		if (prev == root) {
			root->r = cur->r;
		}
		else {
			prev->l = cur->r;
		}

		return;
	}

	if (value > root->value) {
		deleteValue(root->r, value);
	}
	else {
		deleteValue(root->l, value);
	}
}

int main() {
    ifstream fin("input.txt");
    ofstream fout("output.txt");
    
    int delVal;
    fin >> delVal;

	Node* root = new Node;
	fin >> root->value;

	int val, n = 0;

	while (!fin.eof()) {
		fin >> val;
		addNode(root, val, n);
	}

	deleteValue(root, delVal);

	leftBypassPrint(fout, root);
}