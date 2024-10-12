package apd.proj.notfixed;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private Map<String, Integer> dishes;

    public Order() {
        dishes = new HashMap<>();
    }

    // Add a dish to the order with the specified quantity
    public void addDish(String dishName, int quantity) {
        dishes.put(dishName, dishes.getOrDefault(dishName, 0) + quantity);
    }

    public Map<String, Integer> getDishes() {
        return dishes;
    }

    @Override
    public String toString() {
        return "Order: " + dishes.toString();
    }
}
