#ifndef COLORS_H
#define COLORS_H

#include <cmath>
#include "QColor"

class ColorSystem
{
public:
    virtual double getParam1() = 0;
    virtual double getParam2() = 0;
    virtual double getParam3() = 0;
    virtual double getParam4() = 0;

    virtual void setParam1(double val) = 0;
    virtual void setParam2(double val) = 0;
    virtual void setParam3(double val) = 0;
    virtual void setParam4(double val) = 0;

    virtual ColorSystem *toRGB() = 0;
    virtual ColorSystem *toCMYK() = 0;
    virtual ColorSystem *toHSV() = 0;
    virtual ColorSystem *toHLS() = 0;
    virtual ColorSystem *toXYZ() = 0;
    virtual ColorSystem *toLAB() = 0;
};

///////////////////////////////////////////////////////////////////////////////////

class RGB : public ColorSystem
{
private:
    int r;
    int g;
    int b;

    double fXyz(double x);
    double fDop(double x);

public:
    RGB(int R, int G, int B);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

///////////////////////////////////////////////////////////////////////////////////

class CMYK : public ColorSystem
{
private:
    double c;
    double m;
    double y;
    double k;

public:
    CMYK(double C, double M, double Y, double K);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

///////////////////////////////////////////////////////////////////////////////////

class HSV : public ColorSystem
{
private:
    double h;
    double s;
    double v;

public:
    HSV(double H, double S, double V);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

///////////////////////////////////////////////////////////////////////////////////

class HSL : public ColorSystem
{
private:
    double h;
    double s;
    double l;

public:
    HSL(double H, double S, double L);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

///////////////////////////////////////////////////////////////////////////////////

class XYZ : public ColorSystem
{
private:
    double x;
    double y;
    double z;

    double F(double x_);

public:
    XYZ(double X, double Y, double Z);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

///////////////////////////////////////////////////////////////////////////////////

class Lab : public ColorSystem
{
private:
    double L;
    double a;
    double b;

    double F(double x_);

public:
    Lab(double L_, double a_, double b_);

    double getParam1() override;
    double getParam2() override;
    double getParam3() override;
    double getParam4() override;

    void setParam1(double val) override;
    void setParam2(double val) override;
    void setParam3(double val) override;
    void setParam4(double val) override;

    ColorSystem *toRGB() override;
    ColorSystem *toCMYK() override;
    ColorSystem *toHSV() override;
    ColorSystem *toHLS() override;
    ColorSystem *toXYZ() override;
    ColorSystem *toLAB() override;
};

#endif
