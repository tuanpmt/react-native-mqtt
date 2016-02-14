//
//  RCTMqtt.m
//  RCTMqtt
//
//  Created by Tuan PM on 2/2/16.
//  Copyright © 2016 Tuan PM. All rights reserved.
//

#import "RCTMqtt.h"
#import "RCTBridgeModule.h"
#import "RCTLog.h"
#import "RCTUtils.h"
#import "RCTEventDispatcher.h"

#import <MQTTClient/MQTTClient.h>
#import <MQTTClient/MQTTSessionManager.h>
#import <CocoaLumberjack/CocoaLumberjack.h>
#import "Mqtt.h"

@interface RCTMqtt : NSObject<RCTBridgeModule>

@end

@interface RCTMqtt ()
@property NSMutableDictionary *clients;
@end


@implementation RCTMqtt

@synthesize bridge = _bridge;


-(int)getRandomNumberBetween:(int)from to:(int)to {
    
    return (int)from + arc4random() % (to-from+1);
}

RCT_EXPORT_MODULE();


- (instancetype)init
{
    if ((self = [super init])) {
        [DDLog addLogger:[DDASLLogger sharedInstance]];
        [DDLog addLogger:[DDTTYLogger sharedInstance]];
        
        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
        
        _clients = [[NSMutableDictionary alloc] init];
        
        [defaultCenter addObserver:self
                          selector:@selector(appDidBecomeActive)
                              name:UIApplicationDidBecomeActiveNotification
                            object:nil];
        
        
        
    }
    return self;
    
}

- (void)appDidBecomeActive {
    //    if(self.isConnect) {
    //        [self.manager addObserver:self
    //                       forKeyPath:@"state"
    //                          options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
    //                          context:nil];
    //    }
    
}

RCT_EXPORT_METHOD(createClient:(NSDictionary *) options
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    
    int clientRef = [self getRandomNumberBetween:1000 to:9999];
    
    Mqtt *client = [[Mqtt alloc] initWithBrigde:[self bridge]
                                        options:options
                                      clientRef:clientRef];
    
    [[self clients] setObject:client forKey:[NSNumber numberWithInt:clientRef]];
    resolve([NSNumber numberWithInt:clientRef]);
    
}
RCT_EXPORT_METHOD(connect:(int) clientRef) {
    
    [[[self clients] objectForKey:[NSNumber numberWithInt:clientRef]] connect];
    
}


RCT_EXPORT_METHOD(disconnect:(int) clientRef) {
    [[[self clients] objectForKey:[NSNumber numberWithInt:clientRef]] disconnect];
}

RCT_EXPORT_METHOD(subscribe:(int) clientRef topic:(NSString *)topic qos:(int)qos) {
    [[[self clients] objectForKey:[NSNumber numberWithInt:clientRef]] subscribe:topic qos:[NSNumber numberWithInt:qos]];
    
}

RCT_EXPORT_METHOD(publish:(int) clientRef topic:(NSString *)topic data:(NSString*)data qos:(int)qos retain:(int)retain) {
    [[[self clients] objectForKey:[NSNumber numberWithInt:clientRef]] publish:topic
                                                                         data:[data dataUsingEncoding:NSUTF8StringEncoding]
                                                                          qos:[NSNumber numberWithInt:qos]
                                                                       retain:(BOOL)retain];

}

- (void)dealloc
{
    
    
}

@end


