package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.DoctorRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorRecordDtoConverter {
    DoctorRecordDto toDto(DoctorRecord record);
}
