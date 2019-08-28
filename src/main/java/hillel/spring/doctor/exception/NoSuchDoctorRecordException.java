package hillel.spring.doctor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchDoctorRecordException extends RuntimeException {
    public NoSuchDoctorRecordException(Integer id) {
        super(String.format("Doctor record with id %s not found", id));
    }
}
