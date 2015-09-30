package com.emc.xtest.analyzer.utilities;

import org.springframework.stereotype.Component;

@Component
public class XTestAnalyzerProperties {

  private static XTestAnalyzerProperties xTestAnalyzerProperties = new XTestAnalyzerProperties();

  private String p4_serveruri;

  private String p4_user;

  private String p4_password;

  private String p4_clientname;
  
  private String xdb_version;
  
  private String xdb_location;

  private String xtest_home;

  private String xtest_storageLocation;

  public static XTestAnalyzerProperties createInstance() {
    if (xTestAnalyzerProperties == null) {
      xTestAnalyzerProperties = new XTestAnalyzerProperties();
    }
    return xTestAnalyzerProperties;
  }

  public String getP4_serveruri() {
    return p4_serveruri;
  }

  public void setP4_serveruri(String p4_serveruri) {
    this.p4_serveruri = p4_serveruri;
  }

  public String getP4_user() {
    return p4_user;
  }

  public void setP4_user(String p4_user) {
    this.p4_user = p4_user;
  }

  public String getP4_password() {
    return p4_password;
  }

  public void setP4_password(String p4_password) {
    this.p4_password = p4_password;
  }

  public String getP4_clientname() {
    return p4_clientname;
  }

  public void setP4_clientname(String p4_clientname) {
    this.p4_clientname = p4_clientname;
  }

  
  public String getXdb_version() {
    return xdb_version;
  }
  
  public void setXdb_version(String xdb_version) {
    this.xdb_version = xdb_version;
  }

  
  public String getXdb_location() {
    return xdb_location;
  }

  
  public void setXdb_location(String xdb_location) {
    this.xdb_location = xdb_location;
  }

  
  public String getXtest_home() {
    return xtest_home;
  }

  
  public void setXtest_home(String xtest_home) {
    this.xtest_home = xtest_home;
  }

  
  public String getXtest_storageLocation() {
    return xtest_storageLocation;
  }

  
  public void setXtest_storageLocation(String xtest_storageLocation) {
    this.xtest_storageLocation = xtest_storageLocation;
  }

  
}
