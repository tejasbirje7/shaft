package org.shaft.administration.inventory.common;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.shaft.administration.inventory.configuration.HttpClientConfig;
import org.shaft.administration.inventory.configuration.MonitoringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Create different http factory if you need to override default
 * configuration of http request which are mentioned in HttpClientConfig.java
 */
@Component
public class DefaultHttpFactory {

    @Bean
    public CloseableHttpClient getDefaultHTTPClient() {
        HttpClientConfig client = new HttpClientConfig(30000,30000,
                60000,50,2,2000,null);
        PoolingHttpClientConnectionManager connectionManager = client.getPoolingConnectionManager();
        MonitoringConfig.addConnectionManager(connectionManager);
        return client.getHttpClient(connectionManager, client.getConnectionKeepAliveStrategy());
    }
}

