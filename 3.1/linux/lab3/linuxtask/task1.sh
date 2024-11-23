#!/bin/bash


# Input data
names="COLIN,AMANDBA,AMANDAB,CAROL,PauL,JOSEPH"
weights="1,4,4,5,2,1"
n=4


# Convert input strings to arrays
IFS=',' read -r -a names_array <<< "$names"
IFS=',' read -r -a weights_array <<< "$weights"


echo "Names array: ${names_array[@]}"
echo "Weights array: ${weights_array[@]}"


# Check if there are no participants
if [ ${#names_array[@]} -eq 0 ]; then
   echo "No participants"
   exit 0
fi


# Check if there are not enough participants
if [ $n -gt ${#names_array[@]} ]; then
   echo "Not enough participants"
   exit 0
fi


# Initialize results array
results=()


# Calculate the winning number for each participant
for (( i=0; i<${#names_array[@]}; i++ )); do
   name=${names_array[$i]}
   weight=${weights_array[$i]}


   echo "Processing name: $name, weight: $weight"


   # Calculate the sum of alphabetical positions
   sum=0
   for (( j=0; j<${#name}; j++ )); do
       char=${name:$j:1}
       ascii=$(printf '%d' "'$char")
       if [[ $char =~ [A-Z] ]]; then
           position=$((ascii - 64))
       elif [[ $char =~ [a-z] ]]; then
           position=$((ascii - 96))
       else
           position=0
       fi
       sum=$((sum + position))
   done
   som=$((sum + ${#name}))


   echo "Sum of positions: $som"


   # Calculate the winning number
   winning_number=$((som * weight))
   results+=("$winning_number $name")
done


echo "Results before sorting: ${results[@]}"


# Sort the results by winning number (descending) and then by name (ascending)
sorted_results=$(printf '%s\n' "${results[@]}" | sort -rn -k1,1 -k2,2)


# Print sorted results on new lines
echo "Sorted results:"
while IFS= read -r line; do
   echo "$line"
done <<< "$sorted_results"


# Print the n-th participant
echo "$sorted_results" | awk -v n=$n 'NR==n{print $2}'
