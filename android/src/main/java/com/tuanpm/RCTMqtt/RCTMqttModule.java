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


public class RCTMqttModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RCTMqttModule";

    private final ReactApplicationContext _reactContext;


    public RCTMqttModule(ReactApplicationContext reactContext) {
        super(reactContext);
        _reactContext = reactContext;
        String clientId = MqttClient.generateClientId();

    }

    @Override
    public String getName() {
        return "Mqtt";
    }

}
