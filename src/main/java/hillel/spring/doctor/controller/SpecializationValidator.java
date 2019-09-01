package hillel.spring.doctor.controller;

import hillel.spring.doctor.config.DoctorSpecializationsConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SpecializationValidator implements ConstraintValidator<ValidSpecialization, String> {
    @Autowired
    public DoctorSpecializationsConfig doctorSpecializationsConfig;

    @Override
    public boolean isValid(String specialization, ConstraintValidatorContext context) {
        if (specialization == null) {
            return true;
        }

        List<String> specializations = doctorSpecializationsConfig.getSpecializations();

        return specializations.stream()
                .anyMatch(s -> s.equals(specialization.toLowerCase()));
    }
}
