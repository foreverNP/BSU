#!/usr/bin/env bash
# cpu_report.sh
# Produce a technical Markdown CPU summary (architecture, topology, cache hierarchy).
# Works on Linux systems exposing cache info via /sys/devices/system/cpu/* and using lscpu when available.

set -euo pipefail

# --- helpers -----------------------------------------------------------------

# Trim
trim() { printf "%s" "$*" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'; }

# human-readable bytes -> bytes (expects "32K" "1024K" "16M" "16384K" etc)
sz_to_bytes() {
  local s=$(echo "$1" | tr -d '[:space:]')
  if [[ $s =~ ^([0-9]+)([KkMmGg]?)$ ]]; then
    local n=${BASH_REMATCH[1]}
    local u=${BASH_REMATCH[2]}
    case "$u" in
      [Kk]) printf "%d" $(( n * 1024 ));;
      [Mm]) printf "%d" $(( n * 1024 * 1024 ));;
      [Gg]) printf "%d" $(( n * 1024 * 1024 * 1024 ));;
      *) printf "%d" "$n";;
    esac
  else
    # fallback: try to parse as integer
    printf "%d" "${s:-0}"
  fi
}

# bytes -> human readable (KiB/MiB) using powers of 1024, produce e.g. "256 KiB" or "8 MiB"
bytes_to_hr() {
  local b=$1
  if (( b >= 1024*1024 )); then
    awk -v B="$b" 'BEGIN{printf "%.0f MiB", B/(1024*1024)}'
  elif (( b >= 1024 )); then
    awk -v B="$b" 'BEGIN{printf "%.0f KiB", B/1024}'
  else
    printf "%d B" "$b"
  fi
}

