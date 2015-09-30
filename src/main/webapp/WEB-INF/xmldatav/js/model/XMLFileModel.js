(function() {
    var XMLFileModel = Backbone.Model.extend({
	initialize: function() {
	    console.log("init XMLFileModel");
	},

	fetchQueryResult: function(collection, xquery, callback) {
		$.ajax({
			type : "POST",
			//url: "/testdata/test.xml",
			url: "/xTestAnalyzer/graphChart.do",
			data: {
				query: xquery,
				col: collection
			},
			success: function(data) {
				console.dir(data);
				callback(data);
			},
			dataType: "xml"
		});
	},

	getData: function(root, xpathQuery) {
	    var nodes = $(root).xpath(xpathQuery);
	    return _.map(nodes, function(node) {
		return $(node).text().trim();
	    });
	}
    });

    window.XMLFileModel = XMLFileModel;
})();