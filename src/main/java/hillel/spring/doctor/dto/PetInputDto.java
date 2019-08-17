package hillel.spring.doctor.dto;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class PetInputDto {
    private String name;
    private String kind;
    private List<String> notes;
    private String owner;

    public Optional<String> getOwner() {
        return Optional.ofNullable(owner);
    }
}
