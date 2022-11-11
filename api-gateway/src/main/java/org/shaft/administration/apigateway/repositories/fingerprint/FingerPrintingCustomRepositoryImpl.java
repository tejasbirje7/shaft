package org.shaft.administration.apigateway.repositories.fingerprint;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.shaft.administration.apigateway.entity.Fingerprinting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FingerPrintingCustomRepositoryImpl implements FingerPrintingCustomRepository {
    private final ElasticsearchOperations elasticOperations;
    private final RestHighLevelClient esClient;
    private QueryBuilder query;
    private NativeSearchQuery ns;

    @Autowired
    public FingerPrintingCustomRepositoryImpl(ElasticsearchOperations elasticOperations, RestHighLevelClient esClient) {
        this.elasticOperations = elasticOperations;
        this.esClient = esClient;
    }

    @Override
    public List<Fingerprinting> checkIfFpExistsForI(String fp, int i) {
        String index = 1600 + "_devices";

        query = new BoolQueryBuilder()
                .must(QueryBuilders.nestedQuery("fp",
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("fp.g.keyword", fp)), ScoreMode.None))
                .must(QueryBuilders.termQuery("i", i));

        ns = new NativeSearchQueryBuilder()
                .withQuery(query)
                .build();
        try {
            elasticOperations.search(ns, Fingerprinting.class);
            SearchHits<Fingerprinting> hits = elasticOperations.search(ns, Fingerprinting.class);
            return hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Fingerprinting> checkIfIExistsForFp(String fp) {
        String index = 1600 + "_devices";

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
            elasticOperations.search(ns, Fingerprinting.class);
            SearchHits<Fingerprinting> hits = elasticOperations.search(ns, Fingerprinting.class);
            return hits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Long updateFp(String fp,int i) {
        String index = 1600 + "_devices";
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
}
