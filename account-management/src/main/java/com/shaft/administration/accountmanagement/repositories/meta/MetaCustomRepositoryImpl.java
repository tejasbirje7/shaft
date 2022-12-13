package com.shaft.administration.accountmanagement.repositories.meta;

import com.shaft.administration.accountmanagement.entity.Meta;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class MetaCustomRepositoryImpl implements MetaCustomRepository{

    ElasticsearchOperations elasticOperations;
    private final RestHighLevelClient esClient;
    QueryBuilder query;
    NativeSearchQuery ns;

    @Autowired
    public MetaCustomRepositoryImpl(ElasticsearchOperations elasticOperations, RestHighLevelClient esClient) {
        this.elasticOperations = elasticOperations;
        this.esClient = esClient;
    }

    @Override
    public Meta getMetaFields(int account,String[] fields) {
        query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("aid",account));
        return queryWithSource(fields);
    }

    private Meta queryWithSource(String[] fields) {
        //include only specific fields
        final SourceFilter filter = new FetchSourceFilter(fields, null);
        ns = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withMaxResults(1)
                .withSourceFilter(filter)
                .build();
        SearchHits<Meta> hits = elasticOperations.search(ns, Meta.class);
        return hits.getSearchHits().get(0).getContent();
    }

    @Override
    public Long pinToDashboard(int accountId, String query) {
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest("accounts_meta");

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("aid",accountId)));
        updateRequest.setScript(prepareDashboardQueriesUpdateScript(query));
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
    private Script prepareDashboardQueriesUpdateScript(String query) {
        String scriptStr = "ctx._source.dashboardQueries.put("+ System.currentTimeMillis() / 1000 + "," + query + ")";
        return new Script( scriptStr);
    }
}
