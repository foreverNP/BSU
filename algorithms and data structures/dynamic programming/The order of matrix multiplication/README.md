# Task 0.4. The order of matrix multiplication

- Time limit: 1 sec
- Memory limit: 256 MB

A sequence of s matrices A1, A2, ..., As is given. It is necessary to determine in which order they should be multiplied so that the number of atomic multiplication operations is minimal. The matrices are assumed to be compatible with respect to matrix multiplication (i.e., the number of columns of the Ai − 1 matrix coincides with the number of rows of the Ai matrix).

Let's assume that the product of matrices is an operation that takes two matrices of size k × m and m × n as input and returns a matrix of size k × n, spending kmn atomic multiplication operations on this. (The base type allows you to store any element of the final and any possible intermediate matrix, so multiplying two elements requires one atomic operation.)

Since matrix multiplication is associative, the resulting matrix does not depend on the order in which the multiplication operations are performed. In other words, there is no difference in which order the brackets are placed between the multipliers, the result will be the same.

## Input data format
The first line contains the number s of matrices $(2\le s \le 100)$. In the following s rows, the sizes of the matrices are set: row i + 1 contains, separated by a space, the number of ni rows and the number of mi columns of the matrix Ai $(1 \le ni, mi \le 100)$. It is guaranteed that mi coincides with ni + 1 for all indices i from 1 to s − 1.
## Output data format
Print the minimum number of atomic multiplication operations required to multiply s matrices.