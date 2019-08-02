package hillel.spring.doctor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "doctors")
public class DoctorSpecializationsConfig {
    private List<String> specializations = new ArrayList<>();
}
