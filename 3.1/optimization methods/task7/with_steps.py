import numpy as np

def iteration_phase_1(x, basis, A, c1, x_min, x_max, l, Q, cont):
  cont = 1
  # 1 этап

  # Определяем матрицу и вектор
  matrix = A[:, basis-1].T
  vector = c1[basis-1]

  # Умножаем матрицу на вектор
  U = np.linalg.solve(matrix, vector)
  print("\n1.")

  for i in range(3):
    print(matrix[i], " ",  vector.reshape(-1, 1)[i])

  print(f"\nU = {U.reshape(-1, 1)}")


  # 2 этап
  deltas = {}

  for i in range(8):
    if i not in basis-1:
      deltas[i] = [c1[i] - (np.dot(U, A[:, i]))]
      if deltas[i][0] > 0:
        if x_max[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')
      elif deltas[i][0] < 0:
        if x_min[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')
      elif deltas[i][0] == 0:
        if (x_min[i] == x[i]) or x_max[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')

  print("\n2.")
  is_minus_exist = False
  for key, value in deltas.items():
    if value[1] == '-':
      is_minus_exist = True
    print(f"{key + 1}: {value}")

  if not is_minus_exist:
    print("\nМетод сошелся!")
    print(f"X = {x}")
    print(f"Basis = {basis}")
    cont = 0

    return x, basis, cont


  # 3 этап
  j0 = None
  j0sign = None

  for key, (value, sign) in deltas.items():
    if sign == '-':
      j0 = key
      j0sign = '+' if value>0 else '-'

      break

  print("\n3.")
  print(f"j0 = {j0+1}")


  # 4 этап
  l[j0] = 1 if j0sign == '+' else -1

  print("\n4.")
  print(f"l[{j0+1}] = {l[j0]}")

  for i in range(8):
    if i not in basis-1 and i != j0:
      l[i] = 0

  b = np.array([0, 0, 0])
  b = b - (A[:, j0] * l[j0])

  A_reduced = A[: , basis-1]
  print(A_reduced)
  print(b)
  solution = np.linalg.solve(A_reduced, b)

  basis_l = {}
  for i in range(3):
    basis_l[(basis-1)[i]] = solution[i]

  # Замена значений в массиве l
  for i, value in enumerate(basis-1):
    l[value] = solution[i]
  print("Значения базисных L:")

  for index, value in basis_l.items():
    print(f"l{index + 1} = {value}", end = ' ,')
  print('\n')

  print("Значения L:")
  for i in range(len(l)):
    print(f"l{i+1} : {l[i]}", end=', ')
  print('\n')


  # 5 этап
  for i in range(len(l)):
    if l[i] == 0:
      Q[i] = float('inf')

  Q[j0] = x_max[j0] - x_min[j0]

  for i in range(len(Q)):
    if l[i] == 0 or i == j0:
      continue
    else:
      if l[i] < 0:
        Q[i] = (x_min[i] - x[i]) / l[i]
        print(Q[i])
      else:
        Q[i] = (x_max[i] - x[i]) / l[i]

  print("\n5.")
  print("Значения Тета: ")
  for i in range(len(Q)):
    print(f"Q{i+1} : {Q[i]}", end=', ')


  # 6 этап
  print("\n6.")
  # Находим минимальное значение
  Q0 = min(Q)

  # Находим индекс минимального значения
  j_star = Q.index(Q0)

  print(f"Тета[0]: {Q0}, j*: {j_star + 1}")

  # 7 этап
  print("\n7.")
  basis_new = basis.copy()
  basis_new = np.append(basis_new, j0+1)
  basis_new = basis_new[basis_new != j_star+1]
  basis_new.sort()

  print(f"Новый базис = {basis} U {j0+1} \ {j_star+1} = {basis_new}")


  # 8 этап
  print("\n8.")

  x_new = x + Q0*l
  print(f"Новый x = {x} + {Q0} * {l} = {x_new}")

  return x_new, basis_new, cont
def iteration_phase_2(x, basis, A, c, x_min, x_max, l, Q, cont):
  cont = 1
  # 1 этап

  # Определяем матрицу и вектор
  matrix = A[:, basis-1].T
  vector = c[basis-1]

  # Умножаем матрицу на вектор
  U = np.linalg.solve(matrix, vector)
  print("\n1.")

  for i in range(3):
    print(matrix[i], " ",  vector.reshape(-1, 1)[i])

  print(f"\nU = {U.reshape(-1, 1)}")


  # 2 этап
  deltas = {}

  for i in range(5):
    if i not in basis-1:
      deltas[i] = [c[i] - (np.dot(U, A[:, i]))]
      if deltas[i][0] > 0:
        if x_max[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')
      elif deltas[i][0] < 0:
        if x_min[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')
      elif deltas[i][0] == 0:
        if (x_min[i] == x[i]) or x_max[i] == x[i]:
          deltas[i].append('+')
        else:
          deltas[i].append('-')

  print("\n2.")
  is_minus_exist = False
  for key, value in deltas.items():
    if value[1] == '-':
      is_minus_exist = True
    print(f"{key + 1}: {value}")

  if not is_minus_exist:
    print("\nМетод сошелся!")
    print(f"X = {x}")
    print(f"Basis = {basis}")
    cont = 0
    phi_max = c * x
    print(f"Phi max = {sum(phi_max)}")
    return x, basis, cont


  # 3 этап
  j0 = None
  j0sign = None

  for key, (value, sign) in deltas.items():
    if sign == '-':
      j0 = key
      j0sign = '+' if value>0 else '-'

      break

  print("\n3.")
  print(f"j0 = {j0+1}")


  # 4 этап
  l[j0] = 1 if j0sign == '+' else -1

  print("\n4.")
  print(f"l[{j0+1}] = {l[j0]}")

  for i in range(5):
    if i not in basis-1 and i != j0:
      l[i] = 0

  b = np.array([0, 0, 0])
  b = b - (A[:, j0] * l[j0])

  A_reduced = A[: , basis-1]
  print(A_reduced)
  print(b)
  solution = np.linalg.solve(A_reduced, b)

  basis_l = {}
  for i in range(3):
    basis_l[(basis-1)[i]] = solution[i]

  # Замена значений в массиве l
  for i, value in enumerate(basis-1):
    l[value] = solution[i]
  print("Значения базисных L:")

  for index, value in basis_l.items():
    print(f"l{index + 1} = {value}", end = ' ,')
  print('\n')

  print("Значения L:")
  for i in range(len(l)):
    print(f"l{i+1} : {l[i]}", end=', ')
  print('\n')


  # 5 этап
  for i in range(len(l)):
    if l[i] == 0:
      Q[i] = float('inf')

  Q[j0] = x_max[j0] - x_min[j0]

  for i in range(len(Q)):
    if l[i] == 0 or i == j0:
      continue
    else:
      if l[i] < 0:
        Q[i] = (x_min[i] - x[i]) / l[i]
        print(Q[i])
      else:
        Q[i] = (x_max[i] - x[i]) / l[i]

  print("\n5.")
  print("Значения Тета: ")
  for i in range(len(Q)):
    print(f"Q{i+1} : {Q[i]}", end=', ')


  # 6 этап
  print("\n6.")
  # Находим минимальное значение
  Q0 = min(Q)

  # Находим индекс минимального значения
  j_star = Q.index(Q0)

  print(f"Тета[0]: {Q0}, j*: {j_star + 1}")

  # 7 этап
  print("\n7.")
  basis_new = basis.copy()
  basis_new = np.append(basis_new, j0+1)
  basis_new = basis_new[basis_new != j_star+1]
  basis_new.sort()

  print(f"Новый базис = {basis} U {j0+1} \ {j_star+1} = {basis_new}")


  # 8 этап
  print("\n8.")

  x_new = x + Q0*l
  print(f"Новый x = {x} + {Q0} * {l} = {x_new}")

  return x_new, basis_new, cont
# Заменить на свое
# -------------------------------------
A = np.array([[3, 0, -1, 0, 1, 1, 0, 0], # последние 3 - коэфиценты при x6, x7, x8
              [-2, 0, 0, 3, 0, 0, -1, 0],
              [0, 4, 0, 0, 1, 0, 0, 1]])

x_min = [2,0,1,0,-1,0,0,0] # Нижняя граница. Последние 3 - граница x6, x7, x8
x_max = [10,4,5,4,3,26,7,3] # Верхняя граница. Последние 3 - граница x6, x7, x8
c = np.array([5, 0, -4, 15, 5]) # Вектор c из условия

x = np.array([2,0,1,0,-1,26,7,3]) # Получившийся X (я взял просто верхнию границу)
basis = np.array([6,7,8]) # Получившийся базис (у всех такой)

# -------------------------------------


# Саша
# A = np.array([[0, 0, 0, 1, 2, -1, 0, 0],
#               [3, 0, 2, 0, -4, 0, 1, 0],
#               [0, -1, 3, 0, 0, 0, 0, 1]])

# x_min = [1,-1,-2, 1,-3,0,0,0]
# x_max = [4,3,2,4,1,7,22,3]
# x = np.array([1,3,2,1,1,7,22,3])
# basis = np.array([6,7,8])
# c = np.array([6, 3, -2, -1, -14])

# Полина
# A = np.array([[0, 0, 0.5, 4, 0, 1, 0, 0],
#               [0, 0, 0, 3, 1, 0, 1, 0],
#               [2, 1, 2, 0, 0, 0, 0, -1]])

# x_min = [1,-1, 2, 3, 1, 0,0,0]
# x_max = [4,3,5,8,4,16,13,1]
# x = np.array([1,3,2,3,1,16,13,1])
# basis = np.array([6,7,8])
# c = np.array([6, 3, -2, -1, -14])

# Кто-то
# A = np.array([[-1, 0, 0, 0, 2, -1, 0, 0],
#               [0, 0, 0, 3, 1, 0, 1, 0],
#               [2, -1, 4, 0, 0, 0, 0, 1]])
# x_min = [1, 1, 0, 2, 1, 0,0,0]
# x_max = [5,6,4,6,5,3,9,21]
# x = np.array([1, 1, 0, 2, 1 ,3,9,21])
# basis = np.array([6,7,8])
# c = np.array([0, -1, 7, 0, 2])


c1 = np.array([0,0,0,0,0,-1,-1,-1])
l = np.array([None, None, None, None, None, None, None, None])
l2 = np.array([None, None, None, None, None])
Q = [None, None, None, None, None, None, None, None]
Q2 = [None, None, None, None, None]

print("############################ Фаза 1 ############################")
cont = 1 # Костыль)
i = 0
while cont:
  print(f"-----------------------Итерация {i+1}-------------------------")
  x, basis, cont = iteration_phase_1(x, basis, A, c1, x_min, x_max, l, Q, cont)
  i+=1

cont = 1 # Костыль)
i = 0
print("############################ Фаза 2 ############################")
while cont:
  print(f"-----------------------Итерация {i+1}-------------------------")
  x, basis, cont = iteration_phase_2(x[:5], basis, A, c, x_min, x_max, l2, Q2, cont)
  i+=1