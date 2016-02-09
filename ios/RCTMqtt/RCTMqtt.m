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

@interface RCTMqtt : NSObject<RCTBridgeModule,  MQTTSessionManagerDelegate>

@end

@interface RCTMqtt ()

@property (strong, nonatomic) MQTTSessionManager *manager;
@property (nonatomic, strong) NSDictionary *defaultOptions;
@property (nonatomic, retain) NSMutableDictionary *options;
@property BOOL isConnect;
@end


@implementation RCTMqtt

@synthesize bridge = _bridge;



RCT_EXPORT_MODULE();


- (instancetype)init
{
    if ((self = [super init])) {
        [DDLog addLogger:[DDASLLogger sharedInstance]];
        [DDLog addLogger:[DDTTYLogger sharedInstance]];
        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
        
        
        self.isConnect = false;
        [defaultCenter addObserver:self
                          selector:@selector(appDidBecomeActive)
                              name:UIApplicationDidBecomeActiveNotification
                            object:nil];
        
        self.defaultOptions = @{
                                @"host": @"localhost",
                                @"port": @1883,
                                @"protcol": @"tcp", //ws
                                @"tls": @NO,
                                @"keepalive": @120, //second
                                @"clientId" : @"react-native-mqtt",
                                @"protocolLevel": @4,
                                @"clean": @YES,
                                @"auth": @NO,
                                @"user": @"",
                                @"pass": @"",
                                @"will": @NO,
                                @"willMsg": [NSNull null],
                                @"willtopic": @"",
                                @"willQos": @0,
                                @"willRetainFlag": @NO
                                };
       
    }
    return self;
    
}

- (void)appDidBecomeActive {
    if(self.isConnect) {
        [self.manager addObserver:self
                       forKeyPath:@"state"
                          options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                          context:nil];
    }
    
}

RCT_EXPORT_METHOD(connect:(NSDictionary *)options) {
    
    self.options = [NSMutableDictionary dictionaryWithDictionary:self.defaultOptions]; // Set default options
    for (NSString *key in options.keyEnumerator) { // Replace default options
        [self.options setValue:options[key] forKey:key];
    }
   
    [self mqttConnect];
  
    self.isConnect = true;
}


RCT_EXPORT_METHOD(disconnect) {
    [self.manager disconnect];
    [[NSRunLoop currentRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1.0]];
    self.isConnect = false;
}

RCT_EXPORT_METHOD(subscribe:(NSString *)topic qos:(NSNumber*)qos) {
    [self.manager setSubscriptions:[NSDictionary dictionaryWithObject:qos forKey:topic]];
    
}
//
RCT_EXPORT_METHOD(subscribe:(NSString *)topic) {
    [self.manager setSubscriptions:[NSDictionary dictionaryWithObject:[NSNumber numberWithInt:MQTTQosLevelAtMostOnce] forKey:topic]];
    
}
RCT_EXPORT_METHOD(publish:(NSString *)topic data:(NSString *)data) {
    [self.manager sendData:[data dataUsingEncoding:NSUTF8StringEncoding]
                     topic:data
                       qos:MQTTQosLevelAtMostOnce
                    retain:FALSE];
}

- (void)mqttConnect {
    
    if (!self.manager) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
           
            self.manager = [[MQTTSessionManager alloc] init];
            self.manager.delegate = self;
            
        
            MQTTSSLSecurityPolicy *securityPolicy = [MQTTSSLSecurityPolicy policyWithPinningMode:MQTTSSLPinningModeNone];
            securityPolicy.allowInvalidCertificates = YES;
            
            NSData *willMsg = nil;
            if(self.options[@"willMsg"] != [NSNull null]) {
                willMsg = [self.options[@"willMsg"] dataUsingEncoding:NSUTF8StringEncoding];
            }
            [self.manager connectTo:[self.options valueForKey:@"host"]
                               port:[self.options[@"port"] intValue]
                                tls:[self.options[@"tls"] boolValue]
                          keepalive:[self.options[@"keepalive"] intValue]
                              clean:[self.options[@"clean"] intValue]
                               auth:[self.options[@"auth"] boolValue]
                               user:[self.options valueForKey:@"user"]
                               pass:[self.options valueForKey:@"pass"]
                               will:[self.options[@"will"] boolValue]
                          willTopic:[self.options valueForKey:@"willTopic"]
                            willMsg:willMsg
                            willQos:(MQTTQosLevel)[self.options[@"willQos"] intValue]
                     willRetainFlag:[self.options[@"willRetainFlag"] boolValue]
                       withClientId:[self.options valueForKey:@"clientId"]
                     securityPolicy:securityPolicy
                       certificates:nil
             ];
            
            [self.manager addObserver:self
                           forKeyPath:@"state"
                              options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                              context:nil];
            
        }];
        

    } else {
        
        [self.manager connectToLast];
        [self.manager addObserver:self
                       forKeyPath:@"state"
                          options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                          context:nil];
        
    }
    
    
}
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    
    switch (self.manager.state) {
        case MQTTSessionManagerStateClosed:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"closed"
                                                        body:nil];
            @try{
                [[NSOperationQueue mainQueue] cancelAllOperations];
                [self.manager removeObserver:self forKeyPath:@"state"];
            }@catch(id anException){
                //do nothing, obviously it wasn't attached because an exception was thrown
            }
            break;
        case MQTTSessionManagerStateClosing:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"closing"
                                                        body:nil];
            break;
        case MQTTSessionManagerStateConnected:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"connected"
                                                        body:nil];
            break;
        case MQTTSessionManagerStateConnecting:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"connecting"
                                                        body:nil];
            break;
        case MQTTSessionManagerStateError:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"error"
                                                        body:nil];
            break;
        case MQTTSessionManagerStateStarting:
        default:
            break;
    }
}

/*
 * MQTTSessionManagerDelegate
 */
- (void)handleMessage:(NSData *)data onTopic:(NSString *)topic retained:(BOOL)retained {
    /*
     * MQTTClient: process received message
     */
    
    NSString *dataString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    RCTLogInfo(@" %@ : %@", topic, dataString);
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"message"
                                                    body:@{@"topic": topic,
                                                           @"data": data,
                                                           @"retain": @0
                                                           }];
    
}


- (void)dealloc
{
    [self disconnect];
    @try {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        [self.manager removeObserver:self forKeyPath:@"state"];
    }
    @catch (NSException *exception) {
        
    }
    
}

@end


