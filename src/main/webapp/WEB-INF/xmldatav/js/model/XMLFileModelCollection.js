(function() {
    var XMLFileModelCollection = Backbone.Collection.extend({
	model: XMLFileModel,

	initialize: function() {
	    console.log("init XMLFileModleCollection");
	},
	
	getData: function(xpathQuery) {
	    var data = [];
	    this.each(function(xmlFileModel) {
		[].push.apply(data, xmlFileModel.getData(xpathQuery));
	    });
	    return data;
	}
    });
    
    window.XMLFileModelCollection = XMLFileModelCollection;
})();