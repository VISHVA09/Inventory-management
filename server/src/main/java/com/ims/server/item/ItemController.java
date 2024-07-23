package com.ims.server.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemController {

    @Autowired
    ItemService itemService;

     @GetMapping("/items")
    public CollectionModel<EntityModel<Item>> getItems() {
        return itemService.getItems();
    }

    @PostMapping("/items")
    public ResponseEntity<?> saveItem(@RequestBody Item item) {
        return itemService.saveItem(item);
    }

    @GetMapping("/items/{itemId}")
    public EntityModel<Item> getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> replaceItem(@PathVariable Long itemId, @RequestBody Item newItem) {
        return itemService.replaceItem(itemId, newItem);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        return itemService.deleteItem(itemId);
    }
}
