<%@include file="/WEB-INF/xtestanalyzer/head.jsp"%>

<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="#">xTest Performance Analyzer</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li><a id="homeHLink" href="#">Home</a></li>
						<li><a
							href="http://cvs.xhive.archipel/mediawiki/index.php/XTest_:_Test_Framework">About</a></li>
					</ul>
					<!-- <form class="navbar-search pull-left">
						<input type="text" class="search-query" placeholder="Search">
					</form> -->
					<p class="navbar-text pull-right">
						<!-- Logged in as <a href="#" class="navbar-link">Zhenjie Tang</a> -->
						<c:out value="${serverTime}" />
					</p>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<div id="xTestNaviPanel" class="well sidebar-nav">
					<h4 align="center">xTest Test Suite Navigation</h4>
					<ul class="nav nav-tabs nav-stacked">
						<li>
							<div id="xDB10Group" class="accordion-heading">
								<a id="xDB10HLink" class="accordion-toggle"
									data-toggle="collapse" data-parent="#xTestNaviPanel"
									href="#xDB10Collapse">xDB10</a>
							</div>
							<div id="xDB10Collapse" class="accordion-body collapse"
								style="height: 0px;">
								<div class="accordion-inner">
									<ul id="xDB10List" class="nav nav-list">
									</ul>
								</div>
							</div>
						</li>
						<li>
							<div id="xDB101Group" class="accordion-heading">
								<a id="xDB101HLink" class="accordion-toggle"
									data-toggle="collapse" data-parent="#xTestNaviPanel"
									href="#xDB101Collapse">xDB10.1</a>
							</div>
							<div id="xDB101Collapse" class="accordion-body collapse"
								style="height: 0px;">
								<div class="accordion-inner">
									<ul id="xDB101List" class="nav nav-list">
									</ul>
								</div>
							</div>
						</li>
						<li>
							<div id="xDB103Group" class="accordion-heading">
								<a id="xDB103HLink" class="accordion-toggle"
									data-toggle="collapse" data-parent="#xTestNaviPanel"
									href="#xDB103Collapse">xDB10.3</a>
							</div>
							<div id="xDB103Collapse" class="accordion-body collapse"
								style="height: 0px;">
								<div class="accordion-inner">
									<ul id="xDB103List" class="nav nav-list">
									</ul>
								</div>
							</div>
						</li>
					</ul>
				</div>
			</div>
			<!--/span-->
			<div class="span9">
				<div id="xTestTab">
					<ul id="navul" class="nav nav-tabs">
						<li id="abli"><a href="#"
							onClick="javascript:fn_detailXTestSC()">Abstract</a></li>
						<li id="rcli"><a href="#"
							onClick="javascript:fn_runConfiguration()">Run Configuration</a></li>
						<li id="bmli"><a href="#"
							onClick="javascript:fn_benchMarkXTestSC()">BenchMark</a></li>
						<!-- <li id="rali" class="dropdown"><a class="dropdown-toggle"
							data-toggle="dropdown" href="#"
							onClick="javascript:fn_reportAnalyzerXTestSC('all')">Report
								Analysis<b class="caret"></b>
						</a>
							<ul class="dropdown-menu">
								<li><a href="#"
									onClick="javascript:fn_reportAnalyzerXTestSC('xmlSummary')">XML
										Summary Report</a></li>
								<li class="divider"></li>
								<li><a href="#"
									onClick="javascript:fn_reportAnalyzerXTestSC('result')">Performance
										Report</a></li>
								<li class="divider"></li>
								<li><a href="#"
									onClick="javascript:fn_reportAnalyzerXTestSC('log')">Log
										Report</a></li>
							</ul></li> -->
						<li id="gali"><a href="#"
							onClick="javascript:fn_graphAnalyzerXTestSC()">Graph Analysis</a></li>
						<li id="spli" class="dropdown"><a class="dropdown-toggle"
							data-toggle="dropdown" href="#"
							onClick="javascript:fn_systemPerfXTestSC('all')">System
								Performance Analysis<b class="caret bottom-up"></b>
						</a>
							<ul class="dropdown-menu bottom-up pull-right">
								<li><a href="#"
									onClick="javascript:fn_graphAnalyzerXTestSC('cpuConsumption')">CPU
										Consumption</a></li>
								<li class="divider"></li>
								<li><a href="#"
									onClick="javascript:fn_graphAnalyzerXTestSC('memoryConsumption')">Memory
										Consumption</a></li>
								<li class="divider"></li>
								<li><a href="#"
									onClick="javascript:fn_graphAnalyzerXTestSC('ioThroughput')">Disk
										IO Throughput</a></li>
							</ul></li>
					</ul>
				</div>
				<div id='xTestMainPanel' class="hero-unit">
					<p>Welcome to xTest Performance Analysis Home Page. It used to
						run xTest Test Suite and analyze test results in details.</p>
					<p>
						<!-- <a class="btn btn-primary btn-large">Try more &raquo;</a>-->
					</p>
				</div>
				<div id='xTestSCDetails'></div>
				<div id='xTestIntroduction'>
					<div class="row-fluid">
						<div class="span4">
							<h2>Display</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
						<div class="span4">
							<h2>Run</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
						<div class="span4">
							<h2>Report</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
					</div>
					<!--/row-->
					<div class="row-fluid">
						<div class="span4">
							<h2>Analysis</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
						<div class="span4">
							<h2>Graph</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
						<div class="span4">
							<h2>BenchMark</h2>
							<p>Donec id elit non mi porta gravida at eget metus. Fusce
								dapibus, tellus ac cursus commodo, tortor mauris condimentum
								nibh, ut fermentum massa justo sit amet risus. Etiam porta sem
								malesuada magna mollis euismod. Donec sed odio dui.</p>
							<p>
								<!-- <a class="btn" href="#">View details &raquo;</a>-->
							</p>
						</div>
						<!--/span-->
					</div>
					<!--/row-->
				</div>
			</div>
			<!--/span-->
		</div>
		<!--/row-->

		<hr>

		<footer>
			<p>
				<a href="#" class="navbar-link">&copy; EMC IIG xDB 2013 @
					Designed by Zhenjie Tang</a>
			</p>
		</footer>

	</div>
	<!--/.fluid-container-->

	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript" src="js/vendor/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/vendor/ajaxfileupload.js"></script>
	<script src="js/vendor/bootstrap-transition.js"></script>
	<script src="js/vendor/bootstrap-alert.js"></script>
	<script src="js/vendor/bootstrap-modal.js"></script>
	<script src="js/vendor/bootstrap-dropdown.js"></script>
	<script src="js/vendor/bootstrap-scrollspy.js"></script>
	<script src="js/vendor/bootstrap-tab.js"></script>
	<script src="js/vendor/bootstrap-tooltip.js"></script>
	<script src="js/vendor/bootstrap-popover.js"></script>
	<script src="js/vendor/bootstrap-button.js"></script>
	<script src="js/vendor/bootstrap-collapse.js"></script>
	<script src="js/vendor/bootstrap-carousel.js"></script>
	<script src="js/vendor/bootstrap-typeahead.js"></script>
	<script type="text/javascript" src="js/xtest-main.js"></script>
</body>
</html>