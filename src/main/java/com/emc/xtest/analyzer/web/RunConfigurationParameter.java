package com.emc.xtest.analyzer.web;

import java.io.Serializable;

public class RunConfigurationParameter implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String suite = null;

  private String suiteProperties = null;

  private String suiteParams = null;

  private boolean profile = false;

//  private String systemRef = "db_int";
//
//  private String userRef = "Administrator";
//
//  private String systemLib = "data/System";
//
//  private String suiteLib = "data/Suite";
//
//  private String queryLib = "data/Query";
//
//  private String testdataDir = "data/TestData";

  private String logOut = "STDOUT";

  private String resultOut = "STDOUT";

  private String baseOut = null;

  private String logLevel = "INFO";

  private String serverHost = null;

  private String serverPort = null;

  private Integer repeatPeriod = 0;

  private Integer repeatCount = 1;

  private String jvmMaxmem = null;

  public String getSuite() {
    return suite;
  }

  public void setSuite(String suite) {
    this.suite = suite;
  }

  public String getSuiteProperties() {
    return suiteProperties;
  }

  public void setSuiteProperties(String suiteProperties) {
    this.suiteProperties = suiteProperties;
  }

  public String getSuiteParams() {
    return suiteParams;
  }

  public void setSuiteParams(String suiteParams) {
    this.suiteParams = suiteParams;
  }

  public boolean isProfile() {
    return profile;
  }

  public void setProfile(boolean profile) {
    this.profile = profile;
  }

  public String getLogOut() {
    return logOut;
  }

  public void setLogOut(String logOut) {
    this.logOut = logOut;
  }

  public String getResultOut() {
    return resultOut;
  }

  public void setResultOut(String resultOut) {
    this.resultOut = resultOut;
  }

  public String getBaseOut() {
    return baseOut;
  }

  public void setBaseOut(String baseOut) {
    this.baseOut = baseOut;
  }
  
  public String getLogLevel() {
    return logLevel;
  }

  
  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public String getServerHost() {
    return serverHost;
  }

  public void setServerHost(String serverHost) {
    this.serverHost = serverHost;
  }

  public String getServerPort() {
    return serverPort;
  }

  public void setServerPort(String serverPort) {
    this.serverPort = serverPort;
  }

  public Integer getRepeatPeriod() {
    return repeatPeriod;
  }

  public void setRepeatPeriod(Integer repeatPeriod) {
    this.repeatPeriod = repeatPeriod;
  }

  public Integer getRepeatCount() {
    return repeatCount;
  }

  public void setRepeatCount(Integer repeatCount) {
    this.repeatCount = repeatCount;
  }

  public String getJvmMaxmem() {
    return jvmMaxmem;
  }

  public void setJvmMaxmem(String jvmMaxmem) {
    this.jvmMaxmem = jvmMaxmem;
  }
 
}

