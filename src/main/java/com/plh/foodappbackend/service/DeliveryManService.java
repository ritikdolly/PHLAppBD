package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.AVAILABILITY_STATUS;
import com.plh.foodappbackend.model.DeliveryMan;
import com.plh.foodappbackend.request.DeliveryManRequest;

import java.util.List;

public interface DeliveryManService {
    DeliveryMan createDeliveryMan(DeliveryManRequest request) throws Exception;

    DeliveryMan updateDeliveryMan(String id, DeliveryManRequest request) throws Exception;

    void deleteDeliveryMan(String id) throws Exception;

    DeliveryMan getDeliveryManById(String id) throws Exception;

    List<DeliveryMan> getAllDeliveryMen();

    List<DeliveryMan> getAvailableDeliveryMen();

    DeliveryMan updateAvailabilityStatus(String id, AVAILABILITY_STATUS status) throws Exception;
}
