define([ 'backbone', 'jquery', 'underscore', 'app/app',
		'text!template/navbar.html', 'mustache' ], function(Backbone, $, _,
		App, TemplateNavbar, Mustache) {

	var NavbarView = Backbone.View.extend({

		el : '#nav-wrapper',
		
		signUpValidDataCount: 0,
		
		signInValidDataCount: 0,

		events : {
			'click .menu-item' : 'openDetailedView',
			'click #sign-in' : 'login',
			'click #sign-up' : 'register',
			'keydown #username-sign-up' : 'signUpUsernameChange',
			'keydown #password-sign-up' : 'signUpPasswordChange',
			'keydown #confirm-password-sign-up' : 'signUpConfirmPasswordChange',
			'keydown #username-sign-in' : 'signInUsernameChange',
			'keydown #password-sign-in' : 'signInPasswordChange',
			'click #sign-out' : 'logout'
		},

		initialize : function() {

			this.render();
		},

		render : function() {
			var self = this;
			$(self.el).html("");
			App.getFormatedData().done(function(data) {
				$(self.el).html(Mustache.render(TemplateNavbar, data));
				if(App.isLoggedInUser()) {
					self.toggleToRegisteredUser();
				}
			});
			
		},

		openDetailedView : function(event) {
			var self = this;
			var $element = $(event.target).closest('a');
			if ($element.data('product') !== undefined
					&& $element.data('environment') !== undefined) {
				var eventData = {
					'product' : $element.data('product'),
					'environment' : $element.data('environment')
				};
				Backbone.trigger("open-detailed-view", eventData);
			}
		},
		
		login : function() {
			var self = this;
			var username = $('#username-sign-in').val();
			var password = $('#password-sign-in').val();
			$.when(App.login(username, password)).done(function(data){
				console.log("data after login: ", data);
				if(data.status == 'success') {
					self.toggleToRegisteredUser();
					$('#username-sign-in').val("");
					$('#password-sign-in').val("");
				} else {
					alert("login failed");
				}
			}).fail(function(){alert("Login failed due to unhandled exception");});
		},
		
		register : function() {
			var self = this;
			var username = $('#username-sign-up').val();
			var password = $('#password-sign-up').val();
			$.when(App.register(username, password)).done(function(data){	
				console.log("data after register: ", data);
				if(data.status == 'success') {
					self.toggleToRegisteredUser();
					$('#username-sign-up').val("");
					$('#password-sign-up').val("");
					$('#confirm-password-sign-up').val("");
				} else {
					alert("register failed");
				}
			}).fail(function(){alert("Register failed due to unhandled exception");});
		},
		
		logout : function() {
			var self = this;
			$.when(App.logout()).done(function(data){
				console.log("data after logout: ", data);
				if(data.status == 'success') {
					self.toggleToGuestUser();
				} else {
					alert("logout failed");
				}
			}).fail(function(){alert("Logout failed due to unhandled exception");});
		},
		
		signUpUsernameChange : _.debounce(function(event) {
			var self = this;
			if($(event.target)[0].checkValidity()) {
				self.signUpValidDataCount = self.signUpValidDataCount | 1;
			} else {
				self.signUpValidDataCount = self.signUpValidDataCount & 6;
			}
			self.updateSignUpButton();
		}, 500),
		
		signUpPasswordChange : _.debounce(function(event) {
			var self = this;
			if($(event.target).val() != "") {
				self.signUpValidDataCount = self.signUpValidDataCount | 2;
			} else {
				self.signUpValidDataCount = self.signUpValidDataCount & 5;
			}
			self.updateSignUpButton();
		}, 500),
		
		signUpConfirmPasswordChange : _.debounce(function(event) {
			var self = this;
			if($(event.target).val() != "" && $(event.target).val() == $('#password-sign-up').val()) {
				self.signUpValidDataCount = self.signUpValidDataCount | 4;
			} else {
				self.signUpValidDataCount = self.signUpValidDataCount & 3;
			}
			self.updateSignUpButton();
		}, 500),
		
		signInUsernameChange : _.debounce(function(event) {
			var self = this;
			if($(event.target)[0].checkValidity()) {
				self.signInValidDataCount = self.signInValidDataCount | 1;
			} else {
				self.signInValidDataCount = self.signUpValidDataCount & 2;
			}
			self.updateSignInButton();
		}, 500),
		
		signInPasswordChange : _.debounce(function(event) {
			var self = this;
			if($(event.target).val() != "") {
				self.signInValidDataCount = self.signInValidDataCount | 2;
			} else {
				self.signInValidDataCount = self.signInValidDataCount & 1;
			}
			self.updateSignInButton();
		}, 500),
		
		updateSignUpButton : function() {
			var self = this;
			if(self.signUpValidDataCount == 7) {
				$("#sign-up").removeClass("disabled").removeAttr("disabled");
			} else {
				$("#sign-up").addClass("disabled").attr("disabled", "disabled");
			}
		},
		
		updateSignInButton : function() {
			var self = this;
			if(self.signInValidDataCount == 3) {
				$("#sign-in").removeClass("disabled").removeAttr("disabled");
			} else {
				$("#sign-in").addClass("disabled").attr("disabled", "disabled");
			}
		},
		
		toggleToRegisteredUser : function() {
			var self = this;
			$('#guest-user').hide();
			$('#registered-user').show();
		},
		
		toggleToGuestUser : function() {
			var self = this;
			$('#guest-user').show();
			$('#registered-user').hide();
		}

	});

	return NavbarView;
});