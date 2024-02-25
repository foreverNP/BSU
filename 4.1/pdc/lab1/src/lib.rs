use rand::distributions::{Distribution, Uniform};
use rand::rngs::StdRng;
use std::cmp::min;

pub fn gen_matrix(n: usize, rng: &mut StdRng) -> Vec<f64> {
    let between = Uniform::from(-100.0..=100.0);
    let mut m = Vec::with_capacity(n * n);
    for _ in 0..(n * n) {
        m.push(between.sample(rng));
    }
    m
}

//////////////////////////////////                            //////////////////////////////////
//////////////////////////////////           point            //////////////////////////////////
//////////////////////////////////                            //////////////////////////////////

// Точечный алгоритм (1): c = a * b, тройной цикл i-j-k
//
//   a: читаем элементы a[i,0], a[i,1], a[i,2], ...  ← последовательный проход по строке i
//         a[i,0] → a[i,1] → a[i,2] → ...   (хорошо для кэша)
//
//   b: читаем элементы b[0,j], b[1,j], b[2,j], ...  ↓ прыжки по памяти (один элемент из каждой строки)
//         b[0,j]
//          ↓
//         b[1,j]
//          ↓
//         b[2,j]
//   (
//
//   c: c[i,j] = sum_k a[i,k]*b[k,j]
pub fn mul_point(a: &[f64], b: &[f64], n: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];
    for i in 0..n {
        let base_i = n * i;
        for j in 0..n {
            let mut sum = 0.0_f64;
            for k in 0..n {
                sum += a[base_i + k] * b[n * k + j];
            }
            c[base_i + j] = sum;
        }
    }
    c
}

// Точечный алгоритм (1.2): c = a * b, тройной цикл i-k-j
//
//   a: читаем a[i,k]
//         a[i,0] -> a[i,1] -> a[i,2] -> ...
//
//   b: читаем всю строку b[k,0], b[k,1], b[k,2], ...  → последовательный по строке k
//         b[k,0] -> b[k,1] -> b[k,2] -> ...
//
//   c: обновляем строку c[i,0], c[i,1], c[i,2], ...  → последовательные записи по строке i
//         c[i,0] += ..., c[i,1] += ..., c[i,2] += ...
pub fn mul_point_ikj(a: &[f64], b: &[f64], n: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];
    for i in 0..n {
        let base_i = n * i;

        for k in 0..n {
            let aik = a[base_i + k]; // если aik == 0.0 можно пропускать, но т.к. рандомный, польза мала
            let base_k = n * k;
            // b[base_k + j] и c[base_i + j] непрерывны
            for j in 0..n {
                c[base_i + j] += aik * b[base_k + j];
            }
        }
    }
    c
}

//////////////////////////////////                            //////////////////////////////////
//////////////////////////////////           block            //////////////////////////////////
//////////////////////////////////                            //////////////////////////////////

// Блочный алгоритм (2), i-j-k
pub fn mul_block(a: &[f64], b: &[f64], n: usize, r: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];
    let q = (n + r - 1) / r;

    for ig in 0..q {
        let i_start = ig * r;
        let i_end = std::cmp::min(i_start + r, n);

        for jg in 0..q {
            let j_start = jg * r;
            let j_end = std::cmp::min(j_start + r, n);

            for kg in 0..q {
                let k_start = kg * r;
                let k_end = std::cmp::min(k_start + r, n);

                for i in i_start..i_end {
                    let base_i = n * i;
                    for j in j_start..j_end {
                        let mut sum = 0.0_f64;
                        for k in k_start..k_end {
                            sum += a[base_i + k] * b[n * k + j];
                        }
                        c[base_i + j] += sum;
                    }
                }
            }
        }
    }
    c
}

