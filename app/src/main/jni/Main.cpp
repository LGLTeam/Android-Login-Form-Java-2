#include <sys/types.h>
#include <pthread.h>
#include <jni.h>
#include <unistd.h>
#include "Logger.h"

bool loggedin = false;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_loginform2_MainActivity_LoginCheck(JNIEnv *env, jobject thiz) {
    loggedin = true;
}

//Simple security check
void *new_thread(void *) {
    do {
        sleep(1);
    } while (!loggedin);

    LOGD("Logged in!");

    return NULL;
}

__attribute__((constructor))
void lib_main() {
    // Create a new thread so it does not block the main thread, means the app would not freeze
    pthread_t ptid;
    pthread_create(&ptid, NULL, new_thread, NULL);
}

