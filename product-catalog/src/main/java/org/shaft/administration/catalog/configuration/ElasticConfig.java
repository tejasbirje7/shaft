package org.shaft.administration.catalog.configuration;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.http.HttpHeaders;

import javax.net.ssl.SSLContext;

@Configuration
public class ElasticConfig {

    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private int port;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient createSimpleElasticClient() throws Exception {
        try {
            final CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username,password));

            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(null, (x509Certificates, s) -> true);
            final SSLContext sslContext = sslBuilder.build();
            RestHighLevelClient client = new RestHighLevelClient(RestClient
                    //.builder(new HttpHost(host, port, "https")) // If elasticsearch is running with https
                    .builder(new HttpHost(host, port))
                    .setDefaultHeaders(compatibilityHeaders())
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setSSLContext(sslContext)
                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    })
                    .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                        @Override
                        public RequestConfig.Builder customizeRequestConfig(
                                RequestConfig.Builder requestConfigBuilder) {
                            return requestConfigBuilder.setConnectTimeout(5000)
                                    .setSocketTimeout(120000);
                        }
                    }));
            System.out.println("elasticsearch client created");
            return client;
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Could not create an elasticsearch client!!");
        }
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        try {
            return new ElasticsearchRestTemplate(createSimpleElasticClient());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Header[] compatibilityHeaders() {
        return new Header[]{
                new BasicHeader(HttpHeaders.ACCEPT, "application/vnd.elasticsearch+json;compatible-with=7"),
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.elasticsearch+json;compatible-with=7")
        };
    }

}
