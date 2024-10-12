package apd.proj.fixed;

import java.util.Map;

public class Waiter implements Runnable {
    private int waiterId;
    private Kitchen kitchen;
    private int maxServingAttempts;
    private int servingWaitTime;

    public Waiter(int waiterId, Kitchen kitchen) {
        this.waiterId = waiterId;
        this.kitchen = kitchen;

        // Load configuration values
        Config config = Config.getInstance();
        this.maxServingAttempts = config.getIntValue("MAX_SERVING_ATTEMPTS");
        this.servingWaitTime = config.getIntValue("SERVING_WAIT_TIME");
    }

    @Override
    public void run() {
        // Get dish orders from the config
        Config config = Config.getInstance();
        Map<String, Integer> dishOrders = config.getDishOrders();

        // Submit the dishes to the kitchen
        for (Map.Entry<String, Integer> entry : dishOrders.entrySet()) {
            String dishName = entry.getKey();
            int quantity = entry.getValue();

            for (int j = 0; j < quantity; j++) {
                try {
                    // Try to create the dish from the factory
                    Dish dish = DishFactory.createDish(dishName);
                    kitchen.addDishToMake(dish);
                } catch (IllegalArgumentException e) {
                    // handle invalid dish types
                    System.err.println("Waiter " + waiterId + ": Skipping invalid dish type '" + dishName + "'. Error: " + e.getMessage());
                    continue; 
                }
            }
        }

        System.out.println("Waiter " + waiterId + " submitted orders " + dishOrders);

        // Waiter serves the made dishes after submitting the order
        serveDishes();
    }

    private void serveDishes() {
        int attempts = 0;
        while (attempts < maxServingAttempts) {
            Dish dish = kitchen.getMadeDishToServe();
            if (dish != null) {
                System.out.println("Waiter " + waiterId + " served: " + dish.getClass().getSimpleName() + " (item: " + dish.hashCode() + ")");
                kitchen.markDishAsServed(dish);
            } else {
                attempts++;
                try {
                    Thread.sleep(servingWaitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.out.println("Waiter " + waiterId + " finished serving after " + attempts + " attempts.");
    }
}
