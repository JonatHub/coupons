package com.meli.coupons.service;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IProductService {

    Map<String,Float> getValueOfProducts(List<String> listProducts);
    Mono<Map<String, Float>> getValueOfProductsAsync(List<String> listProducts);
}
