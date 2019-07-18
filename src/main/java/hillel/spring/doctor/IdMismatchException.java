package hillel.spring.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Can't change id of existing doctor")
public class IdMismatchException extends RuntimeException {
}
