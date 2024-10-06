import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Kitchen {
    private Queue<Dish> toMakeDishes = new LinkedList<>();
    private List<Dish> madeDishes = new ArrayList<>();

    // Add a dish to the queue of dishes to make
    public synchronized void addDish(Dish dish) {
        toMakeDishes.add(dish);
    }

    // Get a dish that needs to be made
    public synchronized Dish getDishToMake() {
        return toMakeDishes.poll();  // Return null if no dishes to make
    }

    // Mark a dish as made
    public synchronized void markDishAsMade(Dish dish) {
        madeDishes.add(dish);
    }
}
