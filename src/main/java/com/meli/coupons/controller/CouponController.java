package com.meli.coupons.controller;

import com.meli.coupons.model.ItemRequest;
import com.meli.coupons.model.ProductsResponse;
import com.meli.coupons.service.ICouponService;
import com.meli.coupons.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;

    private final IProductService productService;

    @PostMapping("/")
    public ResponseEntity<ProductsResponse> calculateCoupon(@RequestBody ItemRequest items) {
        var itemsProducts = productService.getValueOfProducts(items.itemId());
        var result = couponService.calculate(itemsProducts, items.amount().floatValue());
        var productsResponse = new ProductsResponse(result,itemsProducts.values().stream()
                .mapToInt(Float::intValue)
                .sum());
        return ResponseEntity.ok(productsResponse);
    }

    @PostMapping("/async")
    public Mono<ResponseEntity<ProductsResponse>> calculateCouponAsync(@RequestBody ItemRequest items) {
        return productService.getValueOfProductsAsync(items.itemId())
                .map(itemsProducts -> {
                    List<String> selectedProducts = couponService.calculate(itemsProducts, items.amount().floatValue());
                    int totalAmount = selectedProducts.stream()
                            .mapToInt(productId -> itemsProducts.getOrDefault(productId, 0f).intValue())
                            .sum();
                    return new ProductsResponse(selectedProducts, totalAmount);
                })
                .map(ResponseEntity::ok);
    }

}
