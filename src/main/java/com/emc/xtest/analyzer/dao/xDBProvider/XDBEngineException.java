package com.emc.xtest.analyzer.dao.xDBProvider;


public class XDBEngineException extends Exception{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public XDBEngineException(String message){
    super(message);
  }
  
  public XDBEngineException(Throwable t){
    super(t);
  }
  
  public XDBEngineException(String message,Throwable t) {
    // TODO Auto-generated constructor stub
    super(message,t);
  }

}
