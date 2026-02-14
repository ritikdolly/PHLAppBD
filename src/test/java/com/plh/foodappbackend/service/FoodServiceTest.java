package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Food;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.serviceImpl.FoodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodServiceImpl foodService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFoodWithInvalidDates() {
        Food food = new Food();
        food.setName("Test Food");
        food.setPrice(new BigDecimal("100"));
        food.setOfferActive(true);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date end = cal.getTime(); // End before start

        food.setOfferStartDate(start);
        food.setOfferEndDate(end);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            foodService.addFood(food);
        });

        assertEquals("Offer end date must be after start date", exception.getMessage());
    }

    @Test
    void testAddFoodWithNegativeOfferValue() {
        Food food = new Food();
        food.setName("Test Food");
        food.setPrice(new BigDecimal("100"));
        food.setOfferActive(true);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date end = cal.getTime();

        food.setOfferStartDate(start);
        food.setOfferEndDate(end); // Valid dates
        food.setOfferValue(new BigDecimal("-10")); // Invalid value

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            foodService.addFood(food);
        });

        assertEquals("Offer value must be positive", exception.getMessage());
    }

    @Test
    void testAddFoodWithValidOffer() {
        Food food = new Food();
        food.setName("Test Food");
        food.setPrice(new BigDecimal("100"));
        food.setOfferActive(true);

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date end = cal.getTime();

        food.setOfferStartDate(start);
        food.setOfferEndDate(end);
        food.setOfferValue(new BigDecimal("10"));

        when(foodRepository.save(any(Food.class))).thenReturn(food);

        Food savedFood = foodService.addFood(food);
        assertNotNull(savedFood);
    }
}
