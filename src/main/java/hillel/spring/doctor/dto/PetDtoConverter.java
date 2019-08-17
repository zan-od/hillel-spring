package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PetDtoConverter {
    @Mapping(target = "id", ignore = true)
    Pet toModel(PetInputDto dto);

    Pet toModel(PetInputDto dto, Integer id);

    PetOutputDto toDto(Pet pet);

    default <T> T unpack(Optional<T> maybe){
        return maybe.orElse(null);
    }
}
