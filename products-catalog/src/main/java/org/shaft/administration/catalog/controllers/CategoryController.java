package org.shaft.administration.catalog.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.shaft.administration.catalog.dao.CategoryDAO;
import org.shaft.administration.catalog.entity.category.Category;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/catalog")
public class CategoryController {

    @Autowired
    private CategoryDAO categoryDao;
    HttpHeaders headers = new HttpHeaders();

    public CategoryController() {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @RequestMapping(value = "/categories", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getCategories(@RequestHeader(value="account") int account) {
        return categoryDao.getCategories(account).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/categories/save", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> saveCategory(@RequestHeader(value="account") int account,
                                                     @RequestBody() Map<String,Object> category) {
        return categoryDao.saveCategory(account,category).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/categories/update", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> updateCategory(@RequestHeader(value="account") int account,
                                                       @RequestBody()Map<String,Object> category) {
        return categoryDao.saveCategory(account,category).map(ShaftResponseHandler::generateResponse);
    }
}

