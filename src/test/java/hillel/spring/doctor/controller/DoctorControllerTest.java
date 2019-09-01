package hillel.spring.doctor.controller;

import hillel.spring.TestRunner;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.repository.DoctorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestRunner
public class DoctorControllerTest {

    @Autowired
    public DoctorRepository doctorRepository;

    @Autowired
    public MockMvc mockMvc;

    @Before
    public void clean() {
        doctorRepository.deleteAll();
        //System.out.println("cleaned: " + doctorRepository.findAll().size());
    }

    private Integer addDoctor(Integer id, String name, Set<String> specializations) {
        return doctorRepository.save(new Doctor(id, name, specializations)).getId();
    }

    private Integer addDoctor(Integer id, String name, String specialization) {
        return doctorRepository.save(new Doctor(id, name, Set.of(specialization))).getId();
    }

    private Integer addDoctor(Integer id, String name) {
        return doctorRepository.save(new Doctor(id, name, null)).getId();
    }

    @Test
    public void findById() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Hide"))
                .andExpect(jsonPath("$.specializations[0]").value("dentist"));
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
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(id2))
                .andExpect(jsonPath("$.content[0].name").value("Abbott"))
                .andExpect(jsonPath("$.content[0].specializations[0]").value("surgeon"))
                .andExpect(jsonPath("$.content[1].id").value(id3))
                .andExpect(jsonPath("$.content[1].name").value("archibald"))
                .andExpect(jsonPath("$.content[1].specializations[0]").value("therapist"));
    }

    @Test
    public void findDoctorsByNameNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(get("/doctors?name={name}", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    public void findDoctorsByNameAndSpecialization() throws Exception {
        Integer id1 = addDoctor(1, "Hide", "dentist");
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", "therapist");
        Integer id4 = addDoctor(4, "Abbey", "surgeon");
        Integer id5 = addDoctor(5, "Abbey1");

        this.mockMvc.perform(get("/doctors?specialization={spec}&name={name}", "surgeon", "Abb")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(id2))
                .andExpect(jsonPath("$.content[0].name").value("Abbott"))
                .andExpect(jsonPath("$.content[0].specializations[0]").value("surgeon"))
                .andExpect(jsonPath("$.content[1].id").value(id4))
                .andExpect(jsonPath("$.content[1].name").value("Abbey"))
                .andExpect(jsonPath("$.content[1].specializations[0]").value("surgeon"));
    }

    @Test
    public void findDoctorsByNameAndSpecializations() throws Exception {
        Integer id1 = addDoctor(1, "Abrams", Set.of("oculist", "dentist"));
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", Set.of("therapist", "oculist"));
        Integer id4 = addDoctor(4, "abbey", "surgeon");
        Integer id5 = addDoctor(5, "Abbey1");

        this.mockMvc.perform(get("/doctors?specializations={spec}&name={name}", "surgeon,dentist", "Ab")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id").value(id1))
                .andExpect(jsonPath("$.content[0].name").value("Abrams"))
                .andExpect(jsonPath("$.content[0].specializations", hasSize(2)))
                .andExpect(jsonPath("$.content[1].id").value(id2))
                .andExpect(jsonPath("$.content[1].name").value("Abbott"))
                .andExpect(jsonPath("$.content[1].specializations[0]").value("surgeon"))
                .andExpect(jsonPath("$.content[2].id").value(id4))
                .andExpect(jsonPath("$.content[2].name").value("abbey"))
                .andExpect(jsonPath("$.content[2].specializations[0]").value("surgeon"));
    }

    @Test
    public void createDoctor() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specializations\": [\"dentist\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals(1, savedDoctor.getSpecializations().size());
        assertThat(savedDoctor.getSpecializations(), contains("dentist"));
    }

    @Test
    public void createDoctorWithoutSpecializations() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertNotNull(savedDoctor.getSpecializations());
        assertEquals(0, savedDoctor.getSpecializations().size());
    }

    @Test
    public void createDoctorSpecializationNotExist() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specializations\": [\"dentist1\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, doctorRepository.findAll().size());
    }

    @Test
    public void updateDoctor() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", id)
                .content("{\"name\": \"Dolittle\", \"specializations\": [\"surgeon\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Dolittle", savedDoctor.getName());
        assertEquals(1, savedDoctor.getSpecializations().size());
        assertThat(savedDoctor.getSpecializations(), contains("surgeon"));
    }

    @Test
    public void updateDoctorNotFound() throws Exception {
        addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", "2")
                .content("{\"name\": \"Dolittle\", \"specializations\": [\"surgeon\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals(1, savedDoctor.getSpecializations().size());
        assertThat(savedDoctor.getSpecializations(), contains("dentist"));
    }

    @Test
    public void updateDoctorSpecializationNotExist() throws Exception {
        Integer id = addDoctor(1, "Hide", "dentist");

        this.mockMvc.perform(put("/doctors/{id}", id)
                .content("{\"name\": \"Dolittle\", \"specializations\": [\"surgeon1\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals(1, savedDoctor.getSpecializations().size());
        assertThat(savedDoctor.getSpecializations(), contains("dentist"));
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

    @Test
    public void findDoctorsByNameAndSpecializationsPageable() throws Exception {
        Integer id1 = addDoctor(1, "Abrams", Set.of("oculist", "dentist"));
        Integer id2 = addDoctor(2, "Abbott", "surgeon");
        Integer id3 = addDoctor(3, "archibald", Set.of("therapist", "oculist"));
        Integer id4 = addDoctor(4, "abbey", "surgeon");
        Integer id5 = addDoctor(5, "Abbey1");

        this.mockMvc.perform(get("/doctors?specializations={spec}&name={name}&size=1", "surgeon,dentist", "Ab")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(id1))
                .andExpect(jsonPath("$.content[0].name").value("Abrams"))
                .andExpect(jsonPath("$.content[0].specializations", hasSize(2)));

        this.mockMvc.perform(get("/doctors?specializations={spec}&name={name}&size=1&page=1", "surgeon,dentist", "Ab")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(id2))
                .andExpect(jsonPath("$.content[0].name").value("Abbott"))
                .andExpect(jsonPath("$.content[0].specializations[0]").value("surgeon"));

        this.mockMvc.perform(get("/doctors?specializations={spec}&name={name}&size=1&page=2", "surgeon,dentist", "Ab")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(id4))
                .andExpect(jsonPath("$.content[0].name").value("abbey"))
                .andExpect(jsonPath("$.content[0].specializations[0]").value("surgeon"));
    }
}