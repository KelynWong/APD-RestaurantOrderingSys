import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Kitchen {
    private Queue<Dish> toMakeDishes = new LinkedList<>();
    private List<Dish> madeDishes = new ArrayList<>();

    // Function to display the queue of toMakeDishes
    public synchronized void displayToMakeDishes() {
        Map<String, Integer> dishCount = new HashMap<>();

        // Count the dishes in the toMakeDishes queue
        for (Dish dish : toMakeDishes) {
            String dishName = dish.getClass().getSimpleName();  // Get the name of the dish (class name)
            dishCount.put(dishName, dishCount.getOrDefault(dishName, 0) + 1);
        }

        // Display the dish names and counts
        System.out.println("Dishes to make:");
        for (Map.Entry<String, Integer> entry : dishCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    public synchronized void displayMadeDishes() {
        Map<String, Integer> dishCount = new HashMap<>();

        // Count the dishes in the madeDishes list
        for (Dish dish : madeDishes) {
            String dishName = dish.getClass().getSimpleName();  // Get the name of the dish (class name)
            dishCount.put(dishName, dishCount.getOrDefault(dishName, 0) + 1);
        }

        // Display the dish names and counts
        System.out.println("Made dishes:");
        for (Map.Entry<String, Integer> entry : dishCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
     // Check if all dishes in the order have been made (no synchronization)
     public boolean isOrderComplete(Order order) {
        Map<String, Integer> requiredDishes = order.getDishes();
        Map<String, Integer> madeDishCounts = getMadeDishesCount();

        for (Map.Entry<String, Integer> entry : requiredDishes.entrySet()) {
            String dishName = entry.getKey();
            int requiredQuantity = entry.getValue();

            int madeQuantity = madeDishCounts.getOrDefault(dishName, 0);
            if (madeQuantity < requiredQuantity) {
                return false;  // The order is not complete yet
            }
        }
        return true;  // All dishes in the order are complete
    }

        // Helper method to get the count of made dishes by name (no synchronization)
    public Map<String, Integer> getMadeDishesCount() {
        Map<String, Integer> dishCounts = new HashMap<>();
        for (Dish dish : madeDishes) {
            String dishName = dish.getClass().getSimpleName();
            dishCounts.put(dishName, dishCounts.getOrDefault(dishName, 0) + 1);
        }
        return dishCounts;
    }


    public synchronized void processOrder(Order order) {
        // Synchronize this method to prevent race conditions
        for (Map.Entry<String, Integer> entry : order.getDishes().entrySet()) {
            String dishName = entry.getKey();
            int quantity = entry.getValue();

            // For each dish type, add the required quantity to the kitchen queue
            for (int i = 0; i < quantity; i++) {
                if (dishName.equals("SteamedEgg")) {
                    addDish(new SteamedEgg());
                } else if (dishName.equals("Omelette")) {
                    addDish(new Omelette());
                }
            }
        }
        System.out.println("Order processed: " + order);
    }

    //  Check if there are no more dishes to make (no synchronization)
    public boolean noMoreDishesToMake() {
        return toMakeDishes.isEmpty();
    }

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
// import java.util.Map;

// public class Kitchen {
    // public synchronized void processOrder(Order order) {
    //     // Synchronize this method to prevent race conditions
    //     for (Map.Entry<String, Integer> entry : order.getDishes().entrySet()) {
    //         String dishName = entry.getKey();
    //         int quantity = entry.getValue();

    //         // For each dish type, add the required quantity to the kitchen queue
    //         for (int i = 0; i < quantity; i++) {
    //             if (dishName.equals("SteamedEgg")) {
    //                 addDish(new SteamedEgg());
    //             } else if (dishName.equals("Omelette")) {
    //                 addDish(new Omelette());
    //             }
    //         }
    //     }
    //     System.out.println("Order processed: " + order);
    // }

//     // Existing addDish logic to add the dish to the kitchen queue
//     public synchronized void addDish(Dish dish) {
//         // Add dish to the queue (this is your existing logic)
//         // Assuming there's a queue or list for dishes
        
//     }
// }
// import java.util.Queue;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Map;
// import java.util.HashMap;

// public class Kitchen {
//     private Queue<Dish> toMakeDishes = new LinkedList<>();
//     private List<Dish> madeDishes = new ArrayList<>();

//     // Check if all dishes in the order have been made (no synchronization)
//     public boolean isOrderComplete(Order order) {
//         Map<String, Integer> requiredDishes = order.getDishes();
//         Map<String, Integer> madeDishCounts = getMadeDishesCount();

//         for (Map.Entry<String, Integer> entry : requiredDishes.entrySet()) {
//             String dishName = entry.getKey();
//             int requiredQuantity = entry.getValue();

//             int madeQuantity = madeDishCounts.getOrDefault(dishName, 0);
//             if (madeQuantity < requiredQuantity) {
//                 return false;  // The order is not complete yet
//             }
//         }
//         return true;  // All dishes in the order are complete
//     }

//     // Check if there are no more dishes to make (no synchronization)
//     public boolean noMoreDishesToMake() {
//         return toMakeDishes.isEmpty();
//     }

//     // Add a dish to the queue of dishes to make (no synchronization)
//     public void addDish(Dish dish) {
//         toMakeDishes.add(dish);
//     }

//     // Get a dish that needs to be made (no synchronization)
//     public Dish getDishToMake() {
//         return toMakeDishes.poll();  // Return null if no dishes to make
//     }

//     // Mark a dish as made (no synchronization)
//     public void markDishAsMade(Dish dish) {
//         madeDishes.add(dish);
//     }

//     // Helper method to get the count of made dishes by name (no synchronization)
//     public Map<String, Integer> getMadeDishesCount() {
//         Map<String, Integer> dishCounts = new HashMap<>();
//         for (Dish dish : madeDishes) {
//             String dishName = dish.getClass().getSimpleName();
//             dishCounts.put(dishName, dishCounts.getOrDefault(dishName, 0) + 1);
//         }
//         return dishCounts;
//     }
// }


