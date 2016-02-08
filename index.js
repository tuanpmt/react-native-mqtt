import {
	DeviceEventEmitter,
	NativeModules
} from 'react-native';

var Mqtt = NativeModules.Mqtt;

module.exports = {
	options: {},
	Client: function(options) {
		this.options = options;
		if(options.uri) {
			var pattern = /^((mqtt[s]?|ws[s]?)?:(\/\/)([a-zA-Z_\.]*):?(\d+))$/;
			var matches = options.uri.match(pattern);
			var protocol = matches[2];
			var host = matches[4];
			var port =  matches[5];

			this.options.port = parseInt(port);
			this.options.host = host;
			this.options.protocol = 'tcp';

			if(protocol == 'wss' || protocol == 'mqtts') {
				this.options.tls = true;
			}
			if(protocol == 'ws' || protocol == 'wss') {
				this.options.protocol = 'ws';
			}
		}
		//console.log(options)
		var self = this;
		return {
			on: function(event, callback) {
				console.log('subscribe event', event);
				this[event] = DeviceEventEmitter.addListener(
							  	event,
							  	(data) => callback(data));
			},
			connect: function() {
				console.log('connect', self.options);
				Mqtt.connect(self.options);
			},
			disconnect: function() {
				Mqtt.disconnect()
			},
			subscribe: function(topic) {
				Mqtt.subscribe(topic);
			}
		};
	},
	
};
