package org.shaft.administration.appgateway.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/gateway")
public class TestController {

  @GetMapping("/ip")
  public String getIp() {
    String returnValue;

    try {
      InetAddress ipAddr = InetAddress.getLocalHost();
      returnValue = ipAddr.getHostAddress();
    } catch (UnknownHostException ex) {
      returnValue = ex.getLocalizedMessage();
    }

    return returnValue;
  }
}
