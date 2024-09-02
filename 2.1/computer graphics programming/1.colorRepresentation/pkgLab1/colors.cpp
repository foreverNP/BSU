#include "colors.h"

RGB::RGB(int R, int G, int B)
{
    if (R > 255)
    {
        R = 255;
    }
    else if (R < 0)
    {
        R = 0;
    }
    if (G > 255)
    {
        G = 255;
    }
    else if (G < 0)
    {
        G = 0;
    }
    if (B > 255)
    {
        B = 255;
    }
    else if (B < 0)
    {
        B = 0;
    }

    r = R;
    g = G;
    b = B;
}

double RGB::getParam1()
{
    return r;
}

double RGB::getParam2()
{
    return g;
}

double RGB::getParam3()
{
    return b;
}

double RGB::getParam4()
{
    return 0.0;
}

void RGB::setParam1(double val)
{
    r = val;
}

void RGB::setParam2(double val)
{
    g = val;
}

void RGB::setParam3(double val)
{
    b = val;
}

void RGB::setParam4(double val)
{
}

double RGB::fXyz(double x)
{
    if (x >= 0.04045)
    {
        return pow((x + 0.055) / 1.055, 2.4);
    }

    return x / 12.92;
}

double RGB::fDop(double x)
{
    if (x >= 0.008856)
    {
        return pow(x, 1 / 3);
    }

    return 7.787 * x + 16 / 116;
}

ColorSystem *RGB::toRGB()
{
    return new RGB(r, g, b);
}

ColorSystem *RGB::toCMYK()
{
    double c, m, y, k;

    if (1 - r / 255.0 <= 1 - g / 255.0 && 1 - r / 255.0 <= 1 - b / 255.0)
    {
        k = 1.0 - r / 255.0;
    }
    else if (1 - g / 255.0 <= 1 - b / 255.0)
    {
        k = 1.0 - g / 255.0;
    }
    else
    {
        k = 1.0 - b / 255.0;
    }

    c = (1.0 - r / 255.0 - k) / (1.0 - k);
    m = (1.0 - g / 255.0 - k) / (1.0 - k);
    y = (1.0 - b / 255.0 - k) / (1.0 - k);

    return new CMYK(c * 100.0, m * 100.0, y * 100.0, k * 100.0);
}

ColorSystem *RGB::toHSV()
{
    double h, s, v;

    double Cmax = std::max(r / 255.0, g / 255.0);
    Cmax = std::max(Cmax, b / 255.0);

    double Cmin = std::min(r / 255.0, g / 255.0);
    Cmin = std::min(Cmin, b / 255.0);

    double delta = Cmax - Cmin;

    if (delta == 0)
    {
        h = 0;
    }
    else if (Cmax == r / 255.0)
    {
        h = 60 * fmod(((g / 255.0 - b / 255.0) / delta), 6);
    }
    else if (Cmax == g / 255.0)
    {
        h = 60 * (((b / 255.0 - r / 255.0) / delta) + 2);
    }
    else if (Cmax == b / 255.0)
    {
        h = 60 * (((r / 255.0 - g / 255.0) / delta) + 4);
    }

    while (h < 0)
    {
        h += 360;
    }

    v = Cmax;

    if (Cmax == 0)
    {
        s = 0;
    }
    else
    {
        s = delta / Cmax;
    }

    return new HSV(h, s * 100, v * 100);
}

ColorSystem *RGB::toHLS()
{
    double h, l, s;

    double Cmax = std::max(r / 255.0, g / 255.0);
    Cmax = std::max(Cmax, b / 255.0);

    double Cmin = std::min(r / 255.0, g / 255.0);
    Cmin = std::min(Cmin, b / 255.0);

    double delta = Cmax - Cmin;

    if (delta == 0)
    {
        h = 0;
    }
    else if (Cmax == r / 255.0)
    {
        h = 60 * fmod(((g / 255.0 - b / 255.0) / delta), 6);
    }
    else if (Cmax == g / 255.0)
    {
        h = 60 * (((b / 255.0 - r / 255.0) / delta) + 2);
    }
    else if (Cmax == b / 255.0)
    {
        h = 60 * (((r / 255.0 - g / 255.0) / delta) + 4);
    }

    while (h < 0)
    {
        h += 360;
    }

    l = (Cmax + Cmin) / 2;

    if (Cmax == 0)
    {
        s = 0;
    }
    else
    {
        s = delta / Cmax;
    }

    return new HSL(h, s * 100, l * 100);
}

