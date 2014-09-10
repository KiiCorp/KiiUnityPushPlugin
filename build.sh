#!/bin/bash

current_dir=$(cd $(dirname $0);pwd)

if [ ! -e /Applications/Unity/Unity.app ]; then
	echo "You need to install Unity.app on your Mac"
	exit 1
fi

# Build AndroidPushPlugin
cd AndroidPushPlugin
android update project --path . --target android-19
ant clean release
cd ../

# Place AndroidPushPlugin to UnityPushPlugin
cp AndroidPushPlugin/bin/classes.jar UnityPushPlugin/Assets/Plugins/Android/androidpushplugin.jar

sed -i '' -e 's/com.kii.unity.sample.push/YOUR_APPLICATION_PACKAGE_NAME/g' ./UnityPushPlugin/Assets/Plugins/Android/AndroidManifest.xml

rm -rf bin
mkdir bin

/Applications/Unity/Unity.app/Contents/MacOS/Unity \
    -batchmode -quit \
    -buildTarget android \
    -projectPath "$current_dir"/UnityPushPlugin \
    -exportPackage Assets/Plugins \
                   Assets/Plugins/Android \
                   Assets/Plugins/iOS \
                   ../bin/KiiPushPlugin.unitypackage

sed -i '' -e 's/YOUR_APPLICATION_PACKAGE_NAME/com.kii.unity.sample.push/g' ./UnityPushPlugin/Assets/Plugins/Android/AndroidManifest.xml

