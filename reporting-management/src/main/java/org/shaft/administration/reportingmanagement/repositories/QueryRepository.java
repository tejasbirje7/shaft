package org.shaft.administration.reportingmanagement.repositories;

import org.shaft.administration.reportingmanagement.entity.UserEvents;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface QueryRepository extends ElasticsearchRepository<UserEvents,Object> {

    @Query("{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"term\":{\"e.eid\":{\"value\":0}}},{\"range\":{\"e.ts\":{\"gte\":1635705000,\"lte\":1636137000}}}]}},{\"bool\":{\"must\":[{\"term\":{\"e.nm.keyword\":{\"value\":\"Chicken Handi\"}}},{\"term\":{\"e.eid\":{\"value\":1}}},{\"range\":{\"e.ts\":{\"gte\":1635705000,\"lte\":1636137000}}}]}},{\"bool\":{\"must\":[{\"range\":{\"e.p\":{\"gte\":\"20\"}}},{\"term\":{\"e.eid\":{\"value\":2}}},{\"range\":{\"e.ts\":{\"gte\":1635705000,\"lte\":1636137000}}}]}}]}},{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"term\":{\"e.eid\":{\"value\":0}}},{\"range\":{\"e.ts\":{\"gte\":1635705000,\"lte\":1636137000}}}]}}]}}]}}")
    Object getQueryResults(@Param("query") String query);
}
