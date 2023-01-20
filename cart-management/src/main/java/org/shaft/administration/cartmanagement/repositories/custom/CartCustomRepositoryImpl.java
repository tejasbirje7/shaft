package org.shaft.administration.cartmanagement.repositories.custom;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.cartmanagement.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
@Slf4j
@Repository
public class CartCustomRepositoryImpl implements CartCustomRepository {
    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
    private final ReactiveElasticsearchClient reactiveElasticsearchClient;

    @Autowired
    public CartCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                    ReactiveElasticsearchClient reactiveElasticsearchClient) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    }

    @Override
    public Mono<Long> transactCartProducts(int i, Map<String,Object> product) {
        String index = CartService.getAccount() + "_cart";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
          .boolQuery()
          .must(QueryBuilders
            .termQuery("i",i)));
        updateRequest.setScript(prepareProductsUpdateScript(product));
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

    private Script prepareProductsUpdateScript(Map<String,Object> params) {
        // #TODO Remove Map dependency and insert string here
        String scriptStr = "ctx._source.products = params.get(\"products\")";
        return new Script(ScriptType.INLINE, "painless", scriptStr, params);
    }

    @Override
    public Mono<Long> deleteUsersCart(int i) {

        DeleteByQueryRequest request = new DeleteByQueryRequest();
        request.setQuery(new BoolQueryBuilder()
          .must(QueryBuilders.termQuery("i",i)));

        return reactiveElasticsearchClient.deleteBy(request)
          .map(response -> {
              if(response != null) {
                  log.info("Total Updated {}",response.getTotal());
                  //TimeValue timeTaken = bulkResponse.getTook();
                  return response.getDeleted();
              }
              return 0L;
          })
          .filter(Objects::nonNull)
          .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
