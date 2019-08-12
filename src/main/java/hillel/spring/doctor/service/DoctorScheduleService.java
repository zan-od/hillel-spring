package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.exception.InvalidScheduleException;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.NoSuchPetException;
import hillel.spring.doctor.repository.DoctorRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

@Service
@AllArgsConstructor
public class DoctorScheduleService {
    private static final Integer START_HOUR = 8;
    private static final Integer END_HOUR = 15;
    private DoctorRecordRepository doctorRecordRepository;
    private DoctorService doctorService;
    private PetService petService;

    public DoctorRecord createDoctorRecord(Integer doctorId, Integer petId, LocalDate date, Integer hour) {
        notNull(date, "Date must be not null");
        notNull(hour, "Hour must be not null");
        notNull(doctorId, "doctorId must be not null");
        notNull(petId, "petId must be not null");

        if (doctorService.findById(doctorId).isEmpty()) {
            throw new NoSuchDoctorException(doctorId);
        }
        if (petService.findById(petId).isEmpty()) {
            throw new NoSuchPetException(petId);
        }

        return addDoctorRecord(doctorId, petId, date, hour);
    }

    @Transactional
    private DoctorRecord addDoctorRecord(Integer doctorId, Integer petId, LocalDate date, Integer hour) {
        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.of(hour, 0));

        List<Integer> hours = internalGetAvailableHours(doctorId, date);
        if (!hours.contains(hour)) {
            throw new InvalidScheduleException(
                    String.format(
                            "Error creating doctor record: the time %s is already appointed at doctor's schedule (doctorId=%d).\n" +
                                    "Please select from available hours: %s"
                            , startDate.toString(), doctorId, hours.toString()));
        }

        DoctorRecord record = DoctorRecord.builder()
                .doctorId(doctorId)
                .petId(petId)
                .startDate(startDate)
                .build();

        return doctorRecordRepository.save(record);
    }

    public List<DoctorRecord> findDoctorRecordsByDay(Integer doctorId, LocalDate date) {
        if (doctorService.findById(doctorId).isEmpty()) {
            throw new NoSuchDoctorException(doctorId);
        }

        return internalFindDoctorRecordsByDay(doctorId, date);
    }

    private List<DoctorRecord> internalFindDoctorRecordsByDay(Integer doctorId, LocalDate date) {
        LocalDateTime beginOfDay = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        LocalDateTime beginOfNextDay = beginOfDay.plusDays(1);

        return doctorRecordRepository.findByDoctorIdAndStartDateBetween(doctorId, beginOfDay, beginOfNextDay);
    }

    public List<Integer> getAvailableHours(Integer doctorId, LocalDate date) {
        if (doctorService.findById(doctorId).isEmpty()) {
            throw new NoSuchDoctorException(doctorId);
        }

        return internalGetAvailableHours(doctorId, date);
    }

    private List<Integer> internalGetAvailableHours(Integer doctorId, LocalDate date) {
        List<DoctorRecord> records = internalFindDoctorRecordsByDay(doctorId, date);

        Set<Integer> scheduledHours = records.stream()
                .map(record -> record.getStartHour())
                .distinct()
                .collect(Collectors.toSet());

        return getDayScheduleHours().stream()
                .filter(hour -> !scheduledHours.contains(hour))
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Integer> getDayScheduleHours() {
        List<Integer> hours = new ArrayList<>();
        for (int hour = START_HOUR; hour <= END_HOUR; hour++) {
            hours.add(hour);
        }

        return hours;
    }

    public void moveDoctorRecords(Integer fromDoctorId, Integer toDoctorId, LocalDateTime fromTime) {
        notNull(fromDoctorId, "fromDoctorId must be not null");
        notNull(toDoctorId, "toDoctorId must be not null");

        if (doctorService.findById(fromDoctorId).isEmpty()) {
            throw new NoSuchDoctorException(fromDoctorId);
        }
        if (doctorService.findById(toDoctorId).isEmpty()) {
            throw new NoSuchDoctorException(fromDoctorId);
        }

        internalMoveDoctorRecords(fromDoctorId, toDoctorId, fromTime);
    }

    @Transactional
    private void internalMoveDoctorRecords(Integer fromDoctorId, Integer toDoctorId, LocalDateTime fromTime) {
        List<DoctorRecord> recordsToMove = doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(fromDoctorId, fromTime);
        List<DoctorRecord> existedRecords = doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(toDoctorId, fromTime);

        Set<LocalDateTime> assignedDates = existedRecords.stream()
                .map(record -> record.getStartDate())
                .collect(Collectors.toSet());

        List<LocalDateTime> conflictingDates = recordsToMove.stream()
                .map(record -> record.getStartDate())
                .filter(date -> assignedDates.contains(date))
                .collect(Collectors.toList());

        if (!conflictingDates.isEmpty()) {
            throw new InvalidScheduleException(
                    String.format(
                            "Error moving records from doctor #%d to #%d: there is already appointed dates:\n" +
                                    "%s"
                            , fromDoctorId, toDoctorId, conflictingDates.toString()));
        }

        recordsToMove
                .forEach(record -> {
                    record.setDoctorId(toDoctorId);
                    doctorRecordRepository.save(record);
                });
    }

}
