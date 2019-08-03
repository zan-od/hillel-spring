package hillel.spring.doctor.exception;

public class UnknownSpecializationException extends RuntimeException {
    public UnknownSpecializationException(String message){
        super(message);
    }
}
