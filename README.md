

## Description 

[react-native](https://github.com/facebook/react-native) mqtt client module

## MQTT Featues (inherit from native MQTT framework)
* Use [MQTT Framework](https://github.com/ckrey/MQTT-Client-Framework) for IOS, [Paho MQTT Client](https://eclipse.org/paho/clients/android/) for Android
* Support both IOS and Android (now only support IOS)
* SSL/TSL with 3 mode
* Native library, support mqtt over tcp and mqtt over websocket
* Auto reconnect

## Warning
This library in progress developing, api may change, and not support android now

## Getting started
### Mostly automatic install
1. `npm install rnpm --global`
2. `npm install react-native-mqtt@latest --save`
3. `rnpm link react-native-mqtt`
4. For IOS users: add this linker flag: `-ObjC -licucore`

### Manual install
#### iOS
- `npm install react-native-mqtt@latest --save`
-  In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
- Go to `node_modules` ➜ `react-native-mqtt` and add `RCTmqtt.xcodeproj`
- In XCode, in the project navigator, select your project. Add `libRCTmqtt.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
- Click `RCTmqtt.xcodeproj` in the project navigator and go the `Build Settings` tab. Make sure 'All' is toggled on (instead of 'Basic'). In the `Search Paths` section, look for `Header Search Paths` and make sure it contains both `$(SRCROOT)/../../react-native/React` - mark  as `recursive`.
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

```java
include ':react-native-mqtt'
project(':react-native-mqtt').projectDir = new File(rootProject.projectDir,  '../node_modules/react-native-mqtt/android')
```

- Insert the following lines inside the dependencies block in `android/app/build.gradle`, don't missing `apply plugin:'java'` on top:

```java
compile project(':react-native-mqtt')
```

Notes:

```java
dependencies {
  compile project(':react-native-mqtt')
}
```


But not like this

```java
buildscript {
    ...
    dependencies {
      compile project(':react-native-mqtt')
    }
}
```

## Usage

```javascript
var mqtt    = require('react-native-mqtt');

var client  = mqtt.Client({
  uri: 'mqtt://mqtt.yourhost.com:1883'
});

client.on('connect', function () {
  client.subscribe('/topic/qos0');
  client.subscribe('/topic/qos1', 1);
  client.subscribe('/topic/qos2', 2);

  client.publish('/topic/qos0', 'string will publish');
});

client.on('message', function (topic, message) {
  // message is Buffer
  console.log(message.toString());
  client.disconnect();
});

```

## API
* `mqtt.Client(options)` with
  - `uri`: `protocol://host:port`, protocol is [mqtt | mqtts | ws | wss]
  - `host`: ipaddress or host name (overide by uri if set)
  - `port`: port number (overide by uri if set)
  - `tls`: true/false (overide by uri if set to mqtts or wss)

...

## Todo

* [ ] todo


## LICENSE

```
INHERIT FROM MQTT LIBRARY (progress)
```
