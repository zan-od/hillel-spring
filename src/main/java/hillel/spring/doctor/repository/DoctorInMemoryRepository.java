package hillel.spring.doctor.repository;

import hillel.spring.doctor.NoSuchDoctorException;
import hillel.spring.doctor.domain.Doctor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class DoctorInMemoryRepository {

    private final Map<Integer, Doctor> doctors = new ConcurrentHashMap<>();
    private final AtomicInteger lastId = new AtomicInteger(0);

    // for testing purposes
    public void clearRepository() {
        doctors.clear();
        lastId.set(0);
    }

    public List<Doctor> list() {
        return doctors.values().stream()
                //for testing purposes we need to have the list sorted by id
                .sorted(Comparator.comparing(Doctor::getId))
                .collect(Collectors.toList());
    }

    public Optional<Doctor> findById(Integer id) {
        return Optional.ofNullable(doctors.get(id));
    }

    public Doctor create(Doctor doctor) {
        Integer id = lastId.incrementAndGet();
        doctor.setId(id);
        doctors.put(id, doctor);

        return doctor;
    }

    public void update(Doctor doctor) {
        if (!doctors.containsKey(doctor.getId())) {
            throw new NoSuchDoctorException();
        }

        doctors.put(doctor.getId(), doctor);
    }

    public void delete(Integer id) {
        if (!doctors.containsKey(id)) {
            throw new NoSuchDoctorException();
        }

        doctors.remove(id);
    }
}
