package org.shaft.administration.accountmanagement.repositories.meta;

import org.shaft.administration.accountmanagement.entity.EventsMeta;
import org.shaft.administration.accountmanagement.entity.Meta;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.shaft.administration.accountmanagement.entity.PropsMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Repository
public class MetaCustomRepositoryImpl implements MetaCustomRepository {

    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
    private final ReactiveElasticsearchClient reactiveElasticsearchClient;
    QueryBuilder query;
    NativeSearchQuery ns;

    @Autowired
    public MetaCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                    ReactiveElasticsearchClient reactiveElasticsearchClient) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    }

    @Override
    public Mono<Meta> getMetaFields(int account, String[] fields) {
        query = QueryBuilders.boolQuery()
          .must(QueryBuilders.termQuery("aid",account));
        return queryWithSource(fields);
    }
    @Override
    public Mono<Meta> getMetaFields(int account) {
        query = QueryBuilders.boolQuery()
          .must(QueryBuilders.termQuery("aid",account));
        return queryMetaWithoutSource();
    }

    private Mono<Meta> queryWithSource(String[] fields) {
        //include only specific fields
        final SourceFilter filter = new FetchSourceFilter(fields, null);
        ns = new NativeSearchQueryBuilder()
          .withQuery(query)
          .withMaxResults(1)
          .withSourceFilter(filter)
          .build();
        try {
            return reactiveElasticsearchOperations.search(ns, Meta.class)
              .map(SearchHit::getContent)
              .filter(Objects::nonNull)
              .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
              .single();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }

    private Mono<Meta> queryMetaWithoutSource() {
        ns = new NativeSearchQueryBuilder()
          .withQuery(query)
          .withMaxResults(1)
          .build();
        try {
            return reactiveElasticsearchOperations.search(ns, Meta.class)
              .map(SearchHit::getContent)
              .filter(Objects::nonNull)
              .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
              .single();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }

    @Override
    public Mono<Long> pinToDashboard(int accountId, String query) {
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest("accounts_meta");

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
          .boolQuery()
          .must(QueryBuilders
            .termQuery("aid",accountId)));
        updateRequest.setScript(prepareDashboardQueriesUpdateScript(query));
        updateRequest.setRefresh(true);
        return reactiveElasticsearchClient.updateBy(updateRequest).map(response -> {
              if(response != null) {
                  log.info("Total Updated {}",response.getTotal());
                  //TimeValue timeTaken = bulkResponse.getTook();
                  return response.getTotal();
              }
              return 0L;
          })
          .filter(Objects::nonNull)
          .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    @Override
    public Mono<Meta> saveAccountMeta(Meta accountInfo) {
        return reactiveElasticsearchOperations.save(accountInfo,
          IndexCoordinates.of( "accounts_meta")
        ).doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }


    @Override
    public Flux<EventsMeta> getEventsMeta(int accountId) {
        query = QueryBuilders.matchAllQuery();
        ns = new NativeSearchQueryBuilder()
          .withQuery(query)
          .withMaxResults(100)
          .withPageable(PageRequest.of(0,100))
          .build();

        return reactiveElasticsearchOperations.search(ns, EventsMeta.class,
            IndexCoordinates.of(accountId + "_event_schema"))
          .map(SearchHit::getContent)
          .filter(Objects::nonNull)
          .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    @Override
    public Flux<PropsMeta> getPropsMeta(int accountId) {
        query = QueryBuilders.matchAllQuery();
        ns = new NativeSearchQueryBuilder()
          .withQuery(query)
          .withMaxResults(100)
          .withPageable(PageRequest.of(0,100))
          .build();

        return reactiveElasticsearchOperations.search(ns, PropsMeta.class,
            IndexCoordinates.of(accountId + "_props_schema"))
          .map(SearchHit::getContent)
          .filter(Objects::nonNull)
          .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }

    private Script prepareDashboardQueriesUpdateScript(String query) {
        String scriptStr = "ctx._source.dashboardQueries.put("+ System.currentTimeMillis() / 1000 + "," + query + ")";
        return new Script( scriptStr);
    }
}
