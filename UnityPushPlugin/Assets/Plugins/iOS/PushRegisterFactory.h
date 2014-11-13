//
//  PushRegisterFactory.h
//  KiiPushUnity
//
//  Copyright (c) 2014 Kii. All rights reserved.
#import "CustomPushBehavior.h"

@interface PushRegisterFactory : NSObject
+ (id<CustomPushBehavior>) getPushRegistrator;
@end