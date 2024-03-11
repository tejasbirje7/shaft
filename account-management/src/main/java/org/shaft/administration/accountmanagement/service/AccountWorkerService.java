package org.shaft.administration.accountmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.accountmanagement.clients.ElasticRestClient;
import org.shaft.administration.accountmanagement.dao.AccountWorkerDao;
import org.shaft.administration.accountmanagement.entity.Meta;
import org.shaft.administration.accountmanagement.repositories.MetaRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AccountWorkerService implements AccountWorkerDao {
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  private final ObjectMapper mapper;
  private final ObjectReader mapParser;
  private final ElasticRestClient elasticRestClient;
  private final MetaRepository metaRepository;

  public AccountWorkerService(ElasticRestClient elasticRestClient, MetaRepository metaRepository) {
    this.mapParser = new ObjectMapper().readerFor(Map.class);
    this.elasticRestClient = elasticRestClient;
    this.mapper = new ObjectMapper();
    this.metaRepository = metaRepository;
  }
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  // #TODO Add rollback support here
  @Override
  public Mono<ObjectNode> bootstrapAccount(Map<String, Object> request) {
    int accountId = (int) Instant.now().getEpochSecond();
    String accountIndex = accountId + "_" + Instant.now().getEpochSecond();
    return elasticRestClient.insertDeviceMappings(accountId,getDeviceMappingRequest()) // Insert device mappings
      .flatMap(deviceMappingIndexingResponse -> {
        try {
          Map<String, Object> checkIfSuccess = mapParser.readValue(deviceMappingIndexingResponse);
          if(checkIfSuccess.containsKey("acknowledged") && checkIfSuccess.get("acknowledged").equals(true)) { // Is device mapping updated ?
            return elasticRestClient.insertEventRequestsMapping(accountIndex,getEventsIndexRequest()) // Insert event requests index
              .flatMap(eventIndexingResponse -> {
                try {
                  Map<String, Object> isSuccess = mapParser.readValue(deviceMappingIndexingResponse); // Is event request index updated ?
                  if(isSuccess.containsKey("acknowledged") && isSuccess.get("acknowledged").equals(true)) {
                    return elasticRestClient.createTemplateConfigurationIndex(accountId)
                      .flatMap(templateCreated -> {
                        if(isSuccess.containsKey("acknowledged") && isSuccess.get("acknowledged").equals(true)) {
                          Meta accountMeta = new Meta();
                          accountMeta.setAid(accountId);
                          accountMeta.setName((String) request.get("accountName"));
                          accountMeta.setIdx(accountIndex);
                          accountMeta.setDashboardQueries(new HashMap<>());
                          accountMeta.setTemplateId((String) request.get("templateId"));
                          return metaRepository.saveAccountMeta(accountMeta) // Update event index name in account
                            .map(response -> {
                              // #TODO Check response here - "status" key and then respond to the service call
                              ObjectNode successResp = mapper.createObjectNode();
                              successResp.put("accountIndex",accountIndex);
                              successResp.put("accountId",accountId);
                              return ShaftResponseBuilder.buildResponse(ShaftResponseCode.ACCOUNT_BOOTSTRAPPED,successResp);
                            })
                            .onErrorResume(error -> {
                              if (error instanceof RestStatusException) {
                                try {
                                  ObjectNode on = mapParser.readValue(error.getMessage(), ObjectNode.class);
                                  if(on.get("result").asText().equals("created")) {
                                    ObjectNode response = mapper.createObjectNode();
                                    response.put("accountIndex",accountIndex);
                                    response.put("accountId",accountId);
                                    return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ACCOUNT_BOOTSTRAPPED,response));
                                  } else {
                                    return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_UPDATING_EVENT_IDX_IN_ACC));
                                  }
                                } catch (IOException e) {
                                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_UPDATING_EVENT_IDX_IN_ACC));
                                }
                              } else {
                                return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_UPDATING_EVENT_IDX_IN_ACC));
                              }
                            });
                        } else {
                          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_INSERT_TEMPLATE_CONFIG_BOILER));
                        }
                      })
                      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_INSERTING_TEMPLATE_CONFIG_BOILER)));
                  } else {
                    return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_INSERT_EVENT_TRACKING_INDEX));
                  }
                } catch (JsonProcessingException e) {
                  return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_PARSING_EVENT_TRACKING_INDEX_RESP));
                }
              })
              .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.ERROR_INSERTING_EVENT_TRACKING_INDEX)));
          } else {
            return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_TO_UPDATE_DEVICE_MAPPINGS));
          }
        } catch (JsonProcessingException e) {
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.EXCEPTION_PARSING_DEVICE_MAPPING_RESP));
        }
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_CREATING_ENTRY_IN_ACCOUNT)));
  }

  public String getDeviceMappingRequest() {
    return "    {\n" +
      "  \"mappings\": {\n" +
      "    \"properties\": {\n" +
      "      \"fp\" :{\n" +
      "        \"type\": \"nested\",\n" +
      "        \"include_in_root\": true\n" +
      "      }\n" +
      "    }\n" +
      "  }\n" +
      "}";
  }

  public String getEventsIndexRequest() {
    return "{\n" +
      "  \"mappings\": {\n" +
      "    \"properties\": {\n" +
      "      \"e\": {\n" +
      "        \"type\": \"nested\",\n" +
      "        \"include_in_root\": true\n" +
      "      },\n" +
      "      \"i\": {\n" +
      "        \"type\": \"long\"\n" +
      "      },\n" +
      "      \"props\": {\n" +
      "        \"type\": \"nested\"\n" +
      "      }\n" +
      "    }\n" +
      "  }\n" +
      "}";
  }
}
