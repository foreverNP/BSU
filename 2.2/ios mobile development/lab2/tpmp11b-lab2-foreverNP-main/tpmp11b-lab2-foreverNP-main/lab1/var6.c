#include <stdio.h>  
#include <math.h>  
//comment  
float calculateMedian(float sideTo, float side1, float side2)  
{  
    return 0.5 * sqrt(2 * side1 * side1 + 2 * side2 * side2 - sideTo * sideTo);  
}  
//comment2
int main()  
{  
    float a, b, c;  
  
    printf("Enter the length of side a, b, c: ");  
    scanf("%f%f%f", &a, &b, &c);  
  
    if (a + b <= c || a + c <= b || b + c <= a)  
    {  
        printf("\nThe triangle with the given sides does not exist.\n");  
        return -1;  
    }  
  
    float ma = calculateMedian(a, b, c);  
    float mb = calculateMedian(b, a, c);  
    float mc = calculateMedian(c, a, b);  
  
    printf("\nMedian to side a: %.2f\n", ma);  
    printf("Median to side b: %.2f\n", mb);  
    printf("Median to side c: %.2f\n", mc);  
  
    return 0;  
}
