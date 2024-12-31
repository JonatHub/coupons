package com.meli.coupons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemResponse(

        @JsonProperty("id")
        String id,

        @JsonProperty("price")
        Integer price

) {

}
