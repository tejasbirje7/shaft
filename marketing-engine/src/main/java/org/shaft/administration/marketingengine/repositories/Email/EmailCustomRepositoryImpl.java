package org.shaft.administration.marketingengine.repositories.Email;

import org.shaft.administration.marketingengine.clients.ElasticRestClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EmailCustomRepositoryImpl implements EmailCustomRepository {
  private final ElasticRestClient elasticRestClient;

  public EmailCustomRepositoryImpl(ElasticRestClient elasticRestClient) {
    this.elasticRestClient = elasticRestClient;
  }

  @Override
  public Mono<String> getQueryResults(int accountId, String query) {
    try {
      return elasticRestClient.getQueryResults(accountId,query);
    } catch (Exception ex){
      throw new RuntimeException("Error fetching query results",ex);
    }
  }
}
