package org.shaft.administration.catalog.controllers;

import org.shaft.administration.catalog.dao.CategoryDAO;
import org.shaft.administration.catalog.entity.category.Category;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CategoryController {

    @Autowired
    private CategoryDAO categoryDao;

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategories(@RequestHeader(value="account") int account) {
        List<Category> categories = categoryDao.getCategories(account);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",categories,headers);
    }
}

