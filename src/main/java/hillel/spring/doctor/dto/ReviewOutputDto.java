package hillel.spring.doctor.dto;

import lombok.Data;

@Data
public class ReviewOutputDto {
    private Integer id;
    private Integer doctorRecordId;

    private Byte serviceRating;
    private Byte equipmentRating;
    private Byte qualificationRating;
    private Byte treatmentRating;
    private Byte totalRating;
    private String reviewComment;
}
