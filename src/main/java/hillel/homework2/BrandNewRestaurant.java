package hillel.homework2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrandNewRestaurant extends AbstractRestaurant{

    protected BrandNewRestaurant(List<Dish> menu) {
        super(menu);
    }

    @Override
    public List<Dish> listAllDishes() {
        return getMenu();
    }

    @Override
    public List<Dish> listLowCalorieDishes(int caloriesLimit) {
        return getMenu().stream()
                .filter(dish -> dish.getCalories() <= caloriesLimit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dish> listTop3MostNutrientDishes() {
        return getMenu().stream()
                .sorted(Comparator.comparing(Dish::getCalories).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dish> listDishesSortedByTypeAndName() {
        return getMenu().stream()
                .sorted(Comparator.comparing(Dish::getType).thenComparing(Dish::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Map<DishType, Double> listAverageCaloriesByType() {
        return getMenu().stream()
                .collect(Collectors.groupingBy(Dish::getType, Collectors.averagingDouble(Dish::getCalories)));
    }

    @Override
    public Map<DishType, List<Dish>> groupDishesByType() {
        return getMenu().stream()
                .collect(Collectors.groupingBy(Dish::getType));
    }

    @Override
    public Map<DishType, List<String>> groupBioDishesByType() {
        return getMenu().stream()
                .collect(Collectors.groupingBy(Dish::getType, Collectors.mapping((Dish dish) -> dish.getName(), Collectors.toList())));
    }

    public static void main(String... args){
        System.out.println("Print brand new restaurant data");
        AbstractRestaurant restaurant = new BrandNewRestaurant(AbstractRestaurant.getTypicalMenu());
        restaurant.printData();
    }

}
