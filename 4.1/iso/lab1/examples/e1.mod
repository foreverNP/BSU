var x1; # количество краски 1
var x2; # количество краски 1
maximize z: 10*x1 + 15*x2;
subject to time: (1/40)*x1 + (1/30)*x2 <= 40;
subject to 1_limit: 0 <= x1 <= 1000;
subject to 2_limit: 0 <= x2 <= 860;