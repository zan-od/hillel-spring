package hillel.spring.doctor.dto;

import lombok.Data;

import java.util.Set;

@Data
public class DoctorInputDto {
    private String name;
    private Set<String> specializations;
}
