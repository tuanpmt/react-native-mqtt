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

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;

import java.util.AbstractList;
import java.util.Random;

public class RCTMqttModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RCTMqttModule";
    private final ReactApplicationContext _reactContext;
    private HashMap<Integer, RCTMqtt> clients;

    public RCTMqttModule(ReactApplicationContext reactContext) {
        super(reactContext);
        _reactContext = reactContext;
        clients = new HashMap<Integer, RCTMqtt>();
    }

    @Override
    public String getName() {
        return "Mqtt";
    }

    @ReactMethod
    public void createClient(final ReadableMap _options, Promise promise) {
      int clientRef = randInt(1000, 9999);
      RCTMqtt client = new RCTMqtt(clientRef, _reactContext, _options);
      client.setCallback();
      clients.put(clientRef, client);
      promise.resolve(clientRef);
      Log.d(TAG, "ClientRef:" + clientRef);
    }

    @ReactMethod
    public void connect(final int clientRef) {
      clients.get(clientRef).connect();
    }
    
    @ReactMethod
    public void disconnect(final int clientRef) {
      clients.get(clientRef).disconnect();
    }
    

    @ReactMethod
    public void subscribe(final int clientRef, final String topic, final int qos) {
      clients.get(clientRef).subscribe(topic, qos);
    }

    @ReactMethod
    public void publish(final int clientRef, final String topic, final String payload, final int qos, final boolean retain) {
      clients.get(clientRef).publish(topic, payload, qos, retain);
    }
    
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
