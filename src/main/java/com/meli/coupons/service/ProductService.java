package com.meli.coupons.service;

import com.meli.coupons.feign.ExternalAPIFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ExternalAPIFeign externalAPIFeign;

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
}
