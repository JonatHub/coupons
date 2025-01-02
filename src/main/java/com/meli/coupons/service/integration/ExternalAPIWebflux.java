package com.meli.coupons.service.integration;

import com.meli.coupons.model.ItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ExternalAPIWebflux {

    private final WebClient webClient;

    public ExternalAPIWebflux() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mercadolibre.com/items/")
                .build();
    }

    public Mono<ItemResponse> fetchItemPrice(String itemId) {
        return webClient.get()
                .uri(itemId)
                .retrieve()
                .bodyToMono(ItemResponse.class)
                .onErrorResume(e -> {
                log.error("Error al obtener el precio del producto con ID {}: {}", itemId, e.getMessage());
                return Mono.empty();
                });
    }

}
