package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.dto.*;
import hillel.spring.doctor.exception.BadRequestException;
import hillel.spring.doctor.exception.InvalidScheduleException;
import hillel.spring.doctor.exception.NoSuchDoctorRecordException;
import hillel.spring.doctor.service.DoctorScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;

@RestController
@AllArgsConstructor
public class DoctorScheduleController {
    private final DoctorScheduleService doctorScheduleService;
    private final DoctorScheduleDtoConverter doctorScheduleDtoConverter;
    private final DoctorRecordDtoConverter doctorRecordDtoConverter;

    @GetMapping("/doctors/{id}/schedule/{date}")
    public DoctorScheduleDto getDoctorScheduleByDate(
            @PathVariable("id") Integer doctorId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<Integer, Integer> hourToPetId = doctorScheduleService.findDoctorRecordsByDay(doctorId, date).stream()
                .collect(
                        Collectors.groupingBy(DoctorRecord::getStartHour,
                                TreeMap::new, // hours to be sorted
                                summingInt(DoctorRecord::getPetId))); //only one record with specified hour must be

        return doctorScheduleDtoConverter.toDto(hourToPetId);
    }

    @GetMapping("/doctors/records/{id}")
    public DoctorRecordDto findDoctorRecord(@PathVariable("id") Integer doctorRecordId) {
        return doctorScheduleDtoConverter.toRecordDto(
                doctorScheduleService.findById(doctorRecordId)
                        .orElseThrow(() -> new NoSuchDoctorRecordException(doctorRecordId)));
    }

    @PostMapping("/doctors/{id}/schedule/{date}/{hour}")
    public ResponseEntity<?> createDoctorRecord(
            @RequestBody PetIdDto petIdDto,
            @PathVariable("id") Integer doctorId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("hour") Integer hour) {

        assertNotNull(petIdDto, "Doctor id not specified");
        assertNotNull(petIdDto, "Request body must contain petDto object");
        assertNotNull(petIdDto.getPetId(), "petDto must contain pet id");

        DoctorRecord record = doctorScheduleService.createDoctorRecord(doctorId, petIdDto.getPetId(), date, hour);

        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(doctorRecordDtoConverter.toDto(record));
    }

    @PostMapping("/doctors/{id}/schedule/move/{toDoctorId}")
    public ResponseEntity<?> moveDoctorRecords(
            @PathVariable("id") Integer doctorId,
            @PathVariable("toDoctorId") Integer toDoctorId,
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> dateFrom) {

        doctorScheduleService.moveDoctorRecords(doctorId, toDoctorId, dateFrom.orElse(LocalDateTime.now()));

        return ResponseEntity.ok().build();
    }

    private void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new BadRequestException(message);
        }
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidScheduleExceptionHandler(InvalidScheduleException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getLocalizedMessage());
    }

}
