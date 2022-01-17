#include <jni.h>
#include <android/log.h>
#include "video_processor_interface.h"
#include "string"

#define TAG "VideoProcessor"
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_video_processor_library_JniInterface_nInit(JNIEnv *env, jobject thiz) {
    auto videoProcessorInterface = vak::VideoProcessorInterface::getInstance();
    return reinterpret_cast<jlong>(videoProcessorInterface);
}

JNIEXPORT jint JNICALL
Java_com_video_processor_library_JniInterface_nOpen(JNIEnv *env, jobject thiz, jlong handle,
                                                   jstring jmodel_path, jstring jinput_video_path,
                                                   jstring joutput_path, jstring juuid,
                                                   jint output_width, jint output_height,
                                                   jstring jidx, jint layout_type,
                                                   jstring jsource) {
    auto videoProcessorInterface = (vak::VideoProcessorInterface *) handle;
    std::string model_path = env->GetStringUTFChars(jmodel_path, nullptr);
    std::string input_video_path = env->GetStringUTFChars(jinput_video_path, nullptr);
    std::string output_path = env->GetStringUTFChars(joutput_path, nullptr);
    std::string uuid = env->GetStringUTFChars(juuid, nullptr);
    std::string idx = env->GetStringUTFChars(jidx, nullptr);
    std::string source = env->GetStringUTFChars(jsource, nullptr);
    vak::Status status = videoProcessorInterface->open(model_path, input_video_path, output_path,
                                                       uuid, output_width, output_height, idx,
                                                       layout_type, source);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "open success");
    return status;
}

jmethodID gProgressMethod;
JNIEnv *gJNIEnv;
jobject gProgressCallback;
JNIEXPORT jint JNICALL
Java_com_video_processor_library_JniInterface_nProcess(JNIEnv *env, jobject thiz, jlong handle,
                                                      jobject progress_callback) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "start to process");
    auto videoProcessorInterface = (vak::VideoProcessorInterface *) handle;
    jclass callbackClass = env->GetObjectClass(progress_callback);
    jmethodID progressMethod = env->GetMethodID(callbackClass, "progress", "(IF)V");
    gProgressMethod = progressMethod;
    gJNIEnv = env;
    gProgressCallback = env->NewWeakGlobalRef(progress_callback);
    vak::Status status = videoProcessorInterface->process([](int frame, float progress) {
        gJNIEnv->CallVoidMethod(gProgressCallback, gProgressMethod, frame, progress);
    });
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "finish process");
    return status;
}

JNIEXPORT void JNICALL
Java_com_video_processor_library_JniInterface_nClose(JNIEnv *env, jobject thiz, jlong handle) {
    if (handle != 0) {
        auto instance = (vak::VideoProcessorInterface *) handle;
        instance->close();
        delete instance;
        __android_log_print(ANDROID_LOG_DEBUG, TAG, "finish close!");
    }
    gProgressMethod = nullptr;
    gJNIEnv = nullptr;
    env->DeleteWeakGlobalRef(gProgressCallback);
}

}