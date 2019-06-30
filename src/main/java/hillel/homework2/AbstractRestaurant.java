package hillel.homework2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractRestaurant {
    protected final List<Dish> menu;

    protected AbstractRestaurant(List<Dish> menu) {
        this.menu = menu;
    }

    public List<Dish> getMenu(){
        List<Dish> copy = new ArrayList<>();
        copy.addAll(menu);

        return copy;
    }

    public static List<Dish> getTypicalMenu(){
        List<Dish> menu = new ArrayList<>();

        menu.add(new Dish("Steak", 105, false, DishType.BEEF));
        menu.add(new Dish("Beef Stroganoff", 124, false, DishType.BEEF));

        menu.add(new Dish("Boiled chicken", 80, true, DishType.CHICKEN));
        menu.add(new Dish("Grilled chicken", 95, false, DishType.CHICKEN));
        menu.add(new Dish("Baked chicken", 90, false, DishType.CHICKEN));

        menu.add(new Dish("Greek salad", 71, true, DishType.VEGETABLES));
        menu.add(new Dish("Caesar salad", 98, false, DishType.VEGETABLES));

        return menu;
    }

    public void printList(List<Dish> list){
        if (list == null){
            System.out.println("null");
            return;
        }
        list.forEach(System.out::println);
    }

    public <K, V> void printMap(Map<K, V> map){
        if (map == null){
            System.out.println("null");
            return;
        }
        map.entrySet().forEach(System.out::println);
    }

    public <K, V extends Number> void incrementValue(Map<K, V> map, K key, V value){
        V accumulator = map.get(key);

        if (value.getClass() == Double.class){
            if (accumulator == null){
                accumulator = (V) Double.valueOf(0);
            }
            accumulator = (V) Double.valueOf(((Double) accumulator).doubleValue() +
                    ((Double) value).doubleValue());
        } else {
            if (accumulator == null){
                accumulator = (V) Integer.valueOf(0);
            }
            accumulator = (V) Integer.valueOf(((Integer) accumulator).intValue() +
                    ((Integer) value).intValue());
        }

        map.put(key, accumulator);
    }

    public <K, V> void addToGroup(Map<K, List<V>> map, K key, V value){
        List<V> list = map.get(key);

        if (list == null){
            list = new ArrayList<>();
            map.put(key, list);
        }

        list.add(value);
    }

    public abstract List<Dish> listAllDishes();

    public abstract List<Dish> listLowCalorieDishes(int caloriesLimit);

    public abstract List<Dish> listTop3MostNutrientDishes();

    public abstract List<Dish> listDishesSortedByTypeAndName();

    public abstract Map<DishType, Double> listAverageCaloriesByType();

    public abstract Map<DishType, List<Dish>> groupDishesByType();

    public abstract Map<DishType, List<String>> groupBioDishesByType();

    public void printData() {
        System.out.println("\n1. Print all dishes:");
        printList(listAllDishes());

        System.out.println("\n2. Print all low calorie dishes:");
        printList(listLowCalorieDishes(90));

        System.out.println("\n3. Print top 3 most nutrient dishes:");
        printList(listTop3MostNutrientDishes());

        System.out.println("\n4. Print dishes sorted by type and name:");
        printList(listDishesSortedByTypeAndName());

        System.out.println("\n5. Print average calories by dishes types:");
        printMap(listAverageCaloriesByType());

        System.out.println("\n6. Print dishes grouped by type:");
        printMap(groupDishesByType());

        System.out.println("\n7. Print bio dishes grouped by type:");
        printMap(groupBioDishesByType());
    }

}
