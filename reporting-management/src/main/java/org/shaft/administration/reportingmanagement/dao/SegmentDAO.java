package org.shaft.administration.reportingmanagement.dao;

import org.shaft.administration.reportingmanagement.entity.Segment;

import java.util.List;
import java.util.Map;

public interface SegmentDAO {
     boolean saveSegment(int accountId, Map<String,Object> rawQuery);
     List<Segment> getSavedSegments(int accountId);
}
