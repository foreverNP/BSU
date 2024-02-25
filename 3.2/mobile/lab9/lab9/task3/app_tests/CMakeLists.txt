cmake_minimum_required(VERSION 3.16)

project(TestDatabase LANGUAGES CXX)

enable_testing()

find_package(QT NAMES Qt6 Qt5 REQUIRED COMPONENTS Test Sql Core)
find_package(Qt${QT_VERSION_MAJOR} REQUIRED COMPONENTS Test Sql Core)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(
    ${CMAKE_SOURCE_DIR}/../app
)

set(database_sources
    ../app/data/database.cpp
    ../app/data/database.h
)

set(logic_sources
    ../app/logic/recordsrepository.cpp
    ../app/logic/recordsrepository.h
)

set(core_sources
    ../app/core/appmanager.cpp
    ../app/core/appmanager.h
)

add_executable(TestDatabase
    tst_testdatabase.cpp
    ${database_sources}
    ${logic_sources}
    ${core_sources}
)
add_test(NAME TestDatabase COMMAND TestDatabase)

target_link_libraries(TestDatabase PRIVATE
    Qt${QT_VERSION_MAJOR}::Test
    Qt${QT_VERSION_MAJOR}::Sql
    Qt${QT_VERSION_MAJOR}::Core
)