// Блочный алгоритм (2.1), тройной цикл i-k-j
pub fn mul_block_ikj(a: &[f64], b: &[f64], n: usize, r: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];
    let q = (n + r - 1) / r;

    for ig in 0..q {
        let i_start = ig * r;
        let i_end = std::cmp::min(i_start + r, n);

        for jg in 0..q {
            let j_start = jg * r;
            let j_end = std::cmp::min(j_start + r, n);

            for kg in 0..q {
                let k_start = kg * r;
                let k_end = std::cmp::min(k_start + r, n);

                // одно макрослагаемое: C_{ig,jg} += A_{ig,kg} * B_{kg,jg}
                for i in i_start..i_end {
                    let base_i = n * i;

                    for k in k_start..k_end {
                        let aik = a[base_i + k];
                        let base_k = n * k;

                        for j in j_start..j_end {
                            c[base_i + j] += aik * b[base_k + j];
                        }
                    }
                }
            }
        }
    }
    c
}

//////////////////////////////////                            //////////////////////////////////
//////////////////////////////////      point parallel        //////////////////////////////////
//////////////////////////////////                            //////////////////////////////////

use rayon::prelude::*;
use std::sync::Mutex;

//  Каждая строка C[i,*] вычисляется независимо — каждая строка отдаётся одной задаче.
//  Без sync — каждая задача модифицирует свою c_row (эксклюзивный кусок памяти)
pub fn par_mul_point(a: &[f64], b: &[f64], n: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];

    c.par_chunks_mut(n).enumerate().for_each(|(i, c_row)| {
        let base_i = i * n;
        for j in 0..n {
            let mut sum = 0.0_f64;
            for k in 0..n {
                sum += a[base_i + k] * b[k * n + j];
            }
            c_row[j] = sum;
        }
    });

    c
}

pub fn par_mul_point_ikj(a: &[f64], b: &[f64], n: usize) -> Vec<f64> {
    let mut c: Vec<f64> = vec![0.0_f64; n * n];

    c.par_chunks_mut(n).enumerate().for_each(|(i, row)| {
        let base_i = n * i;
        for k in 0..n {
            let aik = a[base_i + k];
            let base_k = n * k;
            for j in 0..n {
                row[j] += aik * b[base_k + j];
            }
        }
    });
    c
}

// Каждый элемент C[i,j] вычисляется независимо в отдельной итерации параллельного итератора.
// результат собирается из map и затем заполняется collect'ом
pub fn par_mul_point_reduce(a: &[f64], b: &[f64], n: usize) -> Vec<f64> {
    (0..n * n)
        .into_par_iter()
        .map(|idx| {
            let i = idx / n;
            let j = idx % n;
            let base_i = n * i;

            (0..n).map(|k| a[base_i + k] * b[n * k + j]).sum()
        })
        .collect()
}

//////////////////////////////////                            //////////////////////////////////
//////////////////////////////////      block parallel        //////////////////////////////////
//////////////////////////////////                            //////////////////////////////////

// параллелизм по блочным строкам `ig`
//
// Разбивает матрицы на блоки размера `r x r`
// Каждая задача получает набор подряд идущих строк результата `C` размера `r * n`, т.е. параллелизуем по блочным строкам `ig`
pub fn par_mul_block_ikj(a: &[f64], b: &[f64], n: usize, r: usize) -> Vec<f64> {
    let mut c = vec![0.0_f64; n * n];
    let q = (n + r - 1) / r;
    let chunk_size = r * n;

    c.par_chunks_mut(chunk_size)
        .enumerate()
        .for_each(|(ig, c_chunk)| {
            let i_start = ig * r;
            let i_end = min(i_start + r, n);

            for jg in 0..q {
                let j_start = jg * r;
                let j_end = min(j_start + r, n);

                for kg in 0..q {
                    let k_start = kg * r;
                    let k_end = min(k_start + r, n);

                    for i in i_start..i_end {
                        let i_rel = i - i_start;
                        let base_i_global = i * n;
                        let base_i_chunk = i_rel * n;

                        for k in k_start..k_end {
                            let aik = a[base_i_global + k];
                            let base_k = k * n;
                            for j in j_start..j_end {
                                c_chunk[base_i_chunk + j] += aik * b[base_k + j];
                            }
                        }
                    }
                }
            }
        });

    c
}

