package hillel.spring.doctor.service;

import hillel.spring.doctor.config.DoctorSpecializationsConfig;
import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.exception.NoSuchDoctorException;
import hillel.spring.doctor.exception.UnknownSpecializationException;
import hillel.spring.doctor.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorSpecializationsConfig doctorSpecializationsConfig;

    static Specification<Doctor> nameStartsWithIgnoreCase(String name) {
        return (from, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(from.get("name")), name.toLowerCase() + "%");
    }

    static Specification<Doctor> anySpecializationEquals(String specialization) {
        return (from, query, criteriaBuilder) -> {
            Expression<Set<String>> doctorSpecializations = from.get("specializations");
            return criteriaBuilder.isMember(specialization, doctorSpecializations);
        };
    }

    static Specification<Doctor> anySpecializationIn(List<String> specializations) {
        return (from, query, criteriaBuilder) -> {
            EntityType<Doctor> Doctor_ = from.getModel();
            SetJoin<Doctor, String> specJoin = from.join(Doctor_.getSet("specializations", String.class));
            return specJoin.in(specializations);
        };
    }

    public List<Doctor> list() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepository.findById(id);
    }

    public Page<Doctor> findByCriteria(Map<String, Object> parameters, Pageable pageable) {
        Specification<Doctor> compositeCriteria = null;
        for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
            switch (parameter.getKey()) {
                case "name":
                    compositeCriteria = addCriteria(compositeCriteria, nameStartsWithIgnoreCase((String) parameter.getValue()));
                    break;
                case "specialization":
                    compositeCriteria = addCriteria(compositeCriteria, anySpecializationEquals((String) parameter.getValue()));
                    break;
                case "specializations":
                    compositeCriteria = addCriteria(compositeCriteria, anySpecializationIn((List<String>) parameter.getValue()));
                    break;
            }
        }

        if (compositeCriteria == null) {
            return doctorRepository.findAll(pageable);
        } else {
            return doctorRepository.findAll(compositeCriteria, pageable);
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
            throw new NoSuchDoctorException(id);
        }
    }

    public Optional<String> findSpecialization(String specialization) {
        return doctorSpecializationsConfig.getSpecializations().stream()
                .filter(s -> s.equals(specialization.toLowerCase()))
                .findFirst();
    }

    private void assertSpecializationExists(Doctor doctor) {
        if (doctor.getSpecializations() == null) {
            return;
        }

        doctor.getSpecializations()
                .forEach(specialization -> {
                    if (findSpecialization(specialization).isEmpty()) {
                        throw new UnknownSpecializationException("Unknown specialization: " + specialization);
                    }
                });
    }
}
