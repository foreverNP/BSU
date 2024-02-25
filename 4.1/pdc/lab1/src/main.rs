use rand::{SeedableRng, rngs::StdRng};
use std::collections::BTreeMap;
use std::fs::File;
use std::io::{self, Write};
use std::time::{Duration, Instant};

use lab1::{
    gen_matrix, mul_block, mul_block_ikj, mul_point, mul_point_ikj, par_mul_block_ikj,
    par_mul_block_ikj2, par_mul_block_pairs, par_mul_point, par_mul_point_ikj,
    par_mul_point_reduce,
};

const DEFAULT_NS: &[usize] = &[
    100, 128, 200, 256, 300, 400, 500, 512, 800, 1000, 1024, 1500, 2000, 2048,
];
const DEFAULT_R_PERCENTAGES: &[f64] = &[
    0.0, 1.0, 2.0, 3.0, 5.0, 7.5, 8.0, 10.0, 15.0, 30.0, 40.0, 50.0, 75.0, 100.0,
];
const DEFAULT_R: &[usize] = &[
    1, 2, 4, 5, 8, 10, 15, 16, 20, 30, 32, 50, 64, 100, 128, 200, 256, 500, 512,
];

const TRIALS: usize = 10;

fn format_duration(d: Duration) -> String {
    let ms = d.as_secs_f64() * 1000.0;
    format!("{:.3}", ms)
}

