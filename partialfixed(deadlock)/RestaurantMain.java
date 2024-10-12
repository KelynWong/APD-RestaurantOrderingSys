package apd.proj.partialfixed;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestaurantMain {
    public static void main(String[] args) {
        // Load configuration from config.txt
        Config config = Config.getInstance();

        // Get the number of chefs and waiters from the configuration
        int numberOfChefs = config.getIntValue("NUMBER_OF_CHEFS");
        int numberOfWaiters = config.getIntValue("NUMBER_OF_WAITERS");
        
        // Get inventory limited flag
        int makeInventoryLimited = config.getIntValue("MAKE_INVENTORY_LIMITED");

        // Get the dish quantities from the configuration
        int steamedEggCount = config.getIntValue("SteamedEgg") * numberOfWaiters;
        int omeletteCount = config.getIntValue("Omelette") * numberOfWaiters;

        // Calculate total expected dishes (based on config file values)
        int totalDishes = steamedEggCount + omeletteCount;

        // Use the Singleton pattern to get the single instance of Kitchen and Inventory
        Kitchen kitchen = Kitchen.getInstance();
        Inventory inventory = Inventory.getInstance();

        // Create a Random object for generating random limits
        Random random = new Random();

        // Generate random maximum limits for ingredients
        int maxEggQuantity = random.nextInt(150) + 50; // Random limit between 50 and 199
        int maxMilkQuantity = random.nextInt(100) + 20; // Random limit between 20 and 119
        int maxButterQuantity = random.nextInt(100) + 20; // Random limit between 20 and 119
        int maxWaterQuantity = random.nextInt(150) + 50; // Random limit between 50 and 199
        int maxSaltQuantity = random.nextInt(150) + 50; // Random limit between 50 and 199

        // Calculate ingredient quantities based on inventory limit
        int eggQuantity = makeInventoryLimited == 1 ? Math.min(steamedEggCount + omeletteCount, maxEggQuantity) : steamedEggCount + omeletteCount;
        int milkQuantity = makeInventoryLimited == 1 ? Math.min(omeletteCount, maxMilkQuantity) : omeletteCount;
        int butterQuantity = makeInventoryLimited == 1 ? Math.min(omeletteCount, maxButterQuantity) : omeletteCount;
        int waterQuantity = makeInventoryLimited == 1 ? Math.min(steamedEggCount, maxWaterQuantity) : steamedEggCount;
        int saltQuantity = makeInventoryLimited == 1 ? Math.min(steamedEggCount + omeletteCount, maxSaltQuantity) : (steamedEggCount + omeletteCount);

        // Add limited ingredients to the inventory
        inventory.addIngredient("egg", eggQuantity);
        inventory.addIngredient("milk", milkQuantity);
        inventory.addIngredient("butter", butterQuantity);
        inventory.addIngredient("water", waterQuantity);
        inventory.addIngredient("salt", saltQuantity);

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

            // Dynamically calculate expected results based on the dish counts
            System.out.println("--------------------");
            System.err.println("!!! ACTUAL !!!");
            System.out.println("--------------------");
            kitchen.displayDishHistory();

            System.out.println("--------------------");
            System.err.println("!!! EXPECTED !!!");
            System.out.println("--------------------");

            // Use the methods from Kitchen to get the abandoned dish counts
            int abandonedEggs = kitchen.getAbandonedDishCount("SteamedEgg");
            int abandonedOmelettes = kitchen.getAbandonedDishCount("Omelette");

            // Calculate total abandoned dishes based on the actual abandoned counts
            int totalAbandonedDishes = abandonedEggs + abandonedOmelettes;

            System.out.println(
                "Total dishes to make: " + totalDishes + "\n" +
                "To-make dishes:\n" +
                " - SteamedEgg: " + steamedEggCount + "\n" +
                " - Omelette: " + omeletteCount + "\n\n" +
                "Total made dishes: " + (steamedEggCount + omeletteCount - totalAbandonedDishes) + "\n" +
                "Made dishes:\n" +
                " - SteamedEgg: " + (steamedEggCount - abandonedEggs) + "\n" +
                " - Omelette: " + (omeletteCount - abandonedOmelettes) + "\n\n" +
                "Total served dishes: " + (steamedEggCount + omeletteCount - totalAbandonedDishes) + "\n" +
                "Served dishes:\n" +
                " - SteamedEgg: " + (steamedEggCount - abandonedEggs) + "\n" +
                " - Omelette: " + (omeletteCount - abandonedOmelettes) + "\n\n" +
                "Total abandoned dishes: " + totalAbandonedDishes + "\n" +
                "Abandoned dishes:\n" +
                " - SteamedEgg: " + abandonedEggs + "\n" +
                " - Omelette: " + abandonedOmelettes
            );

            System.out.println("\n--------------------------------");
            System.out.println("\tInventory");
            System.out.println("--------------------------------");
            inventory.displayInventory();
            System.out.println("Expected Inventory: {butter=0, salt=0, egg=0, milk=0, water=0}");
        }
    }
}
