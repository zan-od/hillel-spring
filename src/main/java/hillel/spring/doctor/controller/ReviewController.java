package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.dto.ReviewDtoConverter;
import hillel.spring.doctor.dto.ReviewInputDto;
import hillel.spring.doctor.dto.ReviewOutputDto;
import hillel.spring.doctor.dto.ReviewReportAverageRatingDto;
import hillel.spring.doctor.exception.ResourceNotFoundException;
import hillel.spring.doctor.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewDtoConverter reviewDtoConverter;

    @GetMapping("/doctors/reviews")
    public List<ReviewOutputDto> listReviews() {
        return reviewService.listReviews().stream()
                .map(reviewDtoConverter::toOutputDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/doctors/reviews")
    public ResponseEntity<?> createDoctorRecordReview(
            @RequestBody ReviewInputDto reviewInputDto) throws URISyntaxException {

        Review review = reviewService.save(reviewDtoConverter.toModel(reviewInputDto));
        return ResponseEntity.created(new URI("/doctors/reviews/" + review.getId())).build();
    }

    @PutMapping("/doctors/reviews/{id}")
    public ResponseEntity<?> updateDoctorRecordReview(@PathVariable("id") Integer id,
                                                      @RequestBody ReviewInputDto reviewInputDto) {

        if (!reviewService.findById(id).isPresent()) {
            throw new ResourceNotFoundException(String.format("Review with id=%d not found", id));
        }

        Review review = reviewService.save(reviewDtoConverter.toModel(reviewInputDto, id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/doctors/reviews/{id}")
    public ResponseEntity<?> patchDoctorRecordReview(@PathVariable("id") Integer id,
                                                     @RequestBody ReviewInputDto reviewInputDto) {

        Optional<Review> maybeReview = reviewService.findById(id);
        if (!maybeReview.isPresent()) {
            throw new ResourceNotFoundException(String.format("Review with id=%d not found", id));
        }

        Review review = maybeReview.get();
        reviewDtoConverter.update(review, reviewInputDto);
        reviewService.save(review);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors/reviews/reports/averageRating")
    public ReviewReportAverageRatingDto generateAverageRatingReport() {
        return reviewService.getAverageRatings();
    }


}
