package org.shaft.administration.inventory.repositories.order;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.inventory.services.OrdersDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrdersCustomRepositoryImpl implements OrdersCustomRepository{

    private final RestHighLevelClient esClient;

    @Autowired
    public OrdersCustomRepositoryImpl(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public Long updateOrderStage(int orderId,int status) {
        String index = OrdersDAOImpl.getAccount() + "_orders";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);
        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("oid",orderId)));
        updateRequest.setScript(prepareProductsUpdateScript(status));
        updateRequest.setRefresh(true);
        try {
            BulkByScrollResponse bulkResponse = esClient.updateByQuery(updateRequest, RequestOptions.DEFAULT);
            return bulkResponse.getTotal();
            /*
            TimeValue timeTaken = bulkResponse.getTook();
            log.info("[ELASTICSEARCH_SERVICE] [UPDATE_EXPIRATION_DATE] [TOTAL_UPDATED_DOCS: {}] [TOTAL_DURATION: {}]", totalDocs, timeTaken.getMillis()); */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
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
