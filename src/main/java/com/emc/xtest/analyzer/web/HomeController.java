package com.emc.xtest.analyzer.web;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.emc.xtest.analyzer.service.XTestContextService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

  /**
   * Simply selects the home view to render by returning its name.
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String main(Locale locale, Model model) {
    logger.info("Welcome to XTest Performance Analyzer! The client locale is {}.", locale);
    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    String formattedDate = dateFormat.format(date);
    model.addAttribute("serverTime", formattedDate);
    return "index";
  }

//  /**
//  * Simply selects the home view to render by returning its name.
//  */
//  @RequestMapping(value = "/", method = RequestMethod.GET)
//  public String xDBVHome(Locale locale, Model model) {
//    //contextService.startup();
//    return "index";
//  }

  @RequestMapping(value = "/home", method = RequestMethod.POST)
  public String home(Model model) {
    logger.info("Return to xTest Performance Analyzer Home Page!.");
    return "home";
  }

}
