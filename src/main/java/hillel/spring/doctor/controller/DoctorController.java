package hillel.spring.doctor.controller;

import hillel.spring.doctor.config.DoctorEducationServiceConfig;
import hillel.spring.doctor.config.DoctorSpecializationsConfig;
import hillel.spring.doctor.config.DoctorWorkingHoursConfig;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.dto.DoctorDtoConverter;
import hillel.spring.doctor.dto.DoctorEducationDto;
import hillel.spring.doctor.dto.DoctorInputDto;
import hillel.spring.doctor.dto.DoctorOutputDto;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.ResourceNotFoundException;
import hillel.spring.doctor.service.DoctorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorDtoConverter doctorDtoConverter;
    private final DoctorWorkingHoursConfig doctorWorkingHoursConfig;
    private final DoctorSpecializationsConfig doctorSpecializationsConfig;
    private final RestTemplate restTemplate;
    private final DoctorEducationServiceConfig doctorEducationServiceConfig;

    @GetMapping("/doctors/{id}")
    public DoctorOutputDto findById(@PathVariable("id") Integer id) {
        return doctorDtoConverter.toDto(doctorService.findById(id).orElseThrow(() -> new NoSuchDoctorException(id)));
    }

    @GetMapping("/doctors")
    public Page<DoctorOutputDto> findDoctors(
            @RequestParam Optional<String> specialization,
            @RequestParam Optional<String> name,
            @RequestParam Optional<List<String>> specializations,
            Pageable pageable) {

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

        return doctorService.findByCriteria(parameters, pageable)
                .map(doctor -> doctorDtoConverter.toDto(doctor));
    }

    @PostMapping("/doctors")
    public ResponseEntity<?> create(@RequestBody @Valid DoctorInputDto doctorDto) throws URISyntaxException {
        Optional<DoctorEducationDto> doctorEducationOutputDto = findDoctorEducation(doctorDto.getDiplomaNumber());
        if (doctorEducationOutputDto.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Information about diploma No %s not found", doctorDto.getDiplomaNumber()));
        }

        Doctor doctor = doctorService.create(doctorDtoConverter.toModel(doctorDto, doctorEducationOutputDto.get()));

        return ResponseEntity.created(new URI("/doctors/" + doctor.getId())).build();
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid DoctorInputDto doctorDto,
                                    @PathVariable("id") Integer id) {

        assertNotNull(id, "Path variable {id} not specified");

        if (!doctorService.findById(id).isPresent()) {
            throw new NoSuchDoctorException(id);
        }

        Optional<DoctorEducationDto> doctorEducationOutputDto = findDoctorEducation(doctorDto.getDiplomaNumber());
        if (doctorEducationOutputDto.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Information about diploma No %s not found", doctorDto.getDiplomaNumber()));
        }

        Doctor doctor = doctorDtoConverter.toModel(doctorDto, doctorEducationOutputDto.get(), id);
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

    @GetMapping("/doctors/education")
    public ResponseEntity<?> getDoctorEducation(@RequestParam String diplomaNumber) {
        return ResponseEntity.of(findDoctorEducation(diplomaNumber));
    }

    private Optional<DoctorEducationDto> findDoctorEducation(String diplomaNumber) {
        log.info("Calling doctor education service");
        log.debug("Start findDoctorEducation({})", diplomaNumber);

        DoctorEducationDto doctorEducationDto = null;
        try {
            doctorEducationDto = restTemplate.getForObject(
                    doctorEducationServiceConfig.getUrl() + "/education?diplomaNumber=" + diplomaNumber,
                    DoctorEducationDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Doctor education data not found!");
        }

        log.trace("Service response: {}", doctorEducationDto);
        log.debug("Finish findDoctorEducation({})", diplomaNumber);

        return Optional.ofNullable(doctorEducationDto);
    }
}
