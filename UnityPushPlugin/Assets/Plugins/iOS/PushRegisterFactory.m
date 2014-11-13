//
//  PushRegisterFactory.m
//  KiiPushUnity
//
//  Copyright (c) 2014 Kii. All rights reserved.

#import "PushRegisterFactory.h"
#import "DefaultPushBehavior.h"

@implementation PushRegisterFactory

+ (id<CustomPushBehavior>) getPushRegistrator{

    id<CustomPushBehavior> registrator = nil;

    //change class name if you want to customize push registration
    Class clazz = NSClassFromString(@"DefaultPush");
    if(clazz && [clazz conformsToProtocol:@protocol(CustomPushBehavior)]){
        registrator = [[clazz alloc] init];
    }else{
        registrator = [[DefaultPushBehavior alloc]init];
    }

    return registrator;
}

@end