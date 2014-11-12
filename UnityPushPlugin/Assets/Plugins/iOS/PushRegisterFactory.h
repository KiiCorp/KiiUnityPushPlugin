//
//  PushRegisterFactory.h
//  KiiPushUnity
//
//  Copyright (c) 2014 Kii. All rights reserved.
#import "PushRegister.h"

@interface PushRegisterFactory : NSObject
+ (id<PushRegister>) getPushRegistrator;
@end