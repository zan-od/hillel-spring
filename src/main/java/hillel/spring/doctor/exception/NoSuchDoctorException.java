package hillel.spring.doctor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchDoctorException extends RuntimeException {
    public NoSuchDoctorException(Integer id) {
        super(String.format("Doctor with id %s not found", id));
    }
}
