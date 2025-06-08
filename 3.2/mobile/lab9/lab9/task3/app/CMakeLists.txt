cmake_minimum_required(VERSION 3.16)

project(DbApp VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

find_package(Qt6 REQUIRED COMPONENTS Core Quick Sql LinguistTools)
find_package(Qt6 REQUIRED COMPONENTS LinguistTools)

qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(appDbApp
    main.cpp
    core/appmanager.h core/appmanager.cpp
    logic/recordsrepository.h logic/recordsrepository.cpp
    data/database.h data/database.cpp
)

qt_add_translations(appDbApp
    TS_FILES
        DbApp_ru_RU.ts
        DbApp_be_BY.ts
        DbApp_en_US.ts
    RESOURCE_PREFIX "/i18n"
    OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/translations"
)

qt_add_qml_module(appDbApp
    URI DbApp
    VERSION 1.0
    QML_FILES
        ui/AppRoot.qml
        ui/MainScreen.qml
        ui/RecordsScreen.qml
        ui/AggregateScreen.qml
        ui/FilterScreen.qml
        ui/GroupScreen.qml
        ui/SortScreen.qml
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(appDbApp PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.appDbApp
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_link_libraries(appDbApp
    PRIVATE Qt6::Core
    Qt6::Quick
    Qt6::Sql
)

include(GNUInstallDirs)
install(TARGETS appDbApp
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
