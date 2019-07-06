package hillel.homework2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dish {
    private String name;
    private Integer calories;
    private Boolean isBio;
    private DishType type;
}
