package org.shaft.administration.marketingengine.repositories.Campaign;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.shaft.administration.marketingengine.clients.ElasticRestClient;
import org.shaft.administration.marketingengine.constants.CampaignConstants;
import org.shaft.administration.marketingengine.entity.CampaignCriteria.CampaignCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Repository
public class CampaignCustomRepositoryImpl implements CampaignCustomRepository {
  private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;
  private final ElasticRestClient elasticRestClient;
  private QueryBuilder query;
  private NativeSearchQuery ns;
  HttpHeaders httpHeaders;
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  @Value("${spring.elasticsearch.host}")
  private String elasticsearchHost;

  @Autowired
  public CampaignCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                      RestTemplate restTemplate,
                                      ReactiveElasticsearchClient reactiveElasticsearchClient, ElasticRestClient elasticRestClient) {
    this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
    this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    this.restTemplate = restTemplate;
    this.elasticRestClient = elasticRestClient;
    httpHeaders = new HttpHeaders();
    mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Override
  public Flux<CampaignCriteria> checkIfCampaignExistsForEvent(int account, int eventId) {
    // #TODO If te query has addition filter like User who did app launch and have done added to cart, case needs to be handled here
    query = new BoolQueryBuilder()
      .must(QueryBuilders.termQuery("te.e",eventId))
      .must(QueryBuilders.termQuery("status", CampaignConstants.STATUS_SCHEDULED));
    final SourceFilter filter = new FetchSourceFilter(new String[]{"cid","q","te"}, null);
    ns = new NativeSearchQueryBuilder()
      .withSourceFilter(filter)
      .withQuery(query)
      .build();
    try {
      return reactiveElasticsearchOperations.search(ns, CampaignCriteria.class,IndexCoordinates.of(account + "_camp"))
        .map(SearchHit::getContent)
        .filter(Objects::nonNull)
        .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Flux.empty();
  }

  public ObjectNode checkEligibleCampaignsForI(int accountId, String query) {
    String ELASTIC_URL = elasticsearchHost + ":" + "9200";
    httpHeaders.setContentType(MediaType.APPLICATION_NDJSON);
    HttpEntity<String> entity = new HttpEntity<>(query,httpHeaders);
    String url = "http://".concat(ELASTIC_URL).concat("/").concat(String.valueOf(accountId)).concat("_camp/").concat("_msearch");
    try {
      ResponseEntity<ObjectNode> response = restTemplate.exchange(
        url, HttpMethod.POST,entity, ObjectNode.class);
      return mapper.convertValue(response.getBody(),ObjectNode.class);
    } catch (Exception ex){
      System.out.println(ex.getMessage());
      return mapper.createObjectNode();
    }
  }

  @Override
  public Mono<CampaignCriteria> save(int accountId, CampaignCriteria cc) {
    return reactiveElasticsearchOperations.save(cc,
      IndexCoordinates.of(accountId + "_camp")
    ).doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }

  @Override
  public Flux<CampaignCriteria> getSavedCampaigns(int accountId) {
    query = QueryBuilders.matchAllQuery();
    ns = new NativeSearchQueryBuilder()
      .withQuery(query)
      .withMaxResults(100)
      .withPageable(PageRequest.of(0,50))
      .build();

    return reactiveElasticsearchOperations.search(ns, CampaignCriteria.class,
        IndexCoordinates.of(accountId + "_camp"))
      .map(SearchHit::getContent)
      .filter(Objects::nonNull)
      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }

  @Override
  public Flux<CampaignCriteria> getActivePBSCampaigns(int accountId) {
    query = new BoolQueryBuilder()
      .must(QueryBuilders.termQuery("status",CampaignConstants.STATUS_SCHEDULED))
      .must(QueryBuilders.termQuery("mode",CampaignConstants.PBS_MODE));
    ns = new NativeSearchQueryBuilder()
      .withQuery(query)
      .withMaxResults(100)
      .withPageable(PageRequest.of(0,50))
      .build();

    return reactiveElasticsearchOperations.search(ns, CampaignCriteria.class,
        IndexCoordinates.of(accountId + "_camp"))
      .map(SearchHit::getContent)
      .filter(Objects::nonNull)
      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
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
