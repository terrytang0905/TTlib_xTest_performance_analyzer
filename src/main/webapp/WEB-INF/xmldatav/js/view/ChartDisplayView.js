(function() {
    var ChartDisplayView = Backbone.View.extend({
	el: "#chart-container",
	
	initialize: function(model) {
	    console.log('init chart display view');
	    this.model = model;
	    this.model.bind('change', this.render, this);
	},

	render: function(config) {
	    this.$el.empty();
	    var chartConfig = this.model.get('config');
	    chartConfig.chart.renderTo = 'chart-container';
	    var chart = new Highcharts.Chart(chartConfig);
	}

    });

    window.ChartDisplayView = ChartDisplayView;
})();