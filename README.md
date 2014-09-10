# KiiUnityPushPlugin
This project is native plugin to receive push notification for the Unity3D.  
By adding code to this project, you can customize the behavior when it receives a push notification. 

## Overview about the push notification mechanism

      +--------------------------+
      |                          |
      |  Google Cloud Messaging  |
      |                          |
      +--------------------------+
                  |
                  | 1. Push Notification from Google server
                  |
    +-------------+-----------------+---------------------------------------------------------------+
    |             |                 |                                                               |
    |             V                 |                                                               |
    | +--------------------------+  |                        +------------------+                   |
    | |                          |  | 2. UnitySendMessage()  |                  |                   |
    | |  androidpushplugin.jar   |--+----------+-----------> | KiiPushPlugin.cs |                   |
    | |                          |  |          |             |                  |                   |
    | +--------------------------+  |          |             +------------------+                   |
    |                               |          |                      |                             |
    |                               |          |                      |                             |
    |                               |          |                      | 3. OnPushMessageReceived()  |
    |                               |          |                      |                             |
    |                               |          |                      V                             |
    | +--------------------------+  |          |             +------------------+                   |             
    | |                          |  |          |             |                  |                   |
    | | UIApplication+KiiCloud.m |--+----------+             | YourUnityCode.cs |                   |
    | |                          |  |                        |                  |                   |
    | +--------------------------+  |                        +------------------+                   |
    |            4                  |                                                               |
    |            |                  |                                                               |
    +-----[Native|Plugin Layer]-----+-----------------------[Unity Layer]---------------------------+
                 |
                 | 1. Push Notification from Apple server
                 |
      +------------------------+
      |                        |
      |          APNS          |
      |                        |
      +------------------------+

## Build

### Requirements
1. [AndroidSDK](http://developer.android.com/sdk/index.html)
1. [Apache Ant](http://ant.apache.org/)
1. [Unity Pro](http://unity3d.com/)

### How to build
1. `$ sh build.sh`
1. KiiPushPlugin.unitypackage will be generated in bin directory.