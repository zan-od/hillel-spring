package hillel.spring.doctor.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DoctorEducation {
    private String diplomaNumber;
    private String universityName;
    private String specialization;
    private Integer yearGraduated;
}
