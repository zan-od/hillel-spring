package hillel.spring.doctor.controller;

import hillel.spring.doctor.BadRequestException;
import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.dto.DoctorDtoConverter;
import hillel.spring.doctor.dto.DoctorInputDto;
import hillel.spring.doctor.dto.DoctorOutputDto;
import hillel.spring.doctor.service.DoctorService;
import hillel.spring.doctor.service.DoctorWorkingHoursConfig;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter;
    private final DoctorWorkingHoursConfig doctorWorkingHoursConfig;

    @GetMapping("/doctors/{id}")
    public DoctorOutputDto findById(@PathVariable("id") Integer id) {
        return doctorDtoConverter.toDto(doctorService.findById(id).orElseThrow(NoSuchDoctorException::new));
    }

    @GetMapping("/doctors")
    public List<DoctorOutputDto> findDoctors(
            @RequestParam Optional<String> specialization,
            @RequestParam Optional<String> name) {


        Optional<Predicate<Doctor>> maybeNameCriteria = name.map(this::filterByNameStartsWith);
        Optional<Predicate<Doctor>> maybeSpecializationCriteria = specialization.map(this::filterBySpecialization);

        Predicate<Doctor> criteria =
                Stream.of(maybeNameCriteria, maybeSpecializationCriteria)
                .flatMap(Optional::stream)
                .reduce(Predicate::and)
                .orElse(doctor -> true);

        return doctorService.findByCriteria(criteria).stream()
                .map(doctorDtoConverter::toDto)
                .collect(Collectors.toList());
    }

    private Predicate<Doctor> filterByNameStartsWith(String name) {
        return doctor -> doctor.getName().startsWith(name);
    }

    private Predicate<Doctor> filterBySpecialization(String specialization) {
        return doctor -> doctor.getSpecialization().equals(specialization);
    }

    @PostMapping("/doctors")
    public ResponseEntity<?> create(@RequestBody DoctorInputDto doctorDto) throws URISyntaxException {
        Doctor doctor = doctorService.create(doctorDtoConverter.toModel(doctorDto));

        return ResponseEntity.created(new URI("/doctors/" + doctor.getId())).build();
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> update(@RequestBody DoctorInputDto doctorDto,
                                    @PathVariable("id") Integer id) {

        assertNotNull(id, "Path variable {id} not specified");

        Doctor doctor = doctorDtoConverter.toModel(doctorDto, id);
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

    @GetMapping("/doctors/working-hours")
    public String showWorkingHours() {
        return "Working hours: " + doctorWorkingHoursConfig.getStartTime() + "-" + doctorWorkingHoursConfig.getEndTime();
    }
}
