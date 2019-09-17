package hillel.spring.doctor.dto;

import lombok.Data;

@Data
public class DoctorEducationDto {
    private String diplomaNumber;
    private String universityName;
    private String specialization;
    private Integer yearGraduated;
}
