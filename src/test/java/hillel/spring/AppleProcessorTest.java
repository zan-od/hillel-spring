package hillel.spring;

import lombok.val;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class AppleProcessorTest {
    private List<Apple> apples = List.of(
            new Apple(100, "Green"),
            new Apple(120, "Red"),
            new Apple(90, "Yellow"),
            new Apple(150, "Greean")
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
        AppleProcessor.ApplePredicate predicate = new AppleProcessor.ApplePredicate() {
            @Override
            public boolean matchCondition(Apple apple) {
                return apple.getWeight() >= 100 && apple.getWeight() <= 120;
            }
        };

        val heavyApples = AppleProcessor.filter(apples, predicate);
        assertThat(heavyApples).contains(
                new Apple(100, "Green"),
                new Apple(120, "Red"));
    }



}
