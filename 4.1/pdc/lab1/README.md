
## dataset

- ./dataset/ryzen7_7840HS.csv

### cpu

#### architecture
- **Model:** AMD Ryzen 7 7840HS w/ Radeon 780M Graphics  
- **Architecture:** `x86_64`  
- **CPU Operating Modes:** 32-bit, 64-bit  
- **Address Width:** 48 bits physical, 48 bits virtual  

#### Core Topology
| Property              | Value     |
|-----------------------|-----------|
| Total Logical CPUs    | 16        |
| Cores per Socket      | 8         |
| Threads per Core      | 2         |
| Sockets               | 1         |
| Stepping              | 1         |

#### Cache Hierarchy
| Level | Type         | Size (per instance) | Instances | Ways | Cache Line Size |
|-------|---------------|---------------------:|----------:|:----:|-----------------:|
| L1d | Data |       32 KiB |        8 |    8 |       64 bytes |
| L1i | Instruction |       32 KiB |        8 |    8 |       64 bytes |
| L2 | Unified |        1 MiB |        8 |    8 |       64 bytes |
| L3 | Unified |       16 MiB |        1 |   16 |       64 bytes |

**Total Effective Cache:**
- **L1 Data:** 256 KiB
- **L1 Instruction:** 256 KiB
- **L2:** 8 MiB
- **L3:** 16 MiB  

Note

- Each of the 8 cores contains a private 32 KiB L1 Data cache, 32 KiB L1 Instruction cache, and a 1 MiB L2 unified cache.  
- The 16 MiB L3 unified cache is shared across all cores within the single socket.  
- Cache line size is consistent across all levels at **64 bytes**, typical for modern AMD Zen 4 architecture.

### perf

```
sudo perf stat -e instructions,cycles,cache-references,cache-misses -r 3 -- ./target/release/lab1
```

on N=2048 array

- point

 Performance counter stats for './target/release/lab1':

      103544014219      instructions              #    0.20  insn per cycle
      530444249414      cycles
       15219052797      cache-references
        5226720582      cache-misses              #   34.343 % of all cache refs

     119.290851689 seconds time elapsed

     111.706137000 seconds user
       0.065588000 seconds sys

- point_ikj

```
 Performance counter stats for './target/release/lab1' (3 runs):

       28711235708      instructions              #    1.85  insn per cycle           ( +-  0.00% )
       14870628859      cycles                                                        ( +-  2.07% )
        3951416905      cache-references                                              ( +-  0.24% )
          37788752      cache-misses              #    0.961 % of all cache refs      ( +-  0.40% )

             3.620 +- 0.106 seconds time elapsed  ( +-  2.93% )
```

- par_point

```
 Performance counter stats for './target/release/lab1' (3 runs):

       95015455494      instructions              #    0.11  insn per cycle           ( +-  0.00% )
      940763490394      cycles                                                        ( +-  3.19% )
       16060244717      cache-references                                              ( +-  0.99% )
        8395966855      cache-misses              #   51.459 % of all cache refs      ( +-  0.70% )

            14.840 +- 0.750 seconds time elapsed  ( +-  5.06% )
```

- par_point_ikj

```
 Performance counter stats for './target/release/lab1' (3 runs):

       28869063591      instructions              #    1.20  insn per cycle           ( +-  0.01% )
       22764540350      cycles                                                        ( +-  3.13% )
        3668465913      cache-references                                              ( +-  0.67% )
          22988265      cache-misses              #    0.635 % of all cache refs      ( +-  8.16% )

           0.47870 +- 0.00886 seconds time elapsed  ( +-  1.85% )
```

- par_point_ikj_reduce

```
 Performance counter stats for './target/release/lab1' (3 runs):

      120817457795      instructions              #    0.30  insn per cycle           ( +-  0.00% )
      419579932076      cycles                                                        ( +-  3.59% )
       20575923034      cache-references                                              ( +-  3.83% )
        8060925246      cache-misses              #   38.071 % of all cache refs      ( +-  2.96% )

             6.282 +- 0.245 seconds time elapsed  ( +-  3.90% )
```


on N=2048 array, r = 64

- block

```
 Performance counter stats for './target/release/lab1' (3 runs):

      114262014243      instructions              #    0.49  insn per cycle           ( +-  0.00% )
      215752294370      cycles                                                        ( +-  5.99% )
       23713930550      cache-references                                              ( +-  5.95% )
        3343294972      cache-misses              #   13.065 % of all cache refs      ( +- 11.33% )

             53.94 +- 3.68 seconds time elapsed  ( +-  6.82% )
```

- block_ikj

 Performance counter stats for './target/release/lab1' (3 runs):

       40896616096      instructions              #    4.72  insn per cycle           ( +-  0.00% )
        8765492847      cycles                                                        ( +-  0.67% )
        4098056731      cache-references                                              ( +-  1.06% )
         120697384      cache-misses              #    3.003 % of all cache refs      ( +-  7.34% )

            1.9668 +- 0.0204 seconds time elapsed  ( +-  1.04% )

- par_block_ikj

 Performance counter stats for './target/release/lab1' (3 runs):

       44258777356      instructions              #    2.19  insn per cycle           ( +-  0.00% )
       19943748864      cycles                                                        ( +-  0.80% )
        4686852545      cache-references                                              ( +-  1.27% )
         241300402      cache-misses              #    5.059 % of all cache refs      ( +-  6.95% )

           0.44898 +- 0.00469 seconds time elapsed  ( +-  1.04% )

- par_block_ikj2

 Performance counter stats for './target/release/lab1' (12 runs):

      131645186195      instructions              #    3.42  insn per cycle           ( +-  0.00% )
       38387359714      cycles                                                        ( +-  0.29% )
        5067819698      cache-references                                              ( +-  2.80% )
         110154034      cache-misses              #    2.178 % of all cache refs      ( +-  7.22% )

            0.8721 +- 0.0122 seconds time elapsed  ( +-  1.40% )

- par_block_pairs

 Performance counter stats for './target/release/lab1' (12 runs):

       43942424124      instructions              #    2.39  insn per cycle           ( +-  0.00% )
       18066018344      cycles                                                        ( +-  0.30% )
        4556278461      cache-references                                              ( +-  0.66% )
         103073748      cache-misses              #    2.186 % of all cache refs      ( +-  4.61% )

           0.43894 +- 0.00564 seconds time elapsed  ( +-  1.28% )