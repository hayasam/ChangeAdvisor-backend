
// Microsoft Visual Studio compiles this Windows native code into SystemUtilities.dll
// LimeWire uses these functions from the class com.limegroup.gnutella.util.SystemUtils

// Headers generated by Java for functions Java code can call
#ifndef _Included_com_limegroup_gnutella_util_SystemUtils_Registry
#define _Included_com_limegroup_gnutella_util_SystemUtils_Registry
#ifdef __cplusplus
extern "C" {
#endif

	// Functions in Registry.cpp
	JNIEXPORT jint JNICALL Java_org_limewire_util_SystemUtils_registryReadNumberNative(JNIEnv *e, jclass c, jstring root, jstring path, jstring name);
	JNIEXPORT jstring JNICALL Java_org_limewire_util_SystemUtils_registryReadTextNative(JNIEnv *e, jclass c, jstring root, jstring path, jstring name);
	JNIEXPORT jboolean JNICALL Java_org_limewire_util_SystemUtils_registryWriteNumberNative(JNIEnv *e, jclass c, jstring root, jstring path, jstring name, jint value);
	JNIEXPORT jboolean JNICALL Java_org_limewire_util_SystemUtils_registryWriteTextNative(JNIEnv *e, jclass c, jstring root, jstring path, jstring name, jstring value);
	JNIEXPORT jboolean JNICALL Java_org_limewire_util_SystemUtils_registryDeleteNative(JNIEnv *e, jclass c, jstring root, jstring path);

#ifdef __cplusplus
}
#endif
#endif

// Wraps a registry key, taking care of closing it
class CRegistry {
public:

	// The handle to the registry key
	HKEY Key;

	// Open a registry key and store its handle in this object
	bool Open(HKEY root, LPCTSTR path, bool write);
	void Close() { if (Key) RegCloseKey(Key); Key = NULL; }

	// Make a new local CRegistry object, and delete it when it goes out of scope
	CRegistry() { Key = NULL; }
	~CRegistry() { Close(); }
};

// Functions in Registry.cpp
int RegistryReadNumber(JNIEnv *e, HKEY root, LPCTSTR path, LPCTSTR name);
CString RegistryReadText(JNIEnv *e, HKEY root, LPCTSTR path, LPCTSTR name);
bool RegistryWriteNumber(HKEY root, LPCTSTR path, LPCTSTR name, int value);
bool RegistryWriteText(HKEY root, LPCTSTR path, LPCTSTR name, LPCTSTR value);
bool RegistryDelete(HKEY base, LPCTSTR path);
HKEY RegistryName(LPCTSTR name);
