package org.shaft.administration.catalog.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.shaft.administration.catalog.dao.ItemsDAO;
import org.shaft.administration.obligatory.transactions.ShaftResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/catalog")
public class ItemsController {
    private ItemsDAO itemsDao;
    @Autowired
    public void setItemsDao(ItemsDAO itemsDao) {
        this.itemsDao = itemsDao;
    }

    HttpHeaders headers = new HttpHeaders();

    public ItemsController() {
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * #TODO Check best practices for threadlocal to set in controller or services layer
     */
    @RequestMapping(value = "/items", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getItems(@RequestHeader(value="account") int account,
                                                 @RequestBody(required = false) Map<String,Object> body) {
        return itemsDao.getItems(account,body).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/items/id", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getItemsById(@RequestHeader(value="account") int account,
                                                 @RequestBody(required = false) Map<String,Object> body) {
        return itemsDao.getItemsById(account,body).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/items/bulk", method = { RequestMethod.GET, RequestMethod.POST })
    public Mono<ResponseEntity<Object>> getBulkItems(@RequestHeader(value="account") int account,
                                                     @RequestBody(required = false) Map<String,Object> body) {
        // #TODO Check if this call can be replaced with getItems() above
        return itemsDao.getBulkItems(account,body).map(ShaftResponseHandler::generateResponse);
    }

    @RequestMapping(value = "/items/save", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> saveItem(@RequestHeader(value="account") int account,
                                                 @RequestBody Mono<MultiValueMap<String, Part>> request,
                                                 @RequestPart Mono<FilePart> files) {
        return getResponseEntityMono(account, request);
    }

    @RequestMapping(value = "/items/update", method = { RequestMethod.POST })
    public Mono<ResponseEntity<Object>> updateItem(@RequestHeader(value="account") int account,
                                                   @RequestBody Mono<MultiValueMap<String, Part>> request,
                                                   @RequestPart Mono<FilePart> files) {
        return getResponseEntityMono(account, request);
    }

    private Mono<ResponseEntity<Object>> getResponseEntityMono(@RequestHeader("account") int account, @RequestBody Mono<MultiValueMap<String, Part>> request) {
        return request.flatMap(parts -> {
            Map<String, Part> partMap = parts.toSingleValueMap();

            // Handle file
            FilePart image = (FilePart) partMap.get("files");
            log.info("File name: {}", image.filename());

            // Handle item details
            FormFieldPart details = (FormFieldPart) partMap.get("itemDetails");
            String value = details.value();

            // Parsing and saving item details
            try {
                Map<String,Object> itemDetails = new ObjectMapper().readValue(value, new TypeReference<Map<String, Object>>() {});
                return itemsDao.saveItem(account,itemDetails).map(ShaftResponseHandler::generateResponse);
            } catch (Exception ex) {
                // #TODO Throw invalid request [ MAJOR EXCEPTION ] & notify
            }
            // #TODO Remove this different response handling for this call
            return Mono.just(ShaftResponseHandler.generateResponse("Success","S12345",new ArrayList<>(),headers));
        });
    }
}
