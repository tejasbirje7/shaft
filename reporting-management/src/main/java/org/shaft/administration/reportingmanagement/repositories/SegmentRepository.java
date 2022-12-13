package org.shaft.administration.reportingmanagement.repositories;

import org.shaft.administration.reportingmanagement.entity.Segment;
import org.shaft.administration.reportingmanagement.entity.UserEvents;
import org.shaft.administration.reportingmanagement.repositories.query.QueryCustomRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends ElasticsearchRepository<Segment,Object> {

}

