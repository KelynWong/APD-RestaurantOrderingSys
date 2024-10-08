import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        Kitchen kitchen = new Kitchen();
        Inventory inventory = new Inventory();

        // Add limited ingredients to the inventory
        for (int i = 0; i < 5; i++) {
            inventory.addIngredient("egg", 30);
            inventory.addIngredient("milk", 15);
            inventory.addIngredient("butter", 15);
            inventory.addIngredient("water", 15);
            inventory.addIngredient("salt", 30);
        }
        
        int numberOfChefs = 10;
        int numberOfWaiters = 5;

        // Create executor for waiters
        ExecutorService waiterExecutor = Executors.newFixedThreadPool(numberOfWaiters);

        // Waiters submitting orders
        System.out.println("Waiters are submitting orders...");
        for (int i = 0; i < numberOfWaiters; i++) {
            waiterExecutor.submit(new Waiter(i + 1, kitchen));
        }

        // Shutdown the waiter executor and wait for all orders to be submitted
        waiterExecutor.shutdown();
        try {
            waiterExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Display initial kitchen state
        System.out.println("\n--------------------------------");
        System.out.println("Initial kitchen state:");
        System.out.println("--------------------------------");
        kitchen.displayAllDishesCount();
        System.out.println("Total dishes (expected): 150");
        kitchen.displayToMakeDishes();
        System.out.println("Dishes to make (expected): 150");
        System.out.println();

        // Now, start the chefs
        System.out.println("Chefs are starting to cook...\n");
        ExecutorService chefExecutor = Executors.newFixedThreadPool(numberOfChefs);

        // Submit Chef tasks to the executor
        for (int i = 0; i < numberOfChefs; i++) {
            chefExecutor.submit(new Chef(kitchen, inventory));
        }

        chefExecutor.shutdown();

        try {
            while (!chefExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                if (Chef.areAllChefsDone()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Display final kitchen state and inventory
        System.out.println("\n--------------------------------");
        System.out.println("Final kitchen state");
        System.out.println("--------------------------------");
        kitchen.displayAllDishesCount();
        System.out.println("Total dishes (expected): 150");
        kitchen.displayToMakeDishes();
        System.out.println("Dishes to make (expected): 0");
        kitchen.displayMadeDishes();
        System.out.println("Made dishes (expected): 150");
        kitchen.displayAbandonedDishes();
        System.out.println("Abandoned dishes (expected): 0");
        System.out.println("\n--------------------------------");
        System.out.println("Final inventory state");
        System.out.println("--------------------------------");
        inventory.displayInventory();
        System.out.println("Expected Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}");
    }
}