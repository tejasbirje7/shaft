package org.shaft.administration.eventingestion.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
public class EurekaInstanceConfigBeanPostProcessor implements BeanPostProcessor {


  @Value("${spring.application.name}")
  private String serviceName;

  @Value("${server.port}")
  private int port;

  private String fargateIp;

  {
    try {
      fargateIp = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.warn("Could not get the Fargate instance ip address.");
    } catch (Exception ex) {
      log.error("Couldn't get the fargate instance ip address");
    }
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (bean instanceof EurekaInstanceConfigBean) {
      log.info("EurekaInstanceConfigBean detected. Setting IP address to {}", fargateIp);
      EurekaInstanceConfigBean instanceConfigBean = ((EurekaInstanceConfigBean) bean);
      instanceConfigBean.setInstanceId(fargateIp + ":" + serviceName + ":" + port);
      instanceConfigBean.setIpAddress(fargateIp);
      instanceConfigBean.setHostname(fargateIp);
      instanceConfigBean.setStatusPageUrl("http://" + fargateIp + ":" + port + "/actuator/info");
      instanceConfigBean.setHealthCheckUrl("http://" + fargateIp + ":" + port + "/actuator/health");
    }
    return bean;
  }

}
