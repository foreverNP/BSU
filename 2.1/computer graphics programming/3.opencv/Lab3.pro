QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++17

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    main.cpp \
    mainwindow.cpp \
    statustipfilter.cpp

HEADERS += \
    mainwindow.h \
    statustipfilter.h

FORMS += \
    mainwindow.ui

INCLUDEPATH += C:\Users\Lev\Desktop\programming\QtPKGLabs\opencv\QTopencv\install\include
LIBS += C:\Users\Lev\Desktop\programming\QtPKGLabs\opencv\QTopencv\bin\libopencv_core460.dll
LIBS += C:\Users\Lev\Desktop\programming\QtPKGLabs\opencv\QTopencv\bin\libopencv_imgcodecs460.dll
LIBS += C:\Users\Lev\Desktop\programming\QtPKGLabs\opencv\QTopencv\bin\libopencv_imgproc460.dll

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

RESOURCES += \
    rsc.qrc
