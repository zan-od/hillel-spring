package hillel.spring.doctor.controller;

import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.repository.PetRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PetControllerTest {

    @Autowired
    public PetRepository petRepository;

    @Autowired
    public MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        petRepository.deleteAll();
    }

    private Integer addPet(String name, String kind) {
        return petRepository.save(new Pet(null, name, kind)).getId();
    }

    @Test
    public void createPet() throws Exception {
        this.mockMvc.perform(post("/pets")
                .content("{\"name\": \"Tom\", \"kind\": \"cat\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, petRepository.findAll().size());

        Pet savedPet = petRepository.findAll().get(0);
        assertEquals("Tom", savedPet.getName());
        assertEquals("cat", savedPet.getKind());
    }

    @Test
    public void updatePet() throws Exception {
        Integer id = addPet("Tom", "cat");

        this.mockMvc.perform(put("/pets/{id}", id)
                .content("{\"name\": \"Jerry\", \"kind\": \"mouse\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, petRepository.findAll().size());

        Pet savedPet = petRepository.findAll().get(0);
        assertEquals("Jerry", savedPet.getName());
        assertEquals("mouse", savedPet.getKind());
    }

    @Test
    public void updatePetNotFound() throws Exception {
        Integer id = addPet("Tom", "cat");

        this.mockMvc.perform(put("/pets/{id}", id + 1)
                .content("{\"name\": \"Jerry\", \"kind\": \"mouse\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertEquals(1, petRepository.findAll().size());

        Pet savedPet = petRepository.findAll().get(0);
        assertEquals("Tom", savedPet.getName());
        assertEquals("cat", savedPet.getKind());
    }

    @Test
    public void deletePet() throws Exception {
        Integer id = addPet("Tom", "cat");

        this.mockMvc.perform(delete("/pets/{id}", id))
                .andExpect(status().isNoContent());

        assertEquals(0, petRepository.findAll().size());
    }

    @Test
    public void deletePetNotFound() throws Exception {
        Integer id = addPet("Tom", "cat");

        this.mockMvc.perform(delete("/pets/{id}", id + 1))
                .andExpect(status().isNotFound());

        assertEquals(1, petRepository.findAll().size());

        Pet savedPet = petRepository.findAll().get(0);
        assertEquals("Tom", savedPet.getName());
        assertEquals("cat", savedPet.getKind());
    }
}