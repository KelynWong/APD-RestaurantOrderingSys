import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class Kitchen {
    private Queue<Dish> toMakeDishes = new LinkedList<>();
    private List<Dish> madeDishes = new ArrayList<>();

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

