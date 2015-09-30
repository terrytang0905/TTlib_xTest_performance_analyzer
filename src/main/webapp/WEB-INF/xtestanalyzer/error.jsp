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
					<form class="navbar-search pull-left">
						<input type="text" class="search-query" placeholder="Search">
					</form>
					<p class="navbar-text pull-right">
						<!-- Logged in as <a href="#" class="navbar-link">Zhenjie Tang</a> -->
					</p>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<p> System Error ! </p>
		${exception.xTestMessage}
		${exception.message}  <br/>
	 <br/>
		<hr>

		<footer>
			<p>&copy; EMC IIG xDB 2013 @ Designed by Zhenjie Tang</p>
		</footer>

	</div>
	<!--/.fluid-container-->

<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script type="text/javascript" src="js/vendor/jquery-1.8.3.min.js"></script>
	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$("#homeHLink").click(function() {
								window.location.reload();
							});
						});
	</script>
</body>
</html>