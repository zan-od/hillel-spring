package hillel.spring.controller;

import hillel.spring.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/greeting/{language}")
    public String getGreeting(@PathVariable("language") String language) {
        return greetingService.getGreeting(language);
    }

    @GetMapping("/greeting/random")
    public String getRandomGreeting() {
        return greetingService.getRandomGreeting();
    }
}
