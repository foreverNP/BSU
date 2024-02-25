# Task 0.1. The Frog's path

### Time limit: 1 sec
### Memory limit: 256 MB

In one very long and narrow pond, a frog is jumping on water lilies. The water lilies in the pond are arranged in a row. The frog starts jumping from the first water lily of the row and wants to finish on the last one. But due to the harmful nature of the frog, it agrees to jump only forward through one or two water lilies. For example, from water lily number 1, she can only jump to water lilies number 3 and number 4.

Mosquitoes are sitting on some water lilies. Namely, ai mosquitoes are sitting on the i-th water lily. When a frog lands on a water lily, it eats all the mosquitoes sitting on it. The frog wants to plan his route so that he can eat as many mosquitoes as possible. Help her: tell her which water lilies she should visit on her way.

## Input data format
The first entry line contains n — the number of water lilies in the pond (1 ≤ n ≤ 100,000). The second line contains n numbers separated by single spaces. The ith number tells you how many mosquitoes are sitting on the ith lily pad (1 ≤ i ≤ n). All numbers are integers, non-negative and do not exceed 1000.

## Output data format
In the first line, print one number — the maximum number of mosquitoes that a frog can eat. In the second line, print a sequence of numbers — the numbers of those water lilies that the frog should visit, in ascending order. If there are several solutions, print any one.
If the frog cannot get to the last water lily, then print one number -1.