package hillel.spring.doctor.repository;

import hillel.spring.doctor.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer>, DoctorRepositoryCustom, JpaSpecificationExecutor<Doctor> {
    List<Doctor> findByNameStartingWithIgnoreCaseAndSpecializationAndSpecializationIn(String name, String specialization, List<String> specializations);

    List<Doctor> findByNameStartingWithIgnoreCaseAndSpecialization(String name, String specialization);

    List<Doctor> findByNameStartingWithIgnoreCaseAndSpecializationIn(String name, List<String> specializations);

    List<Doctor> findBySpecializationAndSpecializationIn(String specialization, List<String> specializations);

    List<Doctor> findByNameStartingWithIgnoreCase(String name);

    List<Doctor> findBySpecialization(String specialization);

    List<Doctor> findBySpecializationIn(List<String> specializations);
}
