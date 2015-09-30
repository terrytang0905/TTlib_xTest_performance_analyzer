(function() {
    var ChartConfigurationView = Backbone.View.extend({
	el: ".chart-config",
	
	events: {
	    "click .show-chart": "showChart",
	    "change input": "configChanged",
	    "click #collectionSample": "displayXmlTree",
	    "change #chartType": "changeChartType",
	    "click #showXQuery": "showXQueryDiv"
	},
	
	showChart: function() {
	    var formData = {};
	    formData.xquery = $('#xquery-input').val();
	    formData.collection = $("#collection").val();
	    formData.chartType = $("#chartType").val();
	    console.dir(formData);
	    this.model.setAndShow(formData);
	    return false;
	},

	changeChartType: function() {
		this.model.showChart($("#chartType").val());
	},

	showXQueryDiv: function() {
		$("#xquery-container").toggle();
	},

	configChanged: function() {
		var xAxisQuery = $('input[name="xAxisQuery"]', this.$el).val();
	    var yAxisQuery = $('input[name="yAxisQuery"]', this.$el).val();
	    

		if(xAxisQuery && yAxisQuery) {
			var xqueryTemplate = _.template("let $xs := for $x in <%= X_AXIS %> return $x \n " 
				+ " let $values := for $x in $xs return <value><x>{$x}</x><y>{<%= Y_AXIS %>}</y></value> \n " 
				+ " return <chartxml> {$values}</chartxml>");

		    var generatedXquery = xqueryTemplate({
		    	X_AXIS: xAxisQuery, 
		    	Y_AXIS: yAxisQuery
		    });

		    console.log(generatedXquery);

		    $('#xquery-input').val(generatedXquery);		
		}
	},

	displayXmlTree: function() {
		var collection = $("#collection").val();
//		$('#samplexmlcontent').jstree({
//			"xml_data" :{
//				"ajax" : {
//					"type" : "POST",
//					"url": "/xTestAnalyzer/xmlForm.do",
//					"data" : {
//						col: collection
//					},
//					"dataType" : "text"
//				},
//			},
//			"plugins" : ["themes","xml_data"]
//		});
		
		var that = this;
		$.ajax({
			type : "POST",
			url: "/xTestAnalyzer/xmlForm.do",
			data: {
				col: collection
			},
			dataType: "text",
			success: function(data) {
				console.log("display Xml");
				console.log(data);
				$('#samplexml').val(data);
				if(collection=="stock"){
					$("#xAxisQuery").val("distinct-values(/root/element/@symbol)");
					$("#yAxisQuery").val("avg(/root/element[@symbol=$x]/@price)");
				}else if(collection="junit"){
					$("#xAxisQuery").val("distinct-values(//testSuites/@groupName)");
					$("#yAxisQuery").val("avg(//testSuites[@groupName=$x]/testSuite/@total)");
				}
				that.configChanged();
			},
			
		});
	}

    });
    
    window.ChartConfigurationView = ChartConfigurationView;
})();
