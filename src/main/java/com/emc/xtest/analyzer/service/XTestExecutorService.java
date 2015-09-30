package com.emc.xtest.analyzer.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.emc.xtest.analyzer.service.XTestExecutorThread.ExecuteStatus;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerProperties;
import com.emc.xtest.analyzer.utilities.XTestAntParameter;

@Service
public class XTestExecutorService implements IXTestService {

  private static final Logger logger = LoggerFactory.getLogger(XTestExecutorService.class);
  private static XTestAnalyzerProperties xProperties;
  private XTestExecutorThread executorThread;
  private ExecuteStatus executeStatus = ExecuteStatus.NO_RUNNING;
  private DefaultLogger consoleLogger;
  private FileOutputStream logFileStream;
  private ByteArrayOutputStream logByteArrayOutputStream = new ByteArrayOutputStream();
  private PrintStream logPrintStream = new PrintStream(logByteArrayOutputStream);
  private ByteArrayOutputStream systemByteArrayOutputStream = new ByteArrayOutputStream();;
  private PrintStream systemOutPrintStream = new PrintStream(systemByteArrayOutputStream);
  private String xTestHome;
  private File buildFile;
  public static Map<XTestAntParameter, String> runParameters;
  Project project = new Project();

  @Autowired
  public void setXTestAnalyzerProperties(
      @Qualifier("xTestAnalyzerProperties") XTestAnalyzerProperties xTestAnalyzerProperties) {
    if (xProperties == null) {
      xProperties = xTestAnalyzerProperties;
      xTestHome = xProperties.getXtest_home();
    }
  }

  public Project initProject() {
    try {
      buildFile = getBuildFile();
      if (buildFile.exists()) {
        project.fireBuildStarted();
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        helper.parse(project, buildFile);
        consoleLogger = createDefaultLogger();
        project.addBuildListener(consoleLogger);
      }
      return project;
    } catch (FileNotFoundException e) {
      logger.error("initProject FileNotFoundException:" + e);
    }
    return project;

  }

  protected File getBuildFile() throws java.io.FileNotFoundException {
    if (buildFile == null) {
      buildFile = new File(xTestHome + File.separator + "bin" + File.separator + "build.xml");
    }
    return buildFile;
  }

  protected void createLogFile() {
    File logFile =
        new File(xProperties.getXtest_storageLocation() + File.separator + "xTestResult"
            + File.separator + "antlog_" + System.currentTimeMillis() + ".txt");
    try {
      if (!logFile.exists()) {
        logFile.createNewFile();
      }
      logFileStream = new FileOutputStream(logFile, true);
    } catch (IOException e) {
      logger.error("createLogFile IOException:" + e);
    }
  }

  protected DefaultLogger createDefaultLogger() {
    // createLogFile();
    if (consoleLogger == null) {
      consoleLogger = new DefaultLogger();
      consoleLogger.setErrorPrintStream(logPrintStream);
      consoleLogger.setOutputPrintStream(logPrintStream);
      consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
    }
    System.setOut(systemOutPrintStream);
    return consoleLogger;
  }

  public String getLogMessageStream() {
    String message = logByteArrayOutputStream.toString();
    logByteArrayOutputStream.reset();
    return message;
  }

