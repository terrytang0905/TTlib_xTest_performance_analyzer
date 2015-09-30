(function() {
    var XMLFileInputView = Backbone.View.extend({
	el: ".xml-file-upload input[type='file']",
	
	events: {
	    "change" : "uploadFiles"
	},
	
	initialize: function(eventBus) {
	    console.log("Init XMLFileInputView");
	    this.eventBus = eventBus;
	},

	uploadFiles: function() {
	    console.log("do file upload");
	    var files = this.el.files;
	    var xmlFiles = [];
	    _.each(files, function(file) {
		if(file.type.match(/xml/)) {
		    console.log(file.type + " " + file.name);
		    xmlFiles.push(file);
		}
	    });
	    console.log("num of selected files " + files.length);
	    console.log("num of xml files " + xmlFiles.length);

	    this.eventBus.trigger("app:filesReady", xmlFiles);
	}
    });
    
    window.XMLFileInputView = XMLFileInputView;
})();
