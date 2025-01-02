package com.meli.coupons.service;

import com.meli.coupons.feign.ExternalAPIFeign;
import com.meli.coupons.model.ItemResponse;
import com.meli.coupons.webflux.ExternalAPIWebflux;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ExternalAPIFeign externalAPIFeign;
    private final ExternalAPIWebflux externalAPIWebflux;

    @Override
    public Map<String, Float> getValueOfProducts(List<String> listProducts) {
        Map<String,Float> items = new LinkedHashMap<>();
        List<String> list;
        for (String id:listProducts){
            var item = externalAPIFeign.getItemById(id);
            items.put(id,item.price().floatValue());
        }
        return items;
    }

    public Mono<Map<String, Float>> getValueOfProductsAsync(List<String> listProducts) {
        return Flux.fromIterable(listProducts)
                .flatMap(externalAPIWebflux::fetchItemPrice)
                .collectMap(ItemResponse::id, item -> item.price().floatValue(), LinkedHashMap::new);
    }
}
