package hillel.spring.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInputDto {
    private Integer doctorRecordId;

    private Integer serviceRating;
    private Integer equipmentRating;
    private Integer qualificationRating;
    private Integer treatmentRating;
    private Integer totalRating;
    private String reviewComment;
}
