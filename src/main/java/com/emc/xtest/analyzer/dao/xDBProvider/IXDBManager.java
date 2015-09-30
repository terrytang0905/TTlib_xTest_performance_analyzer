package com.emc.xtest.analyzer.dao.xDBProvider;


import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhiveSegmentIf;

public interface IXDBManager
{
    public void init(String xDBStorageLocation) throws XDBEngineException ;

    public void createDatabase();
    
    public void startDatabase() throws XDBEngineException;

    public void shutdownDatabase();
    
    public boolean hasDBStarted();
    
    public void createFederation() throws IOException, XDBEngineException;
    
    public void backupFederation(WritableByteChannel channel, boolean incremental,
        List<XhiveSegmentIf> excludedSegments);

    public IXDBConnectionManager getConnectionManager();

    public XhiveDriverIf getDriver();

}
