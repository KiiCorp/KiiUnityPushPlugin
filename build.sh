#!/bin/bash

current_dir=$(cd $(dirname $0);pwd)

if [ ! -e /Applications/Unity/Unity.app ]; then
    echo "You need to install Unity.app on your Mac"
    exit 1
fi

# Build AndroidPushPlugin
cd AndroidPushPlugin
android update project --path . --target android-19
./gradlew clean copyJar
cd ../


# Copy the plugin project to working directory
rm -rf UnityPushPluginWK
cp -r UnityPushPlugin UnityPushPluginWK

unity_version=$1
if [ -z $unity_version ]; then
    unity_version="unity4"
elif [ ${unity_version} != "unity4" -a ${unity_version} != "unity5" ]; then
    unity_version="unity4"
fi

echo "##### Building unity plugin for ${unity_version} ..."

if [ ${unity_version} = "unity4" ]; then
    cp AndroidPushPlugin/app/build/outputs/jar/release/classes.jar UnityPushPluginWK/Assets/Plugins/Android/androidpushplugin.jar
elif [ ${unity_version} = "unity5" ]; then
    rm -rf UnityPushPluginWK/Assets/Plugins/Android/androidpushplugin.jar
    rm -rf UnityPushPluginWK/Assets/Plugins/Android/res/values
    cp AndroidPushPlugin/app/build/outputs/aar/app-release.aar UnityPushPluginWK/Assets/Plugins/Android/androidpushplugin.aar
fi


# Place AndroidPushPlugin to UnityPushPlugin

sed -i '' -e 's/com.kii.unity.sample.push/com.example.your.application.package.name/g' ./UnityPushPluginWK/Assets/Plugins/Android/AndroidManifest.xml

rm -rf bin
mkdir bin

/Applications/Unity/Unity.app/Contents/MacOS/Unity \
    -batchmode -quit \
    -buildTarget android \
    -projectPath "$current_dir"/UnityPushPluginWK \
    -exportPackage Assets/Plugins \
                   Assets/Plugins/Android \
                   Assets/Plugins/iOS \
                   ../bin/KiiPushPlugin.unitypackage

rm -rf UnityPushPluginWK

