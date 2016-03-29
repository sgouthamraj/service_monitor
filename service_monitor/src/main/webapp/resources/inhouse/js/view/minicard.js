define([ 'backbone', 'jquery', 'underscore', 'mustache',
		'text!template/minicard.html', 'app/app' ], function(Backbone, $, _, Mustache,
		TemplateCard, App) {

	var MiniCard = Backbone.View.extend({

		tagName: 'div',
		options : null,

		events : {
			'click .minicard-viewdetail' : 'switchToDetailedView'
		},

		initialize : function(args) {
			this.options = args;
		},

		render : function() {
			var self = this;
			$(self.el).html(Mustache.render(TemplateCard, self.options));
			return self.el;
		},

		switchToDetailedView : function(event) {
			var self = this;
			var $element = $(event.target).closest('a');
			var eventData = {
				'product' : $element.data('product'),
				'environment' : $element.data('environment')
			};
			Backbone.trigger("open-detailed-view", eventData);
		}

	});

	return MiniCard;
});