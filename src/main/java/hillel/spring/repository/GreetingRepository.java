package hillel.spring.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class GreetingRepository {
    private final Map<String, String> languagesToGreetingsMap;

    public GreetingRepository() {
        languagesToGreetingsMap = new HashMap<>();
        getLanguagesToGreetingsMap().put("en", "Hello");
        getLanguagesToGreetingsMap().put("it", "Ciao");
        getLanguagesToGreetingsMap().put("fr", "Bonjour");
    }

    public Map<String, String> getLanguagesToGreetingsMap() {
        return languagesToGreetingsMap;
    }
}
