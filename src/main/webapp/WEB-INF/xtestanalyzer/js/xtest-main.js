var xTestNameArg;
var xTestPrjCategoryArg;
function fn_linkXTestSCDetails(xTestName) {
	var xTestul = $("#" + xTestName).parent();
	xTestul.children().each(function() {
		$(this).removeClass("active");
	});
	xTestNameArg = xTestName;
	fn_detailXTestSC();
}

function fn_detailXTestSC() {
	if (typeof xTestNameArg != 'undefined') {
		$("#" + xTestNameArg).addClass("active");
		$.ajax({
			type : "POST",
			url : "/xTestAnalyzer/detailXTestSC.do",
			data : xTestNameArg,
			contentType : "application/json",
			dataType : "html",
			success : function(content) {
				// alert(content);
				$("#navul").children().each(function() {
					$(this).removeClass("active");
				});
				$("#abli").addClass("active");
				$("#xTestMainPanel").empty();
				$("#xTestSCDetails").empty();
				$("#xTestMainPanel").html(
						"<p>xTest test suite " + xTestNameArg
								+ " code description</p>");
				$("#xTestSCDetails").html(
						'<div contenteditable="true" class="tab-content row-fluid span10"><pre>'
								+ content + '</pre></div>');
				$("#xTestIntroduction").hide();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseText);
			}
		});
		return false;
	}
}

function fn_runConfiguration() {
	if (typeof xTestNameArg != 'undefined') {
		$("#" + xTestNameArg).addClass("active");
		$("#navul").children().each(function() {
			$(this).removeClass("active");
		});
		$("#rcli").addClass("active");
		$("#xTestMainPanel").empty();
		$("#xTestSCDetails").empty();
		fn_listenRunTarget();
	}
}

function fn_listenRunTarget() {
	$("#xTestMainPanel").html(
			"<table id=\"runStatusTable\"> "
					+ "<thead><tr><th>xTest Ant Run Status: "
					+ "</th></tr></thead><tbody><tr><td>WAITING</td></tr>"
					+ "</tbody></table>");
	$.extend({
		funD : function() {
			$.ajax({
				type : "POST",
				url : "/xTestAnalyzer/listenRunTarget.do",
				data : xTestNameArg,
				contentType : "application/json",
				dataType : "text",
				success : function(content) {
					var responseheader = content.substring(0, content
							.indexOf('='));
					var responseBody = content
							.substring(content.indexOf('=') + 1);
					if (responseBody != null && responseBody != "") {
						$("#xTestSCDetails").append(
								'<pre class="">' + responseBody + '</pre>');
					}
					if (responseheader == "NO_RUNNING") {
						fn_configXTestSC();
					} else if (responseheader == "RUNNING") {
						setTimeout("$.funD()", 5000);
					} else if (responseheader == "COMPLETE") {
						$("#runStatusTable tr").last().html(
								"<td>" + responseheader + "</td>");
						fn_saveXTestResult(xTestNameArg);
					} else {
						$("#runStatusTable tr").last().html(
								"<td>" + responseheader + "</td>");
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.responseText);
				}
			});
		}
	});
	$.funD();
}

