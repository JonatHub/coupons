package com.meli.coupons.service;

import java.util.List;
import java.util.Map;

public interface IProductService {

    Map<String,Float> getValueOfProducts(List<String> listProducts);
}
