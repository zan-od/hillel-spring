package hillel.spring.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewCommentDto {
    private LocalDateTime date;
    private String comment;
}
