package hillel.spring.greeting.service;

import hillel.spring.greeting.repository.GreetingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GreetingService {

    private final GreetingRepository greetingRepository;
    private final Random random = new Random();

    public GreetingService(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    public String getGreeting(String language) {
        String greeting = greetingRepository.getLanguagesToGreetingsMap().get(language);

        if (greeting == null) {
            throw new IllegalArgumentException(String.format("Wrong language: %s", language));
        }

        return greeting;
    }

    public String getRandomGreeting() {
        List<String> greetings = new ArrayList<>(greetingRepository.getLanguagesToGreetingsMap().values());
        int index = random.nextInt(greetings.size());

        return greetings.get(index);
    }
}
