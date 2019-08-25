package hillel.spring.doctor.repository;

import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.dto.ReviewCommentDto;
import hillel.spring.doctor.dto.ReviewReportAverageRatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT new hillel.spring.doctor.dto.ReviewReportAverageRatingDto(" +
            "AVG(r.serviceRating) AS averageServiceRating, " +
            "AVG(r.equipmentRating) AS averageEquipmentRating, " +
            "AVG(r.qualificationRating) AS averageQualificationRating, " +
            "AVG(r.treatmentRating) AS averageTreatmentRating, " +
            "AVG(r.totalRating) AS averageTotalRating) FROM Review AS r")
    ReviewReportAverageRatingDto getRatingsAverages();

    @Query("SELECT new hillel.spring.doctor.dto.ReviewCommentDto(" +
            "r.reviewDate AS date, " +
            "r.reviewComment AS comment) FROM Review AS r")
    List<ReviewCommentDto> getReviewComments();
}
