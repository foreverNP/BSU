use lab1::{
    gen_matrix, mul_block, mul_block_ikj, mul_point, mul_point_ikj, par_mul_block_ikj,
    par_mul_block_ikj2, par_mul_block_pairs, par_mul_point, par_mul_point_ikj,
    par_mul_point_reduce,
};
use rand::{SeedableRng, rngs::StdRng};

fn approx_eq(a: &[f64], b: &[f64], tol: f64) -> bool {
    if a.len() != b.len() {
        return false;
    }
    for (x, y) in a.iter().zip(b.iter()) {
        if (x - y).abs() > tol {
            return false;
        }
    }
    true
}

#[test]
fn test_identity_matrix_multiplication() {
    let n = 3;
    let identity = vec![1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0];
    let matrix = vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0];

    let result_point = mul_point(&identity, &matrix, n);
    let result_point_ikj = mul_point_ikj(&identity, &matrix, n);
    let result_block = mul_block(&identity, &matrix, n, 1);
    let result_block_ikj = mul_block_ikj(&identity, &matrix, n, 1);

    // parallel versions
    let result_par_point = par_mul_point(&identity, &matrix, n);
    let result_par_point_ikj = par_mul_point_ikj(&identity, &matrix, n);
    let result_par_block_ikj = par_mul_block_ikj(&identity, &matrix, n, 1);
    let result_par_block_ikj2 = par_mul_block_ikj2(&identity, &matrix, n, 1);
    let result_par_block_pairs = par_mul_block_pairs(&identity, &matrix, n, 1);
    let result_par_point_reduce = par_mul_point_reduce(&identity, &matrix, n);

    let tol = 1e-10;
    assert!(approx_eq(&matrix, &result_point, tol));
    assert!(approx_eq(&matrix, &result_point_ikj, tol));
    assert!(approx_eq(&matrix, &result_block, tol));
    assert!(approx_eq(&matrix, &result_block_ikj, tol));

    // parallel correctness
    assert!(approx_eq(&matrix, &result_par_point, tol));
    assert!(approx_eq(&matrix, &result_par_point_ikj, tol));
    assert!(approx_eq(&matrix, &result_par_block_ikj, tol));
    assert!(approx_eq(&matrix, &result_par_block_ikj2, tol));
    assert!(approx_eq(&matrix, &result_par_block_pairs, tol));
    assert!(approx_eq(&matrix, &result_par_point_reduce, tol));
}

#[test]
fn test_zero_matrix_multiplication() {
    let n = 3;
    let zero = vec![0.0; 9];
    let matrix = vec![1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0];

    let result_point = mul_point(&zero, &matrix, n);
    let result_point_ikj = mul_point_ikj(&zero, &matrix, n);
    let result_block = mul_block(&zero, &matrix, n, 2);
    let result_block_ikj = mul_block_ikj(&zero, &matrix, n, 2);

    // parallel
    let result_par_point = par_mul_point(&zero, &matrix, n);
    let result_par_point_ikj = par_mul_point_ikj(&zero, &matrix, n);
    let result_par_block_ikj = par_mul_block_ikj(&zero, &matrix, n, 2);
    let result_par_block_ikj2 = par_mul_block_ikj2(&zero, &matrix, n, 2);
    let result_par_block_pairs = par_mul_block_pairs(&zero, &matrix, n, 2);
    let result_par_point_reduce = par_mul_point_reduce(&zero, &matrix, n);

    let tol = 1e-10;
    assert!(approx_eq(&zero, &result_point, tol));
    assert!(approx_eq(&zero, &result_point_ikj, tol));
    assert!(approx_eq(&zero, &result_block, tol));
    assert!(approx_eq(&zero, &result_block_ikj, tol));

    assert!(approx_eq(&zero, &result_par_point, tol));
    assert!(approx_eq(&zero, &result_par_point_ikj, tol));
    assert!(approx_eq(&zero, &result_par_block_ikj, tol));
    assert!(approx_eq(&zero, &result_par_block_ikj2, tol));
    assert!(approx_eq(&zero, &result_par_block_pairs, tol));
    assert!(approx_eq(&zero, &result_par_point_reduce, tol));
}

