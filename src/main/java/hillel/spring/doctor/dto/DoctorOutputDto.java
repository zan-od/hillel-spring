package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.DoctorEducation;
import lombok.Data;

import java.util.Set;

@Data
public class DoctorOutputDto {
    private Integer id;
    private String name;
    private Set<String> specializations;
    private DoctorEducation education;
}
