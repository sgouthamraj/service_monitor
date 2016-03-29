define(
		[ 'jquery' ],
		function($) {

			var instance = null;

			function LoadingIndicator() {
				if (instance !== null) {
					throw new Error(
							"Cannot instantiate more than one MySingleton, use MySingleton.getInstance()");
				}

				this.initialize();
			}

			LoadingIndicator.prototype = {

				initialize : function() {

				},

				startLoader : function() {
					$(".custom-overlay").show();
				},

				stopLoader : function() {
					$(".custom-overlay").hide();
				}
			};

			LoadingIndicator.getInstance = function() {

				if (instance === null) {
					instance = new LoadingIndicator();
				}
				return instance;
			};

			return LoadingIndicator.getInstance();

		});