

## Description 

[react-native](https://github.com/facebook/react-native) mqtt client module

## MQTT Features (inherit from native MQTT framework)
* Use [MQTT Framework](https://github.com/ckrey/MQTT-Client-Framework) for IOS, [Paho MQTT Client](https://eclipse.org/paho/clients/android/) for Android
* Support both IOS and Android
* SSL/TLS 
* Native library, support mqtt over tcp 

## Warning
This library in progress developing, api may change, SSL/TLS non verify 

## Getting started

### Manual install
#### iOS
- `npm install react-native-mqtt@latest --save`
-  In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
- Go to `node_modules` ➜ `react-native-mqtt` and add `RCTMqtt.xcodeproj`
- In XCode, in the project navigator, select your project. Add `libRCTmqtt.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
- Click `RCTMqtt.xcodeproj` in the project navigator and go the `Build Settings` tab. Make sure 'All' is toggled on (instead of 'Basic'). In the `Search Paths` section, look for `Header Search Paths` and make sure it contains both `$(SRCROOT)/../../react-native/React` - mark  as `recursive`.
- Run your project (`Cmd+R`)


#### Android

-  `npm install react-native-mqtt@latest --save`
-   Modify the ReactInstanceManager.builder() calls chain in `android/app/main/java/.../MainActivity.java` to include:

```javascript
import com.tuanpm.RCTMqtt.*; // import


.addPackage(new RCTMqttPackage()) //for older version

new RCTMqttPackage()           // for newest version of react-native
```

-  Append the following lines to `android/settings.gradle` before `include ':app'`:

```
include ':react-native-mqtt'
project(':react-native-mqtt').projectDir = new File(rootProject.projectDir,  '../node_modules/react-native-mqtt/android')

```


- Insert the following lines inside the dependencies block in `android/app/build.gradle`, don't missing `apply plugin:'java'` on top:

```
compile project(':react-native-mqtt')
```

Notes:

```
dependencies {
  compile project(':react-native-mqtt')
}
```


## Usage

```javascript
var mqtt    = require('react-native-mqtt');

/* create mqtt client */
mqtt.createClient({
  uri: 'mqtt://test.mosquitto.org:1883', 
  clientId: 'your_client_id'
}).then(function(client) {

  client.on('closed', function() {
    console.log('mqtt.event.closed');
    
  });
  
  client.on('error', function(msg) {
    console.log('mqtt.event.error', msg);
    
  });

  client.on('message', function(msg) {
    console.log('mqtt.event.message', msg);
  });

  client.on('connect', function() {
    console.log('connected');
    client.subscribe('/data', 0);
    client.publish('/data', "test", 0, false);
  });

  client.connect();
}).catch(function(err){
  console.log(err);
});

```

## API
* `mqtt.createClient(options)`  create new client instance with `options`, async operation
  - `uri`: `protocol://host:port`, protocol is [mqtt | mqtts]
  - `host`: ipaddress or host name (override by uri if set)
  - `port`: port number (override by uri if set)
  - `tls`: true/false (override by uri if set to mqtts or wss)
  - `user`: string username
  - `pass`: string password
  - `auth`: true/false - override = true if `user` or `pass` exist
  - `clientId`: string client id
  - `keepalive`

* `client`
  - `on(event, callback)`: add event listener for
    + event: `connect` - client connected
    + event: `closed` - client disconnected
    + event: `error` - error
    + event: `message` - message object
  - `connect`: begin connection
  - `disconnect`: disconnect
  - `subscribe(topic, qos)`
  - `publish(topic, payload, qos, retain)`

* `message`
  - `retain`: *boolean* `false`
  - `qos`: *number* `2`
  - `data`: *string* `"test message"`
  - `topic`: *string* `"/data"`

## Todo

* [ ] todo


## LICENSE

```
INHERIT FROM MQTT LIBRARY (progress)
```
