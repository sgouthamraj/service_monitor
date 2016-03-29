define([ 'backbone', 'jquery', 'underscore', 'app/app',
		'text!template/detailedcard.html',
		'text!template/servicedetaildisplay.html', 'mustache', 'd3', 'plotly.min',
		'bootstrap/modal' ], function(Backbone, $, _, App,
		TemplateDetailedView, TemplateServiceDetail, Mustache, D3, Plotly) {

	var DetatailedView = Backbone.View.extend({

		el : '#detailedViewBody',

		initialize : function() {
			Backbone.on("open-detailed-view", this.openDetailedView, this);
		},

		events : {
			'click .service-entry' : 'loadServiceSubDetails',
			'click .fav-icon' : 'updateUserPreference'
		},

		render : function() {

		},

		openDetailedView : function(args) {
			var self = this;
			$(self.el).html("");
			$("#detailedViewTitle").html(
					args['product'] + " - " + args['environment']);
			var services = App.getServicesForProductEnvironment(
					args['product'], args['environment']);
			_.each(services, function(element) {
				if (element.Status === 'Running') {
					element.StatusFlag = 'success';
				} else {
					element.StatusFlag = 'danger';
				}
			});
			var renderedDetails = Mustache.render(TemplateDetailedView, {
				"services" : services,
				"product" : args['product'],
				"environment" : args['environment'],
				"registeredUser" : App.isLoggedInUser()
			});
			$(self.el).html(renderedDetails);
			$.when(App.getUserPreferences()).done(function(data){
				_.each(data, function(service) {
							$(self.el).find('span[data-service="' + service + '"]')
									.removeClass('fa-star-o')
									.addClass('fa-star')
									.data('prefered', true);
				})
			}).fail(function(){
				alert("userpreference fetch failed");
			});
			$('#detailedView').modal('show');
		},
		
		updateUserPreference : function(event) {
			var self = this;
			var $element = $(event.target);
			var service = $element.data('service');
			if($element.data('prefered')) {
				$.when(App.deleteUserPreference(service)).done(function() {
					$element.data('prefered', false);
					$element.removeClass('fa-star').addClass(
						'fa-star-o');
				}).fail(function() {
					alert('User preference update failed');
				});
			} else {
				$.when(App.putUserPreference(service)).done(function() {
					$element.data('prefered', true);
					$element.removeClass('fa-star-o').addClass(
						'fa-star');
				}).fail(function() {
					alert('User preference update failed');
				});
			}
			event.stopPropagation();
		},

		loadServiceSubDetails : function(event) {
			
			var self = this;
			var $element = $($(event.target).closest('.service-entry').get(0));
			var displayRow = $element.next();
			var displaySection = displayRow.find("td");

			if (displayRow.is(":hidden")) {
				
				$.when(
						App.getServiceSubDetails($element.data('product'),
								$element.data('environment'), $element
										.data('service'))).done(
						function(data) {
							
							console.log(data);
							
							if(!data.hasAdvancedLog) {
								_.each(data.logDetails, function(element) {
									if (element.status === 'Running') {
										element.StatusFlag = 'success';
									} else {
										element.StatusFlag = 'danger';
									}
								});
								
								var resultHtml = Mustache.render(
										TemplateServiceDetail, {'services' : data.logDetails});
								
								displaySection.html(resultHtml);
								
							} else {
								
								displaySection.html("");
								
								var layout = {
										  xaxis: {
										    title: data.xAxisName,
										    titlefont: {
										      family: 'Arial, sans-serif',
										      size: 18,
										      color: 'lightgrey'
										    },
										    showticklabels: true,
										    tickfont: {
										      family: 'Old Standard TT, serif',
										      size: 14,
										      color: 'black'
										    }
										  },
										  yaxis: {
										    title: data.yAxisName,
										    titlefont: {
										      family: 'Arial, sans-serif',
										      size: 18,
										      color: 'lightgrey'
										    },
										    showticklabels: true,
										    tickfont: {
										      family: 'Old Standard TT, serif',
										      size: 14,
										      color: 'black'
										    }
										  }
										};
								
								var plotlyData = [{
									x: [],
								    y: [],
								    type: 'scatter'
								}];
								
								_.each(data.logDetails, function(element) {
									plotlyData[0]['x'].push(element.entrytime);
									plotlyData[0]['y'].push(element.status);
								});
								
								console.log(plotlyData);
								
								Plotly.newPlot($element.data('service'), plotlyData, layout);
								
							}
							
							
							
							$(self.el).find(".details-opened").removeClass("details-opened").hide();
							
							displayRow.addClass("details-opened").show("slow");
						});
			} else {
				displayRow.slideUp();
			}

		}

	});

	return DetatailedView;
});