import java.util.ArrayList;
import java.util.List;
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

    public void displayToMakeDishes() {
        System.out.println("Dishes to make: " + dishesToMake.size());
    }

    public void displayMadeDishes() {
        System.out.println("Made dishes: " + madeDishes.size());
    }

    public void displayAbandonedDishes() {
        System.out.println("Abandoned dishes: " + abandonedDishes.size());
    }
}