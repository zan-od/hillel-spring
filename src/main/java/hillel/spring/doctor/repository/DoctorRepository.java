package hillel.spring.doctor.repository;

import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class DoctorRepository {

    private final List<Doctor> doctors = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger lastId = new AtomicInteger(0);

    public List<Doctor> list() {
        return doctors;
    }

    public Optional<Doctor> findById(Integer id) {
        return doctors.stream()
                .filter(doctor -> doctor.getId().equals(id))
                .findFirst();
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return doctors.stream()
                .filter(doctor -> doctor.getSpecialization().equals(specialization))
                .collect(Collectors.toList());
    }

    public List<Doctor> findByNameStartsWith(String name) {
        return doctors.stream()
                .filter(doctor -> doctor.getName().startsWith(name))
                .collect(Collectors.toList());
    }

    private Optional<Integer> findIndexById(Integer id) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public void create(Doctor doctor) {
        doctor.setId(lastId.incrementAndGet());
        doctors.add(doctor);
    }

    public synchronized void update(Doctor doctor) {
        findIndexById(doctor.getId()).ifPresentOrElse(
                idx -> doctors.set(idx, doctor)
                , () -> {
                    throw new NoSuchDoctorException();
                });
    }

    public synchronized void delete(Integer id) {
        findIndexById(id).ifPresentOrElse(
                idx -> doctors.remove(idx.intValue())
                , () -> {
                    throw new NoSuchDoctorException();
                });
    }
}
