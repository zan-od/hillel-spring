package hillel.spring.doctor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private Integer doctorRecordId;
    private LocalDateTime reviewDate;

    @Column(columnDefinition = "int2")
    private Integer serviceRating;
    private Integer equipmentRating;
    private Integer qualificationRating;
    private Integer treatmentRating;
    private Integer totalRating;
    private String reviewComment;

    public Optional<Integer> getServiceRating() {
        return Optional.ofNullable(serviceRating);
    }

    public Optional<Integer> getEquipmentRating() {
        return Optional.ofNullable(equipmentRating);
    }

    public Optional<Integer> getQualificationRating() {
        return Optional.ofNullable(qualificationRating);
    }

    public Optional<Integer> getTreatmentRating() {
        return Optional.ofNullable(treatmentRating);
    }

    public Optional<Integer> getTotalRating() {
        return Optional.ofNullable(totalRating);
    }

    public Optional<String> getReviewComment() {
        return Optional.ofNullable(reviewComment);
    }
}
