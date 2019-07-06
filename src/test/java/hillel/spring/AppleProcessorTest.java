package hillel.spring;

import lombok.val;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class AppleProcessorTest {
    private List<Apple> apples = List.of(
            new Apple(100, "Green"),
            new Apple(120, "Red"),
            new Apple(90, "Yellow"),
            new Apple(150, "Green")
    );

    @Test
    public void pickHeaviestTest() {

        final Optional<Apple> heaviestApple = AppleProcessor.pickHeaviest(apples);

        if (heaviestApple.isPresent()){
            assertThat(heaviestApple.get().getColor()).isEqualTo("Yellow");
        } else {
            fail("Apple should be present");
        }
    }

    @Test
    public void pickHeaviestFromEmptyTest() {
        final Optional<Apple> apple = AppleProcessor.pickHeaviest(Collections.emptyList());

        assertThat(apple.isPresent()).isFalse();
    }

    @Test
    public void filterHeavyTest() {
        AppleProcessor.MyPredicate <Apple> predicate = apple -> apple.getWeight() >= 100 && apple.getWeight() <= 120;

        val heavyApples = AppleProcessor.filter(apples, predicate);
        assertThat(heavyApples).contains(
                new Apple(100, "Green"),
                new Apple(120, "Red"));
    }

    @Test
    public void filterHeavyAndRedTest() {
        Predicate <Apple> isHeavy = apple -> apple.getWeight() >= 100;
        Predicate <Apple> isRed = apple -> apple.getColor().equals("Red");

        Predicate <Apple> predicate = isHeavy.and(isRed);

        val filteredApples = apples.stream()
                .filter(predicate)
                .collect(Collectors.toList());

        assertThat(filteredApples).contains(new Apple(120, "Red"));
    }


    @Test
    public void mapColors() {
        val colors = apples.stream()
                .map(Apple::getColor)
                .collect(Collectors.toSet());

        assertThat(colors).contains("Red", "Green", "Yellow");
    }

    @Test
    public void print(){
        System.out.println("Print 1");
        apples.forEach(System.out::println);

        System.out.println("\nPrint 2");
        apples.stream()
                .peek(System.out::println)
                .map(Apple::getColor)
                .peek(System.out::println)
                .collect(Collectors.toList());

        System.out.println("\nPrint 3");
        apples.stream()
                .peek(System.out::println)
                .map(Apple::getColor)
                .peek(System.out::println)
                .findFirst();
    }

    @Test
    public void collectToMap() {
        Map <String, List<Apple>> colorToApples =
                apples.stream()
                .collect(Collectors.groupingBy(Apple::getColor));

        long count =
                colorToApples.entrySet().stream()
                .peek(a -> System.out.println("sdf" + a))
                        .filter(a ->  true)
                .count();

        System.out.println(count);
    }

    @Test
    public void generateRandoms() {
        Stream.generate(Math::random)
                .limit(10)
                .peek(System.out::println)
                .collect(Collectors.toList());
    }

}
