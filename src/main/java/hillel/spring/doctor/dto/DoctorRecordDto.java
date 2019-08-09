package hillel.spring.doctor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorRecordDto {
    private Integer doctorId;
    private Integer petId;
    private LocalDateTime startDate;
}
