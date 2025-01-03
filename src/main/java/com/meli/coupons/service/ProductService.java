package com.meli.coupons.service;

import com.meli.coupons.model.ItemResponse;
import com.meli.coupons.model.ProductsResponse;
import com.meli.coupons.service.integration.ExternalAPIWebflux;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements IProductService{

    private final ExternalAPIWebflux externalAPIWebflux;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ICouponService couponService;

    @Override
    public Mono<ProductsResponse> calculateCoupon(List<String> productIds, float amount) {
        return getValueOfProducts(productIds)
                .map(itemsProducts -> {
                    // Usa couponService para obtener los productos seleccionados
                    List<String> selectedProducts = couponService.calculate(itemsProducts, amount);
                    // Calcula la suma total de los productos seleccionados
                    int totalAmount = selectedProducts.stream()
                            .mapToInt(productId -> itemsProducts.getOrDefault(productId, 0f).intValue())
                            .sum();
                    return new ProductsResponse(selectedProducts, totalAmount);
                });
    }

    public Mono<Map<String, Float>> getValueOfProducts(List<String> listProducts) {
        return Flux.fromIterable(listProducts)
                .flatMap(productId -> getPriceFromCacheOrFetch(productId))  // Get the price from the cache or external API
                .collectMap(ItemResponse::id,
                        item -> {
                            float price = item.price().floatValue();
                            return price;
                        },
                        LinkedHashMap::new);
    }

    // This method retrieves the price from the cache or makes the call to the external API if it's not cached
    private Mono<ItemResponse> getPriceFromCacheOrFetch(String productId) {
        return redisTemplate.opsForValue().get(productId)  // First, attempts to get it from the cache
                .flatMap(price -> {
                    float floatValue = Float.parseFloat(price);
                    return Mono.just(new ItemResponse(productId, (int) floatValue));  // If it exists, creates an ItemResponse from the cache
                })
                .switchIfEmpty(fetchAndCacheProductPrice(productId));  // If it's not in the cache, makes the call to the external API
    }


    // If the price is not cached, it makes the call to the external API and stores it in Redis
    public Mono<ItemResponse> fetchAndCacheProductPrice(String productId) {
        return externalAPIWebflux.fetchItemPrice(productId)  // Makes the call to the external API
                .flatMap(item -> {
                    // Check if the price is null before proceeding
                    if (item.price() == null) {
                        // If the price is null, do nothing and return Mono.empty()
                        log.error("ProductId {} has a null price", productId);
                        return Mono.empty();
                    }
                    // If the price is not null, process it and store it in the cache
                    return Mono.just(item.price().floatValue());
                })
                .flatMap(price -> {
                    // Cache the price in Redis
                    return redisTemplate.opsForValue()
                            .set(productId, price.toString())  // Store the price in Redis
                            .thenReturn(new ItemResponse(productId, price.intValue()));  // Return the ItemResponse object
                });
    }

}
