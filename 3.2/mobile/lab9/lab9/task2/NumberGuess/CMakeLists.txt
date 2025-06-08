cmake_minimum_required(VERSION 3.16)

project(NumberGuess VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 REQUIRED COMPONENTS Quick)
find_package(Qt6 REQUIRED COMPONENTS LinguistTools)

qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(appNumberGuess
    main.cpp
)

qt_add_translations(appNumberGuess
    TS_FILES
        NumberGuess_be_BY.ts
        NumberGuess_ru_RU.ts
        NumberGuess_en_US.ts
    RESOURCE_PREFIX "/i18n"
    OUTPUT_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/translations"
)

qt_add_qml_module(appNumberGuess
    URI GuessMaster
    VERSION 1.0
    QML_FILES
        AppRoot.qml
        AuthScreen.qml
        PlayScreen.qml
    SOURCES GameManager.h GameManager.cpp
)

enable_testing()

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(appNumberGuess PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.example.appNumberGuess
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_link_libraries(appNumberGuess
    PRIVATE Qt6::Quick
)

include(GNUInstallDirs)
install(TARGETS appNumberGuess
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
