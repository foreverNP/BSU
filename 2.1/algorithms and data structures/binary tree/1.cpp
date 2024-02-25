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

void addNode(Node* root, int val)
{
	if (root->value == val)
	{
		return;
	}

	if (root->value > val)
	{
		if (root->l == nullptr)
		{
			root->l = new Node;
			root->l->value = val;
		}
		else
		{
			addNode(root->l, val);
		}
		
		return;
	}

	if (root->r == nullptr)
	{
		root->r = new Node;
		root->r->value = val;
		return;
	}

	addNode(root->r, val);
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

void leftBypassCount(Node* root, int &n)
{
	if (root == nullptr)
	{
		return;
	}

	leftBypassCount(root->l, n);
	leftBypassCount(root->r, n);

	if (root->l != nullptr) 
	{
		root->lNodes = root->l->lNodes + root->l->rNodes + 1;
	}
	if (root->r != nullptr)
	{
		root->rNodes = root->r->lNodes + root->r->rNodes + 1;
	}

	if (root->lNodes != root->rNodes)
	{
		n++;
	}
}

void leftBypassFind(Node* root, vector <int> &values)
{
	if (root == nullptr)
	{
		return;
	}

	leftBypassFind(root->l, values);
	if (root->lNodes != root->rNodes)
	{
		values.push_back(root->value);
	}
	leftBypassFind(root->r, values);
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
    ifstream fin("in.txt");
	ofstream fout("out.txt");

	Node* root = new Node;
	fin >> root->value;

	int val, n = 0;

	while (fin >> val) {
		addNode(root, val);
	}
	leftBypassCount(root, n);

	if (n % 2 != 0)
	{
		vector <int> values;
		values.reserve(n);

		leftBypassFind(root, values);

		deleteValue(root, values[n / 2]);
	}

	leftBypassPrint(fout, root);
}