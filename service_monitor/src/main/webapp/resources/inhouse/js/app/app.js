define(
		[ 'jquery', 'underscore', 'cookie' ],
		function($, _) {

			var instance = null;

			var defer = null;
			
			var defaultApiClientId = "0d466d06-2b57-4669-b59d-56209c58d361";
			
			var defaultUser = "local";

			function App() {
				if (instance !== null) {
					throw new Error(
							"Cannot instantiate more than one App, use App.getInstance()");
				}
				this.initialize();
			}

			App.prototype = {

				clearData : function() {
					this.rawServiceData = {

					};
					this.formatedData = {
						'data' : []
					};
					this.products = [];
				},

				initialize : function() {
					this.clearData();
					this.userPreferences = [];
					this.initializeCookie();
					this.getDataPeriodically();
					
				},

				loadData : function() {
					var self = this;
					if (defer === null || defer.state() !== "pending") {
						this.clearData();
						defer = $.Deferred();
						$
								.getJSON(
										"/service_monitor/api/getServiceDetails")
								.done(function(data) {
									self.rawServiceData = data;
									defer.resolve(self.rawServiceData);
								}).fail(function(){
									defer.reject(arguments);
								});
					}
					return defer.promise();
				},

				getFormatedData : function() {
					var self = this;
					if (_.keys(self.rawServiceData).length == 0) {
						var defer = $.Deferred();
						self.loadData().done(function(data) {
							self._formatData(data);
							defer.resolve(self.formatedData);
						}).fail(function() {
							defer.reject(arguments);
						});
						return defer.promise();
					} else {
						self._formatData(self.rawServiceData);
						return self.formatedData;
					}
				},

				getRawData : function() {
					var self = this;
					if (_.keys(self.rawServiceData).length == 0) {
						return self.loadData();
					}
					return self.rawServiceData;
				},

				_formatData : function(data) {
					var self = this;
					for (var i = 0; i < data.length; i++) {
						var index = _
								.indexOf(self.products, data[i]['product']);
						if (index == -1) {
							self.products.push(data[i]['product']);
							self.formatedData['data'].push({
								'product' : data[i]['product'],
								'environments' : [ {
									'environment' : data[i]['environment'],
									'services' : data[i]['services']
								} ]
							});
						} else {
							self.formatedData['data'][index]['environments']
									.push({
										'environment' : data[i]['environment'],
										'services' : data[i]['services']
									});
						}

					}
				},

				getServicesForProductEnvironment : function(product,
						environment) {
					var self = this;
					var searchResult = _
							.find(
									self.rawServiceData,
									function(obj) {
										return (obj['product'] == product && obj['environment'] == environment);
									});
					if (searchResult === undefined) {
						return [];
					} else {
						return searchResult['services'];
					}
				},

				getDataPeriodically : function() {
					var self = this;
					setInterval(function() {
						self.loadData();
					}, 600000);
				},

				getServiceSubDetails : function(product, environment, service) {
					var self = this;
					if (defer === null || defer.state() !== "pending") {
						defer = $.Deferred();
						$
								.getJSON(
										"/service_monitor/api/getServiceDetail/"
														+ product
														+ "/"
														+ environment
														+ "/"
														+ service).done(
										function(data) {
											defer.resolve(data);
										});
					}
					return defer.promise();
				},

				appendApiClientKey : function(url) {
					var self = this;
					if (url.indexOf("login") > -1
							|| url.indexOf("register") > -1) {
						return url;
					} else {
						return url + "?apiClientId=" + self.apiClientId;
					}
				},

				updateCurrentUserToken : function(token, user) {
					this.apiClientId = token;
					this.user = user;
				},

				register : function(userid, password) {
					var self = this;
					return self._signInUp(userid, password, "/service_monitor/api/register");
				},
				
				login : function(userid, password) {
					var self = this;
					return self._signInUp(userid, password, "/service_monitor/api/login");
				},
				
				_signInUp : function(userid, password, path) {
					var self = this;
					var deferred = $.Deferred();
					$.ajax({
						url : self.appendApiClientKey(path),
						method : 'POST',
						data : {
							'username' : userid,
							'password' : password
						}
					}).done(function(data) {
						if(data.status === 'success') {
							self.createCookie(data.token, data.user);
							self.getUserPreferences();
							deferred.resolve(data);
						} else {
							alert(data.errorMessage);
							deferred.reject(data.errorMessage);
						}
					}).error(function(data) {
						deferred.reject(data);
					});
					return deferred.promise();
				},
				
				createCookie : function(token, user) {
					var self = this;
					$.cookie('apiClientId', token, { expires: 365, path: '/' });
					$.cookie('user', user, { expires: 365, path: '/' });
				    self.updateCurrentUserToken(token, user);
				},
				
				deleteCookie : function() {
					var self = this;
					$.removeCookie('apiClientId');
					$.removeCookie('user');
				},
				
				logout : function() {
					var self = this;
					var deferred = $.Deferred();
					$.ajax({
						url : "/service_monitor/api/logout",
						method : 'POST'
					}).done(function(data) {
						if(data.status === 'success') {
							self.userPreferences = [];
							self.deleteCookie();
							self.createCookie(defaultApiClientId, defaultUser);
							deferred.resolve(data);
						} else {
							alert(data.errorMessage);
							deferred.reject(data.errorMessage);
						}
					}).error(function() {
						deferred.reject(arguments);
					});
					return deferred.promise();
				},
				
				isLoggedInUser : function() {
					var self = this;
					var token = $.cookie("apiClientId");
					if(token == defaultApiClientId) {
						return false;
					} else {
						return true;
					}
				},
				
				initializeCookie : function() {
					var self = this;
					var user = $.cookie('user');
					var token = $.cookie('apiClientId');
					if(user == undefined) {
						self.createCookie(defaultApiClientId, defaultUser);
					} else {
						self.apiClientId = token;
						self.user = user;
						if(self.user != defaultUser) {
							self.getUserPreferences();	
						}
					}
				},
				
				getUserPreferences : function() {
					var self = this;
					if(self.userPreferences.length == 0) {
						var defer = $.Deferred();
						$.get('/service_monitor/api/userpreferences').done(
								function(data) {
									self.userPreferences = data;
									defer.resolve(data);
								}).fail(function() {
							defer.reject(arguments)
						});
						return defer.promise();
					} else {
						return self.userPreferences;
					}
				},
				
				putUserPreference : function(service) {
					var self = this;
					var defer = $.Deferred();
					$.ajax({
						'url' : '/service_monitor/api/userpreferences/'	+ service,
						'method' : 'PUT',
					}).done(function(data) {
						self.userPreferences.push(service);
						defer.resolve(data);
					}).fail(function() {
						defer.reject(arguments)
					});
					return defer.promise();
				},
				
				deleteUserPreference : function(service) {
					var self = this;
					var defer = $.Deferred();
					$.ajax({
						'url' : '/service_monitor/api/userpreferences/' + service,
						'method' : 'DELETE',
					}).done(function(data) {
						var index = self.userPreferences.indexOf(service);
						if (index > -1) {
							self.userPreferences.splice(index, 1);
						}
						defer.resolve(data);
					}).fail(function() {
						defer.reject(arguments)
					});
					return defer.promise();
				}
			};

			App.getInstance = function() {
				if (instance === null) {
					instance = new App();
				}
				return instance;
			};

			return App.getInstance();
		});