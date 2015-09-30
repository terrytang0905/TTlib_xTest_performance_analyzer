package com.emc.xtest.analyzer.dao.xDBProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveSegmentIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryChildIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.error.XhiveException;
import com.xhive.error.XhiveLockNotGrantedException;
import com.xhive.index.interfaces.XhiveExternalIndexConfigurationIf;
import com.xhive.index.interfaces.XhiveIndexIf;
import com.xhive.index.interfaces.XhiveIndexListIf;
import com.xhive.index.interfaces.XhiveSubPathIf;
import com.xhive.index.interfaces.XhiveSubPathIf.SubPathOptions;
import com.xhive.query.interfaces.XhiveXQueryQueryIf;
import com.xhive.query.interfaces.XhiveXQueryResultIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;
import com.xhive.util.interfaces.IterableIterator;
import com.xhive.util.interfaces.XhiveTransformerIf;

public class XDBDaoTemplate {

  private static final Logger logger = LoggerFactory.getLogger(XDBDaoTemplate.class);

  // cache the data format to have better performance
  private final static ThreadLocal<SimpleDateFormat> DATE_FORMAT =
      new ThreadLocal<SimpleDateFormat>();

  private final static int DEFAULT_LIBRARY_OPTION = XhiveLibraryIf.CONCURRENT_LIBRARY;
  private final static int DEFAULT_DETACHABLE_LIBRARY_OPTION = DEFAULT_LIBRARY_OPTION
      | XhiveLibraryIf.DETACHABLE_LIBRARY;
  private final static long DEFAULT_SEGMENT_SIZE = 0;
  public static XhiveSessionIf beginSession(boolean readOnly) throws XDBEngineException {

    XhiveSessionIf session = XDBManager.getInstance().getConnectionManager().getConnection();
    if (session.getReadOnlyMode() != readOnly) session.setReadOnlyMode(readOnly);
    session.begin();
    return session;
  }

  public static XhiveSessionIf beginSessionSkipDistributedDeadlockDetection(boolean readOnly,
      boolean skipDetection) throws XDBEngineException {
    XhiveSessionIf session = XDBManager.getInstance().getConnectionManager().getConnection();
    if (session.getReadOnlyMode() != readOnly) session.setReadOnlyMode(readOnly);
    if (skipDetection) session.setSkipDistributedDeadlockDetection(true);
    session.begin();
    return session;
  }

  public static void endSession(XhiveSessionIf session) {
    if (session != null) {
      if (session.isOpen()) session.rollback();
      if (session.isInterrupted()) session.interrupted();
      if (session.getSkipDistributedDeadlockDetection())
        session.setSkipDistributedDeadlockDetection(false);
      XDBManager.getInstance().getConnectionManager().releaseConnection(session);
    }
  }

