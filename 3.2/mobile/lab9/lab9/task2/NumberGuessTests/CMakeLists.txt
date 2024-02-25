cmake_minimum_required(VERSION 3.16)

project(ControllerTest LANGUAGES CXX)

enable_testing()

find_package(QT NAMES Qt6 Qt5 REQUIRED COMPONENTS Test)
find_package(Qt${QT_VERSION_MAJOR} REQUIRED COMPONENTS Test)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)



add_executable(ControllerTest
    tst_controllertest.cpp
    ${controller_sources}
)
add_test(NAME ControllerTest COMMAND ControllerTest)

target_link_libraries(ControllerTest PRIVATE Qt${QT_VERSION_MAJOR}::Test)

qt_add_test(tst_gamemanagertest
    SOURCES
        tst_gamemanagertest.cpp
)
target_link_libraries(tst_gamemanagertest PRIVATE Qt6::Test)
