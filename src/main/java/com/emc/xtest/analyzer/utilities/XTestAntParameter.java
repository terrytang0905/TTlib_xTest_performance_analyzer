package com.emc.xtest.analyzer.utilities;

/**
 * Specifies the Command Line Parameter allowed by the Runner.
 */
public enum XTestAntParameter {

  // Enum values
 
  /**
   * XTest Home Dir.
   */
  // This is set to "." because in Eclipse, by default, when running a Suite as a JUnit test, the
  // working directory will be the XTest home dir itself. When running xTest using ant, the working
  // directory will generally be the bin subdirectory of the xTest home dir.
  XTEST_HOME("xtest.home", ".", "Home directory where xTest is installed.", true, true, false),
  
  /**
   * The XDB Version.
   */
  XDB_VERSION("xdb.version", "10", "The xDB Version", false, true, false),
  
  /**
   * The XDB Location.
   */
  XDB_LOCATION("xdb.location", ".", "The Test xDB Location", false, true, false),
  
  /**
   * The XHIVE Jar.
   */
  XHIVE_JAR("xhive.jar", ".", "The xDB xhive.jar Test File", false, true, false),
  
  /**
   * The Status of the Performance Profile.
   */
  PROFILE("profile", "false", "Select xTest Performance Profile or not", false, true, false),
  
  /**
   * Name of the Suite.
   */
  SUITE("suite", null, "Name of the Suite to be run.", true, true, false),
  
  /**
   * Parameters specific to the Suite.
   */
  SUITE_PARAMETERS(
      "suite.params",
      null,
      "Parameters specific to the Suite. Overrides values specified in the file specified by suite.properties, if any.",
      false, true, false),
      
  /**
   * Path pointing to properties file containing suite parameters. Will be overridden by
   * SUITE_PARAMETERS
   */
  SUITE_PROPERTIES(
      "suite.properties",
      null,
      "Path to properties file which specifies parameters specific to the Suite. These will be overridden by values specified with the suite.params parameter.",
      false, true, false),

  // - Default System Params

//  /**
//   * Default System reference. Will be used in {@link Test}s where no System is specified.
//   */
//  DEFAULT_SYSTEM_REFERENCE("system.ref.default", "db_int",
//      "Reference to the default System to be used.", false, true, false),
//
//  /**
//   * Default User reference. Will be used in {@link Test}s where no User is specified.
//   */
//  DEFAULT_USER_REFERENCE("user.ref.default", "Administrator",
//      "Reference to the default User to be used.", false, true, false),
//
//  // - Library Path Params
//
//  /**
//   * Path to the System Library.
//   */
//  SYSTEM_LIBRARY(
//      "system.lib",
//      "data/System",
//      "Path to the System Library. If relative, it is resolved relative to the \"xtest.home\" parameter.",
//      false, true, false),
//
//  /**
//   * Path to the Suite Library.
//   */
//  SUITE_LIBRARY(
//      "suite.lib",
//      "data/Suite",
//      "Path to the Suite Library. If relative, it is resolved relative to the \"xtest.home\" parameter.",
//      false, true, false),
//
//  /**
//   * Path to the Query Library.
//   */
//  QUERY_LIBRARY(
//      "query.lib",
//      "data/Query",
//      "Path to the Query Library. If relative, it is resolved relative to the \"xtest.home\" parameter.",
//      false, true, false),
//
//  /**
//   * Path to the Test Data directory.
//   */
//  TESTDATA_DIR(
//      "testdata.dir",
//      "data/TestData",
//      "Path to the Test Data directory. If relative, it is resolved relative to the \"xtest.home\" parameter.",
//      false, true, false),

  // - Logging Params

//  /**
//   * Path to the log file.
//   */
//  LOG_OUT("log.out", "STDOUT", "Path to the file where the test log will be written, or STDOUT.",
//      false, true, false),
//
//  /**
//   * Path to the results file.
//   */
//  RESULT_OUT("result.out", "STDOUT",
//      "Path to the file where the test results will be written, or STDOUT.", false, true, false),

//  /**
//   * Path to the timeline file.
//   */
//  TIMELINE_OUT("timeline.out", "",
//      "Path to the file where the test timeline will be written, or STDOUT.", false, true, false),

  /**
   * Base path/name for generated log and result files.
   */
  BASE_OUT("base.out", null, "Prefix for log and result filenames, which will be generated.",
      false, true, false),

  /**
   * Logging Level.
   */
  LOG_LEVEL("log.level", "INFO",
      "Logging Level (one of : OFF, FATAL, ERROR, WARN, INFO, DEBUG, ALL)", false, true, false),

//  /**
//   * Logging properties path.
//   */
//  LOG_PROPERTIES(
//      "log.properties",
//      "bin/log4j.properties",
//      "Path to a log4j properties file with logging configuration. Any other log settings specified will override the relevant parts of the configuration.",
//      false, true, false),

//  /**
//   * Switch to enable/disable XML Logging.
//   */
//  LOG_XML("log.xml", "true", "Switch which enables the generation of the XML log.", false, true,
//      false),
//
//  /**
//   * Destination mail address.
//   */
//  MAIL_TO("mail.to", null, "Mail address to which the logs and results will be mailed.", false,
//      true, false),
//
//  // - Misc Params
//
//  /**
//   * Path to the ClassName List.
//   */
//  CLASSNAME_LIST("classnames.list", "bin/ClassNames.txt",
//      "Path to the config file containing the classnames of the xTest framework.", false, true,
//      false),
//
//  // - Params for Distributed Operation
//
//  /**
//   * Client ID.
//   */
//  CLIENT_ID("client.id", null, "Client ID of this instance in distributed environments.", false,
//      true, true),

  /**
   * Server Host.
   */
  SERVER_HOST("server.host", null, "Server Host in distributed environments.", false, true, true),

  /**
   * Server Port.
   */
  SERVER_PORT("server.port", null, "Server Port in distributed environments.", false, true, true),

//  /**
//   * Alexandria Host.
//   */
//  ALEXANDRIA_HOST("alx.host", null, "Alexandria Host.", false, true, false),
//
//  /**
//   * Alexandria Port.
//   */
//  ALEXANDRIA_PORT("alx.port", null, "Alexandria Port.", false, true, false),

  /**
   * Repeat Period
   */
  REPEAT_PERIOD("repeat.period", "0", "Run Repeat Period (s).", false, true, false),

  /**
   * Repeat Count
   */
  REPEAT_COUNT("repeat.count", "1", "Run Repeat Count (s).", false, true, false), 
  
  /**
   * Repeat Count
   */
  JVM_MAXMEM("jvm.maxmem", null, "Set JVM max memory.", false, true, false);

  // Instance Properties

  private String literal;
  private String defaultValue;
  private String description;
  private boolean isRequired;
  private boolean hasValue;
  private boolean isHidden;

  // Instance Constructors

  private XTestAntParameter(String literal, String defaultValue, String description,
      boolean isRequired, boolean hasValue, boolean isHidden) {

    this.literal = literal;
    this.defaultValue = defaultValue;
    this.description = description;
    this.isRequired = isRequired;
    this.hasValue = hasValue;
    this.isHidden = isHidden;
  }

  // Instance Accessors

  public String getLiteral() {
    return this.literal;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean isRequired() {
    return this.isRequired;
  }

  public boolean hasValue() {
    return this.hasValue;
  }

  public boolean isHidden() {
    return this.isHidden;
  }
}
