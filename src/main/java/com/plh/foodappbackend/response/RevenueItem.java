package com.plh.foodappbackend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueItem {
    private String label; // e.g., "Mon", "Tue" or "Jan", "Feb"
    private double amount;
}