#[test]
fn test_small_matrix_multiplication() {
    let n = 2;
    let a = vec![1.0, 2.0, 3.0, 4.0];
    let b = vec![5.0, 6.0, 7.0, 8.0];
    let expected = vec![19.0, 22.0, 43.0, 50.0];

    let result_point = mul_point(&a, &b, n);
    let result_point_ikj = mul_point_ikj(&a, &b, n);
    let result_block = mul_block(&a, &b, n, 1);
    let result_block_ikj = mul_block_ikj(&a, &b, n, 1);

    // parallel
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_ikj = par_mul_point_ikj(&a, &b, n);
    let result_par_block_ikj = par_mul_block_ikj(&a, &b, n, 1);
    let result_par_block_ikj2 = par_mul_block_ikj2(&a, &b, n, 1);
    let result_par_block_pairs = par_mul_block_pairs(&a, &b, n, 1);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let tol = 1e-10;
    assert!(approx_eq(&expected, &result_point, tol));
    assert!(approx_eq(&expected, &result_point_ikj, tol));
    assert!(approx_eq(&expected, &result_block, tol));
    assert!(approx_eq(&expected, &result_block_ikj, tol));

    assert!(approx_eq(&expected, &result_par_point, tol));
    assert!(approx_eq(&expected, &result_par_point_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj2, tol));
    assert!(approx_eq(&expected, &result_par_block_pairs, tol));
    assert!(approx_eq(&expected, &result_par_point_reduce, tol));
}

#[test]
fn test_all_algorithms_produce_same_result() {
    let n = 4;
    let mut rng = StdRng::seed_from_u64(12345);
    let a = gen_matrix(n, &mut rng);
    let b = gen_matrix(n, &mut rng);

    let result_point = mul_point(&a, &b, n);
    let result_point_ikj = mul_point_ikj(&a, &b, n);

    let result_block_1 = mul_block(&a, &b, n, 1);
    let result_block_2 = mul_block(&a, &b, n, 2);
    let result_block_4 = mul_block(&a, &b, n, 4);

    let result_block_ikj_1 = mul_block_ikj(&a, &b, n, 1);
    let result_block_ikj_2 = mul_block_ikj(&a, &b, n, 2);
    let result_block_ikj_4 = mul_block_ikj(&a, &b, n, 4);

    // parallel results
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_ikj = par_mul_point_ikj(&a, &b, n);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let result_par_block_ikj_1 = par_mul_block_ikj(&a, &b, n, 1);
    let result_par_block_ikj_2 = par_mul_block_ikj(&a, &b, n, 2);
    let result_par_block_ikj_4 = par_mul_block_ikj(&a, &b, n, 4);

    let result_par_block_ikj2_1 = par_mul_block_ikj2(&a, &b, n, 1);
    let result_par_block_ikj2_2 = par_mul_block_ikj2(&a, &b, n, 2);
    let result_par_block_ikj2_4 = par_mul_block_ikj2(&a, &b, n, 4);

    let result_par_block_pairs_1 = par_mul_block_pairs(&a, &b, n, 1);
    let result_par_block_pairs_2 = par_mul_block_pairs(&a, &b, n, 2);
    let result_par_block_pairs_4 = par_mul_block_pairs(&a, &b, n, 4);

    let tol = 1e-6;
    assert!(approx_eq(&result_point, &result_point_ikj, tol));
    assert!(approx_eq(&result_point, &result_block_1, tol));
    assert!(approx_eq(&result_point, &result_block_2, tol));
    assert!(approx_eq(&result_point, &result_block_4, tol));
    assert!(approx_eq(&result_point, &result_block_ikj_1, tol));
    assert!(approx_eq(&result_point, &result_block_ikj_2, tol));
    assert!(approx_eq(&result_point, &result_block_ikj_4, tol));

    // compare serial vs parallel
    assert!(approx_eq(&result_point, &result_par_point, tol));
    assert!(approx_eq(&result_point, &result_par_point_ikj, tol));
    assert!(approx_eq(&result_point, &result_par_point_reduce, tol));

    assert!(approx_eq(&result_point, &result_par_block_ikj_1, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj_2, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj_4, tol));

    assert!(approx_eq(&result_point, &result_par_block_ikj2_1, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj2_2, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj2_4, tol));

    assert!(approx_eq(&result_point, &result_par_block_pairs_1, tol));
    assert!(approx_eq(&result_point, &result_par_block_pairs_2, tol));
    assert!(approx_eq(&result_point, &result_par_block_pairs_4, tol));
}

#[test]
fn test_non_divisible_block_size() {
    let n = 5;
    let mut rng = StdRng::seed_from_u64(54321);
    let a = gen_matrix(n, &mut rng);
    let b = gen_matrix(n, &mut rng);

    let result_point = mul_point(&a, &b, n);
    let result_block = mul_block(&a, &b, n, 3);
    let result_block_ikj = mul_block_ikj(&a, &b, n, 3);

    // parallel
    let result_par_block_ikj = par_mul_block_ikj(&a, &b, n, 3);
    let result_par_block_ikj2 = par_mul_block_ikj2(&a, &b, n, 3);
    let result_par_block_pairs = par_mul_block_pairs(&a, &b, n, 3);
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let tol = 1e-6;
    assert!(approx_eq(&result_point, &result_block, tol));
    assert!(approx_eq(&result_point, &result_block_ikj, tol));

    assert!(approx_eq(&result_point, &result_par_block_ikj, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj2, tol));
    assert!(approx_eq(&result_point, &result_par_block_pairs, tol));
    assert!(approx_eq(&result_point, &result_par_point, tol));
    assert!(approx_eq(&result_point, &result_par_point_reduce, tol));
}

