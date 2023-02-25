package org.shaft.administration.marketingengine.common;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.shaft.administration.obligatory.protocols.http.ShaftHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Create different http factory if you need to override default
 * configuration of http request which are mentioned in ShaftHttpClient.java
 */
@Component
@EnableScheduling
public class HttpFactory {

    CloseableHttpClient httpClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFactory.class);
    private static final List<PoolingHttpClientConnectionManager> connectionManagers = new ArrayList<>();
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    public HttpFactory() {
        this.httpClient = this.getDefaultHTTPClient();
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        return clientHttpRequestFactory;
    }

    /**
     * Modify or create http configurations based on requirements for each services
     */
    public CloseableHttpClient getDefaultHTTPClient() {
        ShaftHttpClient client = new ShaftHttpClient(30000,30000,
                60000,50,2,2000,null);
        PoolingHttpClientConnectionManager connectionManager = client.getPoolingConnectionManager();
        addConnectionManager(connectionManager);
        return client.getHttpClient(connectionManager, client.getConnectionKeepAliveStrategy());
    }


    /**
     * To support @scheduled annotation used in idleConnectionMonitor()
     * we need to add support for scheduled execution of thread
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("HTTP-POOL-SCHEDULER");
        scheduler.setPoolSize(50);
        return scheduler;
    }

    /**
     * Starts an idle connection monitor to continuously clean up stale connections.
     */
    @Bean
    public Runnable idleConnectionMonitor() {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 10000)
            public void run() {
                LOGGER.info("Idle Connection Monitor");
                if(!connectionManagers.isEmpty()) {
                    LOGGER.info("Connection Manager Size : {}",connectionManagers.size());
                    for (PoolingHttpClientConnectionManager connectionManager : connectionManagers) {
                        try {
                            if (connectionManager != null) {
                                LOGGER.trace("run IdleConnectionMonitor - Closing expired and idle connections...");
                                connectionManager.closeExpiredConnections();
                                connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
                            } else {
                                LOGGER.trace("run IdleConnectionMonitor - Http Client Connection manager is not initialised");
                            }
                        } catch (Exception e) {
                            LOGGER.error("run IdleConnectionMonitor - Exception occurred. msg={}, e={}", e.getMessage(), e);
                        }
                    }

                }
            }
        };
    }

    public static void addConnectionManager(PoolingHttpClientConnectionManager connectionManager) {
        connectionManagers.add(connectionManager);
    }




}

