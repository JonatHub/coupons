package com.meli.coupons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProductsResponse(

        @JsonProperty("item_ids")
        List<String> itemId,

        @JsonProperty("total")
        Integer total

) {

}
