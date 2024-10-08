import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class Kitchen {
    private List<Dish> dishesToMake;  
    private List<Dish> madeDishes;
    private List<Dish> abandonedDishes;
    private AtomicInteger totalDishesCount;

    public Kitchen() {
        dishesToMake = new ArrayList<>();
        madeDishes = new ArrayList<>();
        abandonedDishes = new ArrayList<>();
        totalDishesCount = new AtomicInteger(0);
    }

    // Adding dishes introduces a race condition
    public void addDishToMake(Dish dish) {
        try {
            Thread.sleep(100);  // Artificial delay to exacerbate the race condition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        dishesToMake.add(dish);  // Race condition: multiple waiters adding dishes concurrently
        totalDishesCount.incrementAndGet();
    }

    // Retrieving a dish to make also introduces a race condition
    public Dish getDishToMake() {
        if (dishesToMake.isEmpty()) {
            return null;
        }

        Dish dish = dishesToMake.get(0);  // Get but don't remove the dish yet (potential issue)
        try {
            Thread.sleep(200);  // Artificial delay to simulate chefs racing to pick the same dish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if dish still exists (race condition between different chefs)
        if (!dishesToMake.isEmpty() && dishesToMake.get(0).equals(dish)) {
            dishesToMake.remove(0);  // Remove dish after delay, another chef might have taken it already
            return dish;
        }
        return null;
    }

    public void markDishAsMade(Dish dish) {
        synchronized (madeDishes) {
            madeDishes.add(dish);
        }
    }

    public void markDishAsAbandoned(Dish dish) {
        synchronized (abandonedDishes) {
            abandonedDishes.add(dish);
        }
    }

    public boolean noMoreDishesToMake() {
        return dishesToMake.isEmpty();
    }

    public void displayAllDishesCount() {
        System.out.println("Total dishes count: " + totalDishesCount.get());
    }

    // Display dish types and their counts for dishes to make
    public void displayToMakeDishes() {
        System.out.println("Dishes to make: " + dishesToMake.size());

        // Create a map to count the occurrences of each dish type
        Map<String, Integer> dishCountMap = new HashMap<>();
        for (Dish dish : dishesToMake) {
            if (dish != null) { // Check if the dish is not null
                String dishType = dish.getClass().getSimpleName();
                dishCountMap.put(dishType, dishCountMap.getOrDefault(dishType, 0) + 1);
            } else {
                System.out.println(" - Skipping null dish entry");
            }
        }

        // Print the count for each dish type
        for (Map.Entry<String, Integer> entry : dishCountMap.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue());
        }
    }

    // Display dish types and their counts for made dishes
    public void displayMadeDishes() {
        System.out.println("Made dishes: " + madeDishes.size());

        // Create a map to count the occurrences of each dish type
        Map<String, Integer> dishCountMap = new HashMap<>();
        for (Dish dish : madeDishes) {
            if (dish != null) { // Check if the dish is not null
                String dishType = dish.getClass().getSimpleName();
                dishCountMap.put(dishType, dishCountMap.getOrDefault(dishType, 0) + 1);
            } else {
                System.out.println(" - Skipping null dish entry");
            }
        }

        // Print the count for each dish type
        for (Map.Entry<String, Integer> entry : dishCountMap.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue());
        }
    }

    public void displayAbandonedDishes() {
        System.out.println("Abandoned dishes: " + abandonedDishes.size());
    }
}