  /**
   * Create a new library
   * @param path the library path
   * @throws com.emc.documentum.core.fulltext.indexserver.engine.xhive.XDBEngineException Exception
   *           to indicate any failure.
   */
  public static void createLibrary(String path) throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf xhiveLib = session.getDatabase().getRoot();
      String[] subpaths = path.split("/");
      for (String subpath : subpaths) {
        if ("".equals(subpath) || subpath == null) // it is possible if the path starts with '/'
          continue;
        if (xhiveLib.nameExists(subpath)) {
          xhiveLib = (XhiveLibraryIf)xhiveLib.get(subpath);
        } else {
          // create a library (bootstrap)
          XhiveLibraryIf childLib = xhiveLib.createLibrary(DEFAULT_LIBRARY_OPTION);
          // give the new library a name
          childLib.setName(subpath);
          // append the new libary to its parent
          xhiveLib.appendChild(childLib);
          xhiveLib = childLib;
          logger.info("Create new library :" + xhiveLib.getFullPath());
        }
      }
      session.commit();
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static void createDetachableLibrary(String path, String segmentPath)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf xhiveLib = session.getDatabase().getRoot();
      if (xhiveLib.getByPath(path) != null) return;
      String[] subpaths = path.split("/");
      for (String subpath : subpaths) {
        if (xhiveLib.nameExists(subpath)) {
          xhiveLib = (XhiveLibraryIf)xhiveLib.get(subpath);
        } else {
          String segmentId;
          StringBuilder libPath = new StringBuilder(xhiveLib.getFullPath());
          if (libPath.toString().endsWith("/")) libPath.append(subpath);
          else libPath.append("/").append(subpath);
          // remove the leading character '/' if present
          if (libPath.toString().startsWith("/")) segmentId =
              libPath.toString().substring(1).replaceAll("/", "#");
          else segmentId = libPath.toString().replaceAll("/", "#");
          if (segmentPath == "" || segmentPath != null) {
            segmentPath = XDBManager.getStoragePath();
          }
          if (session.getDatabase().hasSegment(segmentId)) {
            if (session.getDatabase().getSegment(segmentId).getUsage() == XhiveSegmentIf.SegmentUsage.UNUSED) session
                .getDatabase().getSegment(segmentId).delete();
            else throw new XDBEngineException("DM_ERR_SEGMENT_EXISTS:" + segmentId);
          }
          session.getDatabase().createSegment(segmentId, segmentPath, DEFAULT_SEGMENT_SIZE);

          XhiveLibraryIf childLib =
              xhiveLib.createLibrary(DEFAULT_DETACHABLE_LIBRARY_OPTION, segmentId);
          logger.info("created a detachable library " + childLib.getFullPath());

          // give the new library a name
          childLib.setName(subpath);
          // append the new libary to its parent
          xhiveLib.appendChild(childLib);
          xhiveLib = childLib;
        }
      }
      session.commit();
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static XhiveIndexIf createMultiPathIndex(String libraryPath, String name, String path,
      XhiveExternalIndexConfigurationIf configuration, boolean subPathOnRoot)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    XhiveIndexIf multipathIndex = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(libraryPath);

      XhiveIndexListIf iList = xhiveLib.getIndexList();
      XhiveExternalIndexConfigurationIf useConfiguration =
          configuration == null ? iList.createExternalIndexConfiguration() : configuration;

      useConfiguration.setAnalyzer(WhitespaceAnalyzer.class.getCanonicalName());
      if (subPathOnRoot) {
        XhiveSubPathIf sp = useConfiguration.createSubPathIfInstance();
        sp.getOptions().add(SubPathOptions.FULL_TEXT_SEARCH);
        // sp.getOptions().add(SubPathOptions.INCLUDE_DESCENDANTS);
        sp.getOptions().add(SubPathOptions.VALUE_COMPARISON);
        useConfiguration.getSubPaths().put("//*", sp);
      }
      multipathIndex = iList.addMultiPathIndex(name, path, useConfiguration);
      session.commit();
      return multipathIndex;
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  /**
   * Parse the document from the specified library with the given name, append it to the library,
   * set the name and return it. The specified parse options are used.
   */
  private static XhiveDocumentIf parseDocument(XhiveLibraryIf library, String resourceName,
      int parseOptions, String documentName) throws SAXException {

    XhiveDocumentIf document = library.parseDocument(getURL(resourceName), parseOptions);
    document.setName(documentName);
    library.appendChild(document);
    return document;
  }

