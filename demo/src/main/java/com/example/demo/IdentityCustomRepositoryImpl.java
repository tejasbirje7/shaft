package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class IdentityCustomRepositoryImpl implements IdentityCustomRepository {
    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;
    private final ReactiveElasticsearchClient reactiveElasticsearchClient;
    private final RestHighLevelClient esClient;
    private QueryBuilder query;
    private NativeSearchQuery ns;

    @Autowired
    public IdentityCustomRepositoryImpl(ReactiveElasticsearchOperations reactiveElasticsearchOperations,
                                        ReactiveElasticsearchClient reactiveElasticsearchClient,
                                        RestHighLevelClient esClient) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
        this.reactiveElasticsearchClient = reactiveElasticsearchClient;
        this.esClient = esClient;
    }

    @Override
    public Flux<Identity> checkIfFpExistsForI(String fp, int i, boolean isIdentified) {
        query = new BoolQueryBuilder()
                .must(QueryBuilders.nestedQuery("fp",
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("fp.g.keyword", fp)), ScoreMode.None))
                .must(QueryBuilders.termQuery("i", i))
                .must(QueryBuilders.termQuery("isIdentified",isIdentified));
        ns = new NativeSearchQueryBuilder()
                .withQuery(query)
                .withMaxResults(1)
                .build();
        try {
            return reactiveElasticsearchOperations.search(ns,Identity.class)
                    .map(SearchHit::getContent)
                    .filter(Objects::nonNull)
                    .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Flux.empty();
    }

    @Override
    public Flux<Identity> checkIfIExistsForFp(String fp) {
        query = new BoolQueryBuilder()
                .must(QueryBuilders.termQuery("isIdentified",false))
                .must(QueryBuilders.termQuery("fp.g",fp));
        final SourceFilter filter = new FetchSourceFilter(new String[]{"i"}, null);
        ns = new NativeSearchQueryBuilder()
                .withSourceFilter(filter)
                .withSorts(SortBuilders.fieldSort("i").order(SortOrder.DESC))
                .withQuery(query)
                .withMaxResults(1)
                .build();
        try {
            return reactiveElasticsearchOperations.search(ns,Identity.class)
                    .map(SearchHit::getContent)
                    .filter(Objects::nonNull)
                    .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Flux.empty();
    }

    @Override
    public Long updateFp(String fp,int i) {
        String index = IdentityDAOImpl.getAccount() + "_devices";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders
                        .termQuery("i",i)));
        Map<String,Object> fpObject = new HashMap<>();
        fpObject.put("g",fp);
        updateRequest.setScript(prepareFpUpdateScript(fpObject));
        updateRequest.setRefresh(true);
        try {
            reactiveElasticsearchClient.updateBy(updateRequest).map(
                    response -> {
                        if(response != null) {
                            System.out.println(response.getTotal());
                        }
                        log.info("Response is {}",response);
                        return response;
                    }).doOnNext( re -> System.out.println(re)).log();
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

    @Override
    public Long upsertFpAndIPair(int account,String fp, int i) {
        String index = account + "_devices";
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);

        updateRequest.setConflicts("proceed");
        updateRequest.setQuery(QueryBuilders
                .boolQuery()
                .must(QueryBuilders.termQuery("i",i)));
        Map<String,Object> fpObject = new HashMap<>();
        fpObject.put("g",fp);
        updateRequest.setScript(upsertFpAndIScript(fpObject));
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

    private Script prepareFpUpdateScript(Map<String,Object> fp) {
        String scriptStr = "ctx._source.fp.add(params.get(\"object\"))";
        Map<String,Object> params = new HashMap<>();
        params.put("object", fp);
        return new Script(ScriptType.INLINE, "painless", scriptStr, params);
    }

    private Script upsertFpAndIScript(Map<String,Object> fpObject) {
        String scriptStr = "if (ctx._source != null) {boolean e = false; " +
                "for(int i = 0; i < ctx._source.fp.length ; i++) { " +
                "if(ctx._source.fp[i].g.equals(params.get(\"fpObject\").g) ){" +
                "e = true;" +
                "break;" +
                "}}" +
                "if (!e) {" +
                "ctx._source.fp.add(params.get(\"fpObject\"))" +
                "}} ";
        Map<String,Object> params = new HashMap<>();
        params.put("fpObject", fpObject);
        return new Script(ScriptType.INLINE, "painless", scriptStr, params);
    }
}
