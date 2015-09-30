package com.emc.xtest.analyzer.dao.xDBProvider;

import com.emc.xtest.analyzer.dao.ConnectionProvider;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;


public interface IXDBConnectionManager extends ConnectionProvider
{
    public XhiveSessionIf getConnection() throws XDBEngineException;

    public XhiveLibraryIf getLibrary(XhiveSessionIf session, String libpath);

    public void releaseConnection(XhiveSessionIf session);

    public void destroy(); // destroy all connections
}
