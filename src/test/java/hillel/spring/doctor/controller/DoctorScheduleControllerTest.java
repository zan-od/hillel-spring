package hillel.spring.doctor.controller;

import hillel.spring.TestRunner;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.repository.DoctorRecordRepository;
import hillel.spring.doctor.repository.DoctorRepository;
import hillel.spring.doctor.repository.PetRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestRunner
public class DoctorScheduleControllerTest {

    @Autowired
    public DoctorRecordRepository doctorRecordRepository;
    @Autowired
    public DoctorRepository doctorRepository;
    @Autowired
    public PetRepository petRepository;

    @Autowired
    public MockMvc mockMvc;

    @Before
    public void clean() {
        doctorRecordRepository.deleteAll();
        doctorRepository.deleteAll();
        petRepository.deleteAll();
    }

    private Integer addDoctor(String name) {
        return doctorRepository.save(new Doctor(null, name, null)).getId();
    }

    private Integer addPet(String name) {
        return petRepository.save(new Pet(null, name, null)).getId();
    }

    @Test
    public void getDoctorScheduleByDateNoRecords() throws Exception {
        Integer id = addDoctor("Hide");

        this.mockMvc.perform(get("/doctors/{id}/schedule/{date}", id, "2019-08-04")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourToPetId").value(""));
    }

    @Test
    public void getDoctorScheduleByDateDoctorNotFound() throws Exception {
        this.mockMvc.perform(get("/doctors/{id}/schedule/{date}", 1, "2019-08-04")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getDoctorScheduleByDate() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId1 = addPet("Tom");
        Integer petId2 = addPet("Jerry");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 10)
                .content("{\"petId\": " + petId1 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 12)
                .content("{\"petId\": " + petId2 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/doctors/{id}/schedule/{date}", doctorId, "2019-08-04")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourToPetId.10").value(petId1))
                .andExpect(jsonPath("$.hourToPetId.12").value(petId2));

        assertEquals(2, doctorRecordRepository.findAll().size());
    }

    @Test
    public void createDoctorRecordDoctorNotFound() throws Exception {
        Integer petId = addPet("Tom");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", 1, "2019-08-04", 10)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(0, doctorRecordRepository.findAll().size());
    }

    @Test
    public void createDoctorRecordPetNotFound() throws Exception {
        Integer doctorId = addDoctor("Hide");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 10)
                .content("{\"petId\": 1}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(0, doctorRecordRepository.findAll().size());
    }

    @Test
    public void createDoctorRecordHourIsInvalid() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 5)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, doctorRecordRepository.findAll().size());
    }

    @Test
    public void createDoctorRecord() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 13)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, doctorRecordRepository.findAll().size());

        DoctorRecord record = doctorRecordRepository.findAll().get(0);
        assertEquals(doctorId, record.getDoctorId());
        assertEquals(petId, record.getPetId());
        assertEquals(LocalDateTime.parse("2019-08-04T13:00:00"), record.getStartDate());
    }

    @Test
    public void createDoctorRecordHourIsAppointed() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId1 = addPet("Tom");
        Integer petId2 = addPet("Jerry");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 10)
                .content("{\"petId\": " + petId1 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId, "2019-08-04", 10)
                .content("{\"petId\": " + petId2 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(1, doctorRecordRepository.findAll().size());

        DoctorRecord record = doctorRecordRepository.findAll().get(0);
        assertEquals(doctorId, record.getDoctorId());
        assertEquals(petId1, record.getPetId());
        assertEquals(LocalDateTime.parse("2019-08-04T10:00:00"), record.getStartDate());
    }

    @Test
    public void createDoctorRecordTwoDoctors() throws Exception {
        Integer doctorId1 = addDoctor("Hide");
        Integer doctorId2 = addDoctor("Abbott");
        Integer petId1 = addPet("Tom");
        Integer petId2 = addPet("Jerry");
        Integer petId3 = addPet("Pluto");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 10)
                .content("{\"petId\": " + petId1 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 12)
                .content("{\"petId\": " + petId2 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 10)
                .content("{\"petId\": " + petId3 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 14)
                .content("{\"petId\": " + petId1 + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/doctors/{id}/schedule/{date}", doctorId1, "2019-08-04")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourToPetId.10").value(petId1))
                .andExpect(jsonPath("$.hourToPetId.12").value(petId2))
                .andExpect(jsonPath("$.hourToPetId.14").doesNotExist());

        this.mockMvc.perform(get("/doctors/{id}/schedule/{date}", doctorId2, "2019-08-04")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hourToPetId.10").value(petId3))
                .andExpect(jsonPath("$.hourToPetId.14").value(petId1))
                .andExpect(jsonPath("$.hourToPetId.12").doesNotExist());

        assertEquals(4, doctorRecordRepository.findAll().size());
    }

    @Test
    public void moveDoctorRecords() throws Exception {
        //given
        Integer doctorId1 = addDoctor("Hide");
        Integer doctorId2 = addDoctor("Abbott");
        Integer petId = addPet("Tom");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 10)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 12)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 15)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 14)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // when
        LocalDateTime startDate = LocalDateTime.parse("2019-08-04T09:00:00");
        this.mockMvc.perform(post("/doctors/{id}/schedule/move/{id2}", doctorId1, doctorId2)
                .param("dateFrom", "2019-08-04T09:00:00"))
                .andExpect(status().isOk());

        // then
        assertEquals(0, doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(doctorId1, startDate).size());
        assertEquals(4, doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(doctorId2, startDate).size());
    }

    @Test
    public void moveDoctorRecordsAlreadyAssignedDate() throws Exception {
        //given
        Integer doctorId1 = addDoctor("Hide");
        Integer doctorId2 = addDoctor("Abbott");
        Integer petId = addPet("Tom");

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 10)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId1, "2019-08-04", 12)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 15)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(post("/doctors/{id}/schedule/{date}/{hour}", doctorId2, "2019-08-04", 12)
                .content("{\"petId\": " + petId + "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // when
        LocalDateTime startDate = LocalDateTime.parse("2019-08-04T09:00:00");
        this.mockMvc.perform(post("/doctors/{id}/schedule/move/{id2}", doctorId1, doctorId2)
                .param("dateFrom", "2019-08-04T09:00:00"))
                .andExpect(status().isBadRequest());

        // then
        assertEquals(2, doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(doctorId1, startDate).size());
        assertEquals(2, doctorRecordRepository.findByDoctorIdAndStartDateGreaterThanEqual(doctorId2, startDate).size());
    }
}