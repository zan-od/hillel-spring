package hillel.spring.doctor.dto;

import lombok.Data;

@Data
public class ReviewOutputDto {
    private Integer id;
    private Integer doctorRecordId;

    private Integer serviceRating;
    private Integer equipmentRating;
    private Integer qualificationRating;
    private Integer treatmentRating;
    private Integer totalRating;
    private String reviewComment;
}
