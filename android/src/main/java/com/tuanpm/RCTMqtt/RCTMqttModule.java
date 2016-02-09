/**
 * Created by TuanPM (tuanpm@live.com) on 1/4/16.
 */

package com.tuanpm.RCTMqtt;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.react.bridge.*;

import javax.annotation.Nullable;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

public class RCTMqttModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RCTMqttModule";
    private final ReactApplicationContext _reactContext;

    private final WritableMap defaultOptions;

  // @"host": @"localhost",
  //                               @"port": @1883,
  //                               @"protcol": @"tcp", //ws
  //                               @"tls": @NO,
  //                               @"keepalive": @120, //second
  //                               @"clientId" : @"react-native-mqtt",
  //                               @"protocolLevel": @4,
  //                               @"clean": @YES,
  //                               @"auth": @NO,
  //                               @"user": @"",
  //                               @"pass": @"",
  //                               @"will": @NO,
  //                               @"willMsg": [NSNull null],
  //                               @"willtopic": @"",
  //                               @"willQos": @0,
  //                               @"willRetainFlag": @NO


    public RCTMqttModule(ReactApplicationContext reactContext) {
        super(reactContext);
        _reactContext = reactContext;

         /* Create defaults config*/
         defaultOptions = (WritableMap) new WritableNativeMap();

         defaultOptions.putString("host", "localhost");
         defaultOptions.putInt("port", 1883);
         defaultOptions.putString("protocol", "tcp");
         defaultOptions.putBoolean("tls", false);
         defaultOptions.putInt("keepalive", 1883);
         defaultOptions.putString("clientId", "react-native-mqtt");
         defaultOptions.putInt("protocolLevel", 4);
         defaultOptions.putBoolean("clean", true);
         defaultOptions.putBoolean("auth", false);
         defaultOptions.putString("user", "");
         defaultOptions.putString("pass", "");
         defaultOptions.putBoolean("will", false);
         defaultOptions.putInt("protocolLevel", 4);
         defaultOptions.putBoolean("will", false);
         defaultOptions.putString("willMsg", "");
         defaultOptions.putString("willtopic", "");
         defaultOptions.putInt("willQos", 0);
         defaultOptions.putBoolean("willRetainFlag", false);
    }

    @Override
    public String getName() {
        return "Mqtt";
    }

    @ReactMethod
    public void connect(final ReadableMap _options) {
      if (_options.hasKey("host")) 
        defaultOptions.putString("host", _options.getString("host"));
      if(_options.hasKey("port"))
        defaultOptions.putInt("port",_options.getInt("port"));
      if(_options.hasKey("protocol"))
        defaultOptions.putString("protocol",_options.getString("protocol"));
      if(_options.hasKey("tls"))
        defaultOptions.putBoolean("tls",_options.getBoolean("tls"));
      if(_options.hasKey("keepalive"))
        defaultOptions.putInt("keepalive",_options.getInt("keepalive"));
      if(_options.hasKey("clientId"))
        defaultOptions.putString("clientId",_options.getString("clientId"));
      if(_options.hasKey("protocolLevel"))
        defaultOptions.putInt("protocolLevel",_options.getInt("protocolLevel"));
      if(_options.hasKey("clean"))
        defaultOptions.putBoolean("clean",_options.getBoolean("clean"));
      if(_options.hasKey("auth"))
        defaultOptions.putBoolean("auth",_options.getBoolean("auth"));
      if(_options.hasKey("user"))
        defaultOptions.putString("user",_options.getString("user"));
      if(_options.hasKey("pass"))
        defaultOptions.putString("pass",_options.getString("pass"));
      if(_options.hasKey("will"))
        defaultOptions.putBoolean("will",_options.getBoolean("will"));
      if(_options.hasKey("protocolLevel"))
        defaultOptions.putInt("protocolLevel",_options.getInt("protocolLevel"));
      if(_options.hasKey("will"))
        defaultOptions.putBoolean("will",_options.getBoolean("will"));
      if(_options.hasKey("willMsg"))
        defaultOptions.putString("willMsg",_options.getString("willMsg"));
      if(_options.hasKey("willtopic"))
        defaultOptions.putString("willMsg",_options.getString("willMsg"));
      if(_options.hasKey("willQos"))
        defaultOptions.putInt("willQos",_options.getInt("willQos"));
      if(_options.hasKey("willRetainFlag"))
        defaultOptions.putBoolean("willRetainFlag", _options.getBoolean("willRetainFlag"));

      ReadableMap options = (ReadableMap) defaultOptions;

      MqttConnectOptions mqttoptions = new MqttConnectOptions();

      if(options.getInt("protocolLevel") == 3)
        mqttoptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
      mqttoptions.setKeepAliveInterval(options.getInt("keepalive"));

      String uri = "tcp://";
      if(options.getBoolean("tls"))
        uri = "ssl://";
      uri += options.getString("host") + ":";
      uri += options.getInt("port");
      //mqttoptions.setServerURIs({uri});

      if(options.getBoolean("auth")) {
        String user = options.getString("user");
        String pass = options.getString("pass");
        if(user.length() > 0) 
          mqttoptions.setUserName(user);
        if(pass.length() > 0)
          mqttoptions.setPassword(pass.toCharArray());
      }

      if(options.getBoolean("will")) {

      }


    }

    @ReactMethod
    public void disconnect() {
      
    }

    @ReactMethod
    public void subscribe(final String topic) {

    }

    @ReactMethod
    public void publish(final String topic, final String message) {

    }

    @ReactMethod
    public void on(final String event) {

    }



}
