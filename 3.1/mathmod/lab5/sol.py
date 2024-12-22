import numpy as np

A = np.array([[0.7, -0.3, -0.2], [-0.2, 0.2, -0.7], [-0.5, 0.1, 2]])
f = np.array([-1, -3, 8])

x = np.linalg.solve(A, f)
print(x)

A = np.array([[0.7, -0.3, -0.2], [-0.1, 1, 0.1], [-0.5, 0.1, 2]])

b = A @ x

print("Сгенерированная матрица A:")
print(A)
print("\nЗаданный вектор решения x:")
print(x)
print("\nПравая часть b = A x:")
print(b)
