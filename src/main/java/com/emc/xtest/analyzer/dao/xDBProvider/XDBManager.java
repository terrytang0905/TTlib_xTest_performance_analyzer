package com.emc.xtest.analyzer.dao.xDBProvider;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Properties;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;
import com.xhive.XhiveDriverFactory;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveFederationFactoryIf;
import com.xhive.core.interfaces.XhiveFederationIf;
import com.xhive.core.interfaces.XhiveNodeServerInfoIf;
import com.xhive.core.interfaces.XhiveSegmentIf;
import com.xhive.core.interfaces.XhiveSessionIf;

@Repository
public class XDBManager implements IXDBManager {

  private static final Logger logger = LoggerFactory.getLogger(XDBManager.class);

  private IXDBConnectionManager m_connectionMgrInstance;
  private Properties m_defaultBootStrapValues;
  private XhiveDriverIf m_driver; // primary server
  private XhiveDriverIf m_driverToLocalServer;
  private File bootstrapFile;

  private static boolean m_started = false;
  private static final int s_defaultPageSize = 4096;
  // private static final int CACHE_PAGES = 1024;
  private static String DATABASE;
  @SuppressWarnings("unused")
  private static String ADMINISTRATOR_NAME;
  private static String ADMINISTRATOR_PASSWORD;
  private static String SUPERUSER_NAME;
  private static String SUPERUSER_PASSWORD;
  private static String LICENSEKEY;
  private static String BOOTSTRAP;
  private static String PORT;
  private static String STORAGE_PATH;
  private static String CONNECTION_STRING;

  public Properties getDefaultBootStrapValues() {
    return m_defaultBootStrapValues;
  }

  public static String getStoragePath() {
    return STORAGE_PATH;
  }

  private static IXDBManager m_instance = null;

  private XDBManager() {
    m_defaultBootStrapValues = new Properties();
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_MYDATABASE, "xTestDatabase");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_SUPERUSER_NAME, "superuser");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_SUPERUSER_PASSWORD, "xtest");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_ADMINISTRATOR_NAME,
        "Administrator");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_ADMINISTRATOR_PASSWORD,
        "secret");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_LICENSEKEY,
        "025ByMJA6x1xyN#PQTXQyZSSxRPPRMIADgRWbsbrQ");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_HOST, "localhost");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_PORT, "1235");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_BOOTSTRAP,
        "XhiveDatabase.bootstrap");
    m_defaultBootStrapValues.setProperty(XTestAnalyzerConstants.XDB_CONNECTIONSTRING, "xhive://"
        + m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_HOST) + ":"
        + m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_PORT));

    DATABASE = m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_MYDATABASE);
    ADMINISTRATOR_NAME =
        m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_ADMINISTRATOR_NAME);
    ADMINISTRATOR_PASSWORD =
        m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_ADMINISTRATOR_PASSWORD);
    SUPERUSER_NAME =
        m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_SUPERUSER_NAME);
    SUPERUSER_PASSWORD =
        m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_SUPERUSER_PASSWORD);
    LICENSEKEY = m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_LICENSEKEY);
    BOOTSTRAP = m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_BOOTSTRAP);
    PORT = m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_PORT);
    CONNECTION_STRING =
        m_defaultBootStrapValues.getProperty(XTestAnalyzerConstants.XDB_CONNECTIONSTRING);
  }

  public static synchronized IXDBManager getInstance() {
    if (m_instance == null) {
      m_instance = new XDBManager();
    }
    return m_instance;
  }

  public synchronized void init(String xDBLocation) throws XDBEngineException {
    STORAGE_PATH = xDBLocation;
    if (m_connectionMgrInstance == null) {
      m_connectionMgrInstance = new XDBConnectionManager();
    }
    ((XDBConnectionManager)m_connectionMgrInstance).initialize(this);
    bootstrapFile = new File(STORAGE_PATH +File.separator+ BOOTSTRAP);
    if (!bootstrapFile.exists()) {
      try {
        logger.info("The bootstrap file path of new Federation:"+bootstrapFile.getAbsolutePath());
        bootstrapFile = new File(STORAGE_PATH, BOOTSTRAP);
        createFederation();     
        m_driverToLocalServer = XhiveDriverFactory.getDriver(bootstrapFile.getAbsolutePath());
        m_driverToLocalServer.init();
        createDatabase();
      } catch (IOException e) {
        logger.error("XDBManager init IOException:"+e);
      }
    }
    // initializeDriverForPrimaryServer();
  }

  public IXDBConnectionManager getConnectionManager() {
    if (m_connectionMgrInstance == null) {
      m_connectionMgrInstance = new XDBConnectionManager();
    }
    return m_connectionMgrInstance;
  }

  private void initializeDriverForPrimaryServer() {
    m_driver = XhiveDriverFactory.getDriver(CONNECTION_STRING, XhiveFederationIf.PRIMARY_NODE_NAME);
    if (!m_driver.isInitialized()) {
      m_driver.init(getCachePages());
    }
  }

  // This API will return the remote driver, it will be optimized to return local driver if it's
