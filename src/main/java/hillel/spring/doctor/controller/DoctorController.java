package hillel.spring.doctor.controller;

import hillel.spring.doctor.config.DoctorSpecializationsConfig;
import hillel.spring.doctor.config.DoctorWorkingHoursConfig;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.dto.DoctorDtoConverter;
import hillel.spring.doctor.dto.DoctorInputDto;
import hillel.spring.doctor.dto.DoctorOutputDto;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.UnknownSpecializationException;
import hillel.spring.doctor.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter;
    private final DoctorWorkingHoursConfig doctorWorkingHoursConfig;
    private final DoctorSpecializationsConfig doctorSpecializationsConfig;

    @GetMapping("/doctors/{id}")
    public DoctorOutputDto findById(@PathVariable("id") Integer id) {
        return doctorDtoConverter.toDto(doctorService.findById(id).orElseThrow(NoSuchDoctorException::new));
    }

    @GetMapping("/doctors")
    public List<DoctorOutputDto> findDoctors(
            @RequestParam Optional<String> specialization,
            @RequestParam Optional<String> name,
            @RequestParam Optional<List<String>> specializations) {

        Map<String, Object> parameters = new HashMap<>();
        if (specialization.isPresent()) {
            parameters.put("specialization", specialization.get());
        }
        if (name.isPresent()) {
            parameters.put("name", name.get());
        }
        if (specializations.isPresent()) {
            parameters.put("specializations", specializations.get());
        }

        return toDtoList(doctorService.findByCriteria(parameters));
    }

    private List<DoctorOutputDto> toDtoList(List<Doctor> doctors) {
        return doctors.stream()
                .map(doctor -> doctorDtoConverter.toDto(doctor))
                .collect(Collectors.toList());
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

        if (!doctorService.findById(id).isPresent()) {
            throw new NoSuchDoctorException();
        }

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

    @GetMapping("/doctors/specializations")
    public List<String> showSpecializations() {
        return doctorSpecializationsConfig.getSpecializations();
    }

    @ExceptionHandler
    public ResponseEntity<?> unknownSpecializationHandler(UnknownSpecializationException ex){
        return ResponseEntity
                .badRequest()
                .body(ex.getLocalizedMessage());
    }
}
