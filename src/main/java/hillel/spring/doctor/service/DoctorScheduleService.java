package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.exception.InvalidScheduleException;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.NoSuchPetException;
import hillel.spring.doctor.repository.DoctorRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
