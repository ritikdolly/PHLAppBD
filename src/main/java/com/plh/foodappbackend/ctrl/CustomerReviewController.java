package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.CustomerReview;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.service.CustomerReviewService;
import com.plh.foodappbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer-reviews")
@AllArgsConstructor
public class CustomerReviewController {

    private final CustomerReviewService customerReviewService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<com.plh.foodappbackend.response.ApiResponse> createReview(@RequestBody CustomerReview review,
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            CustomerReview savedReview = customerReviewService.createReview(review, user);
            return new ResponseEntity<>(
                    new com.plh.foodappbackend.response.ApiResponse(true,
                            "Your review has been submitted successfully.",
                            savedReview),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new com.plh.foodappbackend.response.ApiResponse(false, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<com.plh.foodappbackend.response.ApiResponse> getAllReviews() {
        return new ResponseEntity<>(
                new com.plh.foodappbackend.response.ApiResponse(true, "Reviews fetched successfully",
                        customerReviewService.getAllReviews()),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.plh.foodappbackend.response.ApiResponse> deleteReview(@PathVariable String id) {
        try {
            customerReviewService.deleteReview(id);
            return new ResponseEntity<>(
                    new com.plh.foodappbackend.response.ApiResponse(true, "Review deleted successfully", null),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new com.plh.foodappbackend.response.ApiResponse(false, e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        }
    }
}
