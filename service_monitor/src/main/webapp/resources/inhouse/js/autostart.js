requirejs.config({
	baseUrl : 'resources/third-party/js/',
	paths : {
		app : '../../inhouse/js/app',
		view : '../../inhouse/js/view',
		model : '../../inhouse/js/model',
		template : '../../inhouse/template',
		bootstrap : './bootstrap'
	},
	skim : {
		'bootstrap/affix' : {
			deps : [ 'jquery' ],
			exports : '$.fn.affix'
		},
		'bootstrap/alert' : {
			deps : [ 'jquery' ],
			exports : '$.fn.alert'
		},
		'bootstrap/button' : {
			deps : [ 'jquery' ],
			exports : '$.fn.button'
		},
		'bootstrap/carousel' : {
			deps : [ 'jquery' ],
			exports : '$.fn.carousel'
		},
		'bootstrap/collapse' : {
			deps : [ 'jquery' ],
			exports : '$.fn.collapse'
		},
		'bootstrap/dropdown' : {
			deps : [ 'jquery' ],
			exports : '$.fn.dropdown'
		},
		'bootstrap/modal' : {
			deps : [ 'jquery' ],
			exports : '$.fn.modal'
		},
		'bootstrap/popover' : {
			deps : [ 'jquery' ],
			exports : '$.fn.popover'
		},
		'bootstrap/scrollspy' : {
			deps : [ 'jquery' ],
			exports : '$.fn.scrollspy'
		},
		'bootstrap/tab' : {
			deps : [ 'jquery' ],
			exports : '$.fn.tab'
		},
		'bootstrap/tooltip' : {
			deps : [ 'jquery' ],
			exports : '$.fn.tooltip'
		},
		'bootstrap/transition' : {
			deps : [ 'jquery' ],
			exports : '$.fn.transition'
		},
		'metisMenu' : {
			deps : [ 'bootstrap' ]
		},
		'responsivedesign' : {
			deps : [ 'metisMenu' ]
		},
		'plotly.min' : {
			deps: ['d3', 'jquery'],
			exports: 'plotly'
		},
		'cookie' : {
			deps : [ 'jquery' ]
		}
	}
});

require([ 'view/consolidated', 'view/navbar', 'view/detailedview' ], function(
		ConsolidatedView, NavbarView, DetailedView) {

	new ConsolidatedView();
	new NavbarView();
	new DetailedView();
});
