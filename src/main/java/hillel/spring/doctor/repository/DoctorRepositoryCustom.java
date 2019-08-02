package hillel.spring.doctor.repository;

import hillel.spring.doctor.domain.Doctor;

import java.util.List;
import java.util.Map;

public interface DoctorRepositoryCustom {
    List<Doctor> findByCriteria(Map<String, Object> parameters);
}
