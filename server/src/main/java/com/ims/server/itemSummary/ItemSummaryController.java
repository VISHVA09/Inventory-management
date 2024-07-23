package com.ims.server.itemSummary;

import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemSummaryController {

    @Autowired
    ItemSummaryService itemSummaryService;

    @GetMapping("/items/summary")
    public EntityModel<ItemSummary> getItemSummary() {
        return itemSummaryService.getItemSummary();
    }
}
