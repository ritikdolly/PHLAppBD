package com.plh.foodappbackend.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

class FoodTest {

    @Test
    void testNoOfferReturnsOriginalPrice() {
        Food food = new Food();
        food.setPrice(new BigDecimal("500.00"));
        food.setOfferActive(false);

        assertEquals(new BigDecimal("500.00"), food.getDiscountedPrice());
    }

    @Test
    void testPercentageDiscount() {
        Food food = new Food();
        food.setPrice(new BigDecimal("500.00"));
        food.setOfferActive(true);
        food.setOfferType("percentage");
        food.setOfferValue(new BigDecimal("20")); // 20% off

        // 500 - (500 * 0.20) = 400
        assertEquals(new BigDecimal("400.00"), food.getDiscountedPrice());
    }

    @Test
    void testFlatDiscount() {
        Food food = new Food();
        food.setPrice(new BigDecimal("500.00"));
        food.setOfferActive(true);
        food.setOfferType("flat");
        food.setOfferValue(new BigDecimal("100")); // 100 off

        // 500 - 100 = 400
        assertEquals(new BigDecimal("400.00"), food.getDiscountedPrice());
    }

    @Test
    void testExpiredOffer() {
        Food food = new Food();
        food.setPrice(new BigDecimal("500.00"));
        food.setOfferActive(true);
        food.setOfferType("percentage");
        food.setOfferValue(new BigDecimal("50"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -2);
        food.setOfferEndDate(cal.getTime()); // Ended 2 days ago

        assertEquals(new BigDecimal("500.00"), food.getDiscountedPrice());
    }

    @Test
    void testFutureOffer() {
        Food food = new Food();
        food.setPrice(new BigDecimal("500.00"));
        food.setOfferActive(true);
        food.setOfferType("percentage");
        food.setOfferValue(new BigDecimal("50"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 2);
        food.setOfferStartDate(cal.getTime()); // Starts in 2 days

        assertEquals(new BigDecimal("500.00"), food.getDiscountedPrice());
    }

    @Test
    void testPriceCannotBeNegative() {
        Food food = new Food();
        food.setPrice(new BigDecimal("100.00"));
        food.setOfferActive(true);
        food.setOfferType("flat");
        food.setOfferValue(new BigDecimal("200")); // 200 off

        // Should be 0, not -100
        assertEquals(BigDecimal.ZERO, food.getDiscountedPrice());
    }
}
