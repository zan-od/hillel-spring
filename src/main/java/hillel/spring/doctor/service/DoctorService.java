package hillel.spring.doctor.service;

import hillel.spring.doctor.domain.Doctor;
import hillel.spring.doctor.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public List<Doctor> list() {
        return doctorRepository.list();
    }

    public Optional<Doctor> findById(Integer id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> findByCriteria(Predicate<Doctor> predicate){
        return doctorRepository.list().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Doctor create(Doctor doctor) {
        return doctorRepository.create(doctor);
    }

    public void update(Doctor doctor) {
        doctorRepository.update(doctor);
    }

    public void delete(Integer id) {
        doctorRepository.delete(id);
    }
}
