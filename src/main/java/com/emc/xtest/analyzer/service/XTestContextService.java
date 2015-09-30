package com.emc.xtest.analyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.emc.xtest.analyzer.dao.xDBProvider.XDBEngineException;
import com.emc.xtest.analyzer.dao.xDBProvider.XDBManager;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerProperties;

@Service
public class XTestContextService implements IXTestService {

  private static final Logger logger = LoggerFactory.getLogger(XTestContextService.class);

  private static XDBManager xDBManager;
  private static XTestAnalyzerProperties xProperties;

  @Autowired
  public void setXTestAnalyzerProperties(
      @Qualifier("xTestAnalyzerProperties") XTestAnalyzerProperties xTestAnalyzerProperties) {
    xProperties = xTestAnalyzerProperties;
  }

  @Autowired
  public void setXDBManager(@Qualifier("xDBManager") XDBManager manager) {
    if (xDBManager == null) {
      xDBManager = manager;
      synchronized (xDBManager) {
        startup();
      }
    }
  }

  public void startup() {
    try {
      if (!xDBManager.hasDBStarted()) {
        xDBManager.init(xProperties.getXtest_storageLocation());
        xDBManager.startDatabase();
      }
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      logger.error("XTestAnalyzerContextService startup:" + e);
    }
  }

  public void shutdown() {
    xDBManager.shutdownDatabase();
  }
}
