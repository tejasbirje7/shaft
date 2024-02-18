package org.shaft.administration.customermanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.customermanagement.dao.TemplateConfigurationDao;
import org.shaft.administration.customermanagement.entity.TemplateConfiguration;
import org.shaft.administration.customermanagement.repositories.TemplateConfigurationRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TemplateConfigurationService implements TemplateConfigurationDao {
  private final TemplateConfigurationRepository templateConfigurationRepository;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  public ObjectMapper mapper;
  @Autowired
  public TemplateConfigurationService(TemplateConfigurationRepository templateConfigurationRepository) {
    this.templateConfigurationRepository = templateConfigurationRepository;
    this.mapper = new ObjectMapper();
  }

  @Override
  public Mono<ObjectNode> getTemplateConfiguration(int accountId, ObjectNode requestObject) {
    return templateConfigurationRepository.getTemplateConfiguration(accountId)
      .collectList()
      .map(config -> {
        log.info("Template Configuration : {}",config);
        return ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.TEMPLATE_CONFIG_FETCHED,mapper.valueToTree(config));
      })
      .onErrorResume(error -> Mono.just(ShaftResponseBuilder.buildResponse(
        ShaftResponseCode.FAILED_TO_FETCH_TEMPLATE_CONFIG)));
  }

  @Override
  public Mono<ObjectNode> updateTemplateConfiguration(int accountId, Mono<MultiValueMap<String, Part>> requestObject) {
    ACCOUNT_ID.set(accountId);
    return requestObject.flatMap(parts -> {
      Map<String, Part> partMap = parts.toSingleValueMap();

      // Fetch template configuration details
      FormFieldPart configuredTemplatePart = (FormFieldPart) partMap.get("configuredTemplate");
      String configuredTemplate = configuredTemplatePart.value();

      // Fetch template blob details
      FormFieldPart templateBlobsPart = (FormFieldPart) partMap.get("templateBlobs");
      String templateBlobsString = templateBlobsPart.value();

      try {
        TemplateConfiguration templateConfiguration = mapper.readValue(configuredTemplate, TemplateConfiguration.class);
        JsonNode templateBlobs = mapper.readValue(templateBlobsString, JsonNode.class);
        ObjectNode failedFilesMap = mapper.createObjectNode();
        Map<String,Object> map = mapper.convertValue(templateConfiguration, new TypeReference<Map<String, Object>>(){});
        return templateConfigurationRepository.update(ACCOUNT_ID.get(),map)
          .map(totalUpdated -> {
            if(totalUpdated > 0){
              templateConfiguration.getTemplateOptions().forEach(templateOption -> {
                templateOption.getChildren().forEach(child -> {
                  if(child.getFields().getType().equals("file")) {

                    // Handle saving files
                    if(templateBlobs.get(templateOption.getGroupName()).has(child.getTitle())) {
                      templateBlobs.get(templateOption.getGroupName()).get(child.getTitle()).forEach(k -> {

                        FilePart image = (FilePart) partMap.get(k.asText());
                        saveAssets(image,k.asText()).map(isSaved -> {
                          if(!isSaved){
                            failedFilesMap.put(k.asText(),false);
                          }
                          return failedFilesMap;
                        }).subscribe();
                      });
                    }}
                });
              });
              ACCOUNT_ID.remove();
              return ShaftResponseBuilder.buildResponse(
                ShaftResponseCode.TEMPLATE_CONFIG_UPDATED,failedFilesMap);
            } else {
              ACCOUNT_ID.remove();
              return ShaftResponseBuilder.buildResponse(
                ShaftResponseCode.FAILED_TO_UPDATE_TEMPLATE_CONFIG,failedFilesMap);
            }
          });
      } catch (JsonProcessingException e) {
        ACCOUNT_ID.remove();
        return Mono.just(ShaftResponseBuilder.buildResponse(
          ShaftResponseCode.EXCEPTION_WHILE_UPDATING_TEMPLATE_CONFIG));
      }
    });
  }

  public Mono<Boolean> saveAssets(FilePart image, String fileName) {
//    log.info("File name: {}", image.filename());
    return image.transferTo(new File("/opt/shop_assets/1600/template",fileName))
      .flatMap(r -> Mono.just(true))
      .onErrorResume(error -> Mono.just(false));
  }
}
