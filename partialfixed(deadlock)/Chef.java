import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.atomic.AtomicInteger;

public class Chef implements Runnable {
    private Kitchen kitchen;
    private Inventory inventory;
    private int chefId;  
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_EMPTY_CHECKS = 5;  // Maximum consecutive empty checks before termination
    private static AtomicInteger activeChefsCount = new AtomicInteger(0);

    public Chef(int chefId, Kitchen kitchen, Inventory inventory) {
        this.chefId = chefId; 
        this.kitchen = kitchen;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        activeChefsCount.incrementAndGet();
        int emptyCheckCount = 0;

        while (true) {
            Dish dish = kitchen.getDishToMake();
            
            if (dish == null) {
                emptyCheckCount++;
                if (emptyCheckCount >= MAX_EMPTY_CHECKS) {
                    System.out.println("Chef " + chefId + " has waited " + MAX_EMPTY_CHECKS + " times without receiving a dish. Chef is terminating.");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            emptyCheckCount = 0;  // Reset empty check counter if a dish is found

            System.out.println("Chef " + chefId + " is preparing: " + dish.getClass().getSimpleName() + " (picked item: " + dish.hashCode() + ")");

            boolean prepared = false;
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                if (inventory.useIngredientsForDish(dish)) {
                    prepared = true;
                    break;
                }
                System.out.println("Chef " + chefId + " failed to prepare " + dish.getClass().getSimpleName() + ". Attempt " + (attempt + 1) + " of " + MAX_ATTEMPTS);
            }

            if (prepared) {
                System.out.println("Chef " + chefId + ": " + dish.getClass().getSimpleName() + " is ready!");
                kitchen.markDishAsMade(dish);
            } else {
                System.out.println("Chef " + chefId + " gave up on preparing " + dish.getClass().getSimpleName() + " after " + MAX_ATTEMPTS + " attempts.");
                kitchen.markDishAsAbandoned(dish);
            }
        }

        activeChefsCount.decrementAndGet();
        System.out.println("Chef " + chefId + " has finished.");
    }

    public static boolean areAllChefsDone() {
        return activeChefsCount.get() == 0;
    }
}
