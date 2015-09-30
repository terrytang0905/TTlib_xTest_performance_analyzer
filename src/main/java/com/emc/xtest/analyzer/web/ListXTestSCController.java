package com.emc.xtest.analyzer.web;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.emc.xtest.analyzer.service.P4ConnectionService;
import com.emc.xtest.analyzer.utilities.XTestAnalyzerConstants;

@Controller
public class ListXTestSCController {

  private static final Logger logger = LoggerFactory.getLogger(ListXTestSCController.class);

  private P4ConnectionService p4ConnectionService;

  private List<String> resultList = null;

  @Autowired
  public void setP4ConnectionService(
      @Qualifier("p4ConnectionService") P4ConnectionService p4ConnectionService) {
    this.p4ConnectionService = p4ConnectionService;
  }

  @RequestMapping(value = "/listXTestSC", method = RequestMethod.POST)
  public @ResponseBody
  List<String> listXTestSC(@RequestBody String xTestCategory) {
    logger.info("listXTestSC: " + xTestCategory);
    resultList = new ArrayList<String>();
    String XTestSC = XTestAnalyzerConstants.xTestSC.get(xTestCategory);
    if (XTestSC != null) {
      resultList = p4ConnectionService.listXTestSC(XTestSC);
    }

    // List<String> resultList = new ArrayList<String>();
    // resultList.add("ParallelQuerySC");
    // resultList.add("PureQuerySC");
    // resultList.add("ConcurrentQuerySC");

    return resultList;
  }

  @RequestMapping(value = "/detailXTestSC", method = RequestMethod.POST)
  public @ResponseBody
  String detailXTestSC(@RequestBody String xTestName) {
    logger.info("DetailXTestSC " + xTestName);
    String xTestSCPath = null;
    for (String xTestSCName : resultList) {
      if (xTestSCName.indexOf(xTestName) >= 0) {
        xTestSCPath = xTestSCName;
        break;
      }
    }
    OutputStream contentStream = p4ConnectionService.detailXTestSC(xTestSCPath);
    // File file=new
    // File("C:\\Dev\\Perforce\\depot\\Platform\\tools\\xTest\\xDB10\\src\\com\\emc\\xtest\\suite\\lib\\xdb10\\qa\\xplore\\XPloreQueryPerfSC.java");
    // FileInputStream fis =null;
    // OutputStream contentStream =null;
    // try {
    // fis = new FileInputStream(file);
    // contentStream = new ByteArrayOutputStream();
    // byte[] buff = new byte[32 * 1024];
    // int len;
    // while ((len = fis.read(buff)) > 0){
    // contentStream.write(buff, 0, len);
    // }
    // fis.close();
    // contentStream.close();
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    return contentStream.toString();
  }

}
