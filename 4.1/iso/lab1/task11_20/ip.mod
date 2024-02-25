param m integer >= 1;
param n integer >= 1;

param A{1..m, 1..n};
param b{1..m};
param c{1..n};
param lb{1..n};
param ub{1..n};

var x{j in 1..n} integer >= lb[j] <= ub[j];

maximize Obj: sum{j in 1..n} c[j] * x[j];

s.t. Constr{i in 1..m}:
sum{j in 1..n} A[i,j] * x[j] <= b[i];