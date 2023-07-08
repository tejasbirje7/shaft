package org.shaft.administration.obligatory.translator.elastic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.obligatory.translator.elastic.services.ElasticQueryGenerator;


public class ShaftQueryTranslator {

    ElasticQueryGenerator elasticQueryGenerator = new ElasticQueryGenerator();

    public ObjectNode translateToElasticQuery(ObjectNode rawQuery, boolean aggs) {
        // #TODO Introduce multiple exceptions here
        return elasticQueryGenerator.prepareAnalyticsQuery(rawQuery,aggs);
    }

}
