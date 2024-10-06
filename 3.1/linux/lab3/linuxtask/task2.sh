#!/bin/bash


# Function to count the occurrences of digit d in the squares of numbers from 0 to n
nb_dig() {
   local n=$1
   local d=$2
   local count=0


   # Iterate through all numbers from 0 to n
   for (( k=0; k<=n; k++ )); do
       square=$((k * k))
       # Convert the square to a string
       square_str="$square"
       # Count occurrences of digit d in the square
       count=$((count + $(echo "$square_str" | grep -o "$d" | wc -l)))
   done


   echo "$count"
}


# Example usage
n=25
d=1
result=$(nb_dig $n $d)
echo "The digit $d appears $result times in the squares of numbers from 0 to $n."
