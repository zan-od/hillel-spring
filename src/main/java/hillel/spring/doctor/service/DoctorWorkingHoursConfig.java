package hillel.spring.doctor.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("doctors.working-hours")
public class DoctorWorkingHoursConfig {

    private String startTime;
    private String endTime;

}
