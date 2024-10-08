public class Chef implements Runnable {  // Runnable to allow multithreading
    private Kitchen kitchen;
    private Inventory inventory;
    private boolean isProcessingOrder = false;  // Flag to track if the chef is processing an order

    public Chef(Kitchen kitchen, Inventory inventory) {
        this.kitchen = kitchen;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        int emptyCheckCount = 0; // Counter to track how many times the queue has been empty

        while (!Thread.currentThread().isInterrupted()) {  // Stop condition based on thread interruption
            Dish dish = kitchen.getDishToMake();  // Get a dish to prepare
            
            // If no dish is available, wait for a while and check again
            if (dish == null) {
                try {
                    Thread.sleep(1000);  // Wait for 3 seconds before checking again
                    dish = kitchen.getDishToMake();  // Check again after waiting
                    if (dish == null && !isProcessingOrder) {
                        emptyCheckCount++;
                        if (emptyCheckCount >= 5) { // Terminate after 5 consecutive empty checks
                            System.out.println(Thread.currentThread().getName() + " waited for 3s and there were no more orders. Chef is terminating.");
                            return;  // Terminate the thread if no new orders
                        }
                    } else {
                        emptyCheckCount = 0; // Reset counter if a dish is found
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;  // Terminate the thread if interrupted
                }
            }

            // Prepare the dish once available
            if (dish != null) {
                isProcessingOrder = true;  // Set flag to true when processing an order
                System.out.println(Thread.currentThread().getName() + " is preparing: " + dish.hashCode());
                boolean allIngredientsAvailable = true;  // Track if all ingredients can be added
                
                for (String ingredient : dish.getIngredients().keySet()) {
                    while (dish.getIngredients().get(ingredient) == 0) {  // While ingredient is not prepared
                        if (inventory.useIngredient(ingredient)) {
                            dish.addIngredient(ingredient);  // Add ingredient to the dish
                            System.out.println(Thread.currentThread().getName() + " Added " + ingredient + " to " + dish.hashCode());
                        } else {
                            allIngredientsAvailable = false;
                            break;  // Break if ingredient is not available
                        }
                    }
                    if (!allIngredientsAvailable) {
                        break;
                    }
                }


                // If all ingredients are available, mark the dish as Made
                if (allIngredientsAvailable && dish.getState().equals("Made")) {
                    System.out.println(dish.hashCode() + " is ready by " + Thread.currentThread().getName() + "!");
                    kitchen.markDishAsMade(dish);
                } else {
                    System.out.println(dish.getClass().getSimpleName() + " by " + Thread.currentThread().getName() + " is not ready. ");
                }

                isProcessingOrder = false;  // Reset flag after processing an order
            }
        }
    }
}