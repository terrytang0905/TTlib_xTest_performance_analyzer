package com.emc.xtest.analyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.emc.xtest.analyzer.dao.xDBProvider.XDBDaoManipulation;

@Service
public class ReportAnalysisService implements IXTestService {

  private XDBDaoManipulation xDBDaoManipulation;

  @Autowired
  public void setXDBDaoManipulation(
      @Qualifier("xDBDaoManipulation") XDBDaoManipulation xDBDaoManipulation) {
    this.xDBDaoManipulation = xDBDaoManipulation;
  }
}
