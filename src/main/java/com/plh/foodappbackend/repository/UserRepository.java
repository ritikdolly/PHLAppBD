package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
    // user findByFirebaseUid(String firebaseUid); // Removing this as we might not
    // need it if we trust phone/email or if we verify token. But let's keep it if
    // needed later or just rely on new flow. The plan didn't explicitly remove it
    // but said "Update class reference".
    // Actually, plan says: Add findByPhone.
}
