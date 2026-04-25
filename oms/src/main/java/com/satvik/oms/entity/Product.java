package com.satvik.oms.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String id;
    private String sku;
    private double price;
    private int quantity;
}
