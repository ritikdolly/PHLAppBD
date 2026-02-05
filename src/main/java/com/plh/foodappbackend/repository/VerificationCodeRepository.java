package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.VerificationCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends MongoRepository<VerificationCode, String> {
    VerificationCode findByEmail(String email);
}
