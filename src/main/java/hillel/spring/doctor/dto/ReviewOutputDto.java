package hillel.spring.doctor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewOutputDto {
    private Integer id;
    private Integer doctorRecordId;
    private LocalDateTime reviewDate;

    private Integer serviceRating;
    private Integer equipmentRating;
    private Integer qualificationRating;
    private Integer treatmentRating;
    private Integer totalRating;
    private String reviewComment;
}
