# Problem 0.7. Strictly increasing subsequence without discontinuities

- Time limit: from 1 s to 5 s
- Memory limit: no

It is necessary to delete the minimum number of elements from a given numerical sequence A, consisting of n elements, so that the remaining elements form a strictly increasing subsequence of elements. The constructed algorithm should have a labor intensity of O(n log n).

Remark
Increasing without discontinuities implies that each subsequent element of the subsequence is strictly greater than the previous one.

## Input data format
The first line of the input file contains the number n (1 ≤ n ≤ 700,000). The next line contains n elements of sequence A, which are separated by spaces (the elements of the sequence are integers not exceeding 1,000,000,000 modulo).
## Output data format
Print one number — the length of a strictly increasing sequence of elements.