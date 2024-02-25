import numpy as np
import math
from scipy import stats

n = 1000
p = 0.75
lambda_param = 1
alpha = 0.05

def bernoulli_simulation(p, n):
    bernoulli_rv = []
    for _ in range(n):
        u = np.random.uniform(0, 1)
        if u <= p:
            bernoulli_rv.append(1)
        else:
            bernoulli_rv.append(0)
    return np.array(bernoulli_rv)

def poisson_simulation(lambd, n):
    samples = []
    for _ in range(n):
        N = -1
        S = 0
        
        while S < 1:
            U = np.random.uniform(0, 1)
            E = -np.log(U) / lambd
            S += E
            N += 1
            
        samples.append(N)
    return np.array(samples)

def estimates(rv):
    mean_estimate = np.mean(rv)         # вычисляем оценку мат. ожидания
    var_estimate = np.var(rv, ddof=1)   # вычисляем оценку дисперсии
    return mean_estimate, var_estimate

def chi_square_test_bernoulli(observed, p, n): 
    expected_counts = np.array([n * (1 - p), n * p])
    observed_counts = np.array([np.sum(observed == 0), np.sum(observed == 1)]) 
    
    chi2_stat = np.sum((observed_counts - expected_counts) ** 2 / expected_counts)
    chi2_crit = stats.chi2.ppf(1 - alpha, df=1)
    return chi2_stat, chi2_crit

def chi_square_test_poisson(observed, lambda_param, n, threshold=5):
    unique_values, counts = np.unique(observed, return_counts=True)
    # Вычисляем вероятности для каждого уникального значения, используя распределение Пуассона
    expected_probs = stats.poisson.pmf(unique_values, lambda_param)
    # Вычисляем ожидаемое количество для каждого уникального значения
    expected_counts = n * expected_probs
    
    # Агрегируем малые значения, если их ожидания меньше порогового значения threshold
    mask = expected_counts >= threshold
    if not np.all(mask):
        observed_aggregated = np.append(counts[mask], np.sum(counts[~mask]))
        expected_aggregated = np.append(expected_counts[mask], np.sum(expected_counts[~mask]))
    else:
        observed_aggregated = counts
        expected_aggregated = expected_counts

    chi2_stat = np.sum((observed_aggregated - expected_aggregated) ** 2 / expected_aggregated)
    chi2_crit = stats.chi2.ppf(1 - alpha, df=len(observed_aggregated) - 1)
    return chi2_stat, chi2_crit


bernoulli_rv = bernoulli_simulation(p, n)
poisson_rv = poisson_simulation(lambda_param, n)

true_mean_bernoulli = p
true_var_bernoulli = p * (1 - p)

true_mean_poisson = lambda_param
true_var_poisson = lambda_param

mean_bernoulli, var_bernoulli = estimates(bernoulli_rv)
mean_poisson, var_poisson = estimates(poisson_rv)

def print_statistics(distribution_name, param_name, param, mean, true_mean, var, true_var):
    print(f"{distribution_name} ({param_name}={param}):")
    print(f"Оценка мат. ожидания: {mean}, Истинное значение: {true_mean}")
    print(f"Оценка дисперсии: {var}, Истинное значение: {true_var}\n")

def perform_chi_square_test(distribution_name, chi_square_test_func, rv, param, n):
    chi2, crit = chi_square_test_func(rv, param, n)
    print(f"χ²-критерий для {distribution_name}: χ² = {chi2}, критическое значение = {crit}")

    if chi2 > crit:
        print(f"Критерий χ² НЕ пройден для {distribution_name}.")
    else:
        print(f"Критерий χ² пройден для {distribution_name}.")

print_statistics("Бернулли", "p", p, mean_bernoulli, true_mean_bernoulli, var_bernoulli, true_var_bernoulli)
print_statistics("Пуассон", "λ", lambda_param, mean_poisson, true_mean_poisson, var_poisson, true_var_poisson)

perform_chi_square_test("Бернулли", chi_square_test_bernoulli, bernoulli_rv, p, n)
perform_chi_square_test("Пуассон", chi_square_test_poisson, poisson_rv, lambda_param, n)

def type_1_error_rate(test_func, distribution_simulation_func, param, n, num_simulations=1000):
    errors = 0
    for _ in range(num_simulations):
        rv = distribution_simulation_func(param, n)
        chi2, crit = test_func(rv, param, n)
        if chi2 > crit:
            errors += 1
    return errors / num_simulations

bernoulli_error_rate = type_1_error_rate(chi_square_test_bernoulli, bernoulli_simulation, p, n)
print(f"Оценка вероятности ошибки I рода для распределения Бернулли: {bernoulli_error_rate}")

poisson_error_rate = type_1_error_rate(chi_square_test_poisson, poisson_simulation, lambda_param, n)
print(f"Оценка вероятности ошибки I рода для распределения Пуассона: {poisson_error_rate}")
