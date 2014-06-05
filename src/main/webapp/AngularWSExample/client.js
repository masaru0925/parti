/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var app = angular.module('app', []);

app.factory('ChatService', function() {
	var service = {};
	var timer;
	var timeout = 5*1000; //5秒
	var reconnect = false;

	service.connect = function() {
		if (!reconnect && service.ws) {
			return;
		}
		reconnect = false;

		var ws = new WebSocket("ws://localhost:8080/parti/echo");

		window.onbeforeunload = function() {
    		ws.onclose = function () {}; // disable onclose handler first
			ws.close();
		};
		var reserveReconnect = function(milisec){
			return setTimeout(function(){
				reconnect = true;
				ws.close();
				service.connect();
			}, milisec);
		};
		ws.onclose = function(){
			//timer = reserveReconnect(timeout);
		};

		ws.onopen = function() {
			timer = reserveReconnect(timeout);
			var date = new Date();
			var time = date.getMinutes() + " " + date.getSeconds();
			service.callback({msg: "Succeeded to open a connection", num: Math.floor(Math.random() * 100), time: time});
		};

		ws.onerror = function() {
			timer = reserveReconnect(timeout);
			var date = new Date();
			var date = new Date();
			var time = date.getMinutes() + " " + date.getSeconds();
			service.callback({msg: "Failed to open a connection", num: Math.floor(Math.random() * 100), time: time});
		};

		ws.onmessage = function(message) {
			clearTimeout(timer);
			timer = reserveReconnect(timeout);
			service.callback(JSON.parse(message.data));
			//service.callback(message.msg);
		};

		service.ws = ws;
	};

	service.send = function(message) {
		service.ws.send(message);
	};

	service.subscribe = function(callback) {
		service.callback = callback;
	};

	return service;
});


function AppCtrl($scope, ChatService) {
	$scope.messages = [];

	ChatService.subscribe(function(message) {
		$scope.messages.push(message);
		$scope.$apply();
	});

	$scope.connect = function() {
		ChatService.connect();
	};

	$scope.send = function() {
		var date = new Date();
		var time = date.getMinutes() + " " + date.getSeconds();
		ChatService.send(JSON.stringify({msg: $scope.text, num: Math.floor(Math.random() * 100), time: time}));
		$scope.text = "";
	};
}