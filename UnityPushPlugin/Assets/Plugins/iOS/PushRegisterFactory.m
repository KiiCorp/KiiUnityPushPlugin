//
//  PushRegisterFactory.m
//  KiiPushUnity
//
//  Copyright (c) 2014 Kii. All rights reserved.

#import "PushRegisterFactory.h"
#import "DefaultPush.h"

@implementation PushRegisterFactory

+ (id<PushRegister>) getPushRegistrator{

    id<PushRegister> registrator = nil;

    //change class name if you want to customize push registration
    Class clazz = NSClassFromString(@"DefaultPush");
    if(clazz && [clazz conformsToProtocol:@protocol(PushRegister)]){
        registrator = [[clazz alloc] init];
    }else{
        registrator = [[DefaultPush alloc]init];
    }

    return registrator;
}

@end