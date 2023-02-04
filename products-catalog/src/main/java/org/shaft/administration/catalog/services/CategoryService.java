package org.shaft.administration.catalog.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.catalog.constants.ProductCatalogConstants;
import org.shaft.administration.catalog.constants.ProductCatalogLogs;
import org.shaft.administration.catalog.dao.CategoryDAO;
import org.shaft.administration.catalog.entity.category.Category;
import org.shaft.administration.catalog.repositories.CategoryRepository;
import org.shaft.administration.obligatory.constants.ShaftResponseCode;
import org.shaft.administration.obligatory.transactions.ShaftResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.RestStatusException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class CategoryService implements CategoryDAO {
  private CategoryRepository categoryRepository;
  private ObjectMapper mapper;
  public static ThreadLocal<Integer> ACCOUNT_ID = ThreadLocal.withInitial(() -> 0);
  //#TODO Remove local thread dependency
  public static int getAccount() {
    return ACCOUNT_ID.get();
  }

  @Autowired
  public void setCategoryRepository(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
    mapper = new ObjectMapper();
  }

  /**
   * #TODO Implement Cache here
   * @param accountId
   * @return List
   */
  @Override
  public Mono<ObjectNode> getCategories(int accountId) {
    ACCOUNT_ID.set(accountId);
    return categoryRepository.findAll()
      .collectList()
      .map(c -> ShaftResponseBuilder.buildResponse(ShaftResponseCode.CATEGORIES_FETCHED_SUCCESSFULLY,
        mapper.valueToTree(c)))
      .onErrorResume( t -> {
        log.error(ProductCatalogLogs.UNABLE_TO_FETCH_CATEGORIES,t);
        return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.FAILED_FETCHING_CATEGORIES));
      });
  }
  @Override
  public Mono<ObjectNode> saveCategory(int accountId, Map<String,Object> category) {
    ACCOUNT_ID.set(accountId);
    Category c = mapper.convertValue(category, new TypeReference<Category>() {});
    return categoryRepository.save(c)
      .map(savedCategory -> {
        ACCOUNT_ID.remove();
        return ShaftResponseBuilder.buildResponse(ShaftResponseCode.CATEGORY_SAVED_SUCCESSFULLY,
          mapper.convertValue(savedCategory, ObjectNode.class));
      })
      .onErrorResume(error -> {
        ACCOUNT_ID.remove();
        if(isRestStatusException(error)) {
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.IDENTITY_FETCHED_SUCCESSFULLY,
            mapper.convertValue(category,ObjectNode.class)));
        } else {
          log.error(ProductCatalogLogs.SHAFT_CATEGORY_SAVE_EXCEPTION,error);
          return Mono.just(ShaftResponseBuilder.buildResponse(ShaftResponseCode.SHAFT_CATEGORY_SAVE_EXCEPTION));
        }
      });
  }
  private boolean isRestStatusException(Throwable t) {
    return t instanceof RestStatusException;
  }
}
