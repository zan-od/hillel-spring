package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorDtoConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "doctorEducationDto", target = "education")
    Doctor toModel(DoctorInputDto dto, DoctorEducationDto doctorEducationDto);

    @Mapping(source = "doctorEducationDto", target = "education")
    Doctor toModel(DoctorInputDto dto, DoctorEducationDto doctorEducationDto, Integer id);

    DoctorOutputDto toDto(Doctor doctor);
}
