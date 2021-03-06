# Zserio C++ runtime library test.
#
# This CMake file defines an executable which contains the zserio C++ runtime tests.
#
# This CMake file is NOT designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntimeTest)

# add gtest library
include(gtest_utils)
gtest_add_library("${CMAKE_CURRENT_SOURCE_DIR}/../../../../../3rdparty/cpp/googletest")

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/inspector/BlobInspectorTreeTest.cpp
    zserio/inspector/BlobInspectorTreeUtilTest.cpp
    zserio/inspector/BlobInspectorValueTest.cpp
    zserio/AlignedStorageTest.cpp
    zserio/AnyHolderTest.cpp
    zserio/BasicArrayTest.cpp
    zserio/BitFieldArrayTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BuildInOperatorsTest.cpp
    zserio/ContainerTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/ObjectArrayTest.cpp
    zserio/OptionalHolderTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/StringHolderTest.cpp
    zserio/VarUInt64UtilTest.cpp
)

if (ZSERIO_RUNTIME_INCLUDE_RELATIONAL)
    set(ZSERIO_CPP_RUNTIME_TEST_SRCS
        ${ZSERIO_CPP_RUNTIME_TEST_SRCS}
        zserio/SqlDatabaseTest.cpp
    )
endif()

add_executable(${PROJECT_NAME} ${ZSERIO_CPP_RUNTIME_TEST_SRCS})
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main)

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ${PROJECT_NAME} SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
