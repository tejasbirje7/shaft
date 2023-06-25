package org.shaft.administration.reportingmanagement.repositories.segment;

import org.shaft.administration.reportingmanagement.entity.Segment;
import reactor.core.publisher.Flux;

public interface SegmentCustomRepository {
  Flux<Segment> getSegments(String[] fields);
}
