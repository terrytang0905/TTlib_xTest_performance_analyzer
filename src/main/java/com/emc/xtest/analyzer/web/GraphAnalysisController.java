package com.emc.xtest.analyzer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.emc.xtest.analyzer.dao.xDBProvider.XDBEngineException;
import com.emc.xtest.analyzer.service.GraphAnalysisService;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;

@Controller
public class GraphAnalysisController {

  private static final Logger logger = LoggerFactory.getLogger(GraphAnalysisController.class);
  private GraphAnalysisService graphAnalysisService;
  private String xTestLibPath;
  private String xquery;

  @Autowired
  public void setGraphAnalysisService(
      @Qualifier("graphAnalysisService") GraphAnalysisService graphAnalysisService) {
    this.graphAnalysisService = graphAnalysisService;
  }

  @RequestMapping(value = "/graphAnalysis", method = RequestMethod.POST)
  public @ResponseBody
  Map<String, String[]> analyzeGraph(@RequestBody String xTestName, String xAxisXPath,
      String yAxisXPath) {
    logger.info("Analyze Graph: " + xTestName);
    xTestLibPath = XTestAnalyzerConstants.XDB_XTESTLIBRARY + "/" + xTestName;
    String xXQuery = "";
    String yXQuery =
        "for $x in document('"
            + xTestLibPath
            + "') //phase[@type='RUN']/action[@name='Execute Query']/aggregated/avgExecTime return $x/text()";
    Map<String, String[]> chartMap =
        graphAnalysisService.generateBenchMarkChart(xTestLibPath, xXQuery, yXQuery);
    return chartMap;
  }

  @RequestMapping(value = "/loadSourceData", method = RequestMethod.POST)
  public @ResponseBody
  Boolean loadSourceData(@RequestBody String libPath, String filePath) throws IOException {
    logger.info("load Source Data: " + libPath);
    filePath = "/home/tangz/git/tangzGitRepo/xProject/xTestPerformanceAnalyzer/data/stockData";
    boolean result = false;
    try {
      graphAnalysisService.createNewLibrary(libPath);
      File resultDir = new File(filePath);
      System.out.println(resultDir.getAbsolutePath());
      if (resultDir.exists()) {
        File[] resultFiles = resultDir.listFiles();
        FileInputStream fis = null;
        Map<String, FileInputStream> inputStreamMap = new HashMap<String, FileInputStream>();
        for (File dataFile : resultFiles) {
          fis = new FileInputStream(dataFile);
          inputStreamMap.put(dataFile.getName(), fis);
        }
        result = graphAnalysisService.saveDocuments(libPath, inputStreamMap);
        fis.close();
      }
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      logger.error(e.getMessage());
    }
    return result;
  }

  @RequestMapping(value = "/xmlForm", method = RequestMethod.POST)
  public @ResponseBody
  String getSampleXMLForm(@RequestBody String xpath) {
    convertInputData(xpath);
    logger.info("Get XML Source Form: " + xTestLibPath);
    String xmlResult = null;
    List<String> resultList = null;
    if (xTestLibPath != null) {
      String xquery = "for $x in doc('" + xTestLibPath + "') return $x ";
      resultList = graphAnalysisService.generateChartByXQuery(xTestLibPath, xquery);
      if (resultList.size() > 0) {
        xmlResult = resultList.get(0);
      }
    }
    return xmlResult;
  }

  @RequestMapping(value = "/graphChart", method = RequestMethod.POST)
  public @ResponseBody
  String analyzeGraphChart(@RequestBody String xquerydata) {
    logger.info("Graph Chart: " + xquerydata);
    String xmlResult = null;
    List<String> resultList = null;
    try {
      if (xquerydata != null) {
        convertInputData(xquerydata);
        if (!graphAnalysisService.libraryExist(xTestLibPath)) {
          String filePath =
              "/home/tangz/git/tangzGitRepo/xProject/xTestPerformanceAnalyzer/data/stockData";
          loadSourceData(xTestLibPath, filePath);
        }
        // if (xquery == null) {
        // xquery =
        // "let $xs := for $x in distinct-values(/root/element/@company return $x);"
        // +
// "let $values := for $x in $xs return <value><x>{$x}</x><y>{avg(/root/element[@company=$x]/@price)}</y></value>"
        // + "return <chartxml>{$values}</chartxml>";
        // }
        if (xTestLibPath != null && xquery != null) {
          resultList = graphAnalysisService.generateChartByXQuery(xTestLibPath, xquery);
          if (resultList.size() > 0) {
            xmlResult = resultList.get(0);
          }
        }
      }
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return xmlResult;
  }

  private void convertInputData(String xquerydata) {
    String[] dataList = xquerydata.split("&");
    for (String data : dataList) {
      int equalTag = data.indexOf("=");
      String key = data.substring(0, equalTag);
      String value = data.substring(equalTag + 1);
      try {
        value = URLDecoder.decode(value, "UTF-8");
        value = value.replace("\n", " ");
        if ("col".equalsIgnoreCase(key)) {
          xTestLibPath = "/" + value;
        } else if ("query".equalsIgnoreCase(key)) {
          xquery = value;
        }
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
