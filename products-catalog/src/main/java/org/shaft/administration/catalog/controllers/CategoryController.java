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
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // #TODO Remove this after experimenting service
@RequestMapping("/catalog")
public class CategoryController {

    @Autowired
    private CategoryDAO categoryDao;
    HttpHeaders headers = new HttpHeaders();

    @RequestMapping(value = "/categories", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getCategories(@RequestHeader(value="account") int account) {
        List<Category> categories = categoryDao.getCategories(account);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",categories,headers);
    }

    @RequestMapping(value = "/categories/save", method = { RequestMethod.POST })
    public ResponseEntity<Object> saveCategory(@RequestHeader(value="account") int account,
                                               @RequestBody(required = true)Map<String,Object> category) {
        Category c = categoryDao.saveCategory(account,category);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",c,headers);
    }

    @RequestMapping(value = "/categories/update", method = { RequestMethod.POST })
    public ResponseEntity<Object> updateCategory(@RequestHeader(value="account") int account,
                                               @RequestBody(required = true)Map<String,Object> category) {
        Category c = categoryDao.saveCategory(account,category);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",c,headers);
    }
}