  public static void parseDocument(String lipPath, String resourceName, String docName)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(lipPath);
      if (xhiveLib == null) {
        throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + lipPath);
      }

      if (!xhiveLib.nameExists(docName)) {
        parseDocument(xhiveLib, resourceName, XhiveLibraryIf.PARSER_NO_VALIDATION, docName);
      }
      session.commit();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static void parseDocument(String lipPath, Document document, String docName)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(lipPath);
      if (xhiveLib == null) {
        throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + lipPath);
      }

      if (!xhiveLib.nameExists(docName)) {
        XhiveDocumentIf newDoc = (XhiveDocumentIf)xhiveLib.importNode(document, true);
        newDoc.setName(docName);
        xhiveLib.appendChild(newDoc);
      }
      session.commit();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static void parseDocument(String lipPath, InputSource inputSource, String docName)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(lipPath);
      if (xhiveLib == null) {
        throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + lipPath);
      }
      if (!xhiveLib.nameExists(docName)) {
        XhiveDocumentIf document =
            xhiveLib.parseDocument(inputSource, XhiveLibraryIf.PARSER_NO_VALIDATION);
        document.setName(docName);
        xhiveLib.appendChild(document);
      }
      session.commit();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public int addXMLFiles(XhiveSessionIf s, String libPath, int num, String namePrefix, String path,
      int addResult) {
    int count = addResult;
    if (!s.isOpen()) {
      s.begin();
    }
    XhiveLibraryIf lib = (XhiveLibraryIf)s.getDatabase().getRoot().getByPath(libPath);
    if (lib.getState() == XhiveLibraryIf.LibraryState.READ_WRITE) {
      for (int i = 0; i < num; i++) {
        try {
          parseDocument(lib, path, XhiveLibraryIf.PARSER_NO_VALIDATION, namePrefix + i);
          count++;
        } catch (Exception e) {
          s.rollback();
          e.printStackTrace();
        }
      }
    }
    s.commit();
    return count;
  }

  public static void removeIndex(String libPath, String indexName) {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf library = (XhiveLibraryIf)session.getDatabase().getRoot().getByPath(libPath);
      XhiveIndexListIf indexList = library.getIndexList();
      XhiveIndexIf index = indexList.getIndex(indexName);
      indexList.removeIndex(index);
      session.commit();
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      endSession(session);
    }
  }

  /**
   * Update the xml document of library by the file from the file system.
   * @param libraryXhiveSessionIf s,XhiveLibraryIf library,
   * @param name
   * @param path
   */
  public static void updateXMLbyFile(XhiveSessionIf s, String libPath, String name, String path)
      throws SAXException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf library = (XhiveLibraryIf)s.getDatabase().getRoot().getByPath(libPath);
      XhiveLibraryChildIf oldDoc = library.get(name);
      if (oldDoc != null) {
        XhiveDocumentIf newDoc =
            library.parseDocument(getURL(path), XhiveLibraryIf.PARSER_NO_VALIDATION);
        newDoc.setName(name);
        library.replaceChild(newDoc, oldDoc);
      }
      session.commit();
    } catch (XDBEngineException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      endSession(session);
    }
  }

  public static URL getURL(String resourceName) {
    try {
      return new File(resourceName).toURI().toURL();
    } catch (MalformedURLException mue) {
      logger.error("Incorrect resource path for resource: " + resourceName + ", message: "
          + mue.getMessage());
    }
    return null;
  }

  public static void createDocumentIfNeeded(String path, String docName, String rootElementName)
      throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(path);
      if (xhiveLib == null) {
        throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + path);
      }

      if (!xhiveLib.nameExists(docName)) {
        XhiveDocumentIf doc = xhiveLib.createDocument(null, rootElementName, null);
        doc.setName(docName);
        xhiveLib.appendChild(doc);
      }
      session.commit();
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static List<XhiveLibraryChildIf> traverseLibrary(XhiveSessionIf session, String path)
      throws XDBEngineException {
    List<XhiveLibraryChildIf> childList = new ArrayList<XhiveLibraryChildIf>();
    XhiveLibraryIf library = getLibrary(session, path);
    if (library == null) throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + path);

    IterableIterator<? extends XhiveLibraryChildIf> children = library.getChildren();
    while (children.hasNext()) {
      XhiveLibraryChildIf child = children.next();
      childList.add(child);
    }
    return childList;
  }
  
  public static Document parseDocumentByURI(XhiveSessionIf session, String libPath, String uri) throws XDBEngineException{
    XhiveLibraryIf xhiveLib = getLibrary(session, libPath);
    if (xhiveLib == null) {
      throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + libPath);
    }
    LSParser parser = xhiveLib.createLSParser();
    Document document=(Document)parser.parseURI(uri);
    return document;
  }
  
  public static Document getChildByName(XhiveSessionIf session, String libPath, String name) throws XDBEngineException{
    XhiveLibraryIf xhiveLib = getLibrary(session, libPath);
    if (xhiveLib == null) {
      throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + libPath);
    }
    Document document=(Document)xhiveLib.get(name);
    return document;
  }
  
  public static String transformToString(Document sourceDoc,Document xslDoc){
      XhiveTransformerIf transformer=XDBManager.getInstance().getDriver().getTransformer();
      String result=transformer.transformToString(sourceDoc, xslDoc);
      return result;
  }
  
  public static List<String> query(XhiveSessionIf session, String libPath, String queryStatement)
      throws XDBEngineException {
    List<String> queryResults = new ArrayList<String>();

    XhiveLibraryIf rootLib = session.getDatabase().getRoot();
    XhiveLibraryIf xhiveLib = (XhiveLibraryIf)rootLib.getByPath(libPath);
    if (xhiveLib == null) {
      throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + libPath);
    }
    IterableIterator<? extends XhiveXQueryValueIf> xqueryResults =
        query(xhiveLib, null, queryStatement, null);
    while (xqueryResults.hasNext()) {
      XhiveXQueryValueIf xv=xqueryResults.next();
      queryResults.add(xv.toString());
    }
    return queryResults;
  }

  private static XhiveXQueryResultIf query(XhiveLibraryChildIf docOrLibToQuery, String queryPrefix,
      String query, String[] facets) {
    StringBuilder sb = new StringBuilder();
    if (queryPrefix != null) {
      sb.append(queryPrefix).append(" ");
    }

    if (facets != null && facets.length > 0) {
      sb.append("(# xhive:index-paths-values '").append(facets[0]);
      for (int i = 1; i < facets.length; i++) {
        sb.append("," + facets[i]);
      }
      sb.append("' #) ");
    }

    sb.append("(# xhive:index-debug 'true' #) (# xhive:queryplan-debug 'true' #)");

    sb.append(" { " + query + " } ");

    XhiveXQueryQueryIf xquery = docOrLibToQuery.createXQuery(sb.toString());

    xquery.setHighlighter(new TrivialHighlighter());
    xquery.setParallelExecution(Executors.newCachedThreadPool());
    XhiveXQueryResultIf res0 = xquery.execute();
    return res0;
  }

  // check if a document exists in a library
  public static boolean hasDocument(XhiveSessionIf session, String path, String documentId)
      throws XDBEngineException {
    boolean isNewSession = false;
    boolean result = false;
    try {
      if (session == null) {
        session = beginSession(false);
        isNewSession = true;
      }
      XhiveLibraryIf library = getLibrary(session, path);
      if (library != null && library.nameExists(documentId)) result = true;

      if (isNewSession) session.commit();
      return result;
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      if (isNewSession) endSession(session);
    }
  }

  // return a String starting from the root element
  public static String getStrFromRootElement(String data) throws IOException {
    // find root element
    int endSlash = data.lastIndexOf('/');
    int start = -1;
    if (endSlash >= 0) {
      if (data.charAt(endSlash + 1) == '>') {
        start = data.lastIndexOf('<', endSlash);
      } else {
        int end = data.indexOf('>', endSlash);
        if (end >= 0) {
          String rootName = data.substring(endSlash + 1, end).trim();
          start = data.indexOf("<" + rootName);
          if (start < 0) start = data.indexOf("< ");
        }
      }
    }

    if (start >= 0) return data.substring(start);
    else return null;
  }

  public static void rebuildIndex(String path, String name) throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf library = getLibrary(session, path);
      if (library == null) throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + path);

      XhiveIndexIf index = library.getIndexList().getIndex(name);

      if (index == null)
        throw new XDBEngineException("DM_ERR_INVALID_INDEX:" + name + " libraryName:"
            + library.getName());

      index.rebuildIndex();
      session.commit();
    }

    catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static XhiveLibraryIf.LibraryState getLibraryState(String path) throws XDBEngineException {
    XhiveSessionIf session = null;
    XhiveLibraryIf.LibraryState ret = null;
    try {
      session = beginSession(false);
      XhiveLibraryIf library = getLibrary(session, path);
      if (library != null) {
        ret = library.getState();
      }
      session.commit();
      return ret;
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static void setLibraryUsable(String segmentId, boolean usable) throws XDBEngineException {
    XhiveSessionIf session = null;
    try {
      session = beginSession(false);
      session.getDatabase().setLibraryUsableBySegmentId(segmentId, usable);
      session.commit();
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static XhiveLibraryIf getLibrary(XhiveSessionIf session, String path) {
    XhiveLibraryIf rootLib = session.getDatabase().getRoot();
    return (XhiveLibraryIf)rootLib.getByPath(path);
  }

  @SuppressWarnings("unused")
  private static boolean checkLibraryIndexes(XhiveLibraryIf xhiveLib) {
    // Xhive always have name & id indexes internally.
    // We just check for user created indexes.
    if (xhiveLib.getIndexList().size() > 2) return true;
    else {
      XhiveLibraryIf parentLib = xhiveLib.getParentNode();
      return parentLib != null && checkLibraryIndexes(parentLib);
    }
  }

  public static void setXhiveLibraryState(XhiveSessionIf session, String libPath,
      XhiveLibraryIf.LibraryState libState, boolean deep) throws XDBEngineException {
    boolean isNewSession = false;
    try {
      if (session == null) {
        session = beginSession(false);
        isNewSession = true;
      }
      XhiveDatabaseIf xdb = session.getDatabase();
      XhiveLibraryIf root = xdb.getRoot();
      XhiveLibraryIf xhiveLib = (XhiveLibraryIf)root.getByPath(libPath);
      if (xhiveLib == null) throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + libPath);

      if (!xhiveLib.isDetachable())
        throw new XDBEngineException("DM_ERR_LIBRARY_NOT_DETACHABLE" + libPath);

      if (xhiveLib.getState() != libState) xhiveLib.setState(libState, deep);

      if (isNewSession) {
        session.commit();
      }
    } catch (XhiveException e) {
      throw new XDBEngineException(e);
    } finally {
      if (isNewSession && session != null) {
        endSession(session);
      }
    }
  }

  public static void backupLibrary(XhiveSessionIf session, Collection<String> libraryPaths,
      FileOutputStream fstream) throws XDBEngineException {
    boolean isNewSession = false;
    ArrayList<XhiveLibraryIf> libraries = new ArrayList<XhiveLibraryIf>();
    try {
      if (session == null) {
        session = beginSession(true);
        isNewSession = true;
      }
      XhiveDatabaseIf xdb = session.getDatabase();
      XhiveLibraryIf root = xdb.getRoot();
      for (String path : libraryPaths) {
        XhiveLibraryIf xhiveLib = (XhiveLibraryIf)root.getByPath(path);
        if (xhiveLib != null) {
          libraries.add(xhiveLib);
        } else {
          throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + path);
        }
      }

      if (libraries.size() > 0) {
        xdb.backupLibrary(libraries, fstream.getChannel());
      }

      if (isNewSession) {
        session.commit();
      }
    } catch (XDBEngineException e) {
      throw new XDBEngineException(e);
    } finally {
      if (isNewSession && session != null) {
        endSession(session);
      }
    }

  }

  public static String convertToXMLDateTime(Date date) {
    SimpleDateFormat dateFormat = DATE_FORMAT.get();

    if (dateFormat == null) {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
      DATE_FORMAT.set(dateFormat);
    }

    String dateString = (dateFormat.format(date)).replace(" ", "T");
    return dateString.substring(0, 22) + ":" + dateString.substring(22);
  }

  public static boolean checkOfflineLibraries(List<String> libpaths) throws XDBEngineException {
    boolean result = false;
    XhiveSessionIf session = null;
    try {
      session = beginSession(true);
      Collection<String> unusableLibs = session.getDatabase().getAllUnusableLibraries();
      for (String path : libpaths) {
        if (path != "" && path != null && unusableLibs.contains(path)) {
          result = true;
          break;
        }
      }
      session.commit();
      return result;
    } catch (XDBEngineException e) {
      throw new XDBEngineException(e);
    } finally {
      endSession(session);
    }
  }

  public static void attachLibrary(XhiveSessionIf session, String path, String segmentId,
      boolean force) throws XDBEngineException {
    boolean newSession = false;
    try {
      if (session == null) {
        session = beginSession(false);
        newSession = true;
      }
      int pos = path.lastIndexOf("/");
      String parent = null;
      if (pos > 0) {
        parent = path.substring(0, pos);
      }

      XhiveLibraryIf rootLib = session.getDatabase().getRoot();
      XhiveLibraryIf parentLib =
          (parent == null) ? rootLib : (XhiveLibraryIf)rootLib.getByPath(parent);

      if (parentLib == null) throw new XDBEngineException("DM_ERR_INVALID_LIBRARY:" + parent);
      if (!session.getDatabase().hasSegment(segmentId)) {
        throw new XDBEngineException("DM_ERR_INVALID_SEGMENT:" + segmentId);
      }

      XhiveLibraryIf lib;
      if (force) lib = parentLib.forceAttach(segmentId);
      else lib = parentLib.attach(segmentId);

      parentLib.appendChild(lib);
      if (newSession) session.commit();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      if (newSession) endSession(session);
    }
  }

  public static void detachLibrary(XhiveSessionIf session, String path) throws XDBEngineException {
    boolean newSession = false;
    try {
      if (session == null) {
        session = beginSession(false);
        newSession = true;
      }
      if (path != "" && path != null) {
        XhiveLibraryIf rootLib = session.getDatabase().getRoot();
        XhiveLibraryIf lib = (XhiveLibraryIf)rootLib.getByPath(path);
        if (lib != null && lib.isDetachable()) {
          lib.detach();
        }
      }
      if (newSession) session.commit();
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      if (newSession) {
        endSession(session);
      }
    }
  }

  public static void forceDetachLibrary(XhiveSessionIf session, String libPath, String segmentId)
      throws XDBEngineException {
    boolean newSession = false;
    try {
      if (session == null) {
        session = beginSession(false);
        newSession = true;
      }
      if (libPath != "" && libPath != null) {
        int pos = libPath.lastIndexOf("/");
        String parent = null;
        if (pos > 0) {
          parent = libPath.substring(0, pos);
        }

        XhiveLibraryIf rootLib = session.getDatabase().getRoot();
        XhiveLibraryIf parentLib =
            (parent == null) ? rootLib : (XhiveLibraryIf)rootLib.getByPath(parent);

        if (parentLib != null && session.getDatabase().hasSegment(segmentId)) {
          parentLib.forceDetachChild(segmentId);
        }
        if (newSession) session.commit();
      }
    } catch (Exception e) {
      throw new XDBEngineException(e);
    } finally {
      if (newSession) {
        endSession(session);
      }
    }
  }

  /**
   * get the session information
   * 
   * @param session the opened session
   * @param nodeName xDB node which those sessions belongs to
   * @return the whole session information
   * */
  public static String getSessionInformation(XhiveSessionIf session, String nodeName) {
    StringWriter writer = new StringWriter();
    PrintWriter w = new PrintWriter(writer);
    if (nodeName != null) session.getDriver().printSessionInformation(nodeName, w);
    else session.getDriver().printSessionInformation(w);
    w.close();

    return writer.toString();
  }

  public static abstract class RetryExecutor {

    /**
     * Internally, the task only need 1)begin session, 2)do business logic, 3)commit session. The
     * task may get XhiveLockNotGrantedException during accessing XML document
     * 
     * @param session XhiveSessionIf
     * @throws XhiveLockNotGrantedException The task will be retried
     * @throws Exception The exception will be re-thrown to upper application
     */
    public abstract void task(XhiveSessionIf session) throws XhiveLockNotGrantedException,
        Exception;

    public void executeTaskWithRetry() throws Exception {
      XhiveSessionIf session = beginSession(false);
      try {
        int i = 0;
        while (i < RETRIES) {
          i++;
          try {
            task(session);
            break;
          } catch (XhiveLockNotGrantedException lnge) {

            session.rollback();
            try {
              Thread.sleep(Math.round(Math.random() * 10) * 100);
            } catch (InterruptedException e) {
              logger.info("RetryExecutor interrupted:", e);
              break;
            }
            if (i >= RETRIES) {
              throw lnge;
            }
          } catch (Exception e) {
            session.rollback();
            throw e;
          }
        }
      } finally {
        endSession(session);
      }
    }

    private final static int RETRIES = 10;
  }

}
