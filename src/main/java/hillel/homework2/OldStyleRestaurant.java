package hillel.homework2;

import java.util.*;

public class OldStyleRestaurant extends AbstractRestaurant{

    protected OldStyleRestaurant(List<Dish> menu) {
        super(menu);
    }

    @Override
    public List<Dish> listAllDishes() {
        return getMenu();
    }

    @Override
    public List<Dish> listLowCalorieDishes(int caloriesLimit) {
        List<Dish> dishes = new ArrayList<>();
        for (Dish dish: getMenu()) {
            if (dish.getCalories() <= caloriesLimit){
                dishes.add(dish);
            }
        }

        return dishes;
    }

    @Override
    public List<Dish> listTop3MostNutrientDishes() {
        List<Dish> copy = getMenu();
        copy.sort(new Comparator<Dish>() {
            // descending order by calories
            @Override
            public int compare(Dish dish1, Dish dish2) {
                if (dish1 == null){
                    return dish2 == null ? 0 : 1;
                }
                if (dish1.getCalories() == null){
                    return dish2.getCalories() == null ? 0 : 1;
                }

                return - dish1.getCalories().compareTo(dish2.getCalories());
            }
        });

        List<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dishes.add(copy.get(i));
        }

        return dishes;
    }

    @Override
    public List<Dish> listDishesSortedByTypeAndName() {
        List<Dish> copy = getMenu();
        copy.sort(new Comparator<Dish>() {
            @Override
            public int compare(Dish dish1, Dish dish2) {
                if (dish1 == null){
                    return dish2 == null ? 0 : -1;
                }

                if (dish1.getType() == null){
                    return dish2.getType() == null ? 0 : -1;
                }

                int result = dish1.getType().compareTo(dish2.getType());
                if (result != 0){
                    return result;
                }

                if (dish1.getName() == null){
                    return dish2.getName() == null ? 0 : -1;
                }

                return dish1.getName().compareTo(dish2.getName());
            }
        });

        return copy;
    }

    @Override
    public Map<DishType, Double> listAverageCaloriesByType() {
        Map<DishType, Double> dishTypeToCaloriesMap = new HashMap<>();
        Map<DishType, Integer> dishTypeCountMap = new HashMap<>();

        for (Dish dish: getMenu()){
            if (dish == null){
                continue;
            }

            incrementValue(dishTypeToCaloriesMap, dish.getType(), dish.getCalories().doubleValue());
            incrementValue(dishTypeCountMap, dish.getType(), 1);
        }

        for (Map.Entry<DishType, Double> entry: dishTypeToCaloriesMap.entrySet()){
            Integer count = dishTypeCountMap.get(entry.getKey());
            if (count == null){
                continue;
            }

            entry.setValue(entry.getValue()/count);
        }

        return dishTypeToCaloriesMap;
    }

    @Override
    public Map<DishType, List<Dish>> groupDishesByType() {
        Map<DishType, List<Dish>> groupsByTypeMap = new HashMap<>();

        for (Dish dish: getMenu()){
            if (dish == null){
                continue;
            }

            addToGroup(groupsByTypeMap, dish.getType(), dish);
        }

        return groupsByTypeMap;
    }

    @Override
    public Map<DishType, List<String>> groupBioDishesByType() {
        Map<DishType, List<String>> groupsByTypeMap = new HashMap<>();

        for (Dish dish: getMenu()){
            if (dish == null){
                continue;
            }
            if (!dish.getIsBio()){
                continue;
            }

            addToGroup(groupsByTypeMap, dish.getType(), dish.getName());
        }

        return groupsByTypeMap;
    }

    public static void main(String... args){
        System.out.println("Print old style restaurant data");
        AbstractRestaurant restaurant = new OldStyleRestaurant(AbstractRestaurant.getTypicalMenu());
        restaurant.printData();
    }

}
