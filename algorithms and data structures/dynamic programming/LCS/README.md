# Task 0.5. LCS

- Time limit: 2 seconds
- Memory limit: 64 MB
        

Two sequences are given $A$ and $B$, each with a length of $n$.Find the largest $k$ for which there are two index sequences $(0 \le i_1 \lt i_2 \lt \ldots \lt i_k \lt n)$ and $(0 \le j_1 \lt j_2 \lt \ldots \lt j_k \lt n)$, such that $A_{i_1} = B_{j_1}$, $A_{i_2} = B_{j_2}$, ..., $A_{i_k} = B_{j_k}$.You also need to find the index sequences themselves.

## The format of the input data
 in the first line is the number $n$ ($1 \le n\le 1000$), the length of the sequences $A$ and $B$.The second line contains $n$ integers $a_i$ ($1 \le a_i \le 1000$) — elements of the sequence $A$.The third line contains $n$ integers $b_j$ ($1\le b_j \le 1000$) — elements of the sequence $B$.

## Output data format 
In the first line, print the number $k$. In the second line, print the indexes $i_{1}\, i_{2}\, \ldots\, i_k$.In the third line, print the indexes $j_{1}\, j_{2}\, \ldots\, j_k$.If there are several suitable index sequences, output any of them.