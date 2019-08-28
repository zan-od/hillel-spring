package hillel.spring.doctor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoctorRecordOutputDto {
    private Integer id;
    private Integer doctorId;
    private Integer petId;
    private LocalDateTime startDate;
}
