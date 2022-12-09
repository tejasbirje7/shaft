package org.shaft.administration.catalog.controllers;

import org.eclipse.jetty.http.MultiPartFormInputStream;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.catalog.entity.item.Item;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // #TODO Remove this after experimenting service
@RequestMapping("/catalog")
public class ItemsController {
    private ItemsDAO itemsDao;
    @Autowired
    public void setItemsDao(ItemsDAO itemsDao) {
        this.itemsDao = itemsDao;
    }

    /**
     * #TODO Check best practices for threadlocal to set in controller or services layer
     */
    @RequestMapping(value = "/items", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getItems(@RequestHeader(value="account") int account,
                                           @RequestBody(required = false) Map<String,Object> body) {
        List<Item> items = itemsDao.getItems(account,body);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S78gsd8v",items,headers);
    }

    @RequestMapping(value = "/items/bulk", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<Object> getBulkItems(@RequestHeader(value="account") int account,
                                               @RequestBody(required = false) Map<String,Object> body) {
        List<Item> items = itemsDao.getBulkItems(account,body);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S12345",items,headers);
    }

    @RequestMapping(value = "/items/save", method = { RequestMethod.POST })
    public ResponseEntity<Object> saveItem(@RequestHeader(value="account") int account,
                                               @RequestParam Map<String,Object> itemDetails,
                                               @RequestParam MultipartFile files) {
        Item item = itemsDao.saveItem(account,itemDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S12345",item,headers);
    }

    @RequestMapping(value = "/items/update", method = { RequestMethod.POST })
    public ResponseEntity<Object> updateItem(@RequestHeader(value="account") int account,
                                           @RequestParam Map<String,Object> itemDetails,
                                           @RequestParam MultipartFile files) {
        Item item = itemsDao.saveItem(account,itemDetails);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ShaftResponseHandler.generateResponse("Success","S12345",item,headers);
    }
}
