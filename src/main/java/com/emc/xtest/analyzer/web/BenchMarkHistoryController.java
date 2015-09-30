package com.emc.xtest.analyzer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.emc.xtest.analyzer.service.BenchMarkArchiveService;
import com.emc.xtest.analyzer.service.XTestExecutorService;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;
import com.emc.xtest.analyzer.utilities.XTestAntParameter;

@Controller
public class BenchMarkHistoryController {

  private static final Logger logger = LoggerFactory.getLogger(BenchMarkHistoryController.class);
  private BenchMarkArchiveService benchMarkArchiveService;
  private String xTestLibPath;

  @Autowired
  public void setBenchMarkArchiveService(
      @Qualifier("benchMarkArchiveService") BenchMarkArchiveService benchMarkArchiveService) {
    this.benchMarkArchiveService = benchMarkArchiveService;
  }

  @RequestMapping(value = "/reviewBenchMark", method = RequestMethod.POST)
  public @ResponseBody
  Map<Date, String> reviewBenchMark(@RequestBody String xTestName) throws XTestAnalyzerException {
    logger.info("Review xTest BenchMark History: " + xTestName);
    xTestLibPath = XTestAnalyzerConstants.XDB_XTESTLIBRARY + "/" + xTestName;
    return benchMarkArchiveService.reviewBenchMark(xTestLibPath);
  }

  @RequestMapping(value = "/detailBenchMark", method = RequestMethod.POST)
  public @ResponseBody
  String detailBenchMark(@RequestBody String resultName) throws XTestAnalyzerException {
    String benchMarkPath = xTestLibPath + "/" + resultName;
    logger.info("Show BenchMark details: " + benchMarkPath);
    String ds = benchMarkArchiveService.detailBenchMark(xTestLibPath,resultName);
    return ds;
  }

  @RequestMapping(value = "/handleUploadFile", method = RequestMethod.POST)
  public @ResponseBody
  Boolean handleUploadFile(@RequestParam("file") CommonsMultipartFile file)
      throws XTestAnalyzerException {
    // @RequestParam("file") must match the html <input> file name
    String fileName = file.getOriginalFilename();
    logger.info("Upload XTest Result: " + fileName);
    boolean uploadResult = false;
    if (!file.isEmpty()) {
      InputStream is;
      try {
        is = file.getInputStream();
        uploadResult = benchMarkArchiveService.saveXTestResult(xTestLibPath, fileName, is);
      } catch (IOException e) {
        throw new XTestAnalyzerException("handleUploadFile IOException:" + e);
      }
    }
    return uploadResult;
  }

  @RequestMapping(value = "/saveXTestResult", method = RequestMethod.POST)
  public @ResponseBody
  Boolean saveXTestResult(@RequestBody String xTestName) throws FileNotFoundException {
    logger.info("Save XTest Result: " + xTestName);
    boolean result = false;
    String logBaseOut = XTestExecutorService.runParameters.get(XTestAntParameter.BASE_OUT);
    if (logBaseOut != null) {
      xTestLibPath = XTestAnalyzerConstants.XDB_XTESTLIBRARY + "/" + xTestName;
      String logBaseOutDir = logBaseOut.substring(0, logBaseOut.lastIndexOf(File.separator));
      String logBaseOutFileSuffix =
          logBaseOut.substring(logBaseOut.lastIndexOf(File.separator) + 1);
      File[] resultFiles = sortFilesByLastModified(logBaseOutDir);
      File xmllog = null;
      int fileNum = resultFiles.length;
      for (int i = fileNum - 1; i > 0; i--) {
        String fileName = resultFiles[i].getName();
        if (fileName.startsWith(logBaseOutFileSuffix) && fileName.lastIndexOf(".xml") >= 0) {
          xmllog = resultFiles[i];
          break;
        }
      }
      if (xmllog != null) {
        FileInputStream fis = new FileInputStream(xmllog);
        result = benchMarkArchiveService.saveXTestResult(xTestLibPath, xmllog.getName(), fis);
      }
    }
    return result;
  }

  public static File[] sortFilesByLastModified(String fileDirectory) {
    File resultDir = new File(fileDirectory);
    File[] resultFiles = resultDir.listFiles();
    Arrays.sort(resultFiles, new Comparator<File>() {

      public int compare(File f1, File f2) {
        Long f1lastM = Long.valueOf(f1.lastModified());
        Long f2lastM = Long.valueOf(f2.lastModified());
        return f1lastM.compareTo(f2lastM);
      }
    });
    return resultFiles;
  }
}
