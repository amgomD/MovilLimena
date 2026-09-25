1. In Project Properties->Java Build Path->Libraries add/modify External Library sdt-brc-android.jar. The path should point to [SD-TOOLKIT Barcode Reader SDK for Android ROOT]/sdt-brc-android.jar file. Ensure that sdt-brc-android.jar is checked in Java Build Path -> Order and Export tab;
2. In the Project root folder create folder libs/armeabi and copy libjsdtbarcode-arm.so to it from [SD-TOOLKIT Barcode Reader SDK for Android ROOT]/libs/armeabi folder;
3. modify AndroidManifest.xml by adding row <uses-permission android:name="android.permission.CAMERA" /> before 'application' tag
4. Refresh the Project;
5. Clean the Project;
