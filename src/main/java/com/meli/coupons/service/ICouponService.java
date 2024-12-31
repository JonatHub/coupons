package com.meli.coupons.service;

import java.util.List;
import java.util.Map;

public interface ICouponService {

    List<String> calculate(Map<String, Float> items, Float amount);

}