fn calc_block_size(n: usize, percentage: f64) -> usize {
    let size = ((n as f64) * percentage / 100.0).ceil() as usize;
    std::cmp::max(1, size)
}
fn main() -> io::Result<()> {
    // TODO: play with number of threads
    // rayon::ThreadPoolBuilder::new().num_threads(16).build_global();

    let mut csv = File::create("results.csv")?;
    writeln!(csv, "N,r_percent,r_actual,trial,algo,duration_ms")?;

    let mut rng = StdRng::from_entropy();

    let ns: Vec<usize> = DEFAULT_NS.to_vec();

    let r_percentages: Vec<f64> = DEFAULT_R_PERCENTAGES.to_vec();
    let default_r_values: Vec<usize> = DEFAULT_R.to_vec();

    println!(
        "Эксперимент: Ns={:?}, R_percentages={:?}, DEFAULT_R={:?}, trials={}",
        ns, r_percentages, default_r_values, TRIALS
    );

    for &n in &ns {
        println!("\n\nЭксперимент: N={}", n);

        let a = gen_matrix(n, &mut rng);
        let b = gen_matrix(n, &mut rng);

        let mut r_map: BTreeMap<usize, Option<f64>> = BTreeMap::new();

        for &percent in &r_percentages {
            let r = calc_block_size(n, percent);
            r_map.entry(r).or_insert(Some(percent));
        }

        for &r_hard in &default_r_values {
            let r_clamped = if r_hard == 0 {
                1
            } else {
                std::cmp::min(r_hard, n)
            };
            r_map.entry(r_clamped).or_insert(None);
        }

        for trial in 1..=TRIALS {
            print!("N={}, trial={} — point  ", n, trial);
            io::stdout().flush().ok();
            let t0 = Instant::now();
            let _c_point = mul_point(&a, &b, n);
            let dt_point = t0.elapsed();
            println!("{} ms", format_duration(dt_point));
            writeln!(
                csv,
                "{},{},{},{},{},{}",
                n,
                '-',
                '-',
                trial,
                "point",
                format_duration(dt_point)
            )?;

            print!("N={}, trial={} — point_ikj  ", n, trial);
            io::stdout().flush().ok();
            let t0o = Instant::now();
            let _c_point_ikj = mul_point_ikj(&a, &b, n);
            let dt_point_ikj = t0o.elapsed();
            println!("{} ms", format_duration(dt_point_ikj));
            writeln!(
                csv,
                "{},{},{},{},{},{}",
                n,
                '-',
                '-',
                trial,
                "point_ikj",
                format_duration(dt_point_ikj)
            )?;

            print!("N={}, trial={} — par_point  ", n, trial);
            io::stdout().flush().ok();
            let t1 = Instant::now();
            let _c_par_point = par_mul_point(&a, &b, n);
            let dt_par_point = t1.elapsed();
            println!("{} ms", format_duration(dt_par_point));
            writeln!(
                csv,
                "{},{},{},{},{},{}",
                n,
                '-',
                '-',
                trial,
                "par_point",
                format_duration(dt_par_point)
            )?;

            print!("N={}, trial={} — par_point_ikj  ", n, trial);
            io::stdout().flush().ok();
            let t2 = Instant::now();
            let _c_par_point_ikj = par_mul_point_ikj(&a, &b, n);
            let dt_par_point_ikj = t2.elapsed();
            println!("{} ms", format_duration(dt_par_point_ikj));
            writeln!(
                csv,
                "{},{},{},{},{},{}",
                n,
                '-',
                '-',
                trial,
                "par_point_ikj",
                format_duration(dt_par_point_ikj)
            )?;

            print!("N={}, trial={} — par_point_ikj_reduce  ", n, trial);
            io::stdout().flush().ok();
            let t_par_red = Instant::now();
            let _c_par_red = par_mul_point_reduce(&a, &b, n);
            let dt_par_red = t_par_red.elapsed();
            println!("{} ms", format_duration(dt_par_red));
            writeln!(
                csv,
                "{},{},{},{},{},{}",
                n,
                '-',
                '-',
                trial,
                "par_point_ikj_reduce",
                format_duration(dt_par_red)
            )?;

            for (r, percent_ikj) in r_map.iter() {
                let percent_for_csv = percent_ikj.unwrap_or(-1.0);

                let percent_label = match percent_ikj {
                    Some(p) => format!("{}", p),
                    None => String::from("-"),
                };

                print!(
                    "N={}, r_percent={} (r={}), trial={} — block        ",
                    n, percent_label, r, trial
                );
                io::stdout().flush().ok();
                let t_block = Instant::now();
                let _c_block_ijk = mul_block(&a, &b, n, *r);
                let dt_block_ijk = t_block.elapsed();
                println!("{} ms", format_duration(dt_block_ijk));
                writeln!(
                    csv,
                    "{},{},{},{},{},{}",
                    n,
                    percent_for_csv,
                    r,
                    trial,
                    "block",
                    format_duration(dt_block_ijk)
                )?;

                print!(
                    "N={}, r_percent={} (r={}), trial={} — block_ikj    ",
                    n, percent_label, r, trial
                );
                io::stdout().flush().ok();
                let t_block_ikj = Instant::now();
                let _c_block_ikj = mul_block_ikj(&a, &b, n, *r);
                let dt_block_ikj = t_block_ikj.elapsed();
                println!("{} ms", format_duration(dt_block_ikj));
                writeln!(
                    csv,
                    "{},{},{},{},{},{}",
                    n,
                    percent_for_csv,
                    r,
                    trial,
                    "block_ikj",
                    format_duration(dt_block_ikj)
                )?;

                print!(
                    "N={}, r_percent={} (r={}), trial={} — par_block_ikj ",
                    n, percent_label, r, trial
                );
                io::stdout().flush().ok();
                let t_par_block_ikj = Instant::now();
                let _c_par_block_ikj = par_mul_block_ikj(&a, &b, n, *r);
                let dt_par_block_ikj = t_par_block_ikj.elapsed();
                println!("{} ms", format_duration(dt_par_block_ikj));
                writeln!(
                    csv,
                    "{},{},{},{},{},{}",
                    n,
                    percent_for_csv,
                    r,
                    trial,
                    "par_block_ikj",
                    format_duration(dt_par_block_ikj)
                )?;

                print!(
                    "N={}, r_percent={} (r={}), trial={} — par_block_ikj2 ",
                    n, percent_label, r, trial
                );
                io::stdout().flush().ok();
                let t_block_opt_par = Instant::now();
                let _c_block_opt_par = par_mul_block_ikj2(&a, &b, n, *r);
                let dt_block_opt_par = t_block_opt_par.elapsed();
                println!("{} ms", format_duration(dt_block_opt_par));
                writeln!(
                    csv,
                    "{},{},{},{},{},{}",
                    n,
                    percent_for_csv,
                    r,
                    trial,
                    "par_block_ikj2",
                    format_duration(dt_block_opt_par)
                )?;

                print!(
                    "N={}, r_percent={} (r={}), trial={} — par_block_pairs ",
                    n, percent_label, r, trial
                );
                io::stdout().flush().ok();
                let t_par_pairs = Instant::now();
                let _c_par_pairs = par_mul_block_pairs(&a, &b, n, *r);
                let dt_par_pairs = t_par_pairs.elapsed();
                println!("{} ms", format_duration(dt_par_pairs));
                writeln!(
                    csv,
                    "{},{},{},{},{},{}",
                    n,
                    percent_for_csv,
                    r,
                    trial,
                    "par_block_pairs",
                    format_duration(dt_par_pairs)
                )?;
            }
        }
    }

    Ok(())
}
