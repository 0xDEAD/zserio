# Zserio C++ runtime library and tests.
#
# This CMake file defines a static library which contains the zserio C++ runtime and an executable which
# contains the zserio C++ runtime tests.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntimeTests)

enable_testing()

set(CPPCHECK_HOME "" CACHE PATH "Home directory of cppcheck tool. If empty, cppcheck tool is not called.")

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../..")
set(CMAKE_MODULE_PATH "${ZSERIO_PROJECT_ROOT}/cmake")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

# cmake helpers
include(cmake_utils)

# set gcc paramaters
include(gcc_utils)
gcc_set_warnings()
gcc_set_static_clibs()

add_subdirectory(src)
add_subdirectory(test)

# install Zserio C++ runtime library (we release sources only)
install(DIRECTORY src/ DESTINATION runtime_libs/cpp
        FILES_MATCHING
        PATTERN "3rdParty.txt"
        PATTERN "CMakeLists.txt"
        PATTERN "*.h"
        PATTERN "*.cpp")

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ZserioCppRuntime SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src
        SUPPRESSION_FILE ${CMAKE_CURRENT_SOURCE_DIR}/src/CppcheckSuppressions.txt)