function fn_configXTestSC() {
	$("#xTestMainPanel").html(
			'<pre>Building xTest Ant run configuration...</pre>');
	$("#xTestSCDetails").empty();
	$
			.ajax({
				type : "POST",
				url : "/xTestAnalyzer/configurateTarget.do",
				data : xTestNameArg,
				contentType : "application/json",
				dataType : "json",
				success : function(map) {
					var responseElement = "";
					$
							.each(
									map,
									function(key, values) {
										if (key == "XTEST_HOME"
												|| key == "XDB_VERSION"
												|| key == "XDB_LOCATION"
												|| key == "SUITE"
												|| key == "SUITE_PARAMETERS") {
											responseElement += "<tr><td>"
													+ key
													+ "</td><td><input width=\"60%\" type=\"text\" name=\""
													+ key
													+ "\" value=\""
													+ values
													+ "\" readonly=\"readonly\" /></td></tr>";
										} else if (key == "XHIVE_JAR") {
											responseElement += "<tr><td>XHIVE_JAR"
													+ "</td><td><input width=\"60%\" type=\"text\" name=\"XHIVE_JAR\" value=\""
													+ values
													+ "\" readonly=\"readonly\" /><span class=\"icon-bar\"></span><a data-toggle=\"modal\" href=\"#xhiveReplace\" onClick=\"javascript:fn_openXHivePage();\">Customize</a></td></tr>";
											// responseElement +="<tr><td><span
											// class=\"icon-bar\"></span></td><td><form
											// id=\"xhiveUpdateForm\"
											// class=\"well form-inline\"
											// enctype=\"multipart/form-data\">"
											// +"<input id=\"xhivefile\"
											// type=\"file\" name=\"xhivefile\"
											// /><span
											// class=\"icon-bar\"></span><input
											// type=\"button\" onclick=\"return
											// fn_replaceXHiveJar();\"
											// value=\"Update\"
											// /></form></td></tr>";
										} else {
											if (values == null) {
												values = "";
											}
											responseElement += "<tr><td>"
													+ key
													+ "</td><td><input width=\"60%\" type=\"text\" name=\""
													+ key + "\" value=\""
													+ values
													+ "\" /></td></tr>";
										}
									});

					$("#xTestMainPanel")
							.html(
									"<div id=\"xhiveReplace\" class=\"modal hide fade in\" style=\"display:none;\"></div>"
											+ "<form id=\"runParametersForm\" class=\"form-horizontal\">"
											+ " <legend>xTest Ant Run Configuration</legend><table class=\"table .span5 table-striped\"> "
											+ "<tbody>"
											+ responseElement
											+ "</tbody></table>"
											+ " <div class=\"form-actions\">"
											// + " <button id=\"buildButton\"
											// type=\"submit\" class=\"btn
											// btn-primary\">Build</button><span
											// class=\"icon-bar\"></span>"
											+ " <button id=\"runButton\" type=\"submit\" class=\"btn btn-primary\" >Run</button>"
											+ " </div></fieldset></form>");
					$("#xTestIntroduction").hide();

					$("#runButton").click(function() {
						$("#runButton").prop("disabled", false);
						var formData = $("#runParametersForm").serialize();
						$("#xTestMainPanel").empty();
						$.ajax({
							type : "POST",
							url : "/xTestAnalyzer/executeTarget.do",
							data : formData,
							contentType : "application/x-www-form-urlencoded",
							dataType : "text",
							success : function(content) {
								fn_listenRunTarget();
							}
						});
						return false;
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.responseText);
				}
			});
	return false;
}

function fn_openXHivePage() {
	var response = "<div class=\"modal-header\">"
			+ "<a class=\"close\" data-dismiss=\"modal\">x</a><h3>Selete customized xhive.jar file</h3></div>";
	response += "<div class=\"modal-body\" id=\"xhiveReplaceBody\"><form id=\"xhiveUpdateForm\" class=\"well form-inline\" enctype=\"multipart/form-data\">"
			+ "<input id=\"xhivefile\" type=\"file\" name=\"xhivefile\" /><span class=\"icon-bar\"></span><input type=\"button\" onclick=\"return fn_replaceXHiveJar();\" value=\"Update\" /></form></div>";
	$("#xhiveReplace").html(response);
}

function fn_replaceXHiveJar() {
	// $("#xhiveReplaceBody").html("<div class=\"progress progress-striped
	// active\"><div class=\"bar\" style=\"width:50%\"></div></div>");
	$
			.ajaxFileUpload({
				type : "POST",
				url : '/xTestAnalyzer/replaceXHiveJar.do',
				secureuri : false,
				// data : xTestNameArg,
				fileElementId : "xhivefile",
				dataType : "text",
				success : function(data, status) {
					if (data) {
						$("#xhiveReplaceBody")
								.html(
										"<p> The new xhive.jar has been updated successfully.</p>");
					} else {
						$("#xhiveReplaceBody")
								.html(
										"<p> Couldn't replace the current xhive.jar successfully.</p>");
					}
				},
				error : function(data, status, e) {
					$("#xhiveReplaceBody")
							.html(
									"<p> Couldn't replace the current xhive.jar successfully.</p>");
				}
			});
	return false;
}

function fn_saveXTestResult(xTestSuiteName) {
	var saveDiv = "<div id=\"saveResult\"><button id=\"saveButton\" type=\"submit\" class=\"btn btn-primary\" >Save</button></div>";
	$("#xTestMainPanel").append(saveDiv);

	$("#saveButton")
			.click(
					function() {
						$("#saveResult")
								.html(
										'<pre>Saving Test Result is in progress...</pre>');
						$
								.ajax({
									type : "POST",
									url : "/xTestAnalyzer/saveXTestResult.do",
									data : xTestSuiteName,
									contentType : "application/json",
									dataType : "text",
									success : function(content) {
										if (content == 'true') {
											$("#saveResult")
													.html(
															'<pre><a href="#" onClick="javascript:fn_benchMarkXTestSC();">Save '
																	+ xTestSuiteName
																	+ ' Test Result in BenchMark DB Successfully</a></pre>');
										} else {
											$("#saveResult")
													.html(
															'<pre><a href="#" onClick="javascript:fn_benchMarkXTestSC();">Fail to save '
																	+ xTestSuiteName
																	+ ' Test Result in BenchMark DB</a></pre>');
										}
									},
									error : function(XMLHttpRequest,
											textStatus, errorThrown) {
										alert(XMLHttpRequest.responseText);
									}
								});
					});
}

var xTestResultList;
function fn_benchMarkXTestSC() {
	if (typeof xTestNameArg != 'undefined') {
		$("#" + xTestNameArg).addClass("active");
		$("#navul").children().each(function() {
			$(this).removeClass("active");
		});
		$("#bmli").addClass("active");
		$("#xTestMainPanel").empty();
		$("#xTestSCDetails").empty();
		$("#xTestMainPanel")
				.html(
						"<p>xTest Test Suite "
								+ xTestNameArg
								+ " BenchMark</p>"
								+ "<form id=\"uploadForm\" class=\"well form-inline\" enctype=\"multipart/form-data\">"
								+ "<fieldset>"
								+ "<p> Local Test Result Upload </p><br><p><input type=\"hidden\" name=\"xTestName\" value="
								+ xTestNameArg
								+ "/> <input id=\"file\" type=\"file\" name=\"file\" /><span class=\"icon-bar\"></span><input type=\"button\" onclick=\"return fn_uploadBenchMark();\" value=\"Upload\" /></p><br>"
								+ "</fieldset></form>");
		$("#xTestIntroduction").hide();
		$
				.ajax({
					type : "POST",
					url : "/xTestAnalyzer/reviewBenchMark.do",
					data : xTestNameArg,
					contentType : "application/json",
					dataType : "json",
					success : function(jsonMap) {
						xTestResultList = jsonMap;
						var responseElements = '<div class="row-fluid span10"><div id="xmlcontent" class="modal hide fade in" style="display:none;"></div>'
								+ '<table class="table table-striped">';
						responseElements += "<thead><th>Date</th><th>xTest TestResult</th></thead><tbody>";
						$
								.each(
										jsonMap,
										function(key, values) {
											console.log(key + ":" + values);
											responseElements += "<tr><td>"
													+ key
													+ "</td><td><a data-toggle=\"modal\" href=\"#xmlcontent\" onClick=\"javascript:fn_detailBenchMark('"
													+ values + "');\">"
													+ values + "</a></td></tr>";
										});
						responseElements += "</tbody></table></div>";
						$("#xTestSCDetails").html(responseElements);
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert(XMLHttpRequest.responseText);
					}
				});
		return false;
	}
}

function fn_uploadBenchMark() {
	$
			.ajaxFileUpload({
				type : "POST",
				url : '/xTestAnalyzer/handleUploadFile.do',
				secureuri : false,
				// data : xTestNameArg,
				fileElementId : "file",
				dataType : "text",
				success : function(data, status) {
					if (data) {
						$("#xTestMainPanel")
								.append(
										'<pre>Upload '
												+ xTestNameArg
												+ ' Test Result in BenchMark DB Successfully</pre>');
					} else {
						$("#xTestMainPanel").append(
								'<pre>Fail to upload ' + xTestNameArg
										+ ' Test Result in BenchMark DB</pre>');
					}
					fn_benchMarkXTestSC();
				},
				error : function(data, status, e) {
					$("#xTestMainPanel").append(
							'<pre>Fail to upload ' + xTestNameArg
									+ ' Test Result in BenchMark DB</pre>');

				}
			});
	return false;
}

function fn_detailBenchMark(xTestTestResult) {
	$("#xmlcontent").empty();
	$
			.ajax({
				type : "POST",
				url : "/xTestAnalyzer/detailBenchMark.do",
				data : xTestTestResult,
				contentType : "application/json",
				dataType : "html",
				success : function(xmlContent) {
					// var contentResult=fn_loadXML(xmlContent);

					var response = "<div id=\"benchmarkModal\" class=\"modal-header\">"

							+ "<ul class=\"nav nav-tabs\" align=\"right\"><li class=\"dropdown\"><a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">Compare</a>"
							+ "<ul class=\"dropdown-menu\">";
					$
							.each(
									xTestResultList,
									function(key, values) {
										response += "<li class=\"\"><a onclick=\"return fn_compareBenchMark('"
												+ xTestTestResult
												+ "','"
												+ values
												+ "');\" href=\"#\">"
												+ values + "</a></li>";
									});
					response += "</ul></li><li><a class=\"btn btn-navbar\" onclick=\"return fn_downloadBenchMark("
							+ xTestTestResult
							+ ");\" href=\"#\">Download</a>"
							+ "</li><li><a class=\"btn btn-navbar\" onclick=\"return fn_removeBenchMark("
							+ xTestTestResult
							+ ");\"  href=\"#\">Delete</a>"
							+ "</li><li><a class=\"close\" data-dismiss=\"modal\">x</a></li></ul><h3 align=\"left\">"
							+ xTestTestResult + "</h3></div>";
					response += "<div id=\"benchmarkModalBody\" class=\"modal-body\">"
							+ xmlContent + "</div>";
					$("#xmlcontent").html(response);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(XMLHttpRequest.responseText);
				}
			});
	return false;
}

function fn_compareBenchMark(xTestTestResult) {
	alert("The development is in progress :(");
}

function fn_downloadBenchMark(xTestTestResult) {
	alert("The development is in progress :(");
}

function fn_removeBenchMark(xTestTestResult) {
	alert("The development is in progress :(");
}

function fn_loadXML(xmlContent) {
	var xmlDoc = null;
	if (!window.DOMParser && window.ActiveXObject) {
		// for IE
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = false;
		xmlDoc.loadXML(xmlContent);
	} else if (window.DOMParser && document.implementation
			&& document.implementation.createDocument) {
		// for Mozilla, Firefox, Opera,
		var domParser = new DOMParser();
		xmlDoc = domParser.parseFromString(xmlContent, "text/xml");
	} else {
		alert('Your browser cannot handle this script');
	}
	var xmlTxt = null;
	if (xmlDoc != null) {
		xmlTxt = "<table border='1'>";
		$(xmlDoc).find("suite").each(function() {
			xmlTxt += "<tr><td>";
			xmlTxt += $(this).attr("name");
			xmlTxt += "</td><td>";
			xmlTxt += $(this).attr("id");
			xmlTxt += "</td></tr>";
		});
		xmlTxt += "</table>";
		return xmlTxt;
	}
	;
}

function fn_reportAnalyzerXTestSC(reportType) {
	if (typeof xTestNameArg != 'undefined') {
		$("#" + xTestNameArg).addClass("active");
		$.ajax({
			type : "POST",
			url : "/xTestAnalyzer/reportAnalysis.do",
			data : xTestNameArg,
			contentType : "application/json",
			dataType : "text",
			success : function(content) {
				$("#navul").children().each(function() {
					$(this).removeClass("active");
				});
				$("#rali").addClass("active");
				$("#xTestMainPanel").empty();
				$("#xTestSCDetails").empty();
				$("#xTestMainPanel").html(
						"<p>xTest test suite " + xTestNameArg
								+ " code description</p>");
				$("#xTestSCDetails").html(
						'<div contenteditable="true" class="row-fluid span10"><pre>'
								+ content + '</pre></div>');
				$("#xTestIntroduction").hide();
			}
		});
		return false;
	}
	;
}

function fn_graphAnalyzerXTestSC() {
	$("#navul").children().each(function() {
		$(this).removeClass("active");
	});
	$("#gali").addClass("active");
	$("#xTestMainPanel").empty();
	$("#xTestSCDetails").empty();

	$.ajax({
		type : "POST",
		url : "/xTestAnalyzer/loadSourceData.do",
		data : xTestNameArg,
		contentType : "application/json",
		dataType : "text",
		success : function(content) {
			alert("load source data status:" + content);
		}
	});

	$("#xTestMainPanel").html(
			"<p>xTest test suite " + xTestNameArg
					+ " BenchMark Graph Chart Analysis</p>");
	$("#xTestSCDetails")
			.html(
					"<div id=\"container\">"
							+ "<div class=\"row\"><div class=\"span6 chart-config\">"
							+ "<form class=\"form-horizontal\"><div class=\"control-group\">"
							+ "<label for=\"collection\" class=\"control-label\">Data collection:</label>"
							+ "<div class=\"controls\"><select id=\"collection\" placeholder=\"Collection\">"
							+ "<option value=\"stock\">Stock</option><option value=\"junit\">JUnit</option></select>"
							+ "<span class=\"help-inline\"><a id='collectionSample' href=\"#\">Sample XML</a></span>"
							+ "</div></div>"
							+ "<div class=\"control-group\"><label for=\"xAxisQuery\" class=\"control-label\">Series 1 (x):</label>"
							+ "<div class=\"controls\"><input type=\"text\" name=\"xAxisQuery\" value=\"\" placeholder=\"X Axis\" id=\"xAxisQuery\"></div>"
							+ "</div>"
							+ "<div class=\"control-group\"> <label for=\"yAxisQuery\" class=\"control-label\"> Series 2 (y):</label>"
							+ "<div class=\"controls\"><input type=\"text\" name=\"yAxisQuery\" value=\"\" placeholder=\"y Axis\" id=\"yAxisQuery\"></div> </div>"
							+ "<div class=\"control-group\"> <label for=\"chartType\" class=\"control-label\">    Chart Type: </label>  "
							+ "<div class=\"controls\"><select id=\"chartType\" placeholder=\"Collection\"><option value=\"column\">Bar</option>"
							+ "<option value=\"pie\">Pie</option><option value=\"line\">line</option></select>"
							+ "<span class=\"help-inline\"><a class=\"btn show-chart\" href=\"#\">Visualize</a></span>"
							+ "</div></div><p><a id=\"showXQuery\" href=\"#\">Advanced mode</a><p>"
							+ "<div id=\"chart-container\" class=\"chart-div\"><p>show chart here</p></div></form></div>"
							+ "<div class=\"span6\"><div id=\"xml-container\" class=\"sample-xml-div\"> <p>Sample XML</p>"
							+ "<textarea rows=\"10\" cols=\"150\" id=\"samplexml\"></textarea></div>"
							+ "<div id=\"xquery-container\" class=\"xquery-div\"><p>XQuery</p><textarea rows=\"10\" cols=\"150\" id=\"xquery-input\"></textarea></div></div>"
							+ "</div></div>");
}

function fn_systemPerfXTestSC(graphType) {
	alert("The development is in progress :(");
	if (typeof xTestNameArg != 'undefined') {
		$("#" + xTestNameArg).addClass("active");
		$.ajax({
			type : "POST",
			url : "/xTestAnalyzer/graphAnalysis.do",
			data : xTestNameArg,
			contentType : "application/json",
			dataType : "text",
			success : function(content) {
				$("#navul").children().each(function() {
					$(this).removeClass("active");
				});
				$("#spli").addClass("active");
				$("#xTestMainPanel").empty();
				$("#xTestSCDetails").empty();
				$("#xTestMainPanel").html(
						"<p>xTest test suite " + xTestNameArg
								+ " code description</p>");
				$("#xTestSCDetails").html(
						'<div contenteditable="true" class="row-fluid span10"><pre>'
								+ content + '</pre></div>');
				$("#xTestIntroduction").hide();
			}
		});
		return false;
	}
	;
}

$(document)
		.ready(
				function() {
					$("#homeHLink").click(function() {
						window.location.reload();
					});
					$("#xDB10HLink").click(function() {
						fn_listXTestSC('xDB10List');
					});
					$("#xDB101HLink").click(function() {
						fn_listXTestSC('xDB101List');
					});
					$("#xDB103HLink").click(function() {
						fn_listXTestSC('xDB103List');
					});
					function fn_listXTestSC(xTestListArg) {
						xTestPrjCategoryArg = xTestListArg;
						$
								.ajax({
									beforeSend : function(req) {
										req.setRequestHeader("Accept",
												"application/json");
									},
									type : "POST",
									url : "/xTestAnalyzer/listXTestSC.do",
									data : xTestListArg,
									contentType : "application/json",
									success : function(data) {
										// var
										// xTestArray=jQuery.makeArray(data);
										var responseElement = $("#"
												+ xTestListArg);
										responseElement.empty();
										for (x in data) {
											var displayName = data[x]
													.substring(
															data[x]
																	.lastIndexOf('/') + 1,
															data[x]
																	.indexOf('.java'));
											responseElement
													.append('<li id="'
															+ displayName
															+ '"><a href="#" onClick="javascript:fn_linkXTestSCDetails(\''
															+ displayName
															+ '\');">'
															+ displayName
															+ '</a></li>');
										}
									},
									error : function(XMLHttpRequest,
											textStatus, errorThrown) {
										alert(XMLHttpRequest.responseText);
									}
								});
						return false;
					}
					$('.dropdown-toggle').dropdown();
				});