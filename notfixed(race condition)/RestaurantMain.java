import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add limited ingredients to the inventory
        for (int i = 0; i < 1000; i++) {
            inventory.addIngredient("egg", 30);
            inventory.addIngredient("milk", 15);
            inventory.addIngredient("butter", 15);
            inventory.addIngredient("water", 15);
            inventory.addIngredient("salt", 30);
        }

        int numberOfChefs = 1000;
        int numberOfWaiters = 1000;

        // Create a shared executor for both waiters and chefs to run simultaneously
        ExecutorService sharedExecutor = Executors.newFixedThreadPool(numberOfChefs + numberOfWaiters);

        // Waiters submitting orders and serving dishes
        System.out.println("Waiters and Chefs are starting concurrently...");
        for (int i = 0; i < numberOfWaiters; i++) {
            sharedExecutor.submit(new Waiter(i + 1, kitchen));
        }

        // Submit Chef tasks to the executor with chefId
        for (int i = 0; i < numberOfChefs; i++) {
            sharedExecutor.submit(new Chef(i + 1, kitchen, inventory));  // Pass i+1 as chefId
        }

        // Shutdown the shared executor and wait for all tasks to finish
        sharedExecutor.shutdown();
        try {
            sharedExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Display final kitchen state and inventory
        if (sharedExecutor.isTerminated()) {
            System.out.println("\n--------------------------------");
            System.out.println("\tThe Kitchen");
            System.out.println("--------------------------------");
            System.out.println("--------------------");
            System.err.println("!!! ACTUAL !!!");
            System.out.println("--------------------");
            kitchen.displayDishHistory();

            System.out.println("--------------------");
            System.err.println("!!! EXPECTED !!!");
            System.out.println("--------------------");
            System.out.println(
                "Total dishes to make: 150\n" +
                "To-make dishes:\n" +
                " - SteamedEgg: 75\n" +
                " - Omelette: 75\n\n" +
                "Total made dishes: 150\n" +
                "Made dishes:\n" +
                " - SteamedEgg: 75\n" +
                " - Omelette: 75\n\n" +
                "Total served dishes: 150\n" +
                "Served dishes:\n" +
                " - SteamedEgg: 75\n" +
                " - Omelette: 75\n\n" +
                "Total abandoned dishes: 0\n" +
                "Abandoned dishes:\n" +
                " - SteamedEgg: 0\n" +
                " - Omelette: 0"
            );

            System.out.println("\n--------------------------------");
            System.out.println("\tInventory");
            System.out.println("--------------------------------");
            inventory.displayInventory();
            System.out.println("Expected Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}");
        }
    }
}