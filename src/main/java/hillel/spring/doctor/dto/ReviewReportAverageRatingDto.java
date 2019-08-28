package hillel.spring.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReviewReportAverageRatingDto {
    private Double averageServiceRating;
    private Double averageEquipmentRating;
    private Double averageQualificationRating;
    private Double averageTreatmentRating;
    private Double averageTotalRating;

    private List<ReviewCommentDto> reviewComments;

    public ReviewReportAverageRatingDto(Double averageServiceRating,
                                        Double averageEquipmentRating,
                                        Double averageQualificationRating,
                                        Double averageTreatmentRating,
                                        Double averageTotalRating) {

        this.averageServiceRating = averageServiceRating;
        this.averageEquipmentRating = averageEquipmentRating;
        this.averageQualificationRating = averageQualificationRating;
        this.averageTreatmentRating = averageTreatmentRating;
        this.averageTotalRating = averageTotalRating;
    }
}
