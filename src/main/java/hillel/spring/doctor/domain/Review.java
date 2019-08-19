package hillel.spring.doctor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private Integer doctorRecordId;
    private LocalDateTime reviewDate;

    private Byte serviceRating;
    private Byte equipmentRating;
    private Byte qualificationRating;
    private Byte treatmentRating;
    private Byte totalRating;
    private String reviewComment;

    public Optional<Byte> getServiceRating() {
        return Optional.ofNullable(serviceRating);
    }

    public Optional<Byte> getEquipmentRating() {
        return Optional.ofNullable(equipmentRating);
    }

    public Optional<Byte> getQualificationRating() {
        return Optional.ofNullable(qualificationRating);
    }

    public Optional<Byte> getTreatmentRating() {
        return Optional.ofNullable(treatmentRating);
    }

    public Optional<Byte> getTotalRating() {
        return Optional.ofNullable(totalRating);
    }

    public Optional<String> getReviewComment() {
        return Optional.ofNullable(reviewComment);
    }
}
