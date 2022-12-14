package org.shaft.administration.usermanagement.repositories;

import org.shaft.administration.usermanagement.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthRepository extends ElasticsearchRepository<User,String> {
    User findByEAndP(String e, String p);
}
