package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Review;
import com.plh.foodappbackend.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@AllArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.plh.foodappbackend.response.ApiResponse> deleteReview(@PathVariable String id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok(new com.plh.foodappbackend.response.ApiResponse(true,
                    "Review status updated successfully.", null));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new com.plh.foodappbackend.response.ApiResponse(false, e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
