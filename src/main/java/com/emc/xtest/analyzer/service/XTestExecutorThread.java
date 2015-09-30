package com.emc.xtest.analyzer.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XTestExecutorThread implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(XTestExecutorThread.class);
  private Lock lock = new ReentrantLock();;
  private Project project;
  private String targetName;
  // private String argumentLine;
  private volatile ExecuteStatus executeStatus = ExecuteStatus.NO_RUNNING;

  public enum ExecuteStatus {
    NO_RUNNING,
    RUNNING,
    COMPLETE,
    INTERRUPT
  }

  public XTestExecutorThread(Project project, String targetName) {
    super();
    this.project = project;
    this.targetName = targetName;
    // this.argumentLine = argumentLine;
  }

  public ExecuteStatus getExecuteStatus() {
    return executeStatus;
  }

  @Override
  public void run() {
    executeTarget();

  }

  private void executeTarget() {
    lock.lock();
    try {
      executeStatus = ExecuteStatus.RUNNING;
      project.executeTarget(targetName);
      project.fireBuildFinished(null);
      executeStatus = ExecuteStatus.COMPLETE;
    } catch (BuildException e) {
      logger.error("XTestExecutorThread run BuildException:" + e);
      project.fireBuildFinished(e);
      executeStatus = ExecuteStatus.INTERRUPT;
    } finally {
      lock.unlock();
    }
  }

}
