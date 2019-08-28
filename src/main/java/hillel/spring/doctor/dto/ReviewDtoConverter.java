package hillel.spring.doctor.dto;

import hillel.spring.doctor.domain.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Optional;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewDtoConverter {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Review toModel(ReviewInputDto dto);

    @Mapping(target = "reviewDate", ignore = true)
    Review toModel(ReviewInputDto dto, Integer id, Integer version);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    void update(@MappingTarget Review review, ReviewInputDto dto);

    ReviewOutputDto toOutputDto(Review review);

    default <T> T unpack(Optional<T> maybe) {
        return maybe.orElse(null);
    }
}
