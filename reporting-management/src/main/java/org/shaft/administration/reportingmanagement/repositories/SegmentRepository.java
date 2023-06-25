package org.shaft.administration.reportingmanagement.repositories;

import org.shaft.administration.reportingmanagement.entity.Segment;
import org.shaft.administration.reportingmanagement.repositories.segment.SegmentCustomRepository;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends ReactiveElasticsearchRepository<Segment,Object>, SegmentCustomRepository {

}