  public Map<XTestAntParameter, String> buildRunConfiguraion(String xTestSuiteClassName)
      throws XTestAnalyzerException {
    Map<XTestAntParameter, String> configurationParameters =
        new LinkedHashMap<XTestAntParameter, String>();
    configurationParameters.put(XTestAntParameter.XTEST_HOME, xTestHome);
    configurationParameters.put(XTestAntParameter.XDB_VERSION, xProperties.getXdb_version());
    configurationParameters.put(XTestAntParameter.XDB_LOCATION, xProperties.getXdb_location());
    configurationParameters.put(XTestAntParameter.XHIVE_JAR, xProperties.getXdb_location()
        + File.separator + "lib" + File.separator + "xhive.jar");

    try {
      runDefaultAntTarget();
      do {
        Thread.sleep(5000);
      } while (getExecuteStatus() == ExecuteStatus.RUNNING);
    } catch (InterruptedException e) {
      logger.error("buildRunConfiguraion InterruptedException:" + e);
      throw new XTestAnalyzerException("buildRunConfiguraion InterruptedException:" + e);
    }
    ExecuteStatus executeStatus = getExecuteStatus();
    logger.info("Execute Default Target: " + getExecuteStatus());

    if (executeStatus == ExecuteStatus.COMPLETE) {
      String systemOutMessage = getSystemOutPrintStream();
      String suiteId = getXTestSuiteId(xTestSuiteClassName, systemOutMessage);
      configurationParameters.put(XTestAntParameter.PROFILE,
          XTestAntParameter.PROFILE.getDefaultValue());
      configurationParameters.put(XTestAntParameter.SUITE, suiteId);
      configurationParameters.put(XTestAntParameter.SUITE_PARAMETERS,
          "bootstrap=" + xProperties.getXdb_location() + File.separator + "data" + File.separator
              + "XhiveDatabase.bootstrap");
      XTestAntParameter[] xTestParameters = XTestAntParameter.values();
      int configurationNum = xTestParameters.length;
      for (int i = 7; i < configurationNum; i++) {
        configurationParameters.put(xTestParameters[i], xTestParameters[i].getDefaultValue());
      }
      configurationParameters.put(XTestAntParameter.BASE_OUT,
          xProperties.getXtest_storageLocation() + File.separator + "xTestResult" + File.separator
              + suiteId + "_" + xProperties.getXdb_version());
    }else{
      throw new XTestAnalyzerException("Fail to complete xTest Test Suite build.");
    }
    return configurationParameters;
  }

  private String getSystemOutPrintStream() {
    String systemOut = systemByteArrayOutputStream.toString();
    try {
      systemOutPrintStream.close();
      systemByteArrayOutputStream.close();
    } catch (IOException e) {
      logger.error("closeSystemOutPrintStream IOException:" + e);
      throw new XTestAnalyzerException("closeSystemOutPrintStream IOException:" + e);
    }
    return systemOut;
  }

  private String getXTestSuiteId(String xTestSuiteClassName, String logMessage) {
    String suiteId = null;
    String[] suiteList = logMessage.split("\n");
    for (String suiteInfo : suiteList) {
      if (suiteInfo.indexOf(xTestSuiteClassName) >= 0) {
        suiteId = suiteInfo.substring(0, suiteInfo.indexOf(" ["));
        break;
      }
    }
    return suiteId;
  }

  public void closeLogStream() {
    try {
      logPrintStream.close();
      logByteArrayOutputStream.close();
    } catch (IOException e) {
      logger.error("closeLogStream IOException:" + e);
      throw new XTestAnalyzerException("closeLogStream IOException:" + e);
    }
  }

  public ExecuteStatus getExecuteStatus() {
    if (executorThread != null) {
      executeStatus = executorThread.getExecuteStatus();
    }
    return executeStatus;
  }

  public void runDefaultAntTarget() {
    String defaultTarget = project.getDefaultTarget();
    executorThread = new XTestExecutorThread(project, defaultTarget);
    Thread executor = new Thread(executorThread);
    executor.start();
    executeStatus = ExecuteStatus.RUNNING;
  }

  public void runAntTarget(String targetName, Map<XTestAntParameter, String> RUN_PARAMS) {
    runParameters = RUN_PARAMS;
    Set<Entry<XTestAntParameter, String>> set = runParameters.entrySet();
    Iterator<Entry<XTestAntParameter, String>> iter = set.iterator();
    while (iter.hasNext()) {
      Entry<XTestAntParameter, String> entry = iter.next();
      project.setProperty(entry.getKey().getLiteral(), entry.getValue());
    }
    executorThread = new XTestExecutorThread(project, targetName);
    Thread executor = new Thread(executorThread);
    executor.start();
    executeStatus = ExecuteStatus.RUNNING;
  }

}
