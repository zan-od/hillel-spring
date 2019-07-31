package hillel.spring.doctor.service;

import hillel.spring.doctor.config.DoctorSpecializationsConfig;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.UnknownSpecializationException;
import hillel.spring.doctor.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorSpecializationsConfig doctorSpecializationsConfig;

    static Specification<Doctor> nameStartsWithIgnoreCase(String name) {
        return (from, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(from.get("name")), name.toLowerCase() + "%");
    }

    static Specification<Doctor> specializationEquals(String specialization) {
        return (from, query, criteriaBuilder) -> criteriaBuilder.equal(from.get("specialization"), specialization);
    }

    static Specification<Doctor> specializationIn(List<String> specializations) {
        return (from, query, criteriaBuilder) -> from.get("specialization").in(specializations);
    }

    public List<Doctor> list() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> findByCriteria(Map<String, Object> parameters) {
        //return findByCriteriaUsingMethodName(parameters);
        //return findByCriteriaUsingCriteriaQueries(parameters);
        return findByCriteriaUsingJpaSpecifications(parameters);
    }

    public List<Doctor> findByCriteriaUsingMethodName(Map<String, Object> parameters) {
        if (parameters.containsKey("specialization") && parameters.containsKey("name") && parameters.containsKey("specializations")) {
            return doctorRepository.findByNameStartingWithIgnoreCaseAndSpecializationAndSpecializationIn(
                    (String) parameters.get("name"), (String) parameters.get("specialization"), (List<String>) parameters.get("specializations"));
        } else if (parameters.containsKey("specialization") && parameters.containsKey("name")) {
            return doctorRepository.findByNameStartingWithIgnoreCaseAndSpecialization(
                    (String) parameters.get("name"), (String) parameters.get("specialization"));
        } else if (parameters.containsKey("specialization") && parameters.containsKey("specializations")) {
            return doctorRepository.findBySpecializationAndSpecializationIn(
                    (String) parameters.get("specialization"), (List<String>) parameters.get("specializations"));
        } else if (parameters.containsKey("name") && parameters.containsKey("specializations")) {
            return doctorRepository.findByNameStartingWithIgnoreCaseAndSpecializationIn(
                    (String) parameters.get("name"), (List<String>) parameters.get("specializations"));
        } else if (parameters.containsKey("specialization")) {
            return doctorRepository.findBySpecialization((String) parameters.get("specialization"));
        } else if (parameters.containsKey("name")) {
            return doctorRepository.findByNameStartingWithIgnoreCase((String) parameters.get("name"));
        } else if (parameters.containsKey("specializations")) {
            return doctorRepository.findBySpecializationIn((List<String>) parameters.get("specializations"));
        } else {
            return doctorRepository.findAll();
        }
    }

    public List<Doctor> findByCriteriaUsingCriteriaQueries(Map<String, Object> parameters) {
        return doctorRepository.findByCriteria(parameters);
    }

    public List<Doctor> findByCriteriaUsingJpaSpecifications(Map<String, Object> parameters) {
        Specification<Doctor> compositeCriteria = null;
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            switch (parameter.getKey()) {
                case "name":
                    compositeCriteria = addCriteria(compositeCriteria, nameStartsWithIgnoreCase((String) parameter.getValue()));
                    break;
                case "specialization":
                    compositeCriteria = addCriteria(compositeCriteria, specializationEquals((String) parameter.getValue()));
                    break;
                case "specializations":
                    compositeCriteria = addCriteria(compositeCriteria, specializationIn((List<String>) parameter.getValue()));
                    break;
            }
        }

        if (compositeCriteria == null) {
            return doctorRepository.findAll();
        } else {
            return doctorRepository.findAll(compositeCriteria);
        }
    }

    private Specification<Doctor> addCriteria(Specification<Doctor> compositeCriteria, Specification<Doctor> criteria) {
        return compositeCriteria == null ? Specification.where(criteria) : compositeCriteria.and(criteria);
    }

    public Doctor create(Doctor doctor) {
        assertSpecializationExists(doctor);
        return doctorRepository.save(doctor);
    }

    public void update(Doctor doctor) {
        assertSpecializationExists(doctor);
        doctorRepository.save(doctor);
    }

    public void delete(Integer id) {
        try {
            doctorRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchDoctorException();
        }
    }

    public Optional<String> findSpecialization(String specialization) {
        return doctorSpecializationsConfig.getSpecializations().stream()
                .filter(s -> s.equals(specialization.toLowerCase()))
                .findFirst();
    }

    private void assertSpecializationExists(Doctor doctor) {
        if (findSpecialization(doctor.getSpecialization()).isEmpty()) {
            throw new UnknownSpecializationException();
        }
    }
}
