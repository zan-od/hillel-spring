package hillel.spring.doctor.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DoctorScheduleDto {
    private Map<Integer, Integer> hourToPetId;
}
