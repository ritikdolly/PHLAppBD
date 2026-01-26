package com.plh.foodappbackend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private long totalOrders;
    private long foodItems;
    private long activeOffers;
    private double revenue;
}
