package com.emc.xtest.analyzer.dao.xDBProvider;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.emc.xtest.analyzer.dao.IXTestDao;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;

public class XDBDaoManipulation implements IXTestDao {

  private static final Logger logger = LoggerFactory.getLogger(XDBDaoManipulation.class);

  public boolean libraryExist(String libPath) throws XDBEngineException {
    boolean exist = false;
    XhiveSessionIf session = XDBDaoTemplate.beginSession(true);
    XhiveLibraryIf xtestlib = XDBDaoTemplate.getLibrary(session, libPath);
    if (xtestlib != null) {
      exist = true;
    }
    XDBDaoTemplate.endSession(session);
    return exist;
  }

  public void createNewLibrary(String basePath) throws XDBEngineException {
    XDBDaoTemplate.createLibrary(basePath);
  }

  public void buildBaseLibrary(String basePath, String indexName, String indexDef)
      throws XDBEngineException {
    XhiveSessionIf session = XDBDaoTemplate.beginSession(false);
    XhiveLibraryIf xtestlib = XDBDaoTemplate.getLibrary(session, basePath);
    XDBDaoTemplate.endSession(session);
    if (xtestlib == null) {
      XDBDaoTemplate.createLibrary(basePath);
      XDBDaoTemplate.createMultiPathIndex(basePath, indexName, indexDef, null, true);
    }
  }

  public Map<Date, String> traverseLibrary(String testLibPath) throws XDBEngineException {
    Map<Date, String> map = new TreeMap<Date, String>();
    XhiveSessionIf session = XDBDaoTemplate.beginSession(false);
    List<XhiveLibraryChildIf> childResults = XDBDaoTemplate.traverseLibrary(session, testLibPath);
    for (XhiveLibraryChildIf child : childResults) {
      map.put(child.getCreated(), child.getName());
    }
    XDBDaoTemplate.endSession(session);
    return map;
  }

  public String publishChildDocument(String testLibPath, String documentName, String xslURI)
      throws XDBEngineException {
    XhiveSessionIf session = XDBDaoTemplate.beginSession(false);
    Document sourceDoc = XDBDaoTemplate.getChildByName(session, testLibPath, documentName);
    Document xslDoc = XDBDaoTemplate.parseDocumentByURI(session, testLibPath, xslURI);
    String result = XDBDaoTemplate.transformToString(sourceDoc, xslDoc);
    XDBDaoTemplate.endSession(session);
    return result;
  }

  public List<String> queryXTestBenchMark(String testLibPath, String query)
      throws XDBEngineException {
    // String query = "for $content in document('" + docXPath + "') return $content";
    XhiveSessionIf session = XDBDaoTemplate.beginSession(false);
    List<String> nodeResults = XDBDaoTemplate.query(session, testLibPath, query);
    XDBDaoTemplate.endSession(session);
    return nodeResults;
  }

  public void saveDocument(String testLibPath, String docName, Document document)
      throws XDBEngineException {
    XDBDaoTemplate.parseDocument(testLibPath, document, docName);
  }
}
