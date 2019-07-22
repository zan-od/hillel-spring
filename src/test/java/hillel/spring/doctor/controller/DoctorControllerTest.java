package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.repository.DoctorRepository;
import hillel.spring.doctor.service.DoctorService;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {

    @Autowired
    public DoctorService doctorService;

    @Autowired
    public DoctorRepository doctorRepository;

    @Autowired
    public MockMvc mockMvc;

    @After
    public void clean() {
        doctorRepository.cleanRepository();
    }

    private void addDoctor(Integer id, String name, String specialization) {
        doctorRepository.create(new Doctor(id, name, specialization));
    }

    @Test
    public void findById() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
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
        addDoctor(1, "Hide", "dentist");
        addDoctor(2, "Abbott", "surgeon");
        addDoctor(3, "archibald", "therapist");

        this.mockMvc.perform(get("/doctors?name={name}", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Abbott"))
                .andExpect(jsonPath("$[0].specialization").value("surgeon"));
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
        addDoctor(1, "Hide", "dentist");
        addDoctor(2, "Abbott", "surgeon");
        addDoctor(3, "archibald", "therapist");
        addDoctor(4, "Abbey", "surgeon");

        this.mockMvc.perform(get("/doctors?specialization={spec}&name={name}", "surgeon", "Abb")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Abbott"))
                .andExpect(jsonPath("$[0].specialization").value("surgeon"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].name").value("Abbey"))
                .andExpect(jsonPath("$[1].specialization").value("surgeon"));
    }

    @Test
    public void createDoctorWithPredefinedId() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"id\": 1, \"name\": \"Hide\", \"specialization\": \"dentist\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(doctorRepository.list().size(), 0);
    }

    @Test
    public void createDoctor() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specialization\": \"dentist\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(doctorRepository.list().size(), 1);

        Doctor savedDoctor = doctorRepository.list().get(0);
        assertEquals(savedDoctor.getName(), "Hide");
        assertEquals(savedDoctor.getSpecialization(), "dentist");
    }

    @Test
    public void updateDoctor() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"id\": 1, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(doctorRepository.list().size(), 1);

        Doctor savedDoctor = doctorRepository.list().get(0);
        assertEquals(savedDoctor.getName(), "Dolittle");
        assertEquals(savedDoctor.getSpecialization(), "surgeon");
    }

    @Test
    public void updateDoctorIdMismatch() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"id\": 2, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(doctorRepository.list().size(), 1);

        Doctor savedDoctor = doctorRepository.list().get(0);
        assertEquals(savedDoctor.getName(), "Hide");
        assertEquals(savedDoctor.getSpecialization(), "dentist");
    }

    @Test
    public void updateDoctorNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", "2")
                .content("{\"id\": 2, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(doctorRepository.list().size(), 1);

        Doctor savedDoctor = doctorRepository.list().get(0);
        assertEquals(savedDoctor.getName(), "Hide");
        assertEquals(savedDoctor.getSpecialization(), "dentist");
    }

    @Test
    public void deleteDoctor() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(delete("/doctors/{id}", "1"))
                .andExpect(status().isNoContent());

        assertEquals(doctorRepository.list().size(), 0);
    }

    @Test
    public void deleteDoctorNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(delete("/doctors/{id}", "2"))
                .andExpect(status().isNotFound());

        assertEquals(doctorRepository.list().size(), 1);
    }

    @Test
    public void updateDoctorCheckIdField() throws Exception {
        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(doctorRepository.list().size(), 0);
    }
}