package hillel.spring.doctor.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import hillel.spring.TestRunner;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.repository.DoctorRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestRunner
public class DoctorEducationServiceTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public DoctorRepository doctorRepository;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void clean() {
        doctorRepository.deleteAll();
    }

    @Test
    public void createDoctorGetEducation() throws Exception {
        // given
        String jsonEducation =
                "{\"diplomaNumber\": \"1\"," +
                        "\"universityName\": \"Oxford\"," +
                        "\"specialization\": \"dentist\"," +
                        "\"yearGraduated\": 2000" +
                        "}";

        // when
        wireMockRule.stubFor(get("/education?diplomaNumber=1")
                .willReturn(okJson(jsonEducation)));

        this.mockMvc.perform(post("/doctors")
                .content("{\"name\": \"Hide\", \"diplomaNumber\": \"1\", \"specializations\": [\"dentist\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // then

        //verify(getRequestedFor(urlPathEqualTo("/education?diplomaNumber=1")));

        assertEquals(1, doctorRepository.findAll().size());

        Doctor savedDoctor = doctorRepository.findAll().get(0);
        assertEquals("Hide", savedDoctor.getName());
        assertEquals(1, savedDoctor.getSpecializations().size());
        assertThat(savedDoctor.getSpecializations(), contains("dentist"));
        assertThat(savedDoctor.getEducation(), notNullValue());
        assertEquals("1", savedDoctor.getEducation().getDiplomaNumber());
        assertEquals("Oxford", savedDoctor.getEducation().getUniversityName());
        assertEquals("dentist", savedDoctor.getEducation().getSpecialization());
        assertEquals(Long.valueOf(2000), Long.valueOf(savedDoctor.getEducation().getYearGraduated()));
    }
}
