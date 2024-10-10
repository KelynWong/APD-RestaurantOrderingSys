import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Waiter implements Runnable {
    private int waiterId;
    private Kitchen kitchen;
    private static final int MAX_SERVING_ATTEMPTS = 1000;
    private static final int SERVING_WAIT_TIME = 10;  // Wait 0.01 second between checks

    public Waiter(int waiterId, Kitchen kitchen) {
        this.waiterId = waiterId;
        this.kitchen = kitchen;
    }

    @Override
    public void run() {
        // Read order from plain text config file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("orders.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String dishName = parts[0];
                int quantity = Integer.parseInt(parts[1]);

                // Submitting the dishes to the kitchen
                for (int j = 0; j < quantity; j++) {
                    Dish dish = DishFactory.createDish(dishName);  // Use factory to create dish
                    kitchen.addDishToMake(dish);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Waiter " + waiterId + " submitted order from config");

        // Waiter serves the made dishes after submitting the order
        serveDishes();
    }

    private void serveDishes() {
        int attempts = 0;
        while (attempts < MAX_SERVING_ATTEMPTS) {
            Dish dish = kitchen.getMadeDishToServe();
            if (dish != null) {
                System.out.println("Waiter " + waiterId + " served: " + dish.getClass().getSimpleName());
                kitchen.markDishAsServed(dish);
            } else {
                attempts++;
                try {
                    Thread.sleep(SERVING_WAIT_TIME);  // Wait before checking again
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.out.println("Waiter " + waiterId + " finished serving after " + attempts + " attempts.");
    }
}