package hillel.spring.doctor.controller;

import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.service.DoctorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoctorControllerTest {

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private DoctorService doctorService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx).build();
    }

    @Test
    public void findById() throws Exception {
        when(doctorService.findById(1)).thenReturn(Optional.of(new Doctor(1, "Hide", "dentist")));

        this.mockMvc.perform(get("/doctors/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hide"))
                .andExpect(jsonPath("$.specialization").value("dentist"));

        verify(doctorService, times(1)).findById(1);
    }

    @Test
    public void findByIdNotFound() throws Exception {
        when(doctorService.findById(2)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/doctors/{id}", "3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(doctorService, times(1)).findById(3);
    }

    @Test
    public void findDoctorsByNameNotFound() throws Exception {
        when(doctorService.findByNameStartsWith("A")).thenReturn(List.of());

        this.mockMvc.perform(get("/doctors?name={name}", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(doctorService, times(1)).findByNameStartsWith("A");
    }

    @Test
    public void findDoctorsBySpecializationIgnoringName() throws Exception {
        when(doctorService.findBySpecialization("surgeon")).thenReturn(List.of(new Doctor(1, "Hide", "surgeon")));

        this.mockMvc.perform(get("/doctors?specialization={spec}&name={name}", "surgeon", "A")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Hide"))
                .andExpect(jsonPath("$[0].specialization").value("surgeon"));

        verify(doctorService, times(1)).findBySpecialization("surgeon");
        verify(doctorService, times(0)).findByNameStartsWith(anyString());
    }

    @Test
    public void createDoctorWithPredefinedId() throws Exception {
        this.mockMvc.perform(post("/doctors")
                .content("{\"id\": 1, \"name\": \"Hide\", \"specialization\": \"dentist\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(doctorService, times(0)).create(any());
    }

    @Test
    public void createDoctor() throws Exception {
        //doNothing().when(doctorService).create(any(Doctor.class));

        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"specialization\": \"dentist\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(doctorService, times(1)).create(new Doctor(null, "Hide", "dentist"));
    }

    @Test
    public void updateDoctor() throws Exception {
        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"id\": 1, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(doctorService, times(1)).update(new Doctor(1, "Dolittle", "surgeon"));
    }

    @Test
    public void updateDoctorIdMismatch() throws Exception {
        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"id\": 2, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(doctorService, times(0)).update(any());
    }

    @Test
    public void updateDoctorNotFound() throws Exception {
        Doctor doctor = new Doctor(2, "Dolittle", "surgeon");
        doThrow(new NoSuchDoctorException()).when(doctorService).update(doctor);

        this.mockMvc.perform(put("/doctors/{id}", "2")
                .content("{\"id\": 2, \"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(doctorService, times(1)).update(doctor);
    }

    @Test
    public void deleteDoctor() throws Exception {
        this.mockMvc.perform(delete("/doctors/{id}", "1"))
                .andExpect(status().isNoContent());

        verify(doctorService, times(1)).delete(1);
    }

    @Test
    public void deleteDoctorNotFound() throws Exception {
        doThrow(new NoSuchDoctorException()).when(doctorService).delete(2);

        this.mockMvc.perform(delete("/doctors/{id}", "2"))
                .andExpect(status().isNotFound());

        verify(doctorService, times(1)).delete(2);
    }

    @Test
    public void updateDoctorCheckIdField() throws Exception {
        this.mockMvc.perform(put("/doctors/{id}", "1")
                .content("{\"name\": \"Dolittle\", \"specialization\": \"surgeon\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}