// single instance and local driver is available.
  public XhiveDriverIf getDriver() {
    if (m_driverToLocalServer != null && m_driverToLocalServer.isInitialized()
        && m_driverToLocalServer.getAllNodeServerInfo().size() == 0) {
      return m_driverToLocalServer;
    } else {
      return m_driver;
    }
  }

  private XhiveSessionIf openSuperuserSession() {
    XhiveSessionIf session = getDriver().createSession();
    session.connect(SUPERUSER_NAME, SUPERUSER_PASSWORD, null);
    return session;
  }

  private void closeSession(XhiveSessionIf session) {
    if (session != null) {
      if (session.isOpen()) {
        session.rollback();
      }
      if (session.isConnected()) {
        session.disconnect();
      }
    }
  }

  public void createFederation() throws IOException, XDBEngineException {
    XhiveFederationFactoryIf ff = XhiveDriverFactory.getFederationFactory();
    if (bootstrapFile != null&&!bootstrapFile.exists()) {
      String bootstrapFileName = bootstrapFile.getAbsolutePath();
      File logFile=new File(STORAGE_PATH+File.separator+"log");
      if(logFile.exists()){
        logFile.delete();
      }
      ff.createFederation(bootstrapFileName, STORAGE_PATH+File.separator+"log", getPageSize(),
          SUPERUSER_PASSWORD);
    }else{
      throw new XDBEngineException("Couldn't find out the bootstrap file of xDB.");
    }
  }

  public void createDatabase() {
    XhiveSessionIf session = null;
    try {
      session = openSuperuserSession();
      session.begin();
      XhiveFederationIf federation = session.getFederation();
      federation.setLicenseKey(LICENSEKEY);
      federation.createDatabase(DATABASE, ADMINISTRATOR_PASSWORD);
      session.commit();
    } finally {
      closeSession(session);
    }
  }

  public void backupFederation(WritableByteChannel channel, boolean incremental,
      List<XhiveSegmentIf> excludedSegments) {
    XhiveSessionIf session = null;
    try {
      // Backup federation requires super user privilige.
      session = openSuperuserSession();
      session.begin();
      XhiveFederationIf federation = session.getFederation();
      int option = 0;
      if (incremental) option |= XhiveFederationIf.BACKUP_INCREMENTAL;
      federation.backup(channel, option, excludedSegments);
      session.commit();
    } finally {
      closeSession(session);
    }
  }
  
  public void shutdownFederation(String nodeName) {
    XhiveSessionIf session = null;
    try {
      session = openSuperuserSession();
      session.begin();
      XhiveFederationIf federation = session.getFederation();
      federation.shutdown(nodeName, null);
      session.commit();
    } finally {
      closeSession(session);
    }
  }

  public synchronized void startDatabase() throws XDBEngineException {
    String nodeName = XhiveFederationIf.PRIMARY_NODE_NAME;

    m_driverToLocalServer = XhiveDriverFactory.getDriver(bootstrapFile.getAbsolutePath(), nodeName);
    if (!m_driverToLocalServer.isInitialized()) {

      int cachePages = getCachePages();
      m_driverToLocalServer.init(cachePages);
      logger.info("Initialize xDB driver to local server with cache pages: "
          + String.valueOf(cachePages));
    }
    if (logger.isDebugEnabled()) m_driverToLocalServer.setStoreStackTraceInLock(true);
    ServerSocket socket;
    ServerSocketFactory factory = ServerSocketFactory.getDefault();
    int port = getPortFromXhiveFederation(nodeName);
    try {
      socket = factory.createServerSocket(port, 0);
      m_driverToLocalServer.startListenerThread(socket);
    } catch (UnknownHostException e) {
      throw new XDBEngineException("startDatabase UnknownHostException:"+e);
    } catch (IOException e) {
      throw new XDBEngineException("startDatabase IOException:"+e);
    }
    
    if (XhiveFederationIf.PRIMARY_NODE_NAME.equals(nodeName)) {
      initializeDriverForPrimaryServer();
      try {
        setKeepXDBTransactionalLog(false);
      } catch (Throwable t) {
        logger.warn("Failed to synchronize keep-xdb-transactional-log option", t);
      }
    }
    m_started = true;
  }

  public void setKeepXDBTransactionalLog(boolean isKeepXDBTransactionalLog) {
    XhiveSessionIf session = null;
    try {
      session = openSuperuserSession();
      session.begin();
      XhiveFederationIf federation = session.getFederation();
      if (isKeepXDBTransactionalLog != federation.getKeepLogFiles()) {
        federation.setKeepLogFiles(isKeepXDBTransactionalLog);
      }
      session.commit();
    } finally {
      closeSession(session);
    }
  }

  public int getPortFromXhiveFederation(String nodeName) {
    List<XhiveNodeServerInfoIf> servers = m_driverToLocalServer.getAllNodeServerInfo();
    for (XhiveNodeServerInfoIf server : servers) {
      if (server.getNodeName().equalsIgnoreCase(nodeName)) {
        return server.getPort();
      }
    }
    return Integer.parseInt(PORT);
  }

  private int getPageSize() {
    return s_defaultPageSize;
  }

  public int getCachePages() {
    int cachePages;
    int pageSize = getPageSize();
    cachePages = (int)(getDefaultCacheSize() / pageSize);
    return cachePages;
  }

  public void setKeepXdbTransactionalLog(boolean isKeepXdbTransactionalLog) {
    XhiveSessionIf session = null;

    try {
      session = openSuperuserSession();
      session.begin();
      XhiveFederationIf federation = session.getFederation();
      if (isKeepXdbTransactionalLog != federation.getKeepLogFiles()) {
        federation.setKeepLogFiles(isKeepXdbTransactionalLog);
      }
      session.commit();
    } finally {
      closeSession(session);
    }
  }

  public synchronized boolean hasDBStarted() {
    return m_started;
  }

  public synchronized void shutdownDatabase() {
    m_started = false;
    String nodeName = XhiveFederationIf.PRIMARY_NODE_NAME;
    shutdownFederation(nodeName);
    
    m_connectionMgrInstance.destroy();
    m_connectionMgrInstance = null;
    // in primary node, m_driverToLocalServer & m_driver are same, so no need to close m_driver
    // separately.
    m_driver.close();

    if (m_driverToLocalServer != null) {
      try {
        m_driverToLocalServer.close();
        m_driverToLocalServer = null;
      } finally {
        if (m_driverToLocalServer != null) {
          m_driverToLocalServer.kill();
          m_driverToLocalServer = null;
        }
        m_driver = null;
        m_instance = null;
      }
    }
    logger.info("The XML database server is shutdown successfully.");
  }

  private long getDefaultCacheSize() {
    // We will use quarter of the max memory (java command line settting of -Xmx ) for xDB cache
// pages.
    return Runtime.getRuntime().maxMemory() >> 2;
  }

}
