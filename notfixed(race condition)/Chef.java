import java.util.concurrent.atomic.AtomicInteger;

public class Chef implements Runnable {
    private Kitchen kitchen;
    private Inventory inventory;
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_EMPTY_CHECKS = 5;  // Maximum consecutive empty checks before termination
    private static AtomicInteger activeChefsCount = new AtomicInteger(0);

    public Chef(Kitchen kitchen, Inventory inventory) {
        this.kitchen = kitchen;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        activeChefsCount.incrementAndGet();
        int emptyCheckCount = 0;  // Counter to track how many times there were no dishes

        while (true) {
            Dish dish = kitchen.getDishToMake();
            
            // If no dish is available, wait for a short time and check again
            if (dish == null) {
                emptyCheckCount++;
                if (emptyCheckCount >= MAX_EMPTY_CHECKS) {
                    System.out.println(Thread.currentThread().getName() + " has waited " + MAX_EMPTY_CHECKS + " times without receiving a dish. Chef is terminating.");
                    break;
                }
                try {
                    Thread.sleep(1000);  // Wait for 1 second before checking again
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // Handle thread interruption
                    break;
                }
                continue;
            }

            emptyCheckCount = 0;  // Reset empty check counter if a dish is found

            System.out.println(Thread.currentThread().getName() + " is preparing: " + dish.getClass().getSimpleName() + " (picked item: " + dish.hashCode() + ")");

            boolean prepared = false;
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                if (inventory.useIngredientsForDish(dish)) {
                    prepared = true;
                    break;
                }
                System.out.println(Thread.currentThread().getName() + " failed to prepare " + dish.getClass().getSimpleName() + ". Attempt " + (attempt + 1) + " of " + MAX_ATTEMPTS);
            }

            if (prepared) {
                System.out.println(Thread.currentThread().getName() + ": " + dish.getClass().getSimpleName() + " is ready!");
                kitchen.markDishAsMade(dish);
            } else {
                System.out.println(Thread.currentThread().getName() + " gave up on preparing " + dish.getClass().getSimpleName() + " after " + MAX_ATTEMPTS + " attempts.");
                kitchen.markDishAsAbandoned(dish);
            }
        }

        activeChefsCount.decrementAndGet();
        System.out.println(Thread.currentThread().getName() + " has finished.");
    }

    public static boolean areAllChefsDone() {
        return activeChefsCount.get() == 0;
    }
}
