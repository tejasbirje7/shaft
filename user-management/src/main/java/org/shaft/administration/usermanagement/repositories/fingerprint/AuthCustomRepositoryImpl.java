package org.shaft.administration.usermanagement.repositories.fingerprint;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class AuthCustomRepositoryImpl implements AuthCustomRepository {
  private final ReactiveElasticsearchClient reactiveElasticsearchClient;

  public AuthCustomRepositoryImpl(ReactiveElasticsearchClient reactiveElasticsearchClient) {
    this.reactiveElasticsearchClient = reactiveElasticsearchClient;
  }

  @Override
  public Mono<Long> updateEmail(String account,String email, int i) {

    String index =  account + "_1*";
    UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(index);

    updateRequest.setConflicts("proceed");
    updateRequest.setQuery(QueryBuilders
      .boolQuery()
      .must(QueryBuilders.termQuery("i",i)));
    updateRequest.setScript(upsertEmailToIMapping(email));
    updateRequest.setRefresh(true);

    return  reactiveElasticsearchClient.updateBy(updateRequest).map(response -> {
        if(response != null) {
          log.info("Total Updated {}",response.getTotal());
          return response.getTotal();
        }
        return 0L;
      })
      .filter(Objects::nonNull)
      .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
  }

  private Script upsertEmailToIMapping(String email) {
    String scriptStr = "    if(ctx._source!=null){\n" +
      "      ctx._source.put(\"email\", params.get(\"email\"))\n" +
      "    } ";
    Map<String,Object> params = new HashMap<>();
    params.put("email", email);
    return new Script(ScriptType.INLINE, "painless", scriptStr, params);
  }
}
