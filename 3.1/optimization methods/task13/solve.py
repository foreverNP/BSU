import sys

# Given data
c = 45
c_i = [20, 24, 5, 20, 9]
p_i = [4, 14, 2, 7, 3]
n = len(c_i)

# Total possible value is the sum of all values
total_value = sum(c_i)

# Initialize dp array where dp[v] is the minimal total weight to achieve value v
dp = [float("inf")] * (total_value + 1)
dp[0] = 0

# For backtracking to find the selected items
prev = [-1] * (total_value + 1)

for i in range(n):
    value = c_i[i]
    weight = p_i[i]
    for v in range(total_value, value - 1, -1):
        if dp[v - value] + weight < dp[v]:
            dp[v] = dp[v - value] + weight
            prev[v] = i

# Find the minimal total weight for total value >= c
min_weight = float("inf")
min_value = -1
for v in range(c, total_value + 1):
    if dp[v] < min_weight:
        min_weight = dp[v]
        min_value = v

if min_weight == float("inf"):
    print("It's not possible to achieve the required total value.")
else:
    print(f"Minimum total weight to achieve value at least {c}: {min_weight}")

    # Reconstruct the items selected
    selected_items = []
    v = min_value
    while v > 0 and prev[v] != -1:
        i = prev[v]
        selected_items.append(i + 1)  # Item indices are 1-based
        v -= c_i[i]

    print("Selected item indices:", selected_items[::-1])
    print("Selected items (value, weight):")
    for idx in selected_items[::-1]:
        print(f"Item {idx}: Value = {c_i[idx - 1]}, Weight = {p_i[idx - 1]}")
