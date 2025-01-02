package com.meli.coupons.service.integration;

import com.meli.coupons.model.ItemResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
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
                .bodyToMono(ItemResponse.class);
    }

}
