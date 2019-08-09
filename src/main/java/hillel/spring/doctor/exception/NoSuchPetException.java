package hillel.spring.doctor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchPetException extends RuntimeException {
    public NoSuchPetException(Integer id) {
        super(String.format("Pet with id %s not found", id));
    }
}
