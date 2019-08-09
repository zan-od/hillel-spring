package hillel.spring.doctor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static org.springframework.util.Assert.notNull;

/* This class stores data
   about all appointments of doctors
   There are no Doctor and Pet entity fields,
   just IDs. The reason is to prevent from EAGER loading
   of huge DoctorRecord entity lists.
   Cascade deleting are not supported, all record history must remain,
   even if related Doctor or Pet entity has been deleted
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class DoctorRecord {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer doctorId;
    private Integer petId;
    private LocalDateTime startDate;

    public Integer getStartHour() {
        notNull(startDate, "Start date must be not null");

        return startDate.getHour();
    }
}
