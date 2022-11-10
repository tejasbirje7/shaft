package org.shaft.administration.cartmanagement.repositories.custom;

import org.apache.commons.codec.binary.StringUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.shaft.administration.cartmanagement.entity.Product;
import org.shaft.administration.cartmanagement.entity.Products;
import org.shaft.administration.cartmanagement.repositories.custom.CartCustomRepository;
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
    public Long updateCartProducts(int i,List<Products> products) {
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(String.valueOf(CartDAOImpl.getAccount()) + "_cart");
        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("i",i)));
        Map<String,Object> params = new HashMap<>();
        params.put("products", params.toString());
        updateRequest.setScript(new Script(ScriptType.INLINE,
                "painless", "ctx._source.products.add(" + params.get("products") +")",
                params));
        updateRequest.setRefresh(true);
        try {
            BulkByScrollResponse bulkResponse = esClient.updateByQuery(updateRequest, RequestOptions.DEFAULT);
            return bulkResponse.getTotal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
