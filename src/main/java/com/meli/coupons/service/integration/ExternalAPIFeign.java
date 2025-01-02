package com.meli.coupons.service.integration;

import com.meli.coupons.model.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mercadoLibreClient", url = "https://api.mercadolibre.com")

public interface ExternalAPIFeign {

    @GetMapping("/items/{itemId}")
    ItemResponse getItemById(@PathVariable("itemId") String itemId);

}
