define([ 'backbone', 'jquery', 'underscore', 'view/loading', 'app/app',
		'view/minicard' ], function(Backbone, $, _, Loader, App, MiniCard) {

	var ConsolidatedView = Backbone.View.extend({

		el : '#cardswrapper',

		initialize : function() {
			this.render();
			this.refreshDataPeriodically();
		},

		render : function() {
			var self = this;
			Loader.startLoader();
			$.when(App.getRawData()).done(function(data) {
				self._render(data);
			}).fail(self.reportFailure);
		},

		_render : function(data) {
			var self = this;
			Loader.stopLoader();
			$(self.el).html("");

			_.each(data, function(item) {

				var totalServiceCount = 0;
				var runningServiceCount = 0;
				var statusColor = '';

				_.each(item['services'], function(service) {
					totalServiceCount++;
					if (service['Status'] == 'Running') {
						runningServiceCount++;
					}
				});

				if (runningServiceCount < totalServiceCount) {
					statusColor = 'yellow';
				}
				if (runningServiceCount == 0) {
					statusColor = 'red';
				}
				if (runningServiceCount == totalServiceCount) {
					statusColor = 'green';
				}

				var options = {
					'product' : item['product'],
					'environment' : item['environment'],
					'status' : statusColor,
					'runningservice' : runningServiceCount,
					'totalservices' : totalServiceCount
				};

				var miniCard = new MiniCard(options);
				var miniCardHtml = miniCard.render();
				$(self.el).append(miniCardHtml);

			});
		},

		reportFailure : function() {
			var self = this;
			console.log(arguments);
			Loader.stopLoader();
			alert("Data fetch failed");
		},
		
		refreshDataPeriodically : function() {
			var self = this;
			setInterval(function() {
				self.render();
			}, 600000);
		}

	});

	return ConsolidatedView;
});