//
//  Mqtt.h
//  RCTMqtt
//
//  Created by Tuan PM on 2/14/16.
//  Copyright © 2016 Tuan PM. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"
#import "RCTLog.h"
#import "RCTUtils.h"
#import "RCTEventDispatcher.h"

#import <MQTTClient/MQTTClient.h>
#import <MQTTClient/MQTTSessionManager.h>
#import <CocoaLumberjack/CocoaLumberjack.h>



@interface Mqtt : NSObject <MQTTSessionManagerDelegate>

- (Mqtt*) initWithBrigde:(RCTBridge *) bridge
                        options:(NSDictionary *) options
                      clientRef:(int) clientRef;
- (void) connect;
- (void) disconnect;
- (void) subscribe:(NSString *)topic qos:(NSNumber *)qos;
- (void) publish:(NSString *) topic data:(NSData *)data qos:(NSNumber *)qos retain:(BOOL) retain;
@end