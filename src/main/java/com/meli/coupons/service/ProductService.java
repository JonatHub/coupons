package com.meli.coupons.service;

import com.meli.coupons.service.integration.ExternalAPIFeign;
import com.meli.coupons.model.ItemResponse;
import com.meli.coupons.service.integration.ExternalAPIWebflux;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ExternalAPIFeign externalAPIFeign;
    private final ExternalAPIWebflux externalAPIWebflux;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

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

    public Mono<Map<String, Float>> getValueOfProductsAsync(List<String> listProducts) {
        return Flux.fromIterable(listProducts)
                .flatMap(productId -> getPriceFromCacheOrFetch(productId))  // Obtén el precio desde la caché o API externa
                .collectMap(ItemResponse::id,
                        item -> {
                            float price = item.price().floatValue();
                            System.out.println(price);
                            return price;
                        },
                        LinkedHashMap::new);
    }

    // Este método obtiene el precio desde la caché o realiza el llamado a la API externa si no está en caché
    private Mono<ItemResponse> getPriceFromCacheOrFetch(String productId) {
        return redisTemplate.opsForValue().get(productId)  // Primero intenta obtenerlo de la caché
                .flatMap(price ->
                        {
                            float floatValue = Float.parseFloat(price);
                            return Mono.just(new ItemResponse(productId, (int) floatValue));
                        })  // Si existe, crea un ItemResponse desde la caché
                .switchIfEmpty(fetchAndCacheProductPrice(productId));  // Si no está en la caché, realiza el llamado a la API externa
    }

    // Si el precio no está en caché, realiza el llamado a la API externa y lo guarda en Redis
    private Mono<ItemResponse> fetchAndCacheProductPrice(String productId) {
        return externalAPIWebflux.fetchItemPrice(productId)  // Realiza el llamado a la API externa
                .map(item -> item.price().floatValue())
                .flatMap(price -> {
                    // Cachea el precio en Redis
                    return redisTemplate.opsForValue()
                            .set(productId, price.toString())  // Guarda el precio en Redis
                            .thenReturn(new ItemResponse(productId, price.intValue()));  // Devuelve el objeto ItemResponse
                });
    }
}
