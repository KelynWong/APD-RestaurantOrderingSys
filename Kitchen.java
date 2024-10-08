import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Kitchen {
    private Queue<Dish> toMakeDishes = new LinkedList<>();
    private List<Dish> madeDishes = new ArrayList<>();

    public void processOrder(Order order) {
        for (Map.Entry<String, Integer> entry : order.getDishes().entrySet()) {
            String dishName = entry.getKey();
            int quantity = entry.getValue();
    
            // Debug: Print the dish name and quantity
            // System.out.println("Processing dish: " + dishName + ", Quantity: " + quantity);
    
            // For each dish type, add the required quantity to the kitchen queue
            for (int i = 0; i < quantity; i++) {
                if (dishName.equals("SteamedEgg")) {
                    addDish(new SteamedEgg());
                    // Debug: Print a message when adding a dish
                    // System.out.println("Added SteamedEgg to the kitchen queue.");
                } else if (dishName.equals("Omelette")) {
                    addDish(new Omelette());
                    // Debug: Print a message when adding a dish
                    // System.out.println("Added Omelette to the kitchen queue.");
                }
            }
        }
        System.out.println("Order processed: " + order);
    }

    // Add a dish to the queue of dishes to make
    public void addDish(Dish dish) {
        toMakeDishes.add(dish);
        // Debug: Print a message when added dish to the queue
        // System.out.println("Dish added to queue: " + dish.getClass().getSimpleName());
        // System.out.println("Current size of toMakeDishes: " + toMakeDishes.size());
    }

    // Get a dish that needs to be made
    public Dish getDishToMake() { 
        Dish dish = null;
        if (!toMakeDishes.isEmpty()) {
            dish = ((LinkedList<Dish>) toMakeDishes).getFirst();  // Get the first dish without removing it
            try {
                Thread.sleep(100);  // Artificial delay to increase the chance of race conditions
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ((LinkedList<Dish>) toMakeDishes).removeFirst();  // Remove the first dish after getting it
        }
        return dish;
        
        // return toMakeDishes.poll();  // Return null if no dishes to make
    }

    // Check if there are any dishes left to make
    public boolean hasDishesToMake() {
        return !toMakeDishes.isEmpty();
    }

    // Mark a dish as made
    public void markDishAsMade(Dish dish) {
        madeDishes.add(dish);
        // Debug: Print a message when dish added to madeDishes
        // System.out.println("Dish marked as made: " + dish.getClass().getSimpleName());
        // System.out.println("Current size of toMakeDishes: " + toMakeDishes.size());
    }

    // Print the count of each dish in the queue
    public void printDishes() {
        Map<String, Integer> toMakeDishCount = new HashMap<>();
        Map<String, Integer> madeDishCount = new HashMap<>();

        // Print the size of toMakeDishes
        System.out.println("Number of dishes to make: " + toMakeDishes.size());

        // Count the dishes in the toMakeDishes queue
        for (Dish dish : toMakeDishes) {
            String dishName = dish.getClass().getSimpleName(); // Get the name of the dish (class name)
            toMakeDishCount.put(dishName, toMakeDishCount.getOrDefault(dishName, 0) + 1);
        }

        // Display the dish names and counts
        System.out.println("Dishes to make:");
        for (Map.Entry<String, Integer> entry : toMakeDishCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Print the size of madeDishes
        System.out.println("Number of made dishes: " + madeDishes.size());
        
        // Count the dishes in the madeDishes list
        for (Dish dish : madeDishes) {
            String dishName = dish.getClass().getSimpleName();  // Get the name of the dish (class name)
            madeDishCount.put(dishName, madeDishCount.getOrDefault(dishName, 0) + 1);
        }

        // Display the dish names and counts
        System.out.println("Made dishes:");
        for (Map.Entry<String, Integer> entry : madeDishCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}