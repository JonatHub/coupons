package com.meli.coupons.service;

import com.meli.coupons.model.ItemResponse;
import com.meli.coupons.model.ProductsResponse;
import com.meli.coupons.service.integration.ExternalAPIWebflux;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ExternalAPIWebflux externalAPIWebflux;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ICouponService couponService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {

    }

    @Test
    void testCalculateCoupon_withCacheHit() {

        String productId = "validProductId";
        float price = 100.0f;
        List<String> productIds = List.of(productId);
        float amount = 150.0f;
        List<String> selectedProducts = List.of(productId);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(productId)).thenReturn(Mono.just(String.valueOf(price)));
        when(couponService.calculate(anyMap(), eq(amount))).thenReturn(selectedProducts);
        when(externalAPIWebflux.fetchItemPrice("validProductId"))
                .thenReturn(Mono.just(new ItemResponse("validProductId", 100)));
        Mono<ProductsResponse> responseMono = productService.calculateCoupon(productIds, amount);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(selectedProducts, response.itemId());
                    assertEquals((int) price, response.total());
                })
                .verifyComplete();
    }

    @Test
    void testCalculateCoupon_withCacheMiss() {

        String productId = "product1";
        float price = 100.0f;
        List<String> productIds = List.of(productId);
        float amount = 150.0f;
        List<String> selectedProducts = List.of(productId);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(productId)).thenReturn(Mono.empty());
        when(externalAPIWebflux.fetchItemPrice(productId))
                .thenReturn(Mono.just(new ItemResponse(productId, (int) price)));
        when(valueOperations.set(eq(productId), eq(String.valueOf(price)))).thenReturn(Mono.just(true));
        when(couponService.calculate(anyMap(), eq(amount))).thenReturn(selectedProducts);

        Mono<ProductsResponse> responseMono = productService.calculateCoupon(productIds, amount);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(selectedProducts, response.itemId());
                    assertEquals((int) price, response.total());
                })
                .verifyComplete();
    }

    @Test
    void testGetValueOfProducts_withCacheMiss() {

        List<String> productIds = List.of("product1", "product2");
        Map<String, Float> prices = Map.of("product1", 100.0f, "product2", 200.0f);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("product1")).thenReturn(Mono.empty());
        when(valueOperations.get("product2")).thenReturn(Mono.empty());
        when(externalAPIWebflux.fetchItemPrice("product1"))
                .thenReturn(Mono.just(new ItemResponse("product1", 100)));
        when(externalAPIWebflux.fetchItemPrice("product2"))
                .thenReturn(Mono.just(new ItemResponse("product2", 200)));
        when(valueOperations.set(eq("product1"), eq("100.0"))).thenReturn(Mono.just(true));
        when(valueOperations.set(eq("product2"), eq("200.0"))).thenReturn(Mono.just(true));

        Mono<Map<String, Float>> result = productService.getValueOfProducts(productIds);

        StepVerifier.create(result)
                .assertNext(products -> {
                    assertEquals(prices, products);
                })
                .verifyComplete();
    }

    @Test
    void testFetchAndCacheProductPrice_nullPrice() {
        String productId = "product1";

        when(externalAPIWebflux.fetchItemPrice(productId))
                .thenReturn(Mono.just(new ItemResponse(productId, null)));

        Mono<ItemResponse> result = productService.fetchAndCacheProductPrice(productId);

        StepVerifier.create(result)
                .verifyComplete();
    }




}
