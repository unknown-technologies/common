/* vim:set ts=8 sts=8 sw=8 tw=80 cc=80 noet: */
#include <windows.h>
#include <winreg.h>
#include <winerror.h>
#include "com_unknown_platform_windows_registry_RegistryKey.h"

static char* ioe_name = "java/io/IOException";
static char* iae_name = "java/lang/IllegalArgumentException";
static char* ioobe_name = "java/lang/IndexOutOfBoundsException";
static char* oome_name = "java/lang/OutOfMemoryError";
static char* npe_name = "java/lang/NullPointerException";
static char* winerror_name = "com/everyware/windows/WinError";

static void throw_winerror(JNIEnv* env, LSTATUS error)
{
	jclass clazz = (*env)->FindClass(env, winerror_name);
	jmethodID ctor = (*env)->GetMethodID(env, clazz, "<init>", "(I)V");
	jobject exception = (*env)->NewObject(env, clazz, ctor, (jint) error);
	(*env)->Throw(env, exception);
}

JNIEXPORT jlong JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_open
  (JNIEnv* env, jclass self, jlong hkey, jcharArray subkey, jint operation)
{
	HKEY key;

	if(!subkey) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "Invalid subkey");
		return 0;
	}

	jchar* subkey_chars = (*env)->GetCharArrayElements(env, subkey, NULL);
	if(!subkey_chars) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	LSTATUS result = RegOpenKeyExW((HKEY) hkey, (LPCWSTR) subkey_chars, 0, operation, &key);

	(*env)->ReleaseCharArrayElements(env, subkey, subkey_chars, JNI_ABORT);

	if(result != ERROR_SUCCESS) {
		throw_winerror(env, result);
	}

	return (jlong) key;
}

JNIEXPORT void JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_close
  (JNIEnv* env, jclass clazz, jlong key)
{
	LSTATUS result = RegCloseKey((HKEY) key);
	if(result != ERROR_SUCCESS) {
		throw_winerror(env, result);
	}
}

JNIEXPORT jbyteArray JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_query
  (JNIEnv* env, jclass self, jlong key, jcharArray value, jint type, jint size)
{
}

JNIEXPORT jobject JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_queryInfoKey
  (JNIEnv* env, jclass self, jlong key)
{
	jint subkeys;
	jint maxSubkeyLength;
	jint maxClassLength;
	jint values;
	jint maxValueNameLength;
	jint maxValueLength;
	jint securityDescriptor;
	FILETIME lastWriteTime;

	LSTATUS result = RegQueryInfoKeyW((HKEY) key, NULL, NULL, NULL, &subkeys,
			&maxSubkeyLength, &maxClassLength, &values,
			&maxValueNameLength, &maxValueLength,
			&securityDescriptor, &lastWriteTime);

	if(result != ERROR_SUCCESS) {
		throw_winerror(env, result);
		return 0;
	}

	jclass clazz = (*env)->FindClass(env, "com/everyware/windows/registry/RegistryKeyInfo");
	jmethodID ctor = (*env)->GetMethodID(env, clazz, "<init>", "(IIIIIIIII)V");
	return (*env)->NewObject(env, clazz, ctor, subkeys, maxSubkeyLength,
			maxClassLength, values, maxValueNameLength,
			maxValueLength, securityDescriptor,
			lastWriteTime.dwLowDateTime, lastWriteTime.dwHighDateTime);
}

JNIEXPORT jcharArray JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_enumKey
  (JNIEnv* env, jclass self, jlong key, jint index, jint namelen)
{
	DWORD len = 0;

	if(namelen == 0) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "Invalid name length");
		return 0;
	} else {
		len = namelen + 1;
	}

	jchar* name = (jchar*) malloc(len * sizeof(jchar));
	if(!name) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	LSTATUS result = RegEnumKeyExW((HKEY) key, index, (LPWSTR) name, &len, NULL, NULL, NULL, NULL);
	if(result != ERROR_SUCCESS) {
		free(name);
		throw_winerror(env, result);
		return 0;
	}

	jcharArray array = (*env)->NewCharArray(env, len);
	if(!array) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}
	(*env)->SetCharArrayRegion(env, array, 0, len, name);

	free(name);

	return array;
}

JNIEXPORT jobject JNICALL Java_com_unknown_platform_windows_registry_RegistryKey_enumValue
  (JNIEnv* env, jclass self, jlong key, jint index, jboolean data, jint valuenamelen, jint valuelen)
{
	DWORD namelen = 0;
	DWORD datalen = 0;
	DWORD type;

	if(valuenamelen == 0) {
		jclass clazz = (*env)->FindClass(env, npe_name);
		(*env)->ThrowNew(env, clazz, "Invalid name length");
		return 0;
	} else {
		namelen = valuenamelen + 1;
	}

	jchar* name = (jchar*) malloc(namelen * sizeof(jchar));
	if(!name) {
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}

	jbyteArray dataout = 0;
	if(data) {
		if(valuelen == 0) {
			LSTATUS result = RegEnumValueW((HKEY) key, index, (LPWSTR) name, &namelen, NULL, NULL, NULL, &datalen);
			if(result != ERROR_SUCCESS) {
				free(name);
				throw_winerror(env, result);
				return 0;
			}
			namelen = valuenamelen + 1;
		} else {
			datalen = valuelen;
		}

		jbyte* data = (jbyte*) malloc(datalen);
		if(!data) {
			free(name);
			jclass clazz = (*env)->FindClass(env, oome_name);
			(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
			return 0;
		}

		LSTATUS result = RegEnumValueW((HKEY) key, index, (LPWSTR) name, &namelen, NULL, &type, data, &datalen);
		if(result != ERROR_SUCCESS) {
			free(data);
			free(name);
			throw_winerror(env, result);
			return 0;
		}

		dataout = (*env)->NewByteArray(env, datalen);
		if(!dataout) {
			jclass clazz = (*env)->FindClass(env, oome_name);
			(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
			free(data);
			free(name);
			return 0;
		}
		(*env)->SetByteArrayRegion(env, dataout, 0, datalen, data);
		free(data);
	} else {
		LSTATUS result = RegEnumValueW((HKEY) key, index, (LPWSTR) name, &namelen, NULL, &type, NULL, NULL);
		if(result != ERROR_SUCCESS) {
			free(name);
			throw_winerror(env, result);
			return 0;
		}
	}

	jcharArray nameout = (*env)->NewCharArray(env, namelen);
	if(!nameout) {
		free(name);
		jclass clazz = (*env)->FindClass(env, oome_name);
		(*env)->ThrowNew(env, clazz, "cannot allocate buffer");
		return 0;
	}
	(*env)->SetCharArrayRegion(env, nameout, 0, namelen, name);

	free(name);

	jclass clazz = (*env)->FindClass(env, "com/everyware/windows/registry/RegistryValueInfo");
	jmethodID ctor = (*env)->GetMethodID(env, clazz, "<init>", "([CI[B)V");
	return (*env)->NewObject(env, clazz, ctor, nameout, type, dataout);
}
