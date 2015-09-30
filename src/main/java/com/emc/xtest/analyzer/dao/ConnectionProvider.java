package com.emc.xtest.analyzer.dao;


public interface ConnectionProvider {

  public Object getConnection() throws Exception;
  
  public void releaseConnection(Object obj) throws Exception;
}
