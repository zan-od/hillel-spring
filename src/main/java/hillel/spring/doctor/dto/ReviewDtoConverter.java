package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ReviewDtoConverter {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    Review toModel(ReviewInputDto dto);

    @Mapping(target = "reviewDate", ignore = true)
    Review toModel(ReviewInputDto dto, Integer id);

    default <T> T unpack(Optional<T> maybe) {
        return maybe.orElse(null);
    }
}
