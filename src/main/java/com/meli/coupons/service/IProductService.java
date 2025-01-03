package com.meli.coupons.service;

import com.meli.coupons.model.ProductsResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IProductService {

    Mono<Map<String, Float>> getValueOfProducts(List<String> listProducts);
    Mono<ProductsResponse> calculateCoupon(List<String> productIds, float amount);

}
