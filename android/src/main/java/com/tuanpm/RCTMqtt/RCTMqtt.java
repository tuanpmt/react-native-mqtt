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
import javax.net.ssl.*;
import java.security.SecureRandom;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
//import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class RCTMqtt implements MqttCallback{
	private static final String TAG = "RCTMqttModule";
	private final ReactApplicationContext _reactContext;
	private final WritableMap defaultOptions;
	private final int clientRef;
  MqttAsyncClient   client;
  MemoryPersistence memPer;
  MqttConnectOptions mqttoptions;


	public RCTMqtt(int ref, ReactApplicationContext reactContext, final ReadableMap _options) {
		clientRef = ref;
		_reactContext = reactContext;
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

		createClient(_options);
	}

	private void createClient(final ReadableMap _options) {
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

		

		// Set this wrapper as the callback handler
		

		mqttoptions = new MqttConnectOptions();

    if(options.getInt("protocolLevel") == 3)
      mqttoptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
    mqttoptions.setKeepAliveInterval(options.getInt("keepalive"));

    String uri = "tcp://";
    if(options.getBoolean("tls")) {
      uri = "ssl://";
      try {
        /* 
        http://stackoverflow.com/questions/3761737/https-get-ssl-with-android-and-self-signed-server-certificate
        
        WARNING: for anybody else arriving at this answer, this is a dirty, 
        horrible hack and you must not use it for anything that matters. 
        SSL/TLS without authentication is worse than no encryption at all - 
        reading and modifying your "encrypted" data is trivial for an attacker and you wouldn't even know it was happening
        */
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }}}, new SecureRandom());

        mqttoptions.setSocketFactory(sslContext.getSocketFactory());
      } catch(Exception e) {

      }
      

    }
    uri += options.getString("host") + ":";
    uri += options.getInt("port");

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

    memPer = new MemoryPersistence();

    try {
    	client = new MqttAsyncClient(uri, options.getString("clientId"), memPer);
    } catch(MqttException e) {

    }

	}

	public void setCallback() {
		client.setCallback(this);
	}


	private void sendEvent(ReactContext reactContext,
                     String eventName,
                     @Nullable WritableMap params) {
		params.putInt("clientRef", this.clientRef);
    reactContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
        .emit(eventName, params);
  }



	public void connect() {
		try {
	         // Connect using a non-blocking connect
	        client.connect(mqttoptions, _reactContext, new IMqttActionListener() {
	          public void onSuccess(IMqttToken asyncActionToken) {

	           	WritableMap params = Arguments.createMap();
	           	params.putString("event", "connect");
	           	params.putString("message", "connected");
	           	sendEvent(_reactContext, "mqtt_events", params);
	           	log("Connected");
	              
	          }

	          public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
	            
	            WritableMap params = Arguments.createMap();
	           	params.putString("event", "error");
	           	params.putString("message", "connection failure");
	           	sendEvent(_reactContext, "mqtt_events", params);
	          }
	        });
	      } catch (MqttException e) {
	        WritableMap params = Arguments.createMap();
         	params.putString("event", "error");
         	params.putString("message", "Can't create connection");
         	sendEvent(_reactContext, "mqtt_events", params);
	     }
	}

  public void disconnect() {  
    IMqttActionListener discListener = new IMqttActionListener() {
      public void onSuccess(IMqttToken asyncActionToken) {
          log("Disconnect Completed");
          WritableMap params = Arguments.createMap();
         	params.putString("event", "closed");
         	params.putString("message", "Disconnect");
          sendEvent(_reactContext, "mqtt_events", params);
        
      }
      public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        
        log ("Disconnect failed" +exception);
        
      }
      
    };

    try {
      client.disconnect(_reactContext, discListener);
    } catch (MqttException e) {
      log ("Disconnect failed ...");
    }

  }

  public void subscribe(final String topic, final int qos) {
	  try {
	    IMqttToken subToken = client.subscribe(topic, qos);
	    subToken.setActionCallback(new IMqttActionListener() {
	        @Override
	        public void onSuccess(IMqttToken asyncActionToken) {
	            // The message was published
	          log("Subscribe success");
	        }
	 
	        @Override
	        public void onFailure(IMqttToken asyncActionToken,
	                              Throwable exception) {
	            // The subscription could not be performed, maybe the user was not
	            // authorized to subscribe on the specified topic e.g. using wildcards

	          log("Subscribe failed");     
	        }
	    });
	  } catch (MqttException e) {
	    log("Cann't subscribe");
	    e.printStackTrace();
	  }
	}


	public void publish(final String topic, final String payload, final int qos, final boolean retain) {
    byte[] encodedPayload = new byte[0];
    try {
        encodedPayload = payload.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        client.publish(topic, message);
    } catch (UnsupportedEncodingException | MqttException e) {
        e.printStackTrace();
    }
  }

   /****************************************************************/
  /* Methods to implement the MqttCallback interface              */
  /****************************************************************/

    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
  public void connectionLost(Throwable cause) {
    // Called when the connection to the server has been lost.
    // An application may choose to implement reconnection
    // logic at this point. This sample simply exits.
    log("Connection to lost! " + cause);
    WritableMap params = Arguments.createMap();
   	params.putString("event", "closed");
   	params.putString("message", "Connection to lost!");
    sendEvent(_reactContext, "mqtt_events", params);
    //System.exit(1);
  }
   /**
   * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
   */
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Called when a message has been delivered to the
    // server. The token passed in here is the same one
    // that was returned from the original call to publish.
    // This allows applications to perform asynchronous
    // delivery without blocking until delivery completes.
    //
    // This sample demonstrates asynchronous deliver, registering
    // a callback to be notified on each call to publish.
    //
    // The deliveryComplete method will also be called if
    // the callback is set on the client
    //
    // note that token.getTopics() returns an array so we convert to a string
    // before printing it on the console
    log("Delivery complete callback: Publish Completed ");
  }

    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
  public void messageArrived(String topic, MqttMessage message) throws MqttException {
    // Called when a message arrives from the server that matches any
    // subscription made by the client
   
    log(                   "  Topic:\t" + topic +
                           "  Message:\t" + new String(message.getPayload()) +
                           "  QoS:\t" + message.getQos());

    WritableMap data = Arguments.createMap();
    data.putString("topic", topic);
    data.putString("data", new String(message.getPayload()));
    data.putInt("qos", message.getQos());
    data.putBoolean("retain", message.isRetained());

    WritableMap params = Arguments.createMap();
   	params.putString("event", "message");
   	params.putMap("message", data);
    sendEvent(_reactContext, "mqtt_events", params);
  }

   /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
    void log(String message) {
      Log.d(TAG, message);
    }
   

}