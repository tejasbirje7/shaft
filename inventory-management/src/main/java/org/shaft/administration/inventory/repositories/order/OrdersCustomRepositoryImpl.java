package org.shaft.administration.inventory.repositories.order;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.inventory.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@Slf4j
public class OrdersCustomRepositoryImpl implements OrdersCustomRepository{

    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
    private final ReactiveElasticsearchClient reactiveElasticsearchClient;

    @Autowired
    public OrdersCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                      ReactiveElasticsearchClient reactiveElasticsearchClient) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
    }

    @Override
    public Mono<Long> updateOrderStage(int orderId, int status) {
        String index = OrdersService.getAccount() + "_orders";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);
        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("oid",orderId)));
        updateRequest.setScript(prepareProductsUpdateScript(status));
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

    private Script prepareProductsUpdateScript(int status) {
        Map<String,Object> params = new HashMap<>();
        params.put("sg",status);
        String scriptStr = "ctx._source.sg = params.get(\"sg\")";
        return new Script(ScriptType.INLINE, "painless", scriptStr, params);
    }

    // #TODO Check benchmark between these 2 implementations
    /*
    public Long updateCart(int i, List<Object> products) {
        String index = CartDAOImpl.getAccount() + "_cart";
        UpdateRequest req = new UpdateRequest(index,"");
        Map<String,Object> params = new HashMap<>();
        params.put("products",products);
        req.docAsUpsert(true).doc(params);
        return 0L;
    }*/
}
