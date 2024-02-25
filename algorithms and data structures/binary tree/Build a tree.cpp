#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

struct Node
{
	int value;
	Node *l = nullptr;
	Node *r = nullptr;
};

void addNode(Node *root, int val)
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

void leftBypass(ofstream &fout, Node *root)
{
	if (root == nullptr)
	{
		return;
	}

	fout << root->value <<"\n";
	leftBypass(fout, root->l);
	leftBypass(fout, root->r);
}

int main()
{
	ifstream fin("input.txt");
	ofstream fout("output.txt");

	Node *root = new Node;
	fin >> root->value;

	int val;
	while (!fin.eof())
	{
		fin >> val;
		addNode(root, val);
	}

	leftBypass(fout, root);
}