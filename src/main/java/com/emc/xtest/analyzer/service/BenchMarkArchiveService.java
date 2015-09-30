package com.emc.xtest.analyzer.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.emc.xtest.analyzer.dao.xDBProvider.XDBDaoManipulation;
import com.emc.xtest.analyzer.dao.xDBProvider.XDBEngineException;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import difflib.Delta;
import difflib.DiffRow;
import difflib.DiffRow.Tag;
import difflib.DiffRowGenerator;
import difflib.DiffUtils;
import difflib.Patch;

@Service
public class BenchMarkArchiveService implements IXTestService {

  private static final Logger logger = LoggerFactory.getLogger(BenchMarkArchiveService.class);

  private XDBDaoManipulation xDBDaoManipulation;

  private String xTestResultFormatFileName;

  
  public void setxTestResultFormatFileName(String xTestResultFormatFileName) {
    this.xTestResultFormatFileName = xTestResultFormatFileName;
  }

  @Autowired
  public void setXDBDaoManipulation(
      @Qualifier("xDBDaoManipulation") XDBDaoManipulation xDBDaoManipulation) {
    this.xDBDaoManipulation = xDBDaoManipulation;
  }

  public void buildBaseLibrary(String path, String indexName, String indexDef)
      throws XDBEngineException {
    xDBDaoManipulation.buildBaseLibrary(path, indexName, indexDef);
  }

  public Map<Date, String> reviewBenchMark(String xTestLibPath) throws XTestAnalyzerException {
    try {
      TreeMap<Date, String> originalMap =
          (TreeMap<Date, String>)xDBDaoManipulation.traverseLibrary(xTestLibPath);
      return originalMap.descendingMap();
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      logger.error("reviewBenchMark XDBEngineException:" + e);
      if (e.getMessage().indexOf("DM_ERR_INVALID_LIBRARY") < 0) {
        throw new XTestAnalyzerException("reviewBenchMark XDBEngineException:" + e);
      }
    }
    return null;
  }

  public boolean saveXTestResult(String testlibPath, String fileName, InputStream inputStream) {
    boolean saveStatus = false;
    try {
      buildBaseLibrary(XTestAnalyzerConstants.XDB_XTESTLIBRARY,
          XTestAnalyzerConstants.XDB_LMPINDEXNAME, XTestAnalyzerConstants.XDB_LMPINDEXDEF);
      xDBDaoManipulation.saveDocument(testlibPath, fileName,generateDocument(inputStream));
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

  public static Document generateDocument(InputStream is) throws ParserConfigurationException,
      SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(is);
  }

  public String detailBenchMark(String xTestSuiteLibPath, String benchMarkXPath)
      throws XTestAnalyzerException {
    String xmlContent="";
    try {
      String fileURI=getFileURI(xTestResultFormatFileName);
      if(fileURI!=null){
       xmlContent =
          xDBDaoManipulation.publishChildDocument(xTestSuiteLibPath, benchMarkXPath,
              fileURI);
      }
      return xmlContent;
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      logger.error("reviewBenchMark XDBEngineException:" + e);
      throw new XTestAnalyzerException("reviewBenchMark XDBEngineException:" + e);
    }
  }

  private String getFileURI(String fileName){
    URL fileURI=Resources.getResource(fileName);
    return fileURI.toString();
  }
  
  public List compareBenchMarks(String orginalSource, String newSource) {
    List<String> original = null;
    List<String> revised = null;
    try {
      original = Files.readLines(new File(orginalSource), null);
      revised = Files.readLines(new File(newSource), null);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Patch patch = DiffUtils.diff(original, revised);

    for (Delta delta : patch.getDeltas()) {
      List<?> list = delta.getRevised().getLines();
      for (Object object : list) {
        System.out.println(object);
      }
    }

    DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
    builder.showInlineDiffs(false);
    DiffRowGenerator generator = builder.build();
    for (Delta delta : patch.getDeltas()) {
      List<DiffRow> generateDiffRows =
          generator.generateDiffRows((List<String>)delta.getOriginal().getLines(),
              (List<String>)delta.getRevised().getLines());
      int leftPos = delta.getOriginal().getPosition();
      int rightPos = delta.getRevised().getPosition();
      for (DiffRow row : generateDiffRows) {
        Tag tag = row.getTag();
        if (tag == Tag.INSERT) {
          System.out.println("Insert: ");
          System.out.println("new-> " + row.getNewLine());
          System.out.println("");
        } else if (tag == Tag.CHANGE) {
          System.out.println("change: ");
          System.out.println("old-> " + row.getOldLine());
          System.out.println("new-> " + row.getNewLine());
          System.out.println("");
        } else if (tag == Tag.DELETE) {
          System.out.println("delete: ");
          System.out.println("old-> " + row.getOldLine());
          System.out.println("");
        } else if (tag == Tag.EQUAL) {
          System.out.println("equal: ");
          System.out.println("old-> " + row.getOldLine());
          System.out.println("new-> " + row.getNewLine());
          System.out.println("");
        } else {
          throw new IllegalStateException("Unknown pattern tag: " + tag);
        }
      }
    }
    return null;

  }

}
