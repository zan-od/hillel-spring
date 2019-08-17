package hillel.spring.doctor.domain;

import hillel.spring.doctor.dto.ListToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String kind;

    @Convert(converter = ListToStringConverter.class)
    private List<String> notes;

    @Nullable
    private String owner;

    @Nullable
    public Optional<String> getOwner() {
        return Optional.ofNullable(owner);
    }
}
