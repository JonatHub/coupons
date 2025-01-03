package com.meli.coupons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ItemRequest(

        @JsonProperty("item_ids")
        List<String> itemId,

        @JsonProperty("amount")
        Integer amount

) {

}
