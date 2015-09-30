package com.emc.xtest.analyzer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.emc.xtest.analyzer.utilities.XTestAnalyzerException;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerProperties;
import com.perforce.p4java.Log;
import com.perforce.p4java.client.IClient;
import com.perforce.p4java.core.file.FileSpecBuilder;
import com.perforce.p4java.core.file.FileSpecOpStatus;
import com.perforce.p4java.core.file.IFileSpec;
import com.perforce.p4java.exception.AccessException;
import com.perforce.p4java.exception.ConnectionException;
import com.perforce.p4java.exception.P4JavaException;
import com.perforce.p4java.exception.RequestException;
import com.perforce.p4java.option.UsageOptions;
import com.perforce.p4java.option.client.SyncOptions;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;

@Service
public class P4ConnectionService implements IXTestService {

  private static final Logger logger = LoggerFactory.getLogger(P4ConnectionService.class);

  private static IOptionsServer server = null;

  private static IClient client = null;

  private static XTestAnalyzerProperties xProperties;

  private List<IFileSpec> targetList = new ArrayList<IFileSpec>();

  @Autowired
  public void setXTestAnalyzerProperties(
      @Qualifier("xTestAnalyzerProperties") XTestAnalyzerProperties xTestAnalyzerProperties) {
    xProperties = xTestAnalyzerProperties;
  }

  /**
   * Returns a singleton instance of XMPPServer.
   * 
   * @return an instance.
   * @throws URISyntaxException
   * @throws P4JavaException
   */
  public static IOptionsServer getP4ServerInstance() throws P4JavaException, URISyntaxException {
    if (server == null) {
      server = getOptionsServer(null, null);
    }
    return server;
  }

  public static void disconnectP4Server() throws ConnectionException, AccessException {
    if (server != null) {
      server.disconnect();
      client = null;
      server = null;
    }
  }

  public static IClient getP4Client(IOptionsServer server) throws P4JavaException,
      URISyntaxException {
    if (client == null) {
      client = server.getClient(xProperties.getP4_clientname());
      if (client == null) {
        Log.error("The P4 client " + xProperties.getP4_clientname() + " couldn't be found!");
      }
    }
    return client;
  }

  public List<IFileSpec> getFileSpecList(String fileSpecList) {
    try {
      targetList = client.haveList(FileSpecBuilder.makeFileSpecList(fileSpecList));
    } catch (ConnectionException ce) {
      // TODO Auto-generated catch block
      logger.error("getP4VFileSpecList ConnectionException: " + ce);
      throw new XTestAnalyzerException("getP4VFileSpecList ConnectionException:" + ce);
    } catch (AccessException ae) {
      // TODO Auto-generated catch block
      logger.error("getP4VFileSpecList AccessException: " + ae);
      throw new XTestAnalyzerException("getP4VFileSpecList AccessException:" + ae);
    }
    return targetList;
  }

  public List<String> listXTestSC(String fileSpecList) {
    try {
      getP4ServerInstance();
      getP4Client(server);
      server.setCurrentClient(client);
      List<IFileSpec> fileSpecs = getFileSpecList(fileSpecList);
      return getXTestSCList(fileSpecs);
    } catch (RequestException rexc) {
      logger.error("listXTestSC RequestException:" + rexc);
      throw new XTestAnalyzerException("listXTestSC RequestException:" + rexc);
    } catch (P4JavaException exc) {
      logger.error("listXTestSC P4JavaException:" + exc);
      throw new XTestAnalyzerException("listXTestSC P4JavaException:" + exc);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      logger.error("listXTestSC URISyntaxException:" + e);
      throw new XTestAnalyzerException("listXTestSC URISyntaxException:" + e);
    }
  }

  public OutputStream detailXTestSC(String fileSpecList) {
    try {
      getP4ServerInstance();
      getP4Client(server);
      server.setCurrentClient(client);
      List<IFileSpec> fileSpecs = getFileSpecList(fileSpecList);
      return getXTestSCDetail(fileSpecs.get(0));
    } catch (RequestException rexc) {
      logger.error("detailXTestSC RequestException:" + rexc);
      throw new XTestAnalyzerException("detailXTestSC RequestException:" + rexc);
    } catch (P4JavaException exc) {
      logger.error("detailXTestSC P4JavaException:" + exc);
      throw new XTestAnalyzerException("detailXTestSC P4JavaException:" + exc);
    } catch (URISyntaxException e) {
      // TODO Auto-generated catch block
      logger.error("detailXTestSC URISyntaxException:" + e);
      throw new XTestAnalyzerException("detailXTestSC URISyntaxException:" + e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      logger.error("detailXTestSC IOException:" + e);
      throw new XTestAnalyzerException("detailXTestSC IOException:" + e);
    }
  }

  private List<String> getXTestSCList(List<IFileSpec> haveList) {
    List<String> xTestSCList = new ArrayList<String>();
    for (IFileSpec fileSpec : haveList) {
      if (fileSpec != null) {
        if (fileSpec.getOpStatus() == FileSpecOpStatus.VALID) {
          String depotPath = fileSpec.getDepotPathString();
          int SCOffet = depotPath.lastIndexOf("SC.java");
          if (SCOffet >= 0) {
            xTestSCList.add(depotPath);
//							logger.info("getXTestSCList: " + fileSpec.getDepotPath()
//									+ "#" + fileSpec.getEndRevision());
          }
        } else {
          logger.error("getXTestSCList: " + fileSpec.getStatusMessage());
        }
      }
    }
    return xTestSCList;
  }

  private OutputStream getXTestSCDetail(IFileSpec fileSpec) throws ConnectionException,
      RequestException, AccessException, IOException {
    InputStream inputStream = fileSpec.getContents(true);
    OutputStream outStream = new ByteArrayOutputStream();
    byte[] buff = new byte[32 * 1024];
    int len;
    while ((len = inputStream.read(buff)) > 0) {
      outStream.write(buff, 0, len);
    }
    inputStream.close();
    outStream.close();
    return outStream;
  }

  @SuppressWarnings("unused")
  private List<IFileSpec> syncFileSpecList(IClient client, String fileSpecList) throws P4JavaException {
    List<IFileSpec> syncList = new ArrayList<IFileSpec>();
      syncList = client.sync(FileSpecBuilder.makeFileSpecList(fileSpecList), new SyncOptions());
      for (IFileSpec fileSpec : syncList) {
        if (fileSpec != null) {
          if (fileSpec.getOpStatus() == FileSpecOpStatus.VALID) {
            logger.info("p4sync'd: " + fileSpec.getDepotPath() + "#" + fileSpec.getEndRevision());
          } else {
            logger.error(fileSpec.getStatusMessage());
          }
        }
      }
    
    return syncList;
  }

  private static IOptionsServer getOptionsServer(Properties props, UsageOptions opts)
      throws P4JavaException, URISyntaxException {
    String serverURI = "p4java://" + xProperties.getP4_serveruri();
    IOptionsServer server = ServerFactory.getOptionsServer(serverURI, props, opts);
    if (server != null) {
      server.connect();
      server.setUserName(xProperties.getP4_user());
      server.setCharsetName("utf8");
      server.login(xProperties.getP4_password());
    }
    return server;
  }
}
