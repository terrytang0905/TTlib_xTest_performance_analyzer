(function() {
    var ChartModel = Backbone.Model.extend({
	
	initialize: function(xmlFilesCollection) {
	    this.xmlFilesCollection = xmlFilesCollection;
	},
	
	setAndShow: function(formData) {
	    var xAxisQuery = "//value/x/text()";
	    var yAxisQuery = "//value/y/text()";   

	    var that = this;

	    this.xmlFilesCollection.fetchQueryResult(formData.collection, formData.xquery, function(xmlroot) {
				
		    that.xdata = that.xmlFilesCollection.getData(xmlroot, xAxisQuery);
		    that.ydata = _.map(that.xmlFilesCollection.getData(xmlroot, yAxisQuery), function(text) {
					    return parseInt(text);
					}); 
		    that.showChart(formData.chartType);
	    });

	    
	}, 

	showChart: function(chartType) {
		if (this.xdata && this.ydata) {
			var that = this;
			var dataCol=$("#collection").val();
			var config = {
			 		chart: {
					    type: chartType,
					    borderWidth: 1
					},
					title: {
					    text: ""
					},
					xAxis: {
					    categories: this.xdata
					},
					yAxis: {
					    title: {
					     text: ""
					    }
					},
					series: [
					    {
						name: dataCol,
						data: this.ydata
					    }
					]
			    };


			if (chartType === 'pie') {
				config.series = [{
					data:  _.map(this.ydata, function(value, index) {
						return [that.xdata[index], value];
					})
				}];
			}

			this.set({config: config});
		}
	}

    });

    window.ChartModel = ChartModel;
})();
