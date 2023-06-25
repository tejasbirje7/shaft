package org.shaft.administration.reportingmanagement.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.reportingmanagement.entity.Segment;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SegmentDAO {
     Mono<ObjectNode> saveSegment(int accountId, Map<String,Object> rawQuery);
     Mono<ObjectNode> getSavedSegments(int accountId, Map<String,Object> body);
}