#[test]
fn test_large_block_size() {
    let n = 3;
    let mut rng = StdRng::seed_from_u64(99999);
    let a = gen_matrix(n, &mut rng);
    let b = gen_matrix(n, &mut rng);

    let result_point = mul_point(&a, &b, n);
    let result_block = mul_block(&a, &b, n, 10);
    let result_block_ikj = mul_block_ikj(&a, &b, n, 10);

    // parallel
    let result_par_block_ikj = par_mul_block_ikj(&a, &b, n, 10);
    let result_par_block_ikj2 = par_mul_block_ikj2(&a, &b, n, 10);
    let result_par_block_pairs = par_mul_block_pairs(&a, &b, n, 10);
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let tol = 1e-6;
    assert!(approx_eq(&result_point, &result_block, tol));
    assert!(approx_eq(&result_point, &result_block_ikj, tol));

    assert!(approx_eq(&result_point, &result_par_block_ikj, tol));
    assert!(approx_eq(&result_point, &result_par_block_ikj2, tol));
    assert!(approx_eq(&result_point, &result_par_block_pairs, tol));
    assert!(approx_eq(&result_point, &result_par_point, tol));
    assert!(approx_eq(&result_point, &result_par_point_reduce, tol));
}

#[test]
fn test_single_element_matrix() {
    let n = 1;
    let a = vec![3.0];
    let b = vec![7.0];
    let expected = vec![21.0];

    let result_point = mul_point(&a, &b, n);
    let result_point_ikj = mul_point_ikj(&a, &b, n);
    let result_block = mul_block(&a, &b, n, 1);
    let result_block_ikj = mul_block_ikj(&a, &b, n, 1);

    // parallel
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_ikj = par_mul_point_ikj(&a, &b, n);
    let result_par_block_ikj = par_mul_block_ikj(&a, &b, n, 1);
    let result_par_block_ikj2 = par_mul_block_ikj2(&a, &b, n, 1);
    let result_par_block_pairs = par_mul_block_pairs(&a, &b, n, 1);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let tol = 1e-10;
    assert!(approx_eq(&expected, &result_point, tol));
    assert!(approx_eq(&expected, &result_point_ikj, tol));
    assert!(approx_eq(&expected, &result_block, tol));
    assert!(approx_eq(&expected, &result_block_ikj, tol));

    assert!(approx_eq(&expected, &result_par_point, tol));
    assert!(approx_eq(&expected, &result_par_point_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj2, tol));
    assert!(approx_eq(&expected, &result_par_block_pairs, tol));
    assert!(approx_eq(&expected, &result_par_point_reduce, tol));
}

#[test]
fn test_negative_values() {
    let n = 2;
    let a = vec![-1.0, 2.0, -3.0, 4.0];
    let b = vec![5.0, -6.0, -7.0, 8.0];
    let expected = vec![-19.0, 22.0, -43.0, 50.0];

    let result_point = mul_point(&a, &b, n);
    let result_point_ikj = mul_point_ikj(&a, &b, n);
    let result_block = mul_block(&a, &b, n, 1);
    let result_block_ikj = mul_block_ikj(&a, &b, n, 1);

    // parallel
    let result_par_point = par_mul_point(&a, &b, n);
    let result_par_point_ikj = par_mul_point_ikj(&a, &b, n);
    let result_par_block_ikj = par_mul_block_ikj(&a, &b, n, 1);
    let result_par_block_ikj2 = par_mul_block_ikj2(&a, &b, n, 1);
    let result_par_block_pairs = par_mul_block_pairs(&a, &b, n, 1);
    let result_par_point_reduce = par_mul_point_reduce(&a, &b, n);

    let tol = 1e-10;
    assert!(approx_eq(&expected, &result_point, tol));
    assert!(approx_eq(&expected, &result_point_ikj, tol));
    assert!(approx_eq(&expected, &result_block, tol));
    assert!(approx_eq(&expected, &result_block_ikj, tol));

    assert!(approx_eq(&expected, &result_par_point, tol));
    assert!(approx_eq(&expected, &result_par_point_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj, tol));
    assert!(approx_eq(&expected, &result_par_block_ikj2, tol));
    assert!(approx_eq(&expected, &result_par_block_pairs, tol));
    assert!(approx_eq(&expected, &result_par_point_reduce, tol));
}
