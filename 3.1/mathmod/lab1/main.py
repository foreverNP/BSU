import math
import numpy as np
from scipy.stats import chi2


# Multiplicative Congruential Method
def mcm(a, beta, M, n):
    x = a
    results = []
    for _ in range(n):
        x = (beta * x) % M
        results.append(x / M)
    return results


# MacLaren-Marsaglia Method
def mmm(gen1, gen2, K, n):
    table = gen1[:K]
    results = []
    for i in range(n):
        j = int(gen2[i] * K)
        results.append(table[j])
        table[j] = gen1[K + i]
    return results


# Empirical Distribution Function
def empirical_distribution(data):
    sorted_data = sorted(data)
    n = len(data)
    edf = []
    for i in range(n):
        edf.append((i + 1) / n)
    return sorted_data, edf


# Kolmogorov criterion
def kolm_criterion(data, e=0.05):
    sorted_data, edf = empirical_distribution(data)
    n = len(data)
    d_max = 0
    for i in range(n):
        theoretical_cdf = sorted_data[i]  # for uniform distribution F(x) = x
        d = abs(edf[i] - theoretical_cdf)
        if d > d_max:
            d_max = d

    critical = math.sqrt(-0.5 * math.log(e / 2) / n)
    return d_max, critical, d_max < critical


# χ²-Pearson criterion
def pearson(data, e=0.05, num_bins=15):
    n = len(data)
    if n == 0:
        raise ValueError("Data cannot be empty")

    bin_edges = np.linspace(0, 1, num_bins + 1)  # Define the bin edges

    # Count the number of elements in each interval
    observed, _ = np.histogram(data, bins=bin_edges)

    expInBin = n / num_bins

    stat = np.sum((observed - expInBin) ** 2 / expInBin)  # χ² = Σ((Oi - Ei)² / Ei)

    # Critical value for χ² with (num_bins - 1) degrees of freedom and significance level e
    critical = chi2.ppf(1 - e, num_bins - 1)

    return stat, critical, stat < critical


def test_data(data, e=0.05):
    d_max, crit, kolm_passed = kolm_criterion(data, e)
    print(
        f"Kolmogorov criterion: D_max = {d_max:.4f}, critical: {crit:.4f}, passed: {kolm_passed}"
    )
    chi2_stat, crit, chi2_passed = pearson(data, e)
    print(
        f"χ²-Pearson criterion: χ² = {chi2_stat:.4f}, critical: {crit:.4f}, passed: {chi2_passed}"
    )


def main():
    a = 32771  # initial value
    beta = 32771  # multiplier
    M = 2**31
    n = 1000
    K = 128
    e = 0.05

    print("Checking MCM sequence:")
    mcm_seq = mcm(a, beta, M, n)
    test_data(mcm_seq, e)

    #####################################################################################

    print("\nChecking MacLaren-Marsaglia sequence:")
    mmm_seq = mmm(mcm(3 * beta, 3 * beta, M, n + K), mcm_seq, K, n)
    test_data(mmm_seq, e)


main()
