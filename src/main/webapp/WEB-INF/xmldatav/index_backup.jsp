<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>XML Data V</title>
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

        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>
                    <a class="brand" href="#">XML DATA V</a>
                    <div class="nav-collapse collapse">
                        <ul class="nav">
                            <li class="active"><a href="#">Home</a></li>
                            <li><a href="#about">About</a></li>
                            <li><a href="#contact">Contact</a></li>
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Action</a></li>
                                    <li><a href="#">Another action</a></li>
                                    <li><a href="#">Something else here</a></li>
                                    <li class="divider"></li>
                                    <li class="nav-header">Nav header</li>
                                    <li><a href="#">Separated link</a></li>
                                    <li><a href="#">One more separated link</a></li>
                                </ul>
                            </li>
                        </ul>
                        <form class="navbar-form pull-right">
                            <input class="span2" type="text" placeholder="Email">
                            <input class="span2" type="password" placeholder="Password">
                            <button type="submit" class="btn">Sign in</button>
                        </form>
                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container">

            <!-- Main hero unit for a primary marketing message or call to action -->
            <div class="hero-unit">
                <h1>Visualize your XML data with XQuery!</h1>		  
            </div>

            <!-- Example row of columns -->
            <div class="row">
                <div class="span4 chart-config">
                    <h2>Configurations</h2>
                    <p>Char configurations here: </p>
            <p>
                <select id="collection" placeholder="Collection">
                 	<option value="stock">Stock</option>
                    <option value="junit">JUnit</option>
                </select>
                <a id="xmlDataLink" href="/testdata/test.xml" target="_blank">sample</a>
            </p>
            <p><input type="text" name="title" placeholder="Chart title"></p>
		    <p><input type="text" name="ytitle" placeholder="Y Axis Title"></p>
		    <p><input type="text" name="seriesName" placeholder="X legend name"></p>
		    <p><input type="text" name="xAxisQuery" value="distinct-values(/root/element/@symbol)" placeholder="X Axis"></p>
            <p><input type="text" name="yAxisQuery" value="avg(/root/element[@symbol=$x]/@price)" placeholder="Y Axis"></p>
                    <p><a class="btn show-chart" href="#">Show chart &raquo;</a></p>
                </div>

                <div class="span6">
                    <h2>XQuery</h2>
                    <p>Generated XQuery: </p>
                    <div id="xquery-container" class="xquery-div">
						<textarea rows="10" cols="150" id="xquery-input"></textarea>
		    		</div>		      
				</div>
            </div>
			
			<div class="row">
				<div class="span12">
                    <h2>Chart</h2>
                    <p>Display chart here: </p>
                    <div id="chart-container" class="chart-div">
		    		</div>		      
				</div>	      
			</div>
			
            <hr>

            <footer>
                <p>&copy; wuh 2012</p>
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

        <script src="js/plugins.js"></script>
	<script src="js/model/XMLFileModel.js"></script>
	<script src="js/model/XMLFileModelCollection.js"></script>
	<script src="js/model/ChartModel.js"></script>
	<script src="js/view/XMLFileInputView.js"></script>
	<script src="js/view/ChartConfigurationView.js"></script>
	<script src="js/view/ChartDisplayView.js"></script>
        <script src="js/main.js"></script>
    </body>
</html>
