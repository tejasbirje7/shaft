package org.shaft.administration.usermanagement.configuration;

import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.obligatory.auth.transact.ShaftHashing;
import org.shaft.administration.obligatory.tokens.ShaftJWT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObligatoryConfig {

  @Bean
  public ShaftJWT getShaftJWT() {
    try {
      return new ShaftJWT();
    } catch (Exception ex) {
      log.error("Couldn't create instance for SHAFT JWT {}",ex);
      return  null;
    }
  }

  @Bean
  public ShaftHashing getHashingBean() {
    return new ShaftHashing();
  }
}
