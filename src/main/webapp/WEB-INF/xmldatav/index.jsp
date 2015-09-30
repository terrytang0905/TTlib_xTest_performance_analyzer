<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>XmlViz</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <link rel="stylesheet" href="css/bootstrap.min.css">
        <style>
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
        </style>
        <link rel="stylesheet" href="css/bootstrap-responsive.min.css">
        <link rel="stylesheet" href="css/main.css">

        <script src="js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
    </head>
    <body>
        <!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

        <!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->


        <div class="container">

            <!-- Example row of columns -->
            <div class="row">
                <div class="span6 chart-config">
                    <form class="form-horizontal">
                        <div class="control-group">
                            <label for="collection" class="control-label">    
                                Data collection:
                            </label>
                            <div class="controls">
                                <select id="collection" placeholder="Collection">
                                    <option value="stock">Stock</option>
                                    <option value="junit">JUnit</option>
                                </select>
                                <span class="help-inline"><a id='collectionSample' href="#">Sample XML</a></span>
                            </div>
                        </div>
                        <div class="control-group">
                            <label for="xAxisQuery" class="control-label">    
                                Series 1 (x):
                            </label>
                            <div class="controls">
                                <input type="text" name="xAxisQuery" value="distinct-values(/root/element/@symbol)" placeholder="X Axis" id="xAxisQuery">
                            </div>
                        </div>
                        <div class="control-group">
                            <label for="yAxisQuery" class="control-label">    
                                Series 2 (y):
                            </label>
                            <div class="controls">
                                <input type="text" name="yAxisQuery" value="avg(/root/element[@symbol=$x]/@price)" placeholder="y Axis" id="yAxisQuery">
                            </div>
                        </div>
                        <div class="control-group">
                            <label for="chartType" class="control-label">    
                                Chart Type:
                            </label>
                            <div class="controls">
                                <select id="chartType" placeholder="Collection">
                                    <option value="column">Bar</option>
                                    <option value="pie">Pie</option>
                                    <option value="line">line</option>
                                </select>
                                <span class="help-inline"><a class="btn show-chart" href="#">Visualize</a></span>
                            </div>
                        </div>
                        <p><a id="showXQuery" href="#">Advanced mode</a><p>
                        <div id="chart-container" class="chart-div">
                            <p>show chart here</p>
                        </div>  
                    </form>
                    
                </div>

                <div class="span6">
                    <div id="xml-container" class="sample-xml-div">
                        <p>Sample XML</p>
						<textarea rows="10" cols="150" id="samplexml"></textarea>
		    		</div>		
                    <div id="xquery-container" class="xquery-div">
                        <p>XQuery</p>
                        <textarea rows="10" cols="150" id="xquery-input"></textarea>
                    </div>        
				</div>
            </div>
			
            <hr>

            <footer>
                <p>&copy; wuh & tangz 2013</p>
            </footer>

        </div> <!-- /container -->

        <!--<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')</script> -->
        <script type="text/javascript" src="js/vendor/jquery-1.9.1.min.js"></script>
        <script src="js/vendor/bootstrap.min.js"></script>
	<script src="http://code.highcharts.com/highcharts.js"></script>
	<script src="js/vendor/underscore.js"></script>
	<script src="js/vendor/backbone.js"></script>
	<script src="js/vendor/jquery.xpath.js"></script>
    <script src="js/vendor/jstree/jquery.jstree.js"></script>
        <script src="js/plugins.js"></script>
	<script src="js/model/XMLFileModel.js"></script>
	<script src="js/model/XMLFileModelCollection.js"></script>
	<script src="js/model/ChartModel.js"></script>
	<script src="js/view/XMLFileInputView.js"></script>
	<script src="js/view/ChartConfigurationView.js"></script>
	<script src="js/view/ChartDisplayView.js"></script>
        <script src="js/xmlv-main.js"></script>
    </body>
</html>
