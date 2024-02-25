import math
import random
from scipy.stats import chi2
import numpy as np

n = 1000
alpha = 0.05
num_simulations = 1000


# Моделирование нормального распределения N(0,1) с помощью преобразования Бокса-Мюллера
def generate_normal(n):
    normals = []
    for _ in range((n + 1) // 2):
        U1 = random.uniform(0, 1)
        U2 = random.uniform(0, 1)

        # Преобразование Бокса-Мюллера
        R = math.sqrt(-2 * math.log(U1))
        theta = 2 * math.pi * U2
        Z1 = R * math.cos(theta)
        Z2 = R * math.sin(theta)

        normals.extend([Z1, Z2])

    return normals[:n]


# Моделирование распределения Лапласа L(a) с помощью обратного преобразования
def generate_laplace(n, a):
    laplace_vars = []
    for _ in range(n):
        U = random.uniform(0, 1)

        if U <= 0.5:
            X = (1 / a) * math.log(2 * U)
        else:
            X = -(1 / a) * math.log(2 * (1 - U))

        laplace_vars.append(X)

    return laplace_vars


# Моделирование распределения Вейбулла с помощью обратного преобразования
def generate_weibull(n, a, b):
    weibull_vars = []
    for _ in range(n):
        U = random.uniform(0, 1)

        X = b * (-math.log(U)) ** (1 / a)

        weibull_vars.append(X)

    return weibull_vars


# Функции распределения для каждой случайной величины


def standard_normal_cdf(x):
    return 0.5 * (1 + math.erf(x / math.sqrt(2)))  #  erf - функция ошибок


def laplace_cdf(x, a):
    if x < 0:
        return 0.5 * math.exp(a * x)
    else:
        return 1 - 0.5 * math.exp(-a * x)


def weibull_cdf(x, a, b):
    if x < 0:
        return 0
    else:
        return 1 - math.exp(-((x / b) ** a))


# Функция для проведения теста Колмогорова-Смирнова
def ks_test(sample, cdf_func, args=(), alpha=0.05):
    n = len(sample)
    sorted_sample = sorted(sample)
    D_max = 0

    for i, x in enumerate(sorted_sample, start=1):
        F_x = cdf_func(x, *args)
        F_n = i / n

        D_current = abs(F_n - F_x)
        if D_current > D_max:
            D_max = D_current

    D_alpha = math.sqrt(-0.5 * math.log(alpha / 2) / n)

    result = D_max > D_alpha

    return D_max, D_alpha, result


# Функция для проведения теста хи-квадрат
def chi_squared_test(sample, cdf_func, args=(), alpha=0.05, k=10):
    n = len(sample)
    # Определение границ интервалов (бинов)
    bin_edges = [np.percentile(sample, 100 * i / k) for i in range(k + 1)]
    # Наблюдаемые частоты в каждом бине
    observed, _ = np.histogram(sample, bins=bin_edges)

    expected = []
    for i in range(k):
        lower = bin_edges[i]
        upper = bin_edges[i + 1]

        F_upper = cdf_func(upper, *args)
        F_lower = cdf_func(lower, *args)

        # Ожидаемая частота в бине
        expected_freq = n * (F_upper - F_lower)
        # Предотвращение деления на ноль
        expected.append(expected_freq if expected_freq > 0 else 1e-6)

    chi2_stat = sum((o - e) ** 2 / e for o, e in zip(observed, expected))
    chi2_critical = chi2.ppf(1 - alpha, df=k - 1)

    result = chi2_stat > chi2_critical

    return chi2_stat, chi2_critical, result


def check_I_error(generate_func, cdf_func, args=(), alpha=0.05):
    ks_passed = 0
    chi2_passed = 0
    for _ in range(num_simulations):
        sample = generate_func(n)
        D, D_alpha, ks_result = ks_test(sample, cdf_func, args, alpha)
        chi2_stat, chi2_critical, chi2_result = chi_squared_test(
            sample, cdf_func, args, alpha, k=10
        )
        if not ks_result:
            ks_passed += 1
        if not chi2_result:
            chi2_passed += 1

    ks_success_rate = 100 * (ks_passed / num_simulations)
    chi2_success_rate = 100 * (chi2_passed / num_simulations)

    return ks_success_rate, chi2_success_rate


def print_results(
    name,
    sample_mean,
    theoretical_mean,
    sample_var,
    theoretical_var,
    D,
    D_alpha,
    ks_passed,
    chi2_stat,
    chi2_critical,
    chi2_passed,
    success_rate_ks,
    success_rate_chi2,
):
    get_checkmark = lambda passed: "✅" if passed else "❌"

    print(f"> {name}:")
    print(f"{'':<12}{'Фактическое':>12}{'Теоретическое':>15}")
    print(f"{'Среднее':<12}{sample_mean:>12.4f}{theoretical_mean:>15.4f}")
    print(f"{'Дисперсия':<12}{sample_var:>12.4f}{theoretical_var:>15.4f}")
    print("-" * 55)
    checkmark_ks = get_checkmark(not ks_passed)
    checkmark_chi2 = get_checkmark(not chi2_passed)
    print(
        f"Колмогорова-Смирнова: {D:.3f} < {D_alpha:.3f} | Тест пройден: {checkmark_ks}"
    )
    print(
        f"Хи-квадрат Пирсона: {chi2_stat:.4f} < {chi2_critical:.4f} | Тест пройден: {checkmark_chi2}"
    )
    print(f"Успех KS: {success_rate_ks:.1f}%")
    print(f"Успех хи-квадрат: {success_rate_chi2:.1f}%")
    print("-" * 55 + "\n")


if __name__ == "__main__":
    # Нормальное распределение
    normals = generate_normal(n)
    mean_normal = sum(normals) / n
    var_normal = sum((x - mean_normal) ** 2 for x in normals) / (n - 1)
    theoretical_mean_normal = 0
    theoretical_var_normal = 1
    D_normal, D_alpha_normal, result_normal = ks_test(
        normals, standard_normal_cdf, alpha=alpha
    )
    chi2_stat_normal, chi2_critical_normal, chi2_result_normal = chi_squared_test(
        normals, standard_normal_cdf, args=(), alpha=alpha, k=10
    )
    success_normal_ks, success_normal_chi2 = check_I_error(
        generate_normal, standard_normal_cdf, args=(), alpha=alpha
    )
    print_results(
        "Нормальное распределение N(0,1)",
        mean_normal,
        theoretical_mean_normal,
        var_normal,
        theoretical_var_normal,
        D_normal,
        D_alpha_normal,
        result_normal,
        chi2_stat_normal,
        chi2_critical_normal,
        chi2_result_normal,
        success_normal_ks,
        success_normal_chi2,
    )

    # Распределение Лапласа
    a_laplace = 0.5
    laplace_vars = generate_laplace(n, a_laplace)
    mean_laplace = sum(laplace_vars) / n
    var_laplace = sum((x - mean_laplace) ** 2 for x in laplace_vars) / (n - 1)
    theoretical_mean_laplace = 0
    theoretical_var_laplace = 2 / (a_laplace**2)
    D_laplace, D_alpha_laplace, result_laplace = ks_test(
        laplace_vars, laplace_cdf, args=(a_laplace,), alpha=alpha
    )
    chi2_stat_laplace, chi2_critical_laplace, chi2_result_laplace = chi_squared_test(
        laplace_vars, laplace_cdf, args=(a_laplace,), alpha=alpha, k=10
    )
    success_laplace_ks, success_laplace_chi2 = check_I_error(
        lambda n: generate_laplace(n, a_laplace),
        laplace_cdf,
        args=(a_laplace,),
        alpha=alpha,
    )
    print_results(
        f"Распределение Лапласа L(a={a_laplace})",
        mean_laplace,
        theoretical_mean_laplace,
        var_laplace,
        theoretical_var_laplace,
        D_laplace,
        D_alpha_laplace,
        result_laplace,
        chi2_stat_laplace,
        chi2_critical_laplace,
        chi2_result_laplace,
        success_laplace_ks,
        success_laplace_chi2,
    )

    # Распределение Вейбулла
    a_weibull = 1
    b_weibull = 0.5
    weibull_vars = generate_weibull(n, a_weibull, b_weibull)
    mean_weibull = sum(weibull_vars) / n
    var_weibull = sum((x - mean_weibull) ** 2 for x in weibull_vars) / (n - 1)
    theoretical_mean_weibull = b_weibull * math.gamma(1 + 1 / a_weibull)
    theoretical_var_weibull = b_weibull**2 * (
        math.gamma(1 + 2 / a_weibull) - (math.gamma(1 + 1 / a_weibull)) ** 2
    )
    D_weibull, D_alpha_weibull, result_weibull = ks_test(
        weibull_vars, weibull_cdf, args=(a_weibull, b_weibull), alpha=alpha
    )
    chi2_stat_weibull, chi2_critical_weibull, chi2_result_weibull = chi_squared_test(
        weibull_vars, weibull_cdf, args=(a_weibull, b_weibull), alpha=alpha, k=10
    )
    success_weibull_ks, success_weibull_chi2 = check_I_error(
        lambda n: generate_weibull(n, a_weibull, b_weibull),
        weibull_cdf,
        args=(a_weibull, b_weibull),
        alpha=alpha,
    )
    print_results(
        f"Распределение Вейбулла W(a={a_weibull}, b={b_weibull})",
        mean_weibull,
        theoretical_mean_weibull,
        var_weibull,
        theoretical_var_weibull,
        D_weibull,
        D_alpha_weibull,
        result_weibull,
        chi2_stat_weibull,
        chi2_critical_weibull,
        chi2_result_weibull,
        success_weibull_ks,
        success_weibull_chi2,
    )
