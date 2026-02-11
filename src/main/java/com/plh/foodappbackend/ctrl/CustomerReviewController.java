package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.CustomerReview;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.service.CustomerReviewService;
import com.plh.foodappbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-reviews")
@AllArgsConstructor
public class CustomerReviewController {

    private final CustomerReviewService customerReviewService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CustomerReview review,
                                          @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            CustomerReview savedReview = customerReviewService.createReview(review, user);
            return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerReview>> getAllReviews() {
        return new ResponseEntity<>(customerReviewService.getAllReviews(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        try {
            customerReviewService.deleteReview(id);
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
