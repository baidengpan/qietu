cmake_minimum_required(VERSION 3.6)

MESSAGE("======${ANDROID_ABI}")

set(jniLibs "${CMAKE_CURRENT_SOURCE_DIR}/libs")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${jniLibs}/${ANDROID_ABI})
set(libDir "${jniLibs}/${ANDROID_ABI}")

message("======${libDir}")

add_library(videoprocessor SHARED
        src/main/cpp/VideoProcessor.cpp
        )

target_link_libraries(videoprocessor
        ${libDir}/libavcodec.so
        ${libDir}/libavfilter.so
        ${libDir}/libavformat.so
        ${libDir}/libavutil.so
        ${libDir}/libMNN.so
        ${libDir}/libopencv_3rd.a
        ${libDir}/libopencv_world.a
        ${libDir}/libpostproc.so
        ${libDir}/libswresample.so
        ${libDir}/libswscale.so
        ${libDir}/libvak.so
        ${libDir}/libx264.a
        ${libDir}/libZipper.so
        android
        log
        )