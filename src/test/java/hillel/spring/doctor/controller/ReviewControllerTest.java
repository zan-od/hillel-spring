package hillel.spring.doctor.controller;

import hillel.spring.TestRunner;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.domain.DoctorRecord;
import hillel.spring.doctor.domain.Pet;
import hillel.spring.doctor.domain.Review;
import hillel.spring.doctor.repository.DoctorRecordRepository;
import hillel.spring.doctor.repository.DoctorRepository;
import hillel.spring.doctor.repository.PetRepository;
import hillel.spring.doctor.repository.ReviewRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestRunner
public class ReviewControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ReviewRepository reviewRepository;
    @Autowired
    public DoctorRecordRepository doctorRecordRepository;
    @Autowired
    public DoctorRepository doctorRepository;
    @Autowired
    public PetRepository petRepository;

    @Before
    public void clean() {
        reviewRepository.deleteAll();
        doctorRecordRepository.deleteAll();
        doctorRepository.deleteAll();
        petRepository.deleteAll();
    }

    private Integer addDoctor(String name) {
        return doctorRepository.save(new Doctor(null, name, null, null)).getId();
    }

    private Integer addPet(String name) {
        return petRepository.save(new Pet(null, name, null, null, null)).getId();
    }

    private Integer addDoctorRecord(Integer doctorId, Integer petId, LocalDateTime startDate) {
        return doctorRecordRepository.save(new DoctorRecord(null, doctorId, petId, startDate)).getId();
    }

    private Integer addReview(Integer doctorRecordId, LocalDateTime date, Integer serviceRating, Integer equipmentRating,
                              Integer qualificationRating, Integer treatmentRating, Integer totalRating, String comment) {
        return reviewRepository.save(new Review(null, null, doctorRecordId, date, serviceRating, equipmentRating,
                qualificationRating, treatmentRating, totalRating, comment)).getId();
    }

    @Test
    public void createReview() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"serviceRating\": 1, " +
                "\"equipmentRating\": 2," +
                "\"qualificationRating\": 3," +
                "\"treatmentRating\": 4," +
                "\"reviewComment\": \"comment\"" +
                "}";

        this.mockMvc.perform(post("/doctors/reviews/")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertEquals(1, reviewRepository.findAll().size());

        Review savedReview = reviewRepository.findAll().get(0);

        assertEquals(doctorRecordId, savedReview.getDoctorRecordId());
        assertEquals(Optional.of(1), savedReview.getServiceRating());
        assertEquals(Optional.of(2), savedReview.getEquipmentRating());
        assertEquals(Optional.of(3), savedReview.getQualificationRating());
        assertEquals(Optional.of(4), savedReview.getTreatmentRating());
        assertEquals(Optional.empty(), savedReview.getTotalRating());
        assertEquals(Optional.of("comment"), savedReview.getReviewComment());
    }

    @Test
    public void createReviewTooEarly() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T12:00:00"));

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"serviceRating\": 1, " +
                "\"equipmentRating\": 2," +
                "\"qualificationRating\": 3," +
                "\"treatmentRating\": 4," +
                "\"reviewComment\": \"comment\"" +
                "}";

        this.mockMvc.perform(post("/doctors/reviews/")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, reviewRepository.findAll().size());
    }

    @Test
    public void createEmptyReview() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"serviceRating\": null, " +
                "}";

        this.mockMvc.perform(post("/doctors/reviews/")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, reviewRepository.findAll().size());
    }

    @Test
    public void createReviewWithInvalidRatingTooHigh() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"serviceRating\": 6, " +
                "}";

        this.mockMvc.perform(post("/doctors/reviews/")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, reviewRepository.findAll().size());
    }

    @Test
    public void createReviewWithInvalidRatingTooLow() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"serviceRating\": 0, " +
                "}";

        this.mockMvc.perform(post("/doctors/reviews/")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(0, reviewRepository.findAll().size());
    }

    @Test
    public void generateAverageRatingReport() throws Exception {
        Integer doctorRecordId = 1;
        LocalDateTime date = LocalDateTime.parse("2019-08-24T10:00:00");

        addReview(doctorRecordId, date, 1, 2, 3, 4, null, "comment1");
        addReview(doctorRecordId, date, 5, 4, 3, 2, null, "comment2");
        addReview(doctorRecordId, date, null, 1, 1, 1, null, "comment3");
        addReview(doctorRecordId, date, 3, 3, 3, 3, null, null);
        addReview(doctorRecordId, date, null, null, 5, 1, null, "comment5");

        assertEquals(5, reviewRepository.findAll().size());

        this.mockMvc.perform(get("/doctors/reviews/reports/averageRating")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageServiceRating").value(3.0))
                .andExpect(jsonPath("$.averageEquipmentRating").value(2.5))
                .andExpect(jsonPath("$.averageQualificationRating").value(3.0))
                .andExpect(jsonPath("$.averageTreatmentRating").value(2.2))
                .andExpect(jsonPath("$.averageTotalRating").isEmpty())
                .andExpect(jsonPath("$.reviewComments", hasSize(5)))
                .andExpect(jsonPath("$.reviewComments[0].comment").value("comment1"))
                .andExpect(jsonPath("$.reviewComments[1].comment").value("comment2"))
                .andExpect(jsonPath("$.reviewComments[2].comment").value("comment3"))
                .andExpect(jsonPath("$.reviewComments[3].comment").isEmpty())
                .andExpect(jsonPath("$.reviewComments[4].comment").value("comment5"));

    }

    @Test
    public void updateReview() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        Integer reviewId = addReview(doctorRecordId, LocalDateTime.parse("2019-08-24T11:00:00"), 1, 2, 3, 4, null, "comment1");

        String content = "{\"doctorRecordId\": \"" + doctorRecordId + "\", " +
                "\"reviewDate\": \"2019-08-24T11:00:05\", " +
                "\"serviceRating\": 5, " +
                "\"equipmentRating\": 4," +
                "\"qualificationRating\": 3," +
                "\"treatmentRating\": 2," +
                "\"totalRating\": 1," +
                "\"reviewComment\": \"comment\"" +
                "}";

        this.mockMvc.perform(put("/doctors/reviews/{id}", reviewId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, reviewRepository.findAll().size());

        Review review = reviewRepository.findAll().get(0);

        assertEquals(reviewId, review.getId());
        assertEquals(doctorRecordId, review.getDoctorRecordId());
        assertEquals(LocalDateTime.parse("2019-08-24T12:00:00"), review.getReviewDate()); //"current" time
        assertEquals(Optional.of(5), review.getServiceRating());
        assertEquals(Optional.of(4), review.getEquipmentRating());
        assertEquals(Optional.of(3), review.getQualificationRating());
        assertEquals(Optional.of(2), review.getTreatmentRating());
        assertEquals(Optional.of(1), review.getTotalRating());
        assertEquals(Optional.of("comment"), review.getReviewComment());
    }

    @Test
    public void patchReview() throws Exception {
        Integer doctorId = addDoctor("Hide");
        Integer petId = addPet("Tom");
        Integer doctorRecordId = addDoctorRecord(doctorId, petId, LocalDateTime.parse("2019-08-24T10:00:00"));

        Integer reviewId = addReview(doctorRecordId, LocalDateTime.parse("2019-08-24T11:00:00"), 1, 2, 3, 4, null, "comment1");

        String content = "{\"serviceRating\": 5, \"totalRating\": 1}";

        this.mockMvc.perform(patch("/doctors/reviews/{id}", reviewId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, reviewRepository.findAll().size());

        Review review = reviewRepository.findAll().get(0);

        assertEquals(reviewId, review.getId());
        assertEquals(doctorRecordId, review.getDoctorRecordId());
        assertEquals(LocalDateTime.parse("2019-08-24T12:00:00"), review.getReviewDate()); //"current" time
        assertEquals(Optional.of(5), review.getServiceRating());
        assertEquals(Optional.of(2), review.getEquipmentRating());
        assertEquals(Optional.of(3), review.getQualificationRating());
        assertEquals(Optional.of(4), review.getTreatmentRating());
        assertEquals(Optional.of(1), review.getTotalRating());
        assertEquals(Optional.of("comment1"), review.getReviewComment());
    }

    @Test
    public void findAllReviewPageable() throws Exception {
        Integer doctorRecordId = 1;

        addReview(doctorRecordId, LocalDateTime.parse("2019-08-24T10:00:00"), 1, 2, 3, 4, null, "comment1");
        addReview(doctorRecordId, LocalDateTime.parse("2019-08-25T10:00:01"), 5, 4, 3, 2, null, "comment2");
        addReview(doctorRecordId, LocalDateTime.parse("2019-08-26T10:01:01"), null, 1, 1, 1, null, "comment3");
        addReview(doctorRecordId, LocalDateTime.parse("2019-08-27T11:00:00"), 3, 3, 3, 3, null, null);
        addReview(doctorRecordId, LocalDateTime.parse("2019-08-28T12:30:00"), null, null, 5, 1, null, "comment5");

        assertEquals(5, reviewRepository.findAll().size());

        this.mockMvc.perform(get("/doctors/reviews?size=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].doctorRecordId").value(doctorRecordId))
                .andExpect(jsonPath("$.content[0].reviewDate").value("2019-08-24T10:00:00"))
                .andExpect(jsonPath("$.content[0].serviceRating").value(1))
                .andExpect(jsonPath("$.content[0].equipmentRating").value(2))
                .andExpect(jsonPath("$.content[0].qualificationRating").value(3))
                .andExpect(jsonPath("$.content[0].treatmentRating").value(4))
                .andExpect(jsonPath("$.content[0].totalRating").isEmpty())
                .andExpect(jsonPath("$.content[0].reviewComment").value("comment1"))
                .andExpect(jsonPath("$.content[1].doctorRecordId").value(doctorRecordId))
                .andExpect(jsonPath("$.content[1].reviewDate").value("2019-08-25T10:00:01"))
                .andExpect(jsonPath("$.content[1].serviceRating").value(5))
                .andExpect(jsonPath("$.content[1].equipmentRating").value(4))
                .andExpect(jsonPath("$.content[1].qualificationRating").value(3))
                .andExpect(jsonPath("$.content[1].treatmentRating").value(2))
                .andExpect(jsonPath("$.content[1].totalRating").isEmpty())
                .andExpect(jsonPath("$.content[1].reviewComment").value("comment2"))
        ;

        this.mockMvc.perform(get("/doctors/reviews?size=2&page=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].doctorRecordId").value(doctorRecordId))
                .andExpect(jsonPath("$.content[0].reviewDate").value("2019-08-26T10:01:01"))
                .andExpect(jsonPath("$.content[0].serviceRating").isEmpty())
                .andExpect(jsonPath("$.content[0].equipmentRating").value(1))
                .andExpect(jsonPath("$.content[0].qualificationRating").value(1))
                .andExpect(jsonPath("$.content[0].treatmentRating").value(1))
                .andExpect(jsonPath("$.content[0].totalRating").isEmpty())
                .andExpect(jsonPath("$.content[0].reviewComment").value("comment3"))
                .andExpect(jsonPath("$.content[1].doctorRecordId").value(doctorRecordId))
                .andExpect(jsonPath("$.content[1].reviewDate").value("2019-08-27T11:00:00"))
                .andExpect(jsonPath("$.content[1].serviceRating").value(3))
                .andExpect(jsonPath("$.content[1].equipmentRating").value(3))
                .andExpect(jsonPath("$.content[1].qualificationRating").value(3))
                .andExpect(jsonPath("$.content[1].treatmentRating").value(3))
                .andExpect(jsonPath("$.content[1].totalRating").isEmpty())
                .andExpect(jsonPath("$.content[1].reviewComment").isEmpty())
        ;

        this.mockMvc.perform(get("/doctors/reviews?size=2&page=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].doctorRecordId").value(doctorRecordId))
                .andExpect(jsonPath("$.content[0].reviewDate").value("2019-08-28T12:30:00"))
                .andExpect(jsonPath("$.content[0].serviceRating").isEmpty())
                .andExpect(jsonPath("$.content[0].equipmentRating").isEmpty())
                .andExpect(jsonPath("$.content[0].qualificationRating").value(5))
                .andExpect(jsonPath("$.content[0].treatmentRating").value(1))
                .andExpect(jsonPath("$.content[0].totalRating").isEmpty())
                .andExpect(jsonPath("$.content[0].reviewComment").value("comment5"))
        ;
    }
}