package com.emc.xtest.analyzer.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.emc.xtest.analyzer.dao.xDBProvider.XDBDaoManipulation;
import com.emc.xtest.analyzer.dao.xDBProvider.XDBEngineException;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;

@Service
public class GraphAnalysisService implements IXTestService{


private static final Logger logger = LoggerFactory.getLogger(GraphAnalysisService.class);

private XDBDaoManipulation xDBDaoManipulation;

  @Autowired
  public void setXDBDaoManipulation(
                  @Qualifier("xDBDaoManipulation") XDBDaoManipulation xDBDaoManipulation) {
    this.xDBDaoManipulation = xDBDaoManipulation;
  }
  
  public boolean libraryExist(String libPath) throws XDBEngineException{
    return xDBDaoManipulation.libraryExist(libPath);
  }
  
  public void createNewLibrary(String path)
      throws XDBEngineException {
    xDBDaoManipulation.createNewLibrary(path);
  }
  
  public boolean saveDocuments(String testlibPath, Map<String,FileInputStream> inputStreamMap) {
    boolean saveStatus = false;
    try {
      Set<Entry<String, FileInputStream>> inputStreamSet=inputStreamMap.entrySet();
      Iterator<Entry<String, FileInputStream>> iter=inputStreamSet.iterator();
      while(iter.hasNext()){
        Entry<String, FileInputStream> entry=iter.next();
        xDBDaoManipulation.saveDocument(testlibPath, entry.getKey(),BenchMarkArchiveService.generateDocument(entry.getValue()));
      }
      saveStatus = true;
    } catch (XDBEngineException e) {
      logger.error("saveXTestResult XDBEngineException:" + e);
      throw new XTestAnalyzerException("saveXTestResult XDBEngineException:" + e);
    } catch (ParserConfigurationException e) {
      logger.error("saveXTestResult ParserConfigurationException" + e);
      throw new XTestAnalyzerException("saveXTestResult ParserConfigurationException:" + e);
    } catch (SAXException e) {
      logger.error("saveXTestResult SAXException" + e);
      throw new XTestAnalyzerException("saveXTestResult SAXException:" + e);
    } catch (IOException e) {
      logger.error("saveXTestResult IOException" + e);
      throw new XTestAnalyzerException("saveXTestResult IOException:" + e);
    }
    return saveStatus;
  }
  
  public Map<String,String[]> generateBenchMarkChart(String xTestLibPath, String xAxisXPath,String yAxisXPath){
    Map<String,String[]> map=new TreeMap<String,String[]>();
    List<String> xResults;
    List<String> yResults;
    try {
      xResults = xDBDaoManipulation.queryXTestBenchMark(xTestLibPath, xAxisXPath);
      yResults=xDBDaoManipulation.queryXTestBenchMark(xTestLibPath, yAxisXPath);
      int xSize=xResults.size();
      int ySize=yResults.size();
      String[] xArray=xResults.toArray(new String[xSize]);
      String[] yArray=yResults.toArray(new String[ySize]);
      map.put("x", xArray);
      map.put("y", yArray);
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return map;
  }
  
  public List<String> generateChartByXQuery(String xTestLibPath, String XQuery){
    List<String> xResults = null;
    try {
      logger.info("Execute XQuery:"+XQuery);
      xResults = xDBDaoManipulation.queryXTestBenchMark(xTestLibPath, XQuery);
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return xResults;
  }
}
