package hillel.spring.doctor.controller;

import hillel.spring.TestRunner;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.exception.InvalidScheduleException;
import hillel.spring.doctor.repository.DoctorRecordRepository;
import hillel.spring.doctor.repository.DoctorRepository;
import hillel.spring.doctor.repository.PetRepository;
import hillel.spring.doctor.service.DoctorScheduleService;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@TestRunner
public class DoctorScheduleServiceTest extends AbstractTestNGSpringContextTests {

    public DoctorScheduleService doctorScheduleService;
    public DoctorRepository doctorRepository;
    public PetRepository petRepository;

    public DoctorRecordRepository doctorRecordRepository;

    private Integer doctorId;
    private Integer petId;

    private Integer addDoctor(String name) {
        return doctorRepository.save(new Doctor(null, name, null)).getId();
    }

    private Integer addPet(String name) {
        return petRepository.save(new Pet(null, name, null, null, null)).getId();
    }

    @BeforeClass
    public void setUp() {
        doctorId = addDoctor("Doctor");
        petId = addPet("Pet");
    }

    @Test(threadPoolSize = 2, invocationCount = 4)
    public void addDoctorRecordsInTransaction() throws Exception {
        //given
        LocalDate date = LocalDate.parse("2019-08-04");
        Integer hour = 12;
        LocalDateTime startDate = LocalDateTime.of(date, LocalTime.of(hour, 0));
        LocalDateTime endDate = LocalDateTime.of(date, LocalTime.of(hour, 59));

        //when
        try {
            doctorScheduleService.createDoctorRecord(doctorId, petId, date, hour);
        } catch (InvalidScheduleException e) {
            System.out.println("LOG: InvalidScheduleException!");
        }

        //then
        assertEquals(1, doctorRecordRepository.findByDoctorIdAndStartDateBetween(doctorId, startDate, endDate).size());
    }

}
