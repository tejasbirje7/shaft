package com.shaft.administration.accountmanagement.repositories.meta;

import com.shaft.administration.accountmanagement.entity.Meta;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;

public class MetaCustomRepositoryImpl implements MetaCustomRepository{

    ElasticsearchOperations elasticOperations;
    QueryBuilder query;
    NativeSearchQuery ns;

    @Autowired
    public MetaCustomRepositoryImpl(ElasticsearchOperations elasticOperations) {
        this.elasticOperations = elasticOperations;
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
}
