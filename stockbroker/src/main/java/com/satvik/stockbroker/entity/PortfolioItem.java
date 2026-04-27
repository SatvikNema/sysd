package com.satvik.stockbroker.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioItem {
    private String userId;
    private Stock stock;
    private int qty;
    private double averagePrice;
}
