package hillel.spring.doctor.repository;

import hillel.spring.doctor.domain.DoctorRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DoctorRecordRepository extends JpaRepository<DoctorRecord, Integer> {
    List<DoctorRecord> findByDoctorIdAndStartDateBetween(Integer doctorId, LocalDateTime startDate, LocalDateTime endDate);

    List<DoctorRecord> findByDoctorIdAndStartDateGreaterThanEqual(Integer doctorId, LocalDateTime startDate);
}