# Popcount for a hex string (mask in shared_cpu_map may contain commas like "ff,0f")
hex_popcount() {
  local hex=$(echo "$1" | tr -d ',\n' | sed -e 's/^0x//' -e 's/^0*//')
  # if empty -> 0
  [[ -z "$hex" ]] && { printf "0"; return; }
  # map nibble to popcount quickly
  local -A pop=( [0]=0 [1]=1 [2]=1 [3]=2 [4]=1 [5]=2 [6]=2 [7]=3 [8]=1 [9]=2 [A]=2 [B]=3 [C]=2 [D]=3 [E]=3 [F]=4 )
  local sum=0
  hex=$(echo "$hex" | tr '[:lower:]' '[:upper:]')
  for ((i=0;i<${#hex};i++)); do
    c=${hex:i:1}
    # if non-hex (shouldn't) skip
    if [[ -n "${pop[$c]:-}" ]]; then
      sum=$((sum + pop[$c]))
    fi
  done
  printf "%d" "$sum"
}

# safe read file
safe_read() {
  local f="$1"
  if [[ -r "$f" ]]; then
    awk '{print $0; exit}' "$f"
  else
    echo ""
  fi
}

# --- gather basic info -------------------------------------------------------

# Use lscpu when available for nicer fields, fallback to uname/proc
_LSCPU_OK=true
if ! command -v lscpu >/dev/null 2>&1; then
  _LSCPU_OK=false
fi

model=""
if [[ -r /proc/cpuinfo ]]; then
  model=$(awk -F: '/model name/ {print $2; exit}' /proc/cpuinfo | sed 's/^[ \t]*//')
fi
# fallback to lscpu
if [[ -z "$model" && "$_LSCPU_OK" == true ]]; then
  model=$(lscpu | awk -F: '/Model name/ {print $2; exit}' | sed 's/^[ \t]*//')
fi
model=${model:-"Unknown"}

architecture=$(uname -m)
if [[ "$_LSCPU_OK" == true ]]; then
  architecture=$(lscpu | awk -F: '/Architecture/ {print $2; exit}' | sed 's/^[ \t]*//')
fi

cpu_opmodes=""
addr_sizes=""
if [[ "$_LSCPU_OK" == true ]]; then
  cpu_opmodes=$(lscpu | awk -F: '/CPU op-mode/ {print $2; exit}' | sed 's/^[ \t]*//')
  addr_sizes=$(lscpu | awk -F: '/Address sizes/ {print $2; exit}' | sed 's/^[ \t]*//')
fi

total_logical=$(nproc --all 2>/dev/null || echo "")
if [[ -z "$total_logical" && "$_LSCPU_OK" == true ]]; then
  total_logical=$(lscpu | awk -F: '/^CPU\(s\):/ {print $2; exit}' | sed 's/^[ \t]*//')
fi
total_logical=${total_logical:-0}

on_line_cpus=$(safe_read /sys/devices/system/cpu/online)
if [[ -z "$on_line_cpus" && "$_LSCPU_OK" == true ]]; then
  on_line_cpus=$(lscpu | awk -F: '/On-line CPU\(s\) list/ {print $2; exit}' | sed 's/^[ \t]*//')
fi
on_line_cpus=${on_line_cpus:-"unknown"}

threads_per_core=""
cores_per_socket=""
sockets=""
stepping=""

if [[ "$_LSCPU_OK" == true ]]; then
  threads_per_core=$(lscpu | awk -F: '/Thread\(s\) per core/ {print $2; exit}' | sed 's/^[ \t]*//')
  cores_per_socket=$(lscpu | awk -F: '/Core\(s\) per socket/ {print $2; exit}' | sed 's/^[ \t]*//')
  sockets=$(lscpu | awk -F: '/Socket\(s\)/ {print $2; exit}' | sed 's/^[ \t]*//')
fi

# stepping from /proc/cpuinfo if present
if [[ -r /proc/cpuinfo ]]; then
  stepping=$(awk -F: '/stepping/ {print $2; exit}' /proc/cpuinfo | sed 's/^[ \t]*//')
fi
threads_per_core=${threads_per_core:-"?"}
cores_per_socket=${cores_per_socket:-"?"}
sockets=${sockets:-"?"}
stepping=${stepping:-"?"}

# --- gather cache info ------------------------------------------------------

declare -A cache_size_bytes       # key -> size in bytes (per-instance)
declare -A cache_line_size        # key -> line size bytes
declare -A cache_ways             # key -> ways_of_associativity
declare -A cache_instances       # key -> computed number of instances
declare -A cache_level_type      # store level/type for ordering / labels

# iterate cpu0 indices
cpu0_cache_dir="/sys/devices/system/cpu/cpu0/cache"
if [[ ! -d "$cpu0_cache_dir" ]]; then
  echo "ERROR: sysfs cache info not available at $cpu0_cache_dir" >&2
  exit 2
fi

# total logical cpus for instance calculation (integer)
TOTAL_LOGICAL="${total_logical:-0}"
if ! [[ $TOTAL_LOGICAL =~ ^[0-9]+$ ]]; then TOTAL_LOGICAL=0; fi

for idx in "$cpu0_cache_dir"/index*; do
  [[ -d "$idx" ]] || continue
  level=$(safe_read "$idx/level")
  type=$(safe_read "$idx/type")
  size_raw=$(safe_read "$idx/size")
  ways=$(safe_read "$idx/ways_of_associativity")
  line_size=$(safe_read "$idx/coherency_line_size")
  shared_map=""
  # prefer 'shared_cpu_map' else try 'shared_cpu_list' (older kernels)
  if [[ -r "$idx/shared_cpu_map" ]]; then
    shared_map=$(safe_read "$idx/shared_cpu_map")
  elif [[ -r "$idx/shared_cpu_list" ]]; then
    shared_map=$(safe_read "$idx/shared_cpu_list")
  fi

  # normalize key label: L1d, L1i, L2, L3, else L{level}-{type}
  key=""
  if [[ "$level" == "1" && "${type,,}" == "data" ]]; then
    key="L1d"
  elif [[ "$level" == "1" && "${type,,}" == "instruction" ]]; then
    key="L1i"
  elif [[ "$level" == "2" ]]; then
    key="L2"
  elif [[ "$level" == "3" ]]; then
    key="L3"
  else
    # fallback label
    key="L${level}_${type}"
  fi

  # parse bytes
  bytes=$(sz_to_bytes "$size_raw")
  # determine instances:
  inst=1
  if [[ -n "$shared_map" ]]; then
    # shared_map is typically a hex mask (maybe with commas). Find number of set bits
    bits=$(hex_popcount "$shared_map")
    if [[ $bits -gt 0 && $TOTAL_LOGICAL -gt 0 ]]; then
      inst=$(( TOTAL_LOGICAL / bits ))
      # ensure at least 1
      (( inst < 1 )) && inst=1
    else
      inst=1
    fi
  else
    # fallback heuristic:
    # assume private caches -> instances = cores_per_socket * sockets if level 1 or 2
    if [[ "$level" == "1" || "$level" == "2" ]]; then
      # try to get physical cores (cores_per_socket * sockets)
      if [[ "$cores_per_socket" =~ ^[0-9]+$ && "$sockets" =~ ^[0-9]+$ ]]; then
        inst=$(( cores_per_socket * sockets ))
      else
        inst=1
      fi
    else
      inst=1
    fi
  fi

  cache_size_bytes["$key"]="$bytes"
  cache_line_size["$key"]="${line_size:-?}"
  cache_ways["$key"]="${ways:-?}"
  cache_instances["$key"]="$inst"
  cache_level_type["$key"]="${level}:${type}"
done

# Now compute totals (per cache type)
total_L1d=0; total_L1i=0; total_L2=0; total_L3=0
for k in "${!cache_size_bytes[@]}"; do
  bytes=${cache_size_bytes[$k]}
  inst=${cache_instances[$k]:-1}
  case "$k" in
    L1d) total_L1d=$(( total_L1d + bytes * inst )) ;;
    L1i) total_L1i=$(( total_L1i + bytes * inst )) ;;
    L2)  total_L2=$(( total_L2 + bytes * inst )) ;;
    L3)  total_L3=$(( total_L3 + bytes * inst )) ;;
    *)   ;; # ignore other
  esac
