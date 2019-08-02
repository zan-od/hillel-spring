package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.repository.DoctorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DoctorControllerTest {

    @Autowired
    public DoctorRepository doctorRepository;

    @Autowired
    public MockMvc mockMvc;

    @Before
    public void clean() {
        //doctorRepository.deleteAll();
        //System.out.println("cleaned: " + doctorRepository.findAll().size());
    }

    private Integer addDoctor(Integer id, String name, String specialization) {
        return doctorRepository.save(new Doctor(id, name, specialization)).getId();
    }

    @Test
    public void findById() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Hide"))
                .andExpect(jsonPath("$.specialization").value("dentist"));
    }

    @Test
    public void findByIdNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors/{id}", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findDoctorsByName() throws Exception {
        Integer id1 = addDoctor(1, "Hide", "dentist");
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", "therapist");

        this.mockMvc.perform(get("/doctors?name={name}", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id2))
                .andExpect(jsonPath("$[0].name").value("Abbott"))
                .andExpect(jsonPath("$[0].specialization").value("surgeon"))
                .andExpect(jsonPath("$[1].id").value(id3))
                .andExpect(jsonPath("$[1].name").value("archibald"))
                .andExpect(jsonPath("$[1].specialization").value("therapist"));
    }

    @Test
    public void findDoctorsByNameNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors?name={name}", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findDoctorsByNameAndSpecialization() throws Exception {
        Integer id1 = addDoctor(1, "Hide", "dentist");
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", "therapist");
        Integer id4 = addDoctor(4, "Abbey", "surgeon");

        this.mockMvc.perform(get("/doctors?specialization={spec}&name={name}", "surgeon", "Abb")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id2))
                .andExpect(jsonPath("$[0].name").value("Abbott"))
                .andExpect(jsonPath("$[0].specialization").value("surgeon"))
                .andExpect(jsonPath("$[1].id").value(id4))
                .andExpect(jsonPath("$[1].name").value("Abbey"))
                .andExpect(jsonPath("$[1].specialization").value("surgeon"));
    }

    @Test
    public void findDoctorsByNameAndSpecializations() throws Exception {
        Integer id1 = addDoctor(1, "Abrams", "dentist");
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", "therapist");
        Integer id4 = addDoctor(4, "abbey", "surgeon");

        this.mockMvc.perform(get("/doctors?specializations={spec}&name={name}", "surgeon,dentist", "Ab")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(id1))
                .andExpect(jsonPath("$[0].name").value("Abrams"))
                .andExpect(jsonPath("$[0].specialization").value("dentist"))
                .andExpect(jsonPath("$[1].id").value(id2))
                .andExpect(jsonPath("$[1].name").value("Abbott"))
                .andExpect(jsonPath("$[1].specialization").value("surgeon"))
                .andExpect(jsonPath("$[2].id").value(id4))
                .andExpect(jsonPath("$[2].name").value("abbey"))
                .andExpect(jsonPath("$[2].specialization").value("surgeon"));
    }

    @Test
    public void createDoctor() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specialization\": \"dentist\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals("dentist", savedDoctor.getSpecialization());
    }

    @Test
    public void createDoctorSpecializationNotExist() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specialization\": \"dentist1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, doctorRepository.findAll().size());
    }

    @Test
    public void updateDoctor() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", id)
                .content("{\"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Dolittle", savedDoctor.getName());
        assertEquals("surgeon", savedDoctor.getSpecialization());
    }

    @Test
    public void updateDoctorNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", "2")
                .content("{\"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals("dentist", savedDoctor.getSpecialization());
    }

    @Test
    public void updateDoctorSpecializationNotExist() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", id)
                .content("{\"name\": \"Dolittle\", \"specialization\": \"surgeon1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals("dentist", savedDoctor.getSpecialization());
    }

    @Test
    public void deleteDoctor() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(delete("/doctors/{id}", id))
                .andExpect(status().isNoContent());

        assertEquals(0, doctorRepository.findAll().size());
    }

    @Test
    public void deleteDoctorNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(delete("/doctors/{id}", "2"))
                .andExpect(status().isNotFound());

        assertEquals(1, doctorRepository.findAll().size());
    }
}