package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.InvalidScheduleException;
import hillel.spring.doctor.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorScheduleService doctorScheduleService;

    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    public Review create(Review review) {
        notNull(review.getDoctorRecordId(), "Doctor record id must be not null");

        review.setReviewDate(LocalDateTime.now());

        //TODO add ratings validation: range 1-5 and at least one rating specified

        Optional<DoctorRecord> maybeDoctorRecord = doctorScheduleService.findById(review.getDoctorRecordId());
        if (maybeDoctorRecord.isEmpty()) {
            throw new InvalidScheduleException(String.format("Doctor record with %d not found", review.getDoctorRecordId()));
        }

        DoctorRecord doctorRecord = maybeDoctorRecord.get();
        if (review.getReviewDate().compareTo(doctorRecord.getStartDate()) <= 0) {
            throw new BadRequestException(String.format(
                    "Review date %s can not be earlier than doctor record date %s"
                    , review.getReviewDate(), doctorRecord.getStartDate()));
        }

        return reviewRepository.save(review);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

}
