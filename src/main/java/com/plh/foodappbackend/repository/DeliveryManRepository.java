package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.AVAILABILITY_STATUS;
import com.plh.foodappbackend.model.DeliveryMan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryManRepository extends MongoRepository<DeliveryMan, String> {
    List<DeliveryMan> findByAvailabilityStatus(AVAILABILITY_STATUS status);

    Optional<DeliveryMan> findByMobileNumber(String mobileNumber);
}
