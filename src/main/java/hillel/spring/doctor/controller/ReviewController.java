package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.dto.ReviewDtoConverter;
import hillel.spring.doctor.dto.ReviewInputDto;
import hillel.spring.doctor.dto.ReviewReportAverageRatingDto;
import hillel.spring.doctor.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewDtoConverter reviewDtoConverter;

    @PostMapping("/doctors/reviews")
    public ResponseEntity<?> createDoctorRecordReview(
            @RequestBody ReviewInputDto reviewInputDto) throws URISyntaxException {

        Review review = reviewService.create(reviewDtoConverter.toModel(reviewInputDto));
        return ResponseEntity.created(new URI("/doctors/reviews/" + review.getId())).build();
    }

    @GetMapping("/doctors/reviews/reports/averageRating")
    public ReviewReportAverageRatingDto generateAverageRatingReport() {
        return reviewService.getAverageRatings();
    }


}
