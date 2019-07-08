package hillel.spring.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class GreetingRepository {
    private final Map<String, String> languagesToGreetingsMap;

    public GreetingRepository() {
        languagesToGreetingsMap = Map.of(
                "en", "Hello",
                "it", "Ciao",
                "fr", "Bonjour"
        );
    }

    public Map<String, String> getLanguagesToGreetingsMap() {
        return languagesToGreetingsMap;
    }
}
