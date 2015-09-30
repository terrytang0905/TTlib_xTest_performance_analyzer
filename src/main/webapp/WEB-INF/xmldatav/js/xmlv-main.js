(function() {
    var eventBus = _.extend({}, Backbone.Events);

    var xmlFileInputView = new XMLFileInputView(eventBus);
    var xmlFilesCollection = new XMLFileModelCollection();

 //    eventBus.on("app:filesReady", function(files) {
	// xmlFilesCollection.reset();
	// var filesToLoad = files.length;
	// var filesLoaed = 0;
	// _.each(files, function(file) {
	//     var reader = new FileReader();
	//     reader.onload = function(e) {
	// 	var xmlFile = new XMLFileModel(e.target.result);
	// 	console.log(xmlFile.getData("//company/name"));
	// 	xmlFilesCollection.add(xmlFile);
	// 	filesLoaed++;
	// 	if(filesLoaed == filesToLoad) {
	// 	    eventBus.trigger("app:filesLoaded");
	// 	}
	//     };
	//     reader.readAsText(file);
	// });
 //    });

    // eventBus.on("app:filesLoaded", function() { 
    var xmlFile = new XMLFileModel();
	var chartModel = new ChartModel(xmlFile);
	var chartConfigurationView = new ChartConfigurationView({model: chartModel});
	var chartDisplayView = new ChartDisplayView(chartModel);
    // });

	$("#xAxisQuery").val("distinct-values(/root/element/@symbol)");
	$("#yAxisQuery").val("avg(/root/element[@symbol=$x]/@price)");
	chartConfigurationView.configChanged();
})();