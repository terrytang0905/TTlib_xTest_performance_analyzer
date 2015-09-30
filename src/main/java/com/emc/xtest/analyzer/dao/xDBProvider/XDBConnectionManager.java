/*
 * (c) 1994-2010, EMC Corporation. All Rights Reserved.
 */

package com.emc.xtest.analyzer.dao.xDBProvider;

import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;

import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class XDBConnectionManager implements IXDBConnectionManager {

  /**
   * Contains a stack of session objects.
   */
  private GenericObjectPool<XhiveSessionIf> m_pool;

  /**
   * Variables of the m_instance, used in the creation of new sessions.
   */
  private String m_connectAsUserName = null;
  private String m_connectAsPassword = null;
  private String m_connectAsDbName = null;
  private XDBManager m_mgr;

  XDBConnectionManager() {
    PoolableObjectFactory<XhiveSessionIf> factory = new PoolableObjectFactory<XhiveSessionIf>() {

      public XhiveSessionIf makeObject() throws Exception {
        XhiveSessionIf xhiveSessionIf = m_mgr.getDriver().createSession();
        xhiveSessionIf.connect(m_connectAsUserName, m_connectAsPassword, m_connectAsDbName);
        return xhiveSessionIf;

      }

      public void destroyObject(XhiveSessionIf session) throws Exception {
        if (!session.isTerminated()) {
          session.join();
          if (session.isJoined()) {
            if (session.isOpen()) {
              session.rollback();
            }
            if (session.isConnected()) {
              session.disconnect();
            }
            session.terminate();
          }
        }
      }

      public boolean validateObject(XhiveSessionIf session) {
        return session.getDriver().equals(m_mgr.getDriver()) && !session.isTerminated();
      }

      public void activateObject(XhiveSessionIf session) throws Exception {
        if (!session.isJoined()) session.join();
      }

      public void passivateObject(XhiveSessionIf session) throws Exception {
        if (session.isJoined()) session.leave();
      }

    };
    GenericObjectPool.Config config = new GenericObjectPool.Config();
    config.maxActive = 50;
    config.maxIdle = -1;
    config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
    config.testOnBorrow = true;
    config.testOnReturn = true;

    config.timeBetweenEvictionRunsMillis = 1000 * 60L * 5L; // 5 minutes
    config.testWhileIdle = true;
    // config.minEvictableIdleTimeMillis // specifies the minimum amount of time that an object may
// sit idle in the pool before it is eligible for eviction due to idle time. The default setting for
// this parameter is 30 minutes.
    // make the factory to be thread safe
    m_pool =
        new GenericObjectPool<XhiveSessionIf>(PoolUtils.synchronizedPoolableFactory(factory),
            config);
  }
  
  public synchronized void initialize(XDBManager mgr) {
    m_mgr = mgr;
    m_connectAsUserName =
        m_mgr.getDefaultBootStrapValues()
            .getProperty(XTestAnalyzerConstants.XDB_ADMINISTRATOR_NAME);
    m_connectAsDbName =
        m_mgr.getDefaultBootStrapValues().getProperty(XTestAnalyzerConstants.XDB_MYDATABASE);
    m_connectAsPassword =
        m_mgr.getDefaultBootStrapValues().getProperty(
            XTestAnalyzerConstants.XDB_ADMINISTRATOR_PASSWORD);
  }

  /**
   * Get a session from the pool of sessions, or create a new one if the pool is currently empty.
   * 
   * From common-pool java-doc, borrowObject is thread-safe
   * @throws XDBEngineException
   */
  public XhiveSessionIf getConnection() throws XDBEngineException {
    try {
      return m_pool.borrowObject();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    }
  }

  public XhiveLibraryIf getLibrary(XhiveSessionIf session, String libpath) {
    if (libpath == null) return null;

    return (XhiveLibraryIf)session.getDatabase().getRoot().getByPath(libpath);
  }

  /**
   * Return a session to the pool of sessions. Role of this method is related to session pooing, no
   * transaction handling.
   * 
   * From common-pool java-doc, returnObject is thread-safe.
   */
  public void releaseConnection(XhiveSessionIf session) {
    try {
      if (session != null && session.isOpen()) session.rollback();
      m_pool.returnObject(session);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Clean up the session pool.
   */
  public synchronized void destroy() {
    try {
      m_pool.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    m_pool = null;
  }

  @Override
  public void releaseConnection(Object obj) throws Exception {
    // TODO Auto-generated method stub
    
  }

}
