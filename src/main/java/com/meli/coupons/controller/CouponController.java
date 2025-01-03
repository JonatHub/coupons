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


@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final IProductService productService;

    @PostMapping("/")
    public Mono<ResponseEntity<ProductsResponse>> calculateCoupon(@RequestBody ItemRequest items) {
        return productService.calculateCoupon(items.itemId(), items.amount().floatValue())
                .map(ResponseEntity::ok);
    }

}
