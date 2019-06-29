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

    public static <T> List<T> filter(List<T> objects, MyPredicate predicate) {
        List<T> filtered = new ArrayList<>();
        for (T obj: objects) {
            if (predicate.matchCondition(obj)){
                filtered.add(obj);
            }
        }

        return filtered;
    }

    @FunctionalInterface
    interface MyPredicate <T> {
        boolean matchCondition(T object);
    }

}
