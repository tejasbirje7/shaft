package org.shaft.administration.cartmanagement.repositories.custom;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.cartmanagement.entity.Product;
import org.shaft.administration.cartmanagement.entity.Products;
import org.shaft.administration.cartmanagement.services.CartDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class CartCustomRepositoryImpl implements CartCustomRepository {
    private final ElasticsearchOperations elasticOperations;
    private final RestHighLevelClient esClient;

    @Autowired
    public CartCustomRepositoryImpl(ElasticsearchOperations elasticOperations, RestHighLevelClient esClient) {
        this.elasticOperations = elasticOperations;
        this.esClient = esClient;
    }

    @Override
    public Long transactCartProducts(int i,Map<String,Object> product) {
        String index = CartDAOImpl.getAccount() + "_cart";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("i",i)));
        updateRequest.setScript(prepareProductsUpdateScript(product));
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

    private Script prepareProductsUpdateScript(Map<String,Object> params) {
        String scriptStr = "ctx._source.products = params.get(\"product\")";
        return new Script(ScriptType.INLINE, "painless", scriptStr, params);
    }

    // https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-update.html
    public Long updateCart(int i, List<Object> products) {
        String index = CartDAOImpl.getAccount() + "_cart";
        UpdateRequest req = new UpdateRequest(index,"");
        Map<String,Object> params = new HashMap<>();
        params.put("products",products);
        req.docAsUpsert(true).doc(params);
        return 0L;
    }
}
