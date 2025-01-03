package com.meli.coupons.controller;

import com.meli.coupons.model.ItemRequest;
import com.meli.coupons.model.ProductsResponse;
import com.meli.coupons.service.IProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CouponController.class)
class CouponControllerTest {

    @MockBean
    private IProductService productService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCalculateCoupon_Success() {
        // Configuración del mock
        List<String> productIds = List.of("prod1", "prod2");
        int amount = 50;
        ItemRequest request = new ItemRequest(productIds, amount);
        List<String> selectedProducts = List.of("prod1");
        int totalAmount = 30; // Supongamos que el monto de "prod1" es 30
        ProductsResponse response = new ProductsResponse(selectedProducts, totalAmount);

        when(productService.calculateCoupon(productIds, amount))
                .thenReturn(Mono.just(response));

        // Prueba del endpoint
        webTestClient.post()
                .uri("/coupon/")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductsResponse.class)
                .value(res -> {
                    assertEquals(selectedProducts, res.itemId());
                    assertEquals(totalAmount, res.total());
                });

        // Verificar que se llamó al servicio con los parámetros correctos
        verify(productService).calculateCoupon(productIds, amount);
    }

    @Test
    void testCalculateCoupon_EmptyResponse() {
        // Configuración del mock
        List<String> productIds = List.of("prod1", "prod2");
        int amount = 50;
        ItemRequest request = new ItemRequest(productIds, amount);

        when(productService.calculateCoupon(productIds, amount))
                .thenReturn(Mono.empty());

        // Prueba del endpoint
        webTestClient.post()
                .uri("/coupon/")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        // Verificar que se llamó al servicio con los parámetros correctos
        verify(productService).calculateCoupon(productIds, amount);
    }

    @Test
    void testCalculateCoupon_Error() {
        // Configuración del mock
        List<String> productIds = List.of("prod1", "prod2");
        int amount = 50;
        ItemRequest request = new ItemRequest(productIds, amount);

        when(productService.calculateCoupon(productIds, amount))
                .thenReturn(Mono.error(new RuntimeException("Error fetching coupon")));

        // Prueba del endpoint
        webTestClient.post()
                .uri("/coupon/")
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(res -> assertTrue(res.contains("Error fetching coupon")));

        // Verificar que se llamó al servicio con los parámetros correctos
        verify(productService).calculateCoupon(productIds, amount);
    }
}
