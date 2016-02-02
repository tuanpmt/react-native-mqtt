

## Description

[react-native](https://github.com/facebook/react-native) mqtt client module 

## Featues
* Support both IOS and Android


## Getting started
### Mostly automatic install
1. `npm install rnpm --global`
2. `npm install react-native-mqtt@latest --save`
3. `rnpm link react-native-mqtt`

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


.addPackage(new RCT<qttPackage()) //for older version

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

[WARNING]
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

```

## Todo

* [ ] todo


## LICENSE

```

```
