package org.shaft.administration.catalog.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
//@Component
public class EurekaInstanceConfigBeanPostProcessor implements BeanPostProcessor {
  @Value("${spring.application.name}")
  private String serviceName;
  @Value("${server.port}")
  private int port;
  private String fargateIp;

  {
    try {
      fargateIp = InetAddress.getLocalHost().getHostAddress();
//      fargateIp = getFargateIpViaApi();
    } catch (UnknownHostException ex) {
      log.error("Couldn't get the fargate instance ip address - Unknown host exception");
    } catch (Exception ex) {
      log.error("Couldn't get the fargate instance ip address - ",ex);
    }
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {

    /*
    if (bean instanceof EurekaInstanceConfigBean) {
      log.info("EurekaInstanceConfigBean detected. Setting IP address to {}", fargateIp);
      EurekaInstanceConfigBean instanceConfigBean = ((EurekaInstanceConfigBean) bean);
      instanceConfigBean.setInstanceId(fargateIp + ":" + serviceName + ":" + port);
      instanceConfigBean.setIpAddress(fargateIp);
      instanceConfigBean.setHostname(fargateIp);
      instanceConfigBean.setStatusPageUrl("http://" + fargateIp + ":" + port + "/actuator/info");
      instanceConfigBean.setHealthCheckUrl("http://" + fargateIp + ":" + port + "/actuator/health");
    }*/

    return bean;
  }

  private static String getFargateIpViaApi() {
    ObjectMapper mapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    final ResponseEntity<String> taskInfoResponse = restTemplate.getForEntity("http://169.254.170.2/v2/metadata", String.class);
    log.info("Got AWS task info: {}", taskInfoResponse);
    log.info("Got AWS task info: {}", taskInfoResponse.getBody());
    if (taskInfoResponse.getStatusCode() == HttpStatus.OK) {
      try {
        final ObjectNode jsonNodes = mapper.readValue(taskInfoResponse.getBody(), ObjectNode.class);
        final JsonNode jsonNode = jsonNodes.get("Containers")
          .get(0).get("Networks")
          .get(0)
          .get("IPv4Addresses").get(0);
        log.info("Got IP to use: {}", jsonNode);
        return jsonNode.asText();
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      throw new RuntimeException("Unable to connect to AWS for fetching fargate ip address");
    }
  }

}
