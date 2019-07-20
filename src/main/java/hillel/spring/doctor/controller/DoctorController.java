package hillel.spring.doctor.controller;

import hillel.spring.doctor.BadRequestException;
import hillel.spring.doctor.IdMismatchException;
import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/doctors/{id}")
    public Doctor findById(@PathVariable("id") Integer id) {
        return doctorService.findById(id).orElseThrow(NoSuchDoctorException::new);
    }

    @GetMapping("/doctors")
    public List<Doctor> findDoctors(
            @RequestParam Optional<String> specialization,
            @RequestParam Optional<String> name) {


        Optional<Predicate<Doctor>> maybeNameCriteria = name.map(DoctorController::filterByNameStartsWith);
        Optional<Predicate<Doctor>> maybeSpecializationCriteria = name.map(DoctorController::filterBySpecialization);

        Predicate<Doctor> criteria =
                Stream.of(maybeNameCriteria, maybeSpecializationCriteria)
                .flatMap(Optional::stream)
                .reduce(Predicate::and)
                .orElse(doctor -> true);

        return doctorService.findByCriteria(criteria);
    }

    public static Predicate<Doctor> filterByNameStartsWith(String name){
        return doctor -> doctor.getName().startsWith(name);
    }

    public static Predicate<Doctor> filterBySpecialization(String specialization){
        return doctor -> doctor.getSpecialization().equals(specialization);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //FORBIDDEN)
    public void noSuchDoctorHandler(NoSuchDoctorException ex){
        // this method handles all NoSuchDoctorException instances and overrides response statuses
    }

    @PostMapping("/doctors")
    public ResponseEntity<?> create(@RequestBody Doctor doctor) throws URISyntaxException {

        if (doctor.getId() != null) {
            return ResponseEntity.badRequest().body("Can't create a doctor with predefined id!");
        }

        doctorService.create(doctor);

        return ResponseEntity.created(new URI("/doctors/" + doctor.getId())).build();
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> update(@RequestBody Doctor doctor,
                                    @PathVariable("id") Integer id) {

        assertNotNull(id, "Path variable {id} not specified");
        assertNotNull(doctor.getId(), "Doctor's id not specified");

        if (!doctor.getId().equals(id)) {
            throw new IdMismatchException();
        }

        doctorService.update(doctor);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/doctors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        doctorService.delete(id);
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new BadRequestException(message);
        }
    }
}
