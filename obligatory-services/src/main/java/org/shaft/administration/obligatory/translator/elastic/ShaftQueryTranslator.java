package org.shaft.administration.obligatory.translator.elastic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.obligatory.translator.elastic.services.QueryGenerator;


public class ShaftQueryTranslator {

    QueryGenerator queryGenerator = new QueryGenerator();

    public ObjectNode translateToElasticQuery(ObjectNode rawQuery, boolean aggs) {
        // #TODO Introduce multiple exceptions here
        return queryGenerator.prepareAnalyticsQuery(rawQuery,aggs);
    }
}
