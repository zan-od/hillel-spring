package hillel.spring.service;

import hillel.spring.repository.GreetingRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GreetingService.class, GreetingRepository.class})
public class GreetingServiceTest {

    @Autowired
    private GreetingService greetingService;

    @Test
    public void getGreeting() {
        Assertions.assertThat(greetingService.getGreeting("en")).isEqualTo("Hello");
        Assertions.assertThat(greetingService.getGreeting("fr")).isEqualTo("Bonjour");
        Assertions.assertThat(greetingService.getGreeting("it")).isEqualTo("Ciao");
    }

    @Test
    public void getRandomGreeting() {

        int callsCount = 9999;
        long expectedValue = callsCount / 3;

        Map<String, Long> greetingsCountMap = IntStream.rangeClosed(1, callsCount)
                .mapToObj(i -> greetingService.getRandomGreeting())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println(greetingsCountMap);

        for (Map.Entry<String, Long> entry : greetingsCountMap.entrySet()) {
            Assertions.assertThat(entry.getValue()).isCloseTo(expectedValue, Percentage.withPercentage(5.0));
        }
    }
}