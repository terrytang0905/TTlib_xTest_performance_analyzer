package com.emc.xtest.analyzer.utilities;

import java.util.HashMap;
import java.util.Map;

public class XTestAnalyzerConstants {

  public final static String XDB10SC_FileSpecList =
      "//depot/Platform/tools/xTest/xDB10/src/com/emc/xtest/suite/...";

  public final static String XDB101SC_FileSpecList =
      "//depot/Platform/tools/xTest/xDB10.1/src/com/emc/xtest/suite/...";

  public final static String XDB103SC_FileSpecList =
      "//depot/Platform/tools/xTest/xDB10.3/src/com/emc/xtest/suite/...";

  public static Map<String, String> xTestSC = new HashMap<String, String>();

  static {
    xTestSC.put("xDB10List", XDB10SC_FileSpecList);
    xTestSC.put("xDB101List", XDB101SC_FileSpecList);
    xTestSC.put("xDB103List", XDB103SC_FileSpecList);
  }
  
  public static final String XDB_MYDATABASE = "xdb_database";
  public static final String XDB_ADMINISTRATOR_NAME = "xdb_administratorName";
  public static final String XDB_ADMINISTRATOR_PASSWORD = "xdb_administratorPassword";
  public static final String XDB_SUPERUSER_NAME = "xdb_superUserName";
  public static final String XDB_SUPERUSER_PASSWORD = "xdb_superPassword";
  public static final String XDB_LICENSEKEY="xdb_licenseKey";
  public static final String XDB_BOOTSTRAP="xdb_bootstrap";
  public static final String XDB_HOST="xdb_host";
  public static final String XDB_PORT="xdb_port";
  public static final String XDB_CONNECTIONSTRING="xdb_connectionString";
  public static final String XDB_STORAGEPATH="xdb_storagepath";
  public static final String XDB_XTESTLIBRARY="/xtest";
  public static final String XDB_LMPINDEXNAME="xtestindex";
  public static final String XDB_LMPINDEXDEF="/";
  public static final String XDB_LMPINDEXDEF2="/root";
  
}
