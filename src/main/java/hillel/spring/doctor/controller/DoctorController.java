package hillel.spring.doctor.controller;

import hillel.spring.doctor.BadRequestException;
import hillel.spring.doctor.IdMismatchException;
import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
            @RequestParam(name = "specialization", required = false) String specialization,
            @RequestParam(name = "name", required = false) String name) {

        if (specialization != null) {
            return doctorService.findBySpecialization(specialization);
        } else if (name != null) {
            return doctorService.findByNameStartsWith(name);
        } else {
            return doctorService.list();
        }
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
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        doctorService.delete(id);

        return ResponseEntity.noContent().build();
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new BadRequestException(message);
        }
    }
}
