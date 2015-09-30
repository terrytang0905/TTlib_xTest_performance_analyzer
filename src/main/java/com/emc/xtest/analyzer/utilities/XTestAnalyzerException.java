package com.emc.xtest.analyzer.utilities;


public class XTestAnalyzerException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private String xTestMessage;

  public XTestAnalyzerException(String message){
    //this.xTestMessage=message;
    super(message);
  }
  
  public XTestAnalyzerException(Throwable t){
    super(t);
  }
  
  public XTestAnalyzerException(String message,Throwable t) {
    // TODO Auto-generated constructor stub
    super(message,t);
  }

  public String getxTestMessage() {
    return xTestMessage;
  }

  
  public void setxTestMessage(String xTestMessage) {
    this.xTestMessage = xTestMessage;
  }

}
