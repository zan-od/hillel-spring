package hillel.spring.doctor.dto;

import hillel.spring.doctor.controller.ValidSpecialization;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class DoctorInputDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private Set<@NotEmpty @ValidSpecialization String> specializations;
}
