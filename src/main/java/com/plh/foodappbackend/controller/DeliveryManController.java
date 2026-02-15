package com.plh.foodappbackend.controller;

import com.plh.foodappbackend.model.AVAILABILITY_STATUS;
import com.plh.foodappbackend.model.DeliveryMan;
import com.plh.foodappbackend.request.DeliveryManRequest;
import com.plh.foodappbackend.service.DeliveryManService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/delivery-men")
public class DeliveryManController {

    @Autowired
    private DeliveryManService deliveryManService;

    @PostMapping
    public ResponseEntity<DeliveryMan> createDeliveryMan(
            @Valid @RequestBody DeliveryManRequest request) throws Exception {
        DeliveryMan deliveryMan = deliveryManService.createDeliveryMan(request);
        return new ResponseEntity<>(deliveryMan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryMan> updateDeliveryMan(
            @PathVariable String id,
            @Valid @RequestBody DeliveryManRequest request) throws Exception {
        DeliveryMan deliveryMan = deliveryManService.updateDeliveryMan(id, request);
        return new ResponseEntity<>(deliveryMan, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryMan(@PathVariable String id) throws Exception {
        deliveryManService.deleteDeliveryMan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryMan> getDeliveryManById(@PathVariable String id) throws Exception {
        DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(id);
        return new ResponseEntity<>(deliveryMan, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryMan>> getAllDeliveryMen() {
        List<DeliveryMan> deliveryMen = deliveryManService.getAllDeliveryMen();
        return new ResponseEntity<>(deliveryMen, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DeliveryMan>> getAvailableDeliveryMen() {
        List<DeliveryMan> deliveryMen = deliveryManService.getAvailableDeliveryMen();
        return new ResponseEntity<>(deliveryMen, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryMan> updateAvailabilityStatus(
            @PathVariable String id,
            @RequestParam AVAILABILITY_STATUS status) throws Exception {
        DeliveryMan deliveryMan = deliveryManService.updateAvailabilityStatus(id, status);
        return new ResponseEntity<>(deliveryMan, HttpStatus.OK);
    }
}
