package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.user;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<user, String> {
    user findByEmail(String email);

    user findByFirebaseUid(String firebaseUid);
}