// Каждый поток выделяет свой локальный буфер `local_c` размером `r * n`, поток заполняет `local_c`, аккумулируя вклады всех `kg` и `jg`.
// В конце берёт `Mutex` на глобальную `C` и копирует свои строки туда.
pub fn par_mul_block_ikj2(a: &[f64], b: &[f64], n: usize, r: usize) -> Vec<f64> {
    let c = Mutex::new(vec![0.0_f64; n * n]);
    let q = (n + r - 1) / r;

    (0..q).into_par_iter().for_each(|ig| {
        let i_start = ig * r;
        let i_end = std::cmp::min(i_start + r, n);

        // локальный буфер для данного потока: r строк по n элементов
        let mut local_c = vec![0.0_f64; r * n];

        for kg in 0..q {
            let k_start = kg * r;
            let k_end = std::cmp::min(k_start + r, n);

            for jg in 0..q {
                let j_start = jg * r;
                let j_end = std::cmp::min(j_start + r, n);

                for (li, i) in (i_start..i_end).enumerate() {
                    let base_i = n * i;
                    for k in k_start..k_end {
                        let aik = a[base_i + k];
                        let base_k = n * k;
                        for j in j_start..j_end {
                            local_c[li * n + j] += aik * b[base_k + j];
                        }
                    }
                }
            }
        }

        // редукция локального буфера в глобальную C (с mutex)
        let mut c_guard = c.lock().unwrap();
        for (li, i) in (i_start..i_end).enumerate() {
            let base_i = n * i;
            for j in 0..n {
                c_guard[base_i + j] = local_c[li * n + j];
            }
        }
    });

    c.into_inner().unwrap()
}

// Параллелизм по парам блоков `(ig, jg)` — каждая пара блоков вычисляется независимо, затем результаты собираются и складываются
// в глобальную матрицу `C`.
//
// Строим вектор пар `(ig, jg)` всех блоков результата.
// В `map` (параллельно) для каждой пары считаем локальный буфер `buf` размера `br * bc`
//  (br = число строк блока, bc = число столбцов блока) — это локальный C_{ig,jg}.
// После параллельного `collect` выполняем последовательную сборку всех буферов в глобальную `C`.
pub fn par_mul_block_pairs(a: &[f64], b: &[f64], n: usize, r: usize) -> Vec<f64> {
    let q = (n + r - 1) / r;

    let mut pairs = Vec::with_capacity(q * q);
    for ig in 0..q {
        for jg in 0..q {
            pairs.push((ig, jg));
        }
    }

    let computed: Vec<(usize, usize, Vec<f64>)> = pairs
        .into_par_iter()
        .map(|(ig, jg)| {
            let i_start = ig * r;
            let i_end = min(i_start + r, n);
            let j_start = jg * r;
            let j_end = min(j_start + r, n);

            let br = i_end - i_start;
            let bc = j_end - j_start;
            let mut buf = vec![0.0_f64; br * bc];

            let q_local = (n + r - 1) / r;
            for kg in 0..q_local {
                let k_start = kg * r;
                let k_end = min(k_start + r, n);

                for i in i_start..i_end {
                    let i_rel = i - i_start;
                    let base_i = i * n;
                    for k in k_start..k_end {
                        let aik = a[base_i + k];
                        let base_k = k * n;
                        for j in j_start..j_end {
                            let j_rel = j - j_start;
                            buf[i_rel * bc + j_rel] += aik * b[base_k + j];
                        }
                    }
                }
            }

            (ig, jg, buf)
        })
        .collect();

    let mut c = vec![0.0_f64; n * n];
    for (ig, jg, buf) in computed {
        let i_start = ig * r;
        let i_end = min(i_start + r, n);
        let j_start = jg * r;
        let j_end = min(j_start + r, n);
        let bc = j_end - j_start;

        for i in i_start..i_end {
            let i_rel = i - i_start;
            let base_c = i * n;
            for j in j_start..j_end {
                let j_rel = j - j_start;
                c[base_c + j] += buf[i_rel * bc + j_rel];
            }
        }
    }

    c
}
