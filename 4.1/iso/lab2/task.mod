set OILS; # types
set VEG within OILS;
set ANI within OILS;

param first integer; # номер первого периода
param last integer; # номер последнего периода
set T := first..last;

param cost {OILS, T}; # цена покупки

param dens {OILS}; # плотности
param dens_low; # мин. плотность итоговой смеси
param dens_high; # макс. плотность итоговой смеси

param I0 {OILS}; # начальные запасы
param p; # цена продажи готового продукта
param h; # стоимость хранения, у.е./т·мес

param capveg; # мощность очистки растительных в месяц
param capani; # мощность очистки животных в месяц

param storvegmax; # склад. емкость растительных в сумме
param storanimax; # склад. емкость животных в сумме

param finalveginstor;
param finalaniinstor;

var x {i in OILS, t in T} >= 0; # куплено масла i в месяце t
var y {i in OILS, t in T} >= 0; # очищено и продано масла i в месяце t
var Inv {i in OILS, t in T} >= 0; # остаток неочищенного масла i на конец месяца t

# Баланс запасов:
subject to Balance_first {i in OILS}:
Inv[i,first] = I0[i] + x[i,first] - y[i,first];

subject to Balance {i in OILS, t in first+1..last}:
Inv[i,t] = Inv[i,t-1] + x[i,t] - y[i,t];

# емкость склада в конецe месяца, по происхождению:
subject to StorVeg {t in T}:
sum {i in VEG} Inv[i,t] <= storvegmax;

subject to StorAni {t in T}:
sum {i in ANI} Inv[i,t] <= storanimax;

# Мощности очистки в месяц, по происхождению:
subject to CapVeg {t in T}:
sum {i in VEG} y[i,t] <= capveg;

subject to CapAni {t in T}:
sum {i in ANI} y[i,t] <= capani;

# Ограничения по плотности:
subject to DensityLow {t in T}:
sum {i in OILS} (dens[i] - dens_low) * y[i,t] >= 0;
subject to DensityHigh {t in T}:
sum {i in OILS} (dens[i] - dens_high) * y[i,t] <= 0;

# иребования на конец горизонта:
subject to FinalSumVeg:
sum {i in VEG} Inv[i,6] = finalveginstor;

subject to FinalSumAni:
sum {i in ANI} Inv[i,6] = finalaniinstor;

# Целевая функция:
maximize Profit:
sum {t in T} (
p * sum {i in OILS} y[i,t]
- sum {i in OILS} cost[i,t] * x[i,t]
- h * sum {i in OILS} Inv[i,t]
);