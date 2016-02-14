//
//  Mqtt.m
//  RCTMqtt
//
//  Created by Tuan PM on 2/13/16.
//  Copyright © 2016 Tuan PM. All rights reserved.
//

#import "Mqtt.h"

@interface Mqtt ()

@property (strong, nonatomic) MQTTSessionManager *manager;
@property (nonatomic, strong) NSDictionary *defaultOptions;
@property (nonatomic, retain) NSMutableDictionary *options;
@property BOOL isConnect;
@property int clientRef;
@property (nonatomic, strong) RCTBridge * bridge;

@end

@implementation Mqtt


- (id)init {
    if ((self = [super init])) {
        
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

- (instancetype) initWithBrigde:(RCTBridge *) bridge
                        options:(NSDictionary *) options
                      clientRef:(int) clientRef {
    self = [self init];
    self.bridge = bridge;
    self.clientRef = clientRef;
    self.options = [NSMutableDictionary dictionaryWithDictionary:self.defaultOptions]; // Set default options
    for (NSString *key in options.keyEnumerator) { // Replace default options
        [self.options setValue:options[key] forKey:key];
    }
    return self;
}

- (void) connect {
    if (!self.manager) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            
            self.manager = [[MQTTSessionManager alloc] init];
            self.manager.delegate = self;
            MQTTSSLSecurityPolicy *securityPolicy = nil;
            if(self.options[@"tls"]) {
                securityPolicy = [MQTTSSLSecurityPolicy policyWithPinningMode:MQTTSSLPinningModeNone];
                securityPolicy.allowInvalidCertificates = YES;
            }
            
            
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
        
    }
}


- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    
    switch (self.manager.state) {
        case MQTTSessionManagerStateClosed:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                            body:@{@"event": @"closed",
                                                                   @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                                   @"message": @"closed"
                                                                   }];

            break;
        case MQTTSessionManagerStateClosing:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                            body:@{@"event": @"closing",
                                                                   @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                                   @"message": @"closing"
                                                                   }];
            break;
        case MQTTSessionManagerStateConnected:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                            body:@{@"event": @"connect",
                                                                   @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                                   @"message": @"connected"
                                                                   }];
            break;
        case MQTTSessionManagerStateConnecting:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                            body:@{@"event": @"connecting",
                                                                   @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                                   @"message": @"connecting"
                                                                   }];
            break;
        case MQTTSessionManagerStateError:
            [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                            body:@{@"event": @"error",
                                                                   @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                                   @"message": @"error"
                                                                   }];
            break;
        case MQTTSessionManagerStateStarting:
        default:
            break;
    }
}

- (void) disconnect {
    [self.manager disconnect];
    [[NSRunLoop currentRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1.0]];
}

- (void) subscribe:(NSString *)topic qos:(NSNumber *)qos {
    
    [self.manager setSubscriptions:[NSDictionary dictionaryWithObject:qos forKey:topic]];
}

- (void) publish:(NSString *) topic data:(NSData *)data qos:(NSNumber *)qos retain:(BOOL) retain {
    [self.manager sendData:data
                     topic:topic
                       qos:(MQTTQosLevel)qos
                    retain:retain];
    //[data dataUsingEncoding:NSUTF8StringEncoding]
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
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"mqtt_events"
                                                    body:@{
                                                           @"event": @"message",
                                                           @"clientRef": [NSNumber numberWithInt:[self clientRef]],
                                                           @"message": @{
                                                                   @"topic": topic,
                                                                   @"data": dataString,
                                                                   @"retain": [NSNumber numberWithBool:retained]
                                                                   }
                                                           }];
    
}


- (void)dealloc
{
    [self disconnect];
    @try {
        
        @try{
            
            [[NSNotificationCenter defaultCenter] removeObserver:self];
            [self.manager removeObserver:self forKeyPath:@"state"];
            [[NSOperationQueue mainQueue] cancelAllOperations];
        }@catch(id anException){
            //do nothing, obviously it wasn't attached because an exception was thrown
        }
    }
    @catch (NSException *exception) {
        
    }
    
}



@end