package hillel.spring;

import lombok.val;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

public class AppleTest {

    @Test
    public void lombokWorks() {
        val apple = new Apple(100, "Green");

        assertThat(apple.getColor()).isEqualTo("Green");
        assertThat(apple).isEqualTo(new Apple(100, "Green"));
    }

    public void sortApples(){
        final List<Integer> weights = new ArrayList<>(){{
                add(1);
                add(6);
                add(2);
                add(3);
            }
        };

        Collections.sort(weights);
        weights.sort(Comparator.naturalOrder());
    }

}