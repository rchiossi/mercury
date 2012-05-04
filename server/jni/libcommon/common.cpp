#include <jni.h>  
#include <string.h>  
#include <android/log.h>  
#include <stdlib.h>
#include <stdio.h>
  
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "mercury-native", __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

#define CHUNK_SIZE 4096

char read_buffer[CHUNK_SIZE];
unsigned read_ptr;

void reset_buffer(FILE* file) {
	fread(read_buffer,1,CHUNK_SIZE,file);
	read_ptr = 0;
}

char read_char(FILE* file) {
	if (read_ptr == CHUNK_SIZE) {
		reset_buffer(file);
	}

	return read_buffer[read_ptr++];
}

/*
 * Function: Extract Strings from file
 * Parameters:  path: taget file
 * 				uri_list: string list to be filled
 * Return: Number of uris found
 */
int strings_file(const char* path, char** uri_list) {
	FILE* file = fopen(path,"r");

	if (file == NULL) {
		LOGE("Error openin file : %s",path);
		return 0;
	}

	reset_buffer(file);


	LOGE("%s",path);

	fclose(file);

	return 2;
}

/*
 * Class:     com_mwr_mercury_Common
 * Method:    native_getUri
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL 
Java_com_mwr_mercury_Common_native_1getUri (JNIEnv * env, jobject obj, jstring jpath)
{
	const char *path = env->GetStringUTFChars(jpath, 0);
	char** uri_list;
	int num;

	num = strings_file(path,uri_list);

	env->ReleaseStringUTFChars(jpath, path);

	/* REMOVE ME*/
	char* message[] = {"first",
		"second",
		"third",
		"fourth",
		"fifth"};
	uri_list = message;
	num = 5;
	/* ------- */

	jobjectArray ret = (jobjectArray)env->NewObjectArray(num, env->FindClass("java/lang/String"), env->NewStringUTF(""));

	for(int i=0;i<num;i++) {
		 env->SetObjectArrayElement(
				ret,i,env->NewStringUTF(uri_list[i]));
	}

	return ret;
}

#ifdef __cplusplus
}
#endif

