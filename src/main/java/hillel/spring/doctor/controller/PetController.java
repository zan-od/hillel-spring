package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.dto.PetDtoConverter;
import hillel.spring.doctor.dto.PetInputDto;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.NoSuchPetException;
import hillel.spring.doctor.service.PetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@AllArgsConstructor
public class PetController {
    private final PetService petService;
    private final PetDtoConverter petDtoConverter;

    @PostMapping("/pets")
    public ResponseEntity<?> create(@RequestBody PetInputDto petDto) throws URISyntaxException {
        Pet pet = petService.create(petDtoConverter.toModel(petDto));

        return ResponseEntity.created(new URI("/pets/" + pet.getId())).build();
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<?> update(@RequestBody PetInputDto petDto,
                                    @PathVariable("id") Integer id) {

        assertNotNull(id, "Path variable {id} not specified");

        if (!petService.findById(id).isPresent()) {
            throw new NoSuchPetException(id);
        }

        Pet pet = petDtoConverter.toModel(petDto, id);
        petService.update(pet);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/pets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        petService.delete(id);
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new BadRequestException(message);
        }
    }
}
