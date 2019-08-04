package hillel.spring.doctor.dto;

import org.mapstruct.Mapper;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface DoctorScheduleDtoConverter {
    DoctorScheduleDto toDto(Map<Integer, Integer> hourToPetId);
}
