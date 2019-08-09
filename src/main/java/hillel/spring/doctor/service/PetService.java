package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.exception.NoSuchPetException;
import hillel.spring.doctor.repository.PetRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public Optional<Pet> findById(Integer id) {
        return petRepository.findById(id);
    }

    public Pet create(Pet pet) {
        return petRepository.save(pet);
    }

    public void update(Pet pet) {
        petRepository.save(pet);
    }

    public void delete(Integer id) {
        try {
            petRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchPetException(id);
        }
    }
}
