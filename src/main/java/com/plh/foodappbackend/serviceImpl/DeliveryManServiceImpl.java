package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.AVAILABILITY_STATUS;
import com.plh.foodappbackend.model.DeliveryMan;
import com.plh.foodappbackend.repository.DeliveryManRepository;
import com.plh.foodappbackend.request.DeliveryManRequest;
import com.plh.foodappbackend.service.DeliveryManService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeliveryManServiceImpl implements DeliveryManService {

    @Autowired
    private DeliveryManRepository deliveryManRepository;

    @Override
    public DeliveryMan createDeliveryMan(DeliveryManRequest request) throws Exception {
        // Check if mobile number already exists
        if (deliveryManRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            throw new Exception("Delivery man with this mobile number already exists");
        }

        DeliveryMan deliveryMan = new DeliveryMan();
        deliveryMan.setName(request.getName());
        deliveryMan.setMobileNumber(request.getMobileNumber());
        deliveryMan.setEmail(request.getEmail());
        deliveryMan.setVehicleType(request.getVehicleType());
        deliveryMan.setAvailabilityStatus(AVAILABILITY_STATUS.AVAILABLE);
        deliveryMan.setCreatedAt(new Date());
        deliveryMan.setUpdatedAt(new Date());

        return deliveryManRepository.save(deliveryMan);
    }

    @Override
    public DeliveryMan updateDeliveryMan(String id, DeliveryManRequest request) throws Exception {
        DeliveryMan deliveryMan = deliveryManRepository.findById(id)
                .orElseThrow(() -> new Exception("Delivery man not found"));

        // Check if mobile number is being changed and if it already exists
        if (!deliveryMan.getMobileNumber().equals(request.getMobileNumber())) {
            if (deliveryManRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
                throw new Exception("Another delivery man with this mobile number already exists");
            }
        }

        deliveryMan.setName(request.getName());
        deliveryMan.setMobileNumber(request.getMobileNumber());
        deliveryMan.setEmail(request.getEmail());
        deliveryMan.setVehicleType(request.getVehicleType());
        deliveryMan.setUpdatedAt(new Date());

        return deliveryManRepository.save(deliveryMan);
    }

    @Override
    public void deleteDeliveryMan(String id) throws Exception {
        DeliveryMan deliveryMan = deliveryManRepository.findById(id)
                .orElseThrow(() -> new Exception("Delivery man not found"));

        // Check if delivery man is currently busy
        if (deliveryMan.getAvailabilityStatus() == AVAILABILITY_STATUS.BUSY) {
            throw new Exception("Cannot delete delivery man who is currently assigned to orders");
        }

        deliveryManRepository.deleteById(id);
    }

    @Override
    public DeliveryMan getDeliveryManById(String id) throws Exception {
        return deliveryManRepository.findById(id)
                .orElseThrow(() -> new Exception("Delivery man not found"));
    }

    @Override
    public List<DeliveryMan> getAllDeliveryMen() {
        return deliveryManRepository.findAll();
    }

    @Override
    public List<DeliveryMan> getAvailableDeliveryMen() {
        return deliveryManRepository.findByAvailabilityStatus(AVAILABILITY_STATUS.AVAILABLE);
    }

    @Override
    public DeliveryMan updateAvailabilityStatus(String id, AVAILABILITY_STATUS status) throws Exception {
        DeliveryMan deliveryMan = deliveryManRepository.findById(id)
                .orElseThrow(() -> new Exception("Delivery man not found"));

        deliveryMan.setAvailabilityStatus(status);
        deliveryMan.setUpdatedAt(new Date());

        return deliveryManRepository.save(deliveryMan);
    }
}