done

# --- output in Markdown ------------------------------------------------------

# Architecture block (match user's sample)
cat <<EOF
#### architecture
- **Model:** ${model}  
- **Architecture:** \`${architecture}\`  
- **CPU Operating Modes:** ${cpu_opmodes:-"unknown"}  
- **Address Width:** ${addr_sizes:-"unknown"}  

#### Core Topology
| Property              | Value     |
|-----------------------|-----------|
| Total Logical CPUs    | ${total_logical}        |
| Cores per Socket      | ${cores_per_socket}         |
| Threads per Core      | ${threads_per_core}         |
| Sockets               | ${sockets}         |
| Stepping              | ${stepping}         |

#### Cache Hierarchy
| Level | Type         | Size (per instance) | Instances | Ways | Cache Line Size |
|-------|---------------|---------------------:|----------:|:----:|-----------------:|
EOF

# Print table rows in order L1d, L1i, L2, L3 then any others
order=(L1d L1i L2 L3)
for k in "${order[@]}"; do
  if [[ -n "${cache_size_bytes[$k]:-}" ]]; then
    size_hr=$(bytes_to_hr "${cache_size_bytes[$k]}")
    inst=${cache_instances[$k]:-1}
    ways=${cache_ways[$k]:-"?"}
    line=${cache_line_size[$k]:-"?"}
    # type for table: derive from stored level:type
    level_type="${cache_level_type[$k]:-}"
    # print
    printf "| %s | %s | %12s | %8s | %4s | %14s |\n" \
      "$k" "${level_type#*:}" "$size_hr" "$inst" "$ways" "${line} bytes"
  fi
done

# print any other caches discovered
for k in "${!cache_size_bytes[@]}"; do
  skip=false
  for s in "${order[@]}"; do [[ "$k" == "$s" ]] && skip=true; done
  $skip && continue
  size_hr=$(bytes_to_hr "${cache_size_bytes[$k]}")
  inst=${cache_instances[$k]:-1}
  ways=${cache_ways[$k]:-"?"}
  line=${cache_line_size[$k]:-"?"}
  level_type="${cache_level_type[$k]:-}"
  printf "| %s | %s | %12s | %8s | %4s | %14s |\n" \
    "$k" "${level_type#*:}" "$size_hr" "$inst" "$ways" "${line} bytes"
done

echo
echo "**Total Effective Cache:**"
echo "- **L1 Data:** $(bytes_to_hr $total_L1d)  "
echo "- **L1 Instruction:** $(bytes_to_hr $total_L1i)  "
echo "- **L2:** $(bytes_to_hr $total_L2)  "
echo "- **L3:** $(bytes_to_hr $total_L3)  "

# print note about how instances computed
cat <<'NOTE'

*Notes:*
- Instances are computed from each cache index's `shared_cpu_map` when available (the script computes how many logical CPUs share a cache and derives the number of distinct cache instances). If `shared_cpu_map` is not available, the script uses a conservative heuristic.
- Sizes shown as "Size (per instance)" are per-cache-instance values read from sysfs; "Instances" is the number of distinct caches of that type.
- Ways (associativity) and cache-line sizes are read from sysfs when available.
NOTE
