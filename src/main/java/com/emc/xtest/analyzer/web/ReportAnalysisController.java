package com.emc.xtest.analyzer.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportAnalysisController {

  private static final Logger logger = LoggerFactory.getLogger(ReportAnalysisController.class);

  @RequestMapping(value = "/reportAnalysis", method = RequestMethod.POST)
  public @ResponseBody
  String analyzeReport(@RequestBody String xTestName, Model model) {
    logger.info("Analyze Report: " + xTestName);

    return "reportAnalysis";
  }

  public void loadXDBLog() {
    String javaHome = System.getProperty("JAVA_HOME");
  }
}
