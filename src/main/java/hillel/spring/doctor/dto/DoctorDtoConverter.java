package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DoctorDtoConverter {

    @Mapping(target = "id", ignore = true)
    Doctor toModel(DoctorInputDto dto);

}
