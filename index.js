import {
	DeviceEventEmitter,
	NativeModules
} from 'react-native';

var Mqtt = NativeModules.Mqtt;

var MqttClient = function(options, clientRef){
	this.options = options;
	this.clientRef = clientRef;
	this.eventHandler = {};

	this.dispatchEvent = function(data) {
		
		if(data && data.clientRef == this.clientRef && data.event) {

			if(this.eventHandler[data.event]) {
				this.eventHandler[data.event](data.message);
			}
		}	
	}
}

MqttClient.prototype.on = function (event, callback) {
	console.log('setup event', event);
	this.eventHandler[event] = callback;
}

MqttClient.prototype.connect = function () {
	Mqtt.connect(this.clientRef);
}

MqttClient.prototype.disconnect = function () {
	Mqtt.disconnect(this.clientRef);
}

MqttClient.prototype.subscribe = function (topic, qos) {
	Mqtt.subscribe(this.clientRef, topic, qos);
}

MqttClient.prototype.publish = function(topic, payload, qos, retain) {
	Mqtt.publish(this.clientRef, topic, payload, qos, retain);
}

module.exports = {
	clients: [],
	eventHandler: null,
	dispatchEvents: function(data) {
		this.clients.forEach(function(client) {
			client.dispatchEvent(data);
		});
	},
	createClient: async function(options) {
		if(options.uri) {
			var pattern = /^((mqtt[s]?|ws[s]?)?:(\/\/)([a-zA-Z_\.]*):?(\d+))$/;
			var matches = options.uri.match(pattern);
			var protocol = matches[2];
			var host = matches[4];
			var port =  matches[5];

			options.port = parseInt(port);
			options.host = host;
			options.protocol = 'tcp';
			

			if(protocol == 'wss' || protocol == 'mqtts') {
				options.tls = true;
			}
			if(protocol == 'ws' || protocol == 'wss') {
				options.protocol = 'ws';
			}
			
		}
		
		let clientRef = await Mqtt.createClient(options);

		var client = new MqttClient(options, clientRef);

		/* Listen mqtt event */
		if(this.eventHandler === null) {
			console.log('add mqtt_events listener')
			this.eventHandler = DeviceEventEmitter.addListener(
							  	"mqtt_events",
							  	(data) => this.dispatchEvents(data));
		}
		this.clients.push(client);

		return client;
	},
	removeClient: function(client) {
		var clientIdx = this.clients.indexOf(client);

		/* TODO: destroy client in native module */

		if(clientIdx > -1)
			this.clients.splice(clientIdx, 1);

		if(this.clients.length > 0) {
			this.eventHandler.remove();
			this.eventHandler = null;
		}
	}
	
};