ColorSystem *RGB::toXYZ()
{
    double x, y, z;

    x = 0.4124453 * fXyz(r / 255.0) * 100 + 0.357580 * fXyz(g / 255.0) * 100 + 0.180423 * fXyz(b / 255.0) * 100;
    y = 0.212671 * fXyz(r / 255.0) * 100 + 0.715160 * fXyz(g / 255.0) * 100 + 0.072169 * fXyz(b / 255.0) * 100;
    z = 0.019334 * fXyz(r / 255.0) * 100 + 0.119193 * fXyz(g / 255.0) * 100 + 0.950227 * fXyz(b / 255.0) * 100;

    return new XYZ(x, y, z);
}

ColorSystem *RGB::toLAB()
{
    double R = r / 255.0;
    double G = g / 255.0;
    double B = b / 255.0;

    R = (R <= 0.04045) ? (R / 12.92) : std::pow((R + 0.055) / 1.055, 2.4);
    G = (G <= 0.04045) ? (G / 12.92) : std::pow((G + 0.055) / 1.055, 2.4);
    B = (B <= 0.04045) ? (B / 12.92) : std::pow((B + 0.055) / 1.055, 2.4);

    double X = R * 0.4124564 + G * 0.3575761 + B * 0.1804375;
    double Y = R * 0.2126729 + G * 0.7151522 + B * 0.0721750;
    double Z = R * 0.0193339 + G * 0.1191920 + B * 0.9503041;

    X = X / 0.95047;
    Y = Y / 1.00000;
    Z = Z / 1.08883;

    double fx = (X > 0.008856) ? std::pow(X, 1.0 / 3.0) : ((903.3 * X) + 16.0) / 116.0;
    double fy = (Y > 0.008856) ? std::pow(Y, 1.0 / 3.0) : ((903.3 * Y) + 16.0) / 116.0;
    double fz = (Z > 0.008856) ? std::pow(Z, 1.0 / 3.0) : ((903.3 * Z) + 16.0) / 116.0;

    return new Lab((116.0 * fy) - 16.0, (fx - fy) * 500.0, (fy - fz) * 200.0);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

CMYK::CMYK(double C, double M, double Y, double K)
{
    if (C > 100 || C < 0 || M > 100 || M < 0 || Y > 100 || Y < 0 || K > 100 || K < 0)
    {
        throw "ERROR";
        c = 0;
        m = 0;
        y = 0;
        k = 0;
        return;
    }
    if (C > 100)
    {
        C = 100;
    }
    else if (C < 0)
    {
        C = 0;
    }
    if (M > 100)
    {
        M = 100;
    }
    else if (M < 0)
    {
        M = 0;
    }
    if (Y > 100)
    {
        Y = 100;
    }
    else if (Y < 0)
    {
        Y = 0;
    }
    if (K > 100)
    {
        K = 100;
    }
    else if (K < 0)
    {
        K = 0;
    }

    c = C;
    m = M;
    y = Y;
    k = K;
}

double CMYK::getParam1()
{
    return c;
}

double CMYK::getParam2()
{
    return m;
}

double CMYK::getParam3()
{
    return y;
}

double CMYK::getParam4()
{
    return k;
}

void CMYK::setParam1(double val)
{
    c = val;
}

void CMYK::setParam2(double val)
{
    m = val;
}

void CMYK::setParam3(double val)
{
    y = val;
}

void CMYK::setParam4(double val)
{
    k = val;
}

ColorSystem *CMYK::toRGB()
{
    return new RGB(255 * (1.0 - c / 100.0) * (1.0 - k / 100.0), 255 * (1.0 - m / 100.0) * (1.0 - k / 100.0), 255 * (1.0 - y / 100.0) * (1.0 - k / 100.0));
}

ColorSystem *CMYK::toCMYK()
{
    return new CMYK(c, m, y, k);
}

ColorSystem *CMYK::toHSV()
{
    return toRGB()->toHSV();
}

ColorSystem *CMYK::toHLS()
{
    return toRGB()->toHLS();
}

ColorSystem *CMYK::toXYZ()
{
    return toRGB()->toXYZ();
}

ColorSystem *CMYK::toLAB()
{
    return toRGB()->toLAB();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

HSV::HSV(double H, double S, double V)
{
    if (H > 360)
    {
        H = 360;
    }
    else if (H < 0)
    {
        H = 0;
    }
    if (S > 100)
    {
        S = 100;
    }
    else if (S < 0)
    {
        S = 0;
    }
    if (V > 100)
    {
        V = 100;
    }
    else if (V < 0)
    {
        V = 0;
    }

    h = H;
    s = S;
    v = V;
}

double HSV::getParam1()
{
    return h;
}

double HSV::getParam2()
{
    return s;
}

double HSV::getParam3()
{
    return v;
}

double HSV::getParam4()
{
    return 0.0;
}

void HSV::setParam1(double val)
{
    h = val;
}

void HSV::setParam2(double val)
{
    s = val;
}

void HSV::setParam3(double val)
{
    v = val;
}

void HSV::setParam4(double val)
{
}

ColorSystem *HSV::toRGB()
{
    double r, g, b;

    double c = v / 100 * s / 100;
    double x = c * (1. - abs((h - (int)(h / 120) * 120) / 60 - 1));
    double m = v / 100 - c;

    if (h >= 0 && h < 60)
    {
        r = (c + m) * 255;
        g = (x + m) * 255;
        b = m * 255;
    }
    else if (h >= 60 && h < 120)
    {
        r = (x + m) * 255;
        g = (c + m) * 255;
        b = m * 255;
    }
    else if (h >= 120 && h < 180)
    {
        r = (0 + m) * 255;
        g = (c + m) * 255;
        b = (x + m) * 255;
    }
    else if (h >= 180 && h < 240)
    {
        r = (0 + m) * 255;
        g = (x + m) * 255;
        b = (c + m) * 255;
    }
    else if (h >= 240 && h < 300)
    {
        r = (x + m) * 255;
        g = (0 + m) * 255;
        b = (c + m) * 255;
    }
    else if (h >= 300 && h < 360)
    {
        r = (c + m) * 255;
        g = (0 + m) * 255;
        b = (x + m) * 255;
    }

    return new RGB(r, g, b);
}

ColorSystem *HSV::toCMYK()
{
    return toRGB()->toCMYK();
}

ColorSystem *HSV::toHSV()
{
    return new HSV(h, s, v);
}

ColorSystem *HSV::toHLS()
{
    return toRGB()->toHLS();
}

ColorSystem *HSV::toXYZ()
{
    return toRGB()->toXYZ();
}

ColorSystem *HSV::toLAB()
{
    return toRGB()->toLAB();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

HSL::HSL(double H, double S, double L)
{
    if (H > 360)
    {
        H = 360;
    }
    else if (H < 0)
    {
        H = 0;
    }
    if (S > 100)
    {
        S = 100;
    }
    else if (S < 0)
    {
        S = 0;
    }
    if (L > 100)
    {
        L = 100;
    }
    else if (L < 0)
    {
        L = 0;
    }

    h = H;
    s = S;
    l = L;
}

double HSL::getParam1()
{
    return h;
}

double HSL::getParam2()
{
    return s;
}

double HSL::getParam3()
{
    return l;
}

double HSL::getParam4()
{
    return 0.0;
}

void HSL::setParam1(double val)
{
    h = val;
}

void HSL::setParam2(double val)
{
    s = val;
}

void HSL::setParam3(double val)
{
    l = val;
}

void HSL::setParam4(double val)
{
}

ColorSystem *HSL::toRGB()
{
    double r, g, b;

    double c = (1 - abs(2 * l / 100 - 1)) * s / 100;
    double x = c * (1. - abs((h - (int)(h / 120) * 120) / 60 - 1));
    double m = l / 100 - c / 2;

    if (h >= 0 && h < 60)
    {
        r = (c + m) * 255;
        g = (x + m) * 255;
        b = m * 255;
    }
    else if (h >= 60 && h < 120)
    {
        r = (x + m) * 255;
        g = (c + m) * 255;
        b = m * 255;
    }
    else if (h >= 120 && h < 180)
    {
        r = (0 + m) * 255;
        g = (c + m) * 255;
        b = (x + m) * 255;
    }
    else if (h >= 180 && h < 240)
    {
        r = (0 + m) * 255;
        g = (x + m) * 255;
        b = (c + m) * 255;
    }
    else if (h >= 240 && h < 300)
    {
        r = (x + m) * 255;
        g = (0 + m) * 255;
        b = (c + m) * 255;
    }
    else if (h >= 300 && h < 360)
    {
        r = (c + m) * 255;
        g = (0 + m) * 255;
        b = (x + m) * 255;
    }

    return new RGB(r, g, b);
}

ColorSystem *HSL::toCMYK()
{
    return toRGB()->toCMYK();
}

ColorSystem *HSL::toHSV()
{
    return toRGB()->toHSV();
}

ColorSystem *HSL::toHLS()
{
    return new HSL(h, s, l);
}

ColorSystem *HSL::toXYZ()
{
    return toRGB()->toXYZ();
}

ColorSystem *HSL::toLAB()
{
    return toRGB()->toLAB();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

XYZ::XYZ(double X = 0, double Y = 0, double Z = 0)
{
    if (X > 95.048)
    {
        X = 95.048;
    }
    else if (X < 0)
    {
        X = 0;
    }
    if (Y > 100.0)
    {
        Y = 100.0;
    }
    else if (Y < 0)
    {
        Y = 0;
    }
    if (Z > 108.884)
    {
        Z = 108.884;
    }
    else if (Z < 0)
    {
        Z = 0;
    }

    x = X;
    y = Y;
    z = Z;
}

double XYZ::getParam1()
{
    return x;
}

double XYZ::getParam2()
{
    return y;
}

double XYZ::getParam3()
{
    return z;
}

double XYZ::getParam4()
{
    return 0.0;
}

void XYZ::setParam1(double val)
{
    x = val;
}

void XYZ::setParam2(double val)
{
    y = val;
}

void XYZ::setParam3(double val)
{
    z = val;
}

void XYZ::setParam4(double val)
{
}

double XYZ::F(double val)
{
    if (val >= 0.0031308)
    {
        return 1.055 * pow(val, 1 / 2.4) - 0.055;
    }
    else
    {
        return 12.92 * val;
    }
}

ColorSystem *XYZ::toRGB()
{
    double Rn = 3.2406 * (x / 100) - 1.5372 * (y / 100) - 0.4986 * (z / 100);
    double Gn = -0.9689 * (x / 100) + 1.8758 * (y / 100) + 0.0415 * (z / 100);
    double Bn = 0.0557 * (x / 100) - 0.2040 * (y / 100) + 1.0570 * (z / 100);

    Rn = std::max(0.0, std::min(1.0, F(Rn))) * 255.0;
    Gn = std::max(0.0, std::min(1.0, F(Gn))) * 255.0;
    Bn = std::max(0.0, std::min(1.0, F(Bn))) * 255.0;

    return new RGB(Rn, Gn, Bn);
}

ColorSystem *XYZ::toCMYK()
{
    return toRGB()->toCMYK();
}

ColorSystem *XYZ::toHSV()
{
    return toRGB()->toHSV();
}

ColorSystem *XYZ::toHLS()
{
    return toRGB()->toHLS();
}

ColorSystem *XYZ::toXYZ()
{
    return new XYZ(x, y, z);
}

ColorSystem *XYZ::toLAB()
{
    const double Xn = 95.047;
    const double Yn = 100.000;
    const double Zn = 108.883;

    double fx = (x / Xn > 0.008856) ? std::pow(x / Xn, 1.0 / 3.0) : (903.3 * x / Xn + 16.0) / 116.0;
    double fy = (y / Yn > 0.008856) ? std::pow(y / Yn, 1.0 / 3.0) : (903.3 * y / Yn + 16.0) / 116.0;
    double fz = (z / Zn > 0.008856) ? std::pow(z / Zn, 1.0 / 3.0) : (903.3 * z / Zn + 16.0) / 116.0;

    return new Lab(116.0 * fy - 16.0, (fx - fy) * 500.0, (fy - fz) * 200.0);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Lab::Lab(double L_, double a_, double b_)
{
    if (L_ > 100)
    {
        L_ = 100;
    }
    else if (L_ < 0)
    {
        L_ = 0;
    }
    if (a_ > 128)
    {
        a_ = 128;
    }
    else if (a_ < -128)
    {
        a_ = -128;
    }
    if (b_ > 128)
    {
        b_ = 128;
    }
    else if (b_ < -128)
    {
        b_ = -128;
    }

    L = L_;
    a = a_;
    b = b_;
}

double Lab::getParam1()
{
    return L;
}

double Lab::getParam2()
{
    return a;
}

double Lab::getParam3()
{
    return b;
}

double Lab::getParam4()
{
    return 0.0;
}

void Lab::setParam1(double val)
{
    L = val;
}

void Lab::setParam2(double val)
{
    a = val;
}

void Lab::setParam3(double val)
{
    b = val;
}

void Lab::setParam4(double val)
{
}

double Lab::F(double x_)
{
    if (pow(x_, 3.) >= 0.008856)
    {
        return pow(x_, 3.);
    }

    return (x_ - 16. / 116) / 7.787;
}

ColorSystem *Lab::toRGB()
{
    return toXYZ()->toRGB();
}

ColorSystem *Lab::toCMYK()
{
    return toRGB()->toCMYK();
}

ColorSystem *Lab::toHSV()
{
    return toRGB()->toHSV();
}

ColorSystem *Lab::toHLS()
{
    return toRGB()->toHLS();
}

ColorSystem *Lab::toXYZ()
{
    const double Xw = 95.047;
    const double Yw = 100;
    const double Zw = 108.883;

    return new XYZ(F(a / 500 + (L + 16.) / 116.) * Xw,
                   F((L + 16.) / 116.) * Yw,
                   F((L + 16.) / 116. - b / 200.) * Zw);
}

ColorSystem *Lab::toLAB()
{
    return new Lab(L, a, b);
}
