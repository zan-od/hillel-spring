package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.DoctorRecord;
import org.mapstruct.Mapper;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface DoctorScheduleDtoConverter {
    DoctorScheduleDto toDto(Map<Integer, Integer> hourToPetId);

    DoctorRecordDto toRecordDto(DoctorRecord model);
}
