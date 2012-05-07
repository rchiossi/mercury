#include <jni.h>  
#include <string.h>  
#include <android/log.h>  
#include <stdlib.h>
#include <stdio.h>
  
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "mercury-native", __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

#define CHUNK_SIZE 16348
#define MAX_STR_SIZE 4096
#define MIN_STR_SIZE 4
#define LIST_START_SIZE 4096

char read_buffer[CHUNK_SIZE];
unsigned bytes_in_buffer;
unsigned read_ptr;
unsigned eof;

char* uri_list = NULL;

void reset_buffer(FILE* file) {
	bytes_in_buffer = fread(read_buffer,1,CHUNK_SIZE,file);
	read_ptr = 0;
	eof = 0;
}

char read_char(FILE* file) {
	if (bytes_in_buffer != CHUNK_SIZE && read_ptr >= bytes_in_buffer) {
		eof = 1;
		return 0xff;
	}

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
int strings_file(const char* path) {
	FILE* file = fopen(path,"r");

	unsigned msize = LIST_START_SIZE;
	unsigned csize = 0;

	char buffer[MAX_STR_SIZE];
	char* next_str = uri_list;
	unsigned char c_byte;
	unsigned length;

	unsigned num_strings = 0;

	if (file == NULL) {
		LOGE("Error openin file : %s",path);
		return 0;
	}

	uri_list = (char*) malloc(sizeof(char)*msize);
	memset(uri_list,0x0,msize);

	reset_buffer(file);

	c_byte = read_char(file);
	length = 0;
	while (!eof) {
		/* check if character is printable */
		while (0x20 <= (unsigned) c_byte && (unsigned) c_byte <= 0x7E && length < MAX_STR_SIZE - 1 && !eof) {
			buffer[length++] = c_byte;
			c_byte = read_char(file);
		}

		if (length >= MIN_STR_SIZE) {
			/* grow list */
			if (csize+length >= msize) {
				uri_list = (char*) realloc(uri_list, sizeof(char)*msize*2);
				if (uri_list == NULL) {
					LOGE("Error: out of memory!");
					return -1;
				}
				msize *= 2;
			}

			next_str = &uri_list[csize];
			strncpy(next_str,buffer,length);
			next_str[length] = '\n';

			csize+=length+1;

			num_strings++;
		}

		length = 0;

		c_byte = read_char(file);
	}

	fclose(file);

	return num_strings;
}

/*
 * Class:     com_mwr_mercury_Common
 * Method:    native_strings
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_mwr_mercury_Common_native_1strings
  (JNIEnv *env, jclass obj, jstring jpath)
{
	const char *path = env->GetStringUTFChars(jpath, 0);
	int num;

	num = strings_file(path);

	env->ReleaseStringUTFChars(jpath, path);
	jstring ret = env->NewStringUTF(uri_list);

	free(uri_list);

	return ret;
}

#ifdef __cplusplus
}
#endif

