UltraCam Android project

This project wraps the supplied UltraCam HTML app in an Android WebView.
Minimum Android API: 16 (Android 4.1).
Target SDK: 28.

Important compatibility note:
The HTML app uses modern JavaScript and Supabase JavaScript v2. Android 4.1's
built-in WebView is too old for several required JavaScript features/APIs.
The APK may install on Android 4.1 but the full app (especially login,
Supabase requests, uploads and realtime features) is not guaranteed to work
on that version. A newer Android System WebView/browser engine is required
for reliable operation; Android 5.0+ is a more realistic minimum, and newer
is recommended. This project has not yet been compiled or device-tested.

Build in GitHub Actions:
1. Upload all files/folders in this project to the root of a GitHub repository.
2. Open the repository's Actions tab.
3. Select "Build UltraCam APK" and click "Run workflow" (or push to main).
4. When complete, open the workflow run and download the
   "UltraCam-debug-apk" artifact. Extract app-debug.apk.
