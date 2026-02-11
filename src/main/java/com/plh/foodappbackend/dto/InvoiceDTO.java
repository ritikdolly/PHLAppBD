package com.plh.foodappbackend.dto;

import com.plh.foodappbackend.model.Address;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceDTO {
    private String orderId;
    private Date orderDate;
    private String customerName;
    private String customerPhone;
    private Address shippingAddress;
    private List<InvoiceItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal grandTotal;
    private String status;

    @Data
    public static class InvoiceItemDTO {
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
