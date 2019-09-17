package hillel.spring.doctor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties("doctors.education-service")
@Component
@Validated
public class DoctorEducationServiceConfig {
    @NotBlank
    private String url;
}
