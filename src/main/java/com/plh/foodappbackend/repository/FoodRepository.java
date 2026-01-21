package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.Food;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends MongoRepository<Food,String> {

}
