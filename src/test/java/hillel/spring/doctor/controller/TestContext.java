package hillel.spring.doctor.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.*;

@Configuration
public class TestContext {
    @Primary
    @Bean
    public Clock testClock() {
        Instant instant = LocalDateTime.parse("2019-08-24T12:00:00").toInstant(ZoneOffset.UTC);
        return Clock.fixed(instant, ZoneId.of("UTC"));
    }
}
