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
    private Byte equipmentRating;
    private Byte qualificationRating;
    private Byte treatmentRating;
    private Byte totalRating;
    private String reviewComment;
}
