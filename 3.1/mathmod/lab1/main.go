package main

import (
	"fmt"
	"math"
	"sort"

	"gonum.org/v1/gonum/floats"
	"gonum.org/v1/gonum/stat"
	"gonum.org/v1/gonum/stat/distuv"
)

// Multiplicative Congruential Method
func mcm(a, beta, M, n int) []float64 {
	x := float64(a)
	results := make([]float64, n)
	for i := 0; i < n; i++ {
		x = math.Mod(float64(beta)*x, float64(M))
		results[i] = x / float64(M)
	}
	return results
}

// MacLaren-Marsaglia Method
func mmm(gen1, gen2 []float64, K, n int) []float64 {
	table := make([]float64, K)
	copy(table, gen1[:K])
	results := make([]float64, n)

	for i := 0; i < n; i++ {
		j := int(gen2[i] * float64(K))
		results[i] = table[j]
		table[j] = gen1[K+i]
	}

	return results
}

// Empirical Distribution Function
func empiricalDistribution(data []float64) ([]float64, []float64) {
	n := len(data)
	sortedData := make([]float64, n)
	copy(sortedData, data)
	sort.Float64s(sortedData)

	edf := make([]float64, n)
	for i := 0; i < n; i++ {
		edf[i] = float64(i+1) / float64(n)
	}

	return sortedData, edf
}

// Kolmogorov criterion
func kolmCriterion(data []float64, e float64) (float64, float64) {
	sortedData, edf := empiricalDistribution(data)
	n := len(data)
	dMax := 0.0

	for i := 0; i < n; i++ {
		theoreticalCdf := sortedData[i] //  for uniform distribution F(x) = x
		d := math.Abs(edf[i] - theoreticalCdf)
		if d > dMax {
			dMax = d
		}
	}

	critical := math.Sqrt(-0.5 * math.Log(e/2) / float64(n))
	return dMax, critical
}

// χ²-Pearson criterion
func pearson(data []float64, e float64, numBins int) (float64, float64) {
	n := len(data)
	if n == 0 {
		panic("data cannot be empty")
	}

	// Sort the data for histogram function
	sortedData := make([]float64, len(data))
	copy(sortedData, data)
	sort.Float64s(sortedData)

	// Create histogram for observed values
	binEdges := make([]float64, numBins+1)
	binEdges = floats.Span(binEdges, 0, 1)

	observed := stat.Histogram(nil, binEdges, sortedData, nil)
	expected := float64(n) / float64(numBins)

	statSum := 0.0
	for _, obs := range observed {
		statSum += (obs - expected) * (obs - expected) / expected
	}

	// Critical value for χ² with (numBins - 1) degrees of freedom
	critical := distuv.ChiSquared{K: float64(numBins - 1)}.Quantile(1 - e)

	return statSum, critical
}

// Test Data
func testData(data []float64, e float64) {
	dMax, critKolm := kolmCriterion(data, e)
	fmt.Printf("Kolmogorov criterion: D_max = %.4f, critical = %.4f, passed = %v\n", dMax, critKolm, dMax < critKolm)

	chi2Stat, critChi2 := pearson(data, e, 15)
	fmt.Printf("χ²-Pearson criterion: χ² = %.4f, critical = %.4f, passed = %v\n", chi2Stat, critChi2, chi2Stat < critChi2)
}

func main() {
	a := 32771    // initial value
	beta := 32771 // multiplier
	M := 1 << 31  // modulus
	n := 1000     // number of random numbers
	K := 128      // size of the table
	e := 0.05     // significance level

	fmt.Println("Checking MCM sequence:")
	mcmSeq := mcm(a, beta, M, n)
	testData(mcmSeq, e)

	/////////////////////////////////////////////////////////////////////////////////////

	fmt.Println("\nChecking MacLaren-Marsaglia sequence:")
	gen1 := mcm(3*beta, 3*beta, M, n+K)
	mmmSeq := mmm(gen1, mcmSeq, K, n)
	testData(mmmSeq, e)
}
