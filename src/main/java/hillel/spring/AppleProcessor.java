package hillel.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppleProcessor {
    public static Optional<Apple> pickHeaviest(List<Apple> apples) {
        Apple result = null;
        for (Apple apple: apples){
            if (result == null || result.getWeight() > apple.getWeight()){
                result = apple;
            }
        }

        return Optional.ofNullable(result);
    }

    public static List<Apple> filter(List<Apple> apples, ApplePredicate predicate) {
        List<Apple> filtered = new ArrayList<>();
        for (Apple apple: apples) {
            if (predicate.matchCondition(apple)){
                filtered.add(apple);
            }
        }

        return filtered;
    }

    interface ApplePredicate{
        boolean matchCondition(Apple apple);
    }

}
