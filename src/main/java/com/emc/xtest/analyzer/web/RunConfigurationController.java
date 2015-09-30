package com.emc.xtest.analyzer.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.emc.xtest.analyzer.service.XTestExecutorService;
import com.emc.xtest.analyzer.service.XTestExecutorThread;
import com.emc.xtest.analyzer.service.XTestExecutorThread.ExecuteStatus;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;
import com.emc.xtest.analyzer.utilities.XTestAntParameter;
import com.google.common.io.Files;

/**
 * Handles requests for the application RunConfigurationController page.
 */
@Controller
@Scope("globalSession")
public class RunConfigurationController {

  private static final Logger logger = LoggerFactory.getLogger(RunConfigurationController.class);
  private XTestExecutorService executorService;
  private ExecuteStatus xTestExecuteStatus;
  private Map<XTestAntParameter, String> configurationParameters = null;
  private String xTestSuiteClassName;
  private static Map<XTestAntParameter, String> RUN_PARAMS = null;
  private String antLogMessage = "";

  @Autowired
  public void setXTestExecutorService(
      @Qualifier("xTestExecutorService") XTestExecutorService executorService) {
    this.executorService = executorService;
  }

  @RequestMapping(value = "/configurateTarget", method = RequestMethod.POST)
  public @ResponseBody
  Map<XTestAntParameter, String> configurateExecutionTarget(@RequestBody String xTestName) {
    this.xTestSuiteClassName = xTestName;
    logger.info("Configurate Execution Target: " + xTestSuiteClassName);
    if (xTestExecuteStatus == XTestExecutorThread.ExecuteStatus.NO_RUNNING) {
      try {
        configurationParameters = executorService.buildRunConfiguraion(xTestSuiteClassName);
      } catch (XTestAnalyzerException e) {
        // TODO Auto-generated catch block
        logger.error("Couldn't configurate xTest Execution Target:" + e);
        throw new XTestAnalyzerException(
            "XTestAnalyzerException when configurating xTest Execution Target:" + e);
      }
    }
    return configurationParameters;
  }

  @RequestMapping(value = "/replaceXHiveJar", method = RequestMethod.POST)
  public @ResponseBody
  boolean replaceXHiveJar(@RequestParam("xhivefile") CommonsMultipartFile xhivefile) {
    boolean updateResult = false;
    try {
      String updatedFileLocation = xhivefile.getOriginalFilename();
      File newXHive = getCustomizedXHiveJar(updatedFileLocation, xhivefile.getInputStream());
      if (newXHive.exists()) {
        String currentXHiveLocation = configurationParameters.get(XTestAntParameter.XHIVE_JAR);
        File currentXHive = new File(currentXHiveLocation);
        File backupXHive = new File(currentXHiveLocation + "_backup");
        if (currentXHive.exists()) {
          Files.move(currentXHive, backupXHive);
        }
        Files.copy(newXHive, currentXHive);
        updateResult = true;
      }
    } catch (IOException e) {
      logger.error("Couldn't replace the current XHive jar file correctly!");
      throw new XTestAnalyzerException(
          "XTestAnalyzerException when replacing the current XHive jar file:" + e);
    }
    return updateResult;
  }

  private File getCustomizedXHiveJar(String fileName, InputStream in) {
    File xhiveJarFile = new File(fileName);
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(xhiveJarFile);
      int c;
      while ((c = in.read()) != -1) {
        fout.write(c);
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      logger.error("getCustomizedXHiveJar FileNotFoundException:" + e);
      throw new XTestAnalyzerException("getCustomizedXHiveJar FileNotFoundException:" + e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      logger.error("getCustomizedXHiveJar IOException:" + e);
      throw new XTestAnalyzerException("getCustomizedXHiveJar IOException:" + e);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
        if (fout != null) {
          fout.close();
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        logger.error("getCustomizedXHiveJar IOException:" + e);
        throw new XTestAnalyzerException("getCustomizedXHiveJar IOException:" + e);
      }
    }
    return xhiveJarFile;
  }

  @RequestMapping(value = "/executeTarget", method = RequestMethod.POST)
  public @ResponseBody
  String executeTarget(@RequestBody String runParameters) {
    logger.info("Execute Target Command: " + runParameters);
    generateTaskArgument(runParameters);
    executorService.runAntTarget("run", RUN_PARAMS);
    // xTestExecuteStatus = executorService.getExecuteStatus();
    return ExecuteStatus.RUNNING.toString();
  }

  private void generateTaskArgument(String runParameters) {
    RUN_PARAMS = new LinkedHashMap<XTestAntParameter, String>();
    String[] runParaList = runParameters.split("&");
    for (String runParameter : runParaList) {
      runParameter = runParameter.replaceAll("%2F", "/");
      runParameter = runParameter.replaceAll("%3D", "=");
      runParameter = runParameter.replaceAll("%5C", File.separator);
      int equalTag = runParameter.indexOf("=");
      int len = runParameter.length();
      String key = runParameter.substring(0, equalTag);
      String value = runParameter.substring(equalTag + 1);
      if (XTestAntParameter.PROFILE == XTestAntParameter.valueOf(key)) {
        if (!"true".equals(value)) {
          continue;
        }
      }
      if (equalTag < len - 1) {
        RUN_PARAMS.put(XTestAntParameter.valueOf(key), value);
      }
    }
  }

  @RequestMapping(value = "/listenRunTarget", method = RequestMethod.POST)
  public @ResponseBody
  String listenRunTarget(HttpServletRequest request,HttpServletResponse response) {
    logger.info("Listen Running Target: ");
    ServletContext application=request.getSession().getServletContext();
    xTestExecuteStatus = executorService.getExecuteStatus();
    if (xTestExecuteStatus == XTestExecutorThread.ExecuteStatus.COMPLETE
        || xTestExecuteStatus == XTestExecutorThread.ExecuteStatus.INTERRUPT) {
      executorService.closeLogStream();
    }
    antLogMessage = executorService.getLogMessageStream();
    String responseMessage = xTestExecuteStatus.toString() + "=" + antLogMessage;
    application.setAttribute("executionInfo", responseMessage);
    return (String)application.getAttribute("executionInfo");
  }

